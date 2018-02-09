package views.graph.styles;

import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.input.*;
import selections.*;
import views.data.*;
import views.graph.elements.*;
import views.toggle.*;

import java.util.*;

public class SpaceFillingStyle extends Style {

    private final ObservableMap<Integer, AtomView> atomViewMap = FXCollections.observableHashMap();

    private final SelectionModel<AtomView> selectionModel;

    public SpaceFillingStyle(List<AtomViewData> atomViewData, List<BondViewData> bondViewData) {
        addListener();

        addAtomViews(atomViewData);

        selectionModel = new SelectionModel<>(atomViewMap);
    }

    private void addAtomViews(List<AtomViewData> atomViewData) {
        for (AtomViewData atom : atomViewData) {
            AtomView atomView = new AtomView(atom, atom.getRadius());
            atomView.addEventHandler(MouseEvent.MOUSE_CLICKED, this::atomViewClicked);
            atomViewMap.put(atom.getId(), atomView);
        }
    }

    private void atomViewClicked(MouseEvent event) {
        if (!event.isStillSincePress())
            return;

        selectionModel.clearAndSelect(((AtomView) event.getSource()).getAtomId());
    }

    private void addListener() {
        atomViewMap.addListener((MapChangeListener<Integer, Group>) change -> updateGroup(change, this));
    }

    @Override
    public void colorModeChange(ColorModeType colorMode) {
        atomViewMap.values()
                .forEach(atom -> atom.colorize(colorMode));
    }

    @Override
    public SelectionModel<AtomView> getSelectionModel() {
        return selectionModel;
    }
}
