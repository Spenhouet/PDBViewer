package presenters;

import helpers.*;
import javafx.beans.*;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.concurrent.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.util.*;
import models.*;
import views.*;
import views.data.*;
import views.dialogs.*;
import views.graph.styles.*;
import views.toggle.*;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;

import static java.util.stream.Collectors.*;

class ProteinViewPresenter {

    private final PdbView view;
    private final PdbViewerModel model;

    // Prevent concurrent selection cycles:
    private final AtomicBoolean sequenceSelectionInProgress = new AtomicBoolean(false);
    private final AtomicBoolean atomSelectionInProgress = new AtomicBoolean(false);

    private final BooleanProperty showOnlyBackbone = new SimpleBooleanProperty(true);

    ProteinViewPresenter(PdbView view, PdbViewerModel model) {
        this.view = view;
        this.model = model;

        addBindings();
        addListener();
    }

    private static void syncSelectionRange(IndexRange oldValue, IndexRange newValue, TextArea textArea) {
        if (newValue.getStart() == oldValue.getStart())
            textArea.selectRange(newValue.getStart(), newValue.getEnd());
        else if (newValue.getEnd() == oldValue.getEnd())
            textArea.selectRange(newValue.getEnd(), newValue.getStart());
        else
            textArea.selectRange(newValue.getStart(), newValue.getEnd());
    }

    private void addBindings() {
        view.getProteinViewStackPane()
                .backgroundProperty()
                .bind(Bindings.createObjectBinding(() -> new Background(new BackgroundFill(view.getBackgroundColorPicker()
                        .getValue(), CornerRadii.EMPTY, Insets.EMPTY)), view.getBackgroundColorPicker()
                        .valueProperty()));

        showOnlyBackbone.bind(view.getVisualizationModelSelectionProperty()
                .isEqualTo(StyleType.BACKBONE));

        view.getProteinSequence()
                .scrollLeftProperty()
                .bindBidirectional(view.getSecondaryStructure()
                        .scrollLeftProperty());

        RadioMenuItemSync.bind(view.getColorToggleGroup(), view.getColorModeChoiceBox());
        RadioMenuItemSync.bind(view.getStyleToggleGroup(), view.getStyleChoiceBox());
    }

    private void addListener() {
        model.proteinStructureProperty()
                .addListener(listener -> view.getProteinSequence()
                        .setText(model.getProteinStructure()
                                .getSequence()));

        model.proteinStructureProperty()
                .addListener(listener -> setSecondaryStructure());

        view.getProteinSequence()
                .selectionProperty()
                .addListener((observable, oldValue, newValue) -> syncSelectionRange(oldValue, newValue, view.getSecondaryStructure()));
        view.getSecondaryStructure()
                .selectionProperty()
                .addListener((observable, oldValue, newValue) -> syncSelectionRange(oldValue, newValue, view.getProteinSequence()));

        model.proteinStructureProperty()
                .addListener((observable, oldValue, newValue) -> initView(newValue));

        view.getBottomPane()
                .addEventHandler(MouseEvent.MOUSE_CLICKED, this::handleGraphSelection);

        view.getProteinView()
                .aminoAcidSelectionProperty()
                .addListener(listener -> handleAminoAcidSelection());

        view.getProteinSequence()
                .selectionProperty()
                .addListener((observable, oldValue, newValue) -> handleSequenceSelection(newValue));

        view.getProteinView()
                .styleViewProperty()
                .addListener(listener -> view.getProteinSequence()
                        .deselect());
    }

    private void handleSequenceSelection(IndexRange range) {
        if (sequenceSelectionInProgress.get() || view.getProteinView()
                .aminoAcidSelectionProperty()
                .isNull()
                .get())
            return;

        atomSelectionInProgress.set(true);

        if (range.getLength() <= 0) {
            view.getProteinView()
                    .getAminoAcidSelection()
                    .clearSelection();
            atomSelectionInProgress.set(false);
            return;
        }

        Set<Integer> atomIds = IntStream.range(range.getStart(), range.getEnd())
                .boxed()
                .map(model.getProteinStructure()
                        .getAminoAcids()::get)
                .map(AminoAcidModel::getAtomIdMap)
                .map(Map::keySet)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        view.getProteinView()
                .getAminoAcidSelection()
                .clearAndSelectAll(atomIds);

        atomSelectionInProgress.set(false);
    }

    private void handleAminoAcidSelection() {
        if (view.getProteinView()
                .aminoAcidSelectionProperty()
                .isNull()
                .get())
            return;

        view.getProteinView()
                .getAminoAcidSelection()
                .getSelectedIndices()
                .addListener((InvalidationListener) observable -> {
                    if (atomSelectionInProgress.get())
                        return;

                    sequenceSelectionInProgress.set(true);
                    Set<Integer> selectedIndicesSet = view.getProteinView()
                            .getAminoAcidSelection()
                            .getSelectedIndicesSet();

                    if (selectedIndicesSet.isEmpty()) {
                        view.getProteinSequence()
                                .deselect();
                    } else {
                        IndexRange range = getAminoAcidSelectionRange(selectedIndicesSet);
                        view.getProteinSequence()
                                .selectRange(range.getStart(), range.getEnd());
                    }
                    sequenceSelectionInProgress.set(false);
                });
    }

    private void handleGraphSelection(MouseEvent event) {
        if (!event.isStillSincePress() || view.getProteinView()
                .aminoAcidSelectionProperty()
                .isNull()
                .get())
            return;

        if (event.getSource()
                .equals(event.getTarget())) {
            view.getProteinView()
                    .getAminoAcidSelection()
                    .clearSelection();
        } else {
            Set<Integer> atomIds = view.getProteinView()
                    .getAminoAcidSelection()
                    .getSelectedIndicesSet()
                    .stream()
                    .map(model.getProteinStructure()
                            .getAtoms()::get)
                    .map(AtomModel::getAminoAcidId)
                    .map(model.getProteinStructure()
                            .getAminoAcids()::get)
                    .map(AminoAcidModel::getAtomIdMap)
                    .map(Map::keySet)
                    .flatMap(Collection::stream)
                    .collect(toSet());

            view.getProteinView()
                    .getAminoAcidSelection()
                    .selectAll(atomIds);
        }
    }

    private void initView(ProteinStructureModel proteinStructureModel) {
        Task<Pair<List<AtomViewData>, List<BondViewData>>> loadViewDataTask = new Task<Pair<List<AtomViewData>, List<BondViewData>>>() {
            @Override
            protected Pair<List<AtomViewData>, List<BondViewData>> call() {
                List<AtomViewData> atomViewData = proteinStructureModel.getAtoms()
                        .values()
                        .stream()
                        .map(atomModel -> {
                            String atomHint = atomModel.getAminoAcidPosition() + ":" + atomModel.getName();

                            double radius = atomModel.getElement()
                                    .getRadius();
                            EnumMap<ColorModeType, Color> colorMap = getColorMapForAtom(atomModel);

                            SecondaryStructure secondaryStructure = model.getProteinStructure()
                                    .getAminoAcids()
                                    .get(atomModel.getAminoAcidId())
                                    .getSecondaryStructureElement();

                            return new AtomViewData(atomModel.getId(), atomModel.getName(), atomModel.getSequenceId(), atomModel.getAminoAcidId(), atomHint, atomModel.getCoordinates()
                                    .multiply(50), atomModel.isBackbone(), secondaryStructure, radius, colorMap);
                        })
                        .collect(toList());

                List<BondViewData> bondViewData = proteinStructureModel.getBonds()
                        .values()
                        .stream()
                        .map(bondModel -> new BondViewData(bondModel.getId(), bondModel.getAtom1()
                                .getId(), bondModel.getAtom2()
                                .getId(), bondModel.isBackbone()))
                        .collect(toList());

                return new Pair<>(atomViewData, bondViewData);
            }
        };

        loadViewDataTask.valueProperty()
                .addListener((observable, oldValue, data) -> {
                    Task<Style> styleTask = view.getProteinView()
                            .setData(data.getKey(), data.getValue());

                    Progress.showSpinner(styleTask, view.getProteinViewProgressIndicator(), view.getProteinViewVBox());
                    Progress.showSpinner(styleTask, view.getStatisticsProgressIndicator(), view.getStatisticsScrollPane());
                    new Thread(styleTask).start();
                });

        Progress.showSpinner(loadViewDataTask, view.getProteinViewProgressIndicator(), view.getProteinViewVBox());
        Progress.showSpinner(loadViewDataTask, view.getStatisticsProgressIndicator(), view.getStatisticsScrollPane());
        new Thread(loadViewDataTask).start();
    }

    private void setSecondaryStructure() {
        String structure = model.getProteinStructure()
                .getAminoAcids()
                .values()
                .stream()
                .map(AminoAcidModel::getSecondaryStructureElement)
                .map(SecondaryStructure::getCode)
                .map(String::valueOf)
                .collect(Collectors.joining());

        view.getSecondaryStructure()
                .setText(structure);
    }

    private IndexRange getAminoAcidSelectionRange(Set<Integer> atomIds) {
        int minId = Integer.MAX_VALUE;
        int maxId = Integer.MIN_VALUE;

        for (Integer id : atomIds) {
            int aminoAcidId = model.getProteinStructure()
                    .getAtoms()
                    .get(id)
                    .getAminoAcidId();

            if (aminoAcidId < minId)
                minId = aminoAcidId;

            if (aminoAcidId > maxId)
                maxId = aminoAcidId;
        }

        return new IndexRange(minId, maxId + 1);
    }

    private EnumMap<ColorModeType, Color> getColorMapForAtom(AtomModel atomModel) {
        EnumMap<ColorModeType, Color> colorMap = new EnumMap<>(ColorModeType.class);
        colorMap.put(ColorModeType.CPK, atomModel.getElement()
                .getColor());
        colorMap.put(ColorModeType.STRUCTURE, model.getProteinStructure()
                .getAminoAcids()
                .get(atomModel.getAminoAcidId())
                .getSecondaryStructureElement()
                .getColor());
        colorMap.put(ColorModeType.SHAPELY, atomModel.getAcid()
                .getColor());
        colorMap.put(ColorModeType.SEQUENCE, model.getProteinStructure()
                .getSequences()
                .get(atomModel.getSequenceId())
                .getColor());
        colorMap.put(ColorModeType.TEMPERATURE, TemperatureFactor.color(atomModel.getTemperatureFactor()));
        return colorMap;
    }

}
