package views.graph.styles;

import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.input.*;
import selections.*;
import views.data.*;
import views.graph.elements.*;
import views.toggle.*;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

import static java.util.stream.Collectors.*;

public class RibbonStyle extends Style {
    private final ObservableMap<Integer, AtomView> atomViewMap = FXCollections.observableHashMap();
    private final ObservableMap<Integer, RibbonView> ribbonViewMap = FXCollections.observableHashMap();

    private final Group atomViewGroup = new Group();
    private final Group ribbonViewGroup = new Group();

    private final SelectionModel<AtomView> selectionModel;

    public RibbonStyle(List<AtomViewData> atomViewData, List<BondViewData> bondViewData) {
        addListener();

        addAtomViews(atomViewData);
        addRibbonViews(atomViewData);

        this.getChildren()
                .addAll(atomViewGroup, ribbonViewGroup);

        selectionModel = new SelectionModel<>(atomViewMap);
    }

    private void addAtomViews(List<AtomViewData> atomViewData) {
        for (AtomViewData atom : atomViewData) {
            double radius = atom.getRadius() / 15;
            AtomView atomView = new AtomView(atom, radius);
            atomView.addEventHandler(MouseEvent.MOUSE_CLICKED, this::atomViewClicked);
            atomViewMap.put(atom.getId(), atomView);
        }
    }

    private void addRibbonViews(List<AtomViewData> atomViewData) {
        Map<String, Map<Integer, Map<String, AtomViewData>>> map = atomViewData.stream()
                .collect(groupingBy(AtomViewData::getSequence, groupingBy(AtomViewData::getAminoAcid, toMap(AtomViewData::getName, Function.identity()))));

        AtomicInteger uniqueRibbonId = new AtomicInteger(0);

        for (Map<Integer, Map<String, AtomViewData>> sequence : map.values()) {
            Map<String, AtomViewData> lastAminoAcidAtoms = null;

            for (Map<String, AtomViewData> aminoAcidAtoms : sequence.values()) {
                if (lastAminoAcidAtoms != null && aminoAcidAtoms != null && lastAminoAcidAtoms.size() > 0 && aminoAcidAtoms.size() > 0) {
                    RibbonView ribbonView = new RibbonView(lastAminoAcidAtoms, aminoAcidAtoms);
                    ribbonViewMap.put(uniqueRibbonId.getAndIncrement(), ribbonView);
                }

                lastAminoAcidAtoms = aminoAcidAtoms;
            }

        }
    }

    private void atomViewClicked(MouseEvent event) {
        if (!event.isStillSincePress())
            return;

        selectionModel.clearAndSelect(((AtomView) event.getSource()).getAtomId());
    }

    private void addListener() {
        atomViewMap.addListener((MapChangeListener<Integer, Group>) change -> updateGroup(change, atomViewGroup));
        ribbonViewMap.addListener((MapChangeListener<Integer, Group>) change -> updateGroup(change, ribbonViewGroup));
    }

    @Override
    public void colorModeChange(ColorModeType colorMode) {
        atomViewMap.values()
                .forEach(atom -> atom.colorize(colorMode));
        ribbonViewMap.values()
                .forEach(ribbon -> ribbon.colorize(colorMode));
    }

    @Override
    public SelectionModel<AtomView> getSelectionModel() {
        return selectionModel;
    }
}
