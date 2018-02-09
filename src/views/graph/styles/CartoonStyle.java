package views.graph.styles;

import helpers.*;
import javafx.collections.*;
import javafx.scene.*;
import selections.*;
import views.data.*;
import views.graph.elements.*;
import views.toggle.*;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

import static helpers.SecondaryStructure.*;
import static java.util.stream.Collectors.*;

public class CartoonStyle extends Style {

    private static final double RADIUS = 6;

    private final ObservableMap<Integer, SheetView> sheetViewMap = FXCollections.observableHashMap();
    private final ObservableMap<Integer, HelixView> helixViewMap = FXCollections.observableHashMap();
    private final ObservableMap<Integer, SplineView> splineViewMap = FXCollections.observableHashMap();
    private final ObservableMap<Integer, ConnectionView> connectionViewMap = FXCollections.observableHashMap();

    private final Group ribbonViewGroup = new Group();
    private final Group helixViewGroup = new Group();
    private final Group splineViewGroup = new Group();
    private final Group connectionViewGroup = new Group();

    public CartoonStyle(List<AtomViewData> atomViewData, List<BondViewData> bondViewData) {
        this.getChildren()
                .addAll(ribbonViewGroup, helixViewGroup, splineViewGroup, connectionViewGroup);

        addListener();

        createViews(atomViewData);
    }

    private void createViews(List<AtomViewData> atomViewData) {
        Map<String, Map<Integer, Map<String, AtomViewData>>> sequences = atomViewData.stream()
                .collect(groupingBy(AtomViewData::getSequence, groupingBy(AtomViewData::getAminoAcid, toMap(AtomViewData::getName, Function.identity()))));

        AtomicInteger uniqueSheetId = new AtomicInteger(0);
        AtomicInteger uniqueHelixId = new AtomicInteger(0);
        AtomicInteger uniqueSplineId = new AtomicInteger(0);
        AtomicInteger uniqueConnectionId = new AtomicInteger(0);

        for (Map<Integer, Map<String, AtomViewData>> sequence : sequences.values()) {

            List<Map<String, AtomViewData>> tempSheetList = new ArrayList<>();
            List<Map<String, AtomViewData>> tempHelixList = new ArrayList<>();
            List<Map<String, AtomViewData>> tempSplineList = new ArrayList<>();

            ProteinShapeView lastProteinShapeView = null;
            for (Map<String, AtomViewData> aminoAcidAtoms : sequence.values()) {
                ProteinShapeView currentProteinShapeView = null;

                SecondaryStructure secondaryStructure = aminoAcidAtoms.get("C")
                        .getSecondaryStructure();

                if (SHEET.equals(secondaryStructure)) {
                    tempSheetList.add(aminoAcidAtoms);
                } else {
                    if (!tempSheetList.isEmpty()) {
                        SheetView sheetView = new SheetView(tempSheetList);
                        sheetViewMap.put(uniqueSheetId.getAndIncrement(), sheetView);
                        currentProteinShapeView = sheetView;
                        tempSheetList = new ArrayList<>();
                    }
                }

                if (HELIX.equals(secondaryStructure)) {
                    tempHelixList.add(aminoAcidAtoms);
                } else {
                    if (!tempHelixList.isEmpty()) {
                        HelixView helixView = new HelixView(tempHelixList);
                        helixViewMap.put(uniqueHelixId.getAndIncrement(), helixView);
                        currentProteinShapeView = helixView;
                        tempHelixList = new ArrayList<>();
                    }
                }

                if (NONE.equals(secondaryStructure)) {
                    tempSplineList.add(aminoAcidAtoms);
                } else {
                    if (!tempSplineList.isEmpty()) {
                        SplineView splineView = new SplineView(tempSplineList);
                        splineView.setRadius(RADIUS);
                        splineViewMap.put(uniqueSplineId.getAndIncrement(), splineView);
                        currentProteinShapeView = splineView;
                        tempSplineList = new ArrayList<>();
                    }
                }

                if (currentProteinShapeView != null && lastProteinShapeView != null)
                    connectionViewMap.put(uniqueConnectionId.getAndIncrement(), new ConnectionView(lastProteinShapeView, currentProteinShapeView));

                if (currentProteinShapeView != null)
                    lastProteinShapeView = currentProteinShapeView;
            }
        }
    }

    private void addListener() {
        sheetViewMap.addListener((MapChangeListener<Integer, Group>) change -> updateGroup(change, ribbonViewGroup));
        helixViewMap.addListener((MapChangeListener<Integer, Group>) change -> updateGroup(change, helixViewGroup));
        splineViewMap.addListener((MapChangeListener<Integer, Group>) change -> updateGroup(change, splineViewGroup));
        connectionViewMap.addListener((MapChangeListener<Integer, Group>) change -> updateGroup(change, connectionViewGroup));
    }

    @Override
    public void colorModeChange(ColorModeType colorMode) {
        sheetViewMap.values()
                .forEach(ribbon -> ribbon.colorize(colorMode));
        helixViewMap.values()
                .forEach(helix -> helix.colorize(colorMode));
        splineViewMap.values()
                .forEach(bond -> bond.colorize(colorMode));
        connectionViewMap.values()
                .forEach(connection -> connection.colorize(colorMode));
    }

    @Override
    public SelectionModel<AtomView> getSelectionModel() {
        return null;
    }
}
