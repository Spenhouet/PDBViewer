package views.graph.styles;

import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.input.*;
import selections.*;
import views.data.*;
import views.graph.elements.*;
import views.toggle.*;

import java.util.*;
import java.util.stream.*;

public class BackboneStyle extends Style {

    private static final double RADIUS = 6;

    private final ObservableMap<Integer, AtomView> atomViewMap = FXCollections.observableHashMap();
    private final ObservableMap<Integer, BondView> bondViewMap = FXCollections.observableHashMap();

    private final Group atomViewGroup = new Group();
    private final Group bondViewGroup = new Group();

    private final SelectionModel<AtomView> selectionModel;

    public BackboneStyle(List<AtomViewData> atomViewData, List<BondViewData> bondViewData) {
        this.getChildren()
                .addAll(bondViewGroup, atomViewGroup);

        addListener();

        addAtomViews(atomViewData);
        addBondViews(bondViewData);

        selectionModel = new SelectionModel<>(atomViewMap);
    }

    private void addAtomViews(List<AtomViewData> atomViewData) {
        for (AtomViewData atom : atomViewData) {
            if (!atom.isBackbone())
                continue;

            AtomView atomView = new AtomView(atom, RADIUS);
            atomView.addEventHandler(MouseEvent.MOUSE_CLICKED, this::atomViewClicked);
            atomViewMap.put(atom.getId(), atomView);
        }
    }

    private void addBondViews(List<BondViewData> bondViewData) {
        for (BondViewData bond : bondViewData) {
            if (!bond.isBackbone())
                continue;

            AtomViewData sourceAtom = atomViewMap.get(bond.getSourceAtomId())
                    .getData();
            AtomViewData targetAtom = atomViewMap.get(bond.getTargetAtomId())
                    .getData();

            BondView bondView = new BondView(sourceAtom, targetAtom);
            bondView.addEventHandler(MouseEvent.MOUSE_CLICKED, this::bondViewClicked);
            bondView.setRadius(RADIUS);

            bondViewMap.put(bond.getId(), bondView);
        }
    }

    private void atomViewClicked(MouseEvent event) {
        if (!event.isStillSincePress())
            return;

        selectionModel.clearAndSelect(((AtomView) event.getSource()).getAtomId());
    }

    private void bondViewClicked(MouseEvent event) {
        if (!event.isStillSincePress())
            return;

        BondView bondView = (BondView) event.getSource();

        Set<Integer> atomIds = Stream.of(bondView.getAtom1(), bondView.getAtom2())
                .map(AtomViewData::getId)
                .collect(Collectors.toSet());

        selectionModel.clearAndSelectAll(atomIds);
    }

    private void addListener() {
        atomViewMap.addListener((MapChangeListener<Integer, Group>) change -> updateGroup(change, atomViewGroup));
        bondViewMap.addListener((MapChangeListener<Integer, Group>) change -> updateGroup(change, bondViewGroup));
    }

    @Override
    public void colorModeChange(ColorModeType colorMode) {
        atomViewMap.values()
                .forEach(atom -> atom.colorize(colorMode));
        bondViewMap.values()
                .forEach(bond -> bond.colorize(colorMode));
    }

    @Override
    public SelectionModel<AtomView> getSelectionModel() {
        return selectionModel;
    }
}
