package views.graph.elements;

import helpers.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.util.*;
import views.data.*;
import views.graph.shapes.*;
import views.toggle.*;

import java.util.*;
import java.util.stream.*;

public class SheetView extends ProteinShapeView {

    private static final int SMOOTH_FACTOR = 10;

    private final ObservableList<Plane3d> planes = FXCollections.observableArrayList();

    private final List<AtomViewData> backboneAtoms;
    private final List<Pair<Point3D, Point3D>> strands;

    public SheetView(List<Map<String, AtomViewData>> aminoAcidList) {
        addListener();

        backboneAtoms = aminoAcidList.stream()
                .map(Map::values)
                .flatMap(Collection::stream)
                .filter(AtomViewData::isBackbone)
                .collect(Collectors.toList());

        strands = createStrands(aminoAcidList);
        createSheet(strands);
    }

    private void createSheet(List<Pair<Point3D, Point3D>> strands) {
        Pair<Point3D, Point3D> lastStrand = null;
        Plane3d lastPlane = null;
        for (Pair<Point3D, Point3D> strand : strands) {

            if (lastPlane == null) {
                if (lastStrand != null && strand != null) {
                    Plane3d plane = new Plane3d(lastStrand.getKey(), lastStrand.getValue(), strand.getKey(), strand.getValue());
                    planes.add(plane);
                    lastPlane = plane;
                }
            } else {
                Plane3d plane = new Plane3d(lastPlane.getCube(), strand.getKey(), strand.getValue());
                planes.add(plane);
                lastPlane = plane;
            }

            lastStrand = strand;
        }
    }

    private List<Pair<Point3D, Point3D>> createStrands(List<Map<String, AtomViewData>> aminoAcidList) {
        List<Point3D> cAPoints = aminoAcidList.stream()
                .filter(aminoAcid -> aminoAcid.containsKey("CA"))
                .map(aminoAcid -> aminoAcid.get("CA"))
                .map(AtomViewData::getPosition)
                .collect(Collectors.toList());

        List<Point3D> leftPoints = getSidePoints(aminoAcidList, true);
        List<Point3D> rightPoints = getSidePoints(aminoAcidList, false);

        if (leftPoints.size() == 1 && rightPoints.size() == 1)
            return Collections.singletonList(new Pair<>(leftPoints.get(0), rightPoints.get(0)));

        Spline3D leftSpline = new Spline3D(leftPoints);
        Spline3D rightSpline = new Spline3D(rightPoints);

        List<Pair<Point3D, Point3D>> points = new ArrayList<>();

        for (double position = 0; position <= 1; position += 1.0 / (double) (cAPoints.size() * SMOOTH_FACTOR)) {
            points.add(new Pair<>(leftSpline.getPoint(position), rightSpline.getPoint(position)));
        }

        return points;
    }

    private List<Point3D> getSidePoints(List<Map<String, AtomViewData>> aminoAcidList, boolean orientation) {
        List<Point3D> sidePoints = new ArrayList<>();

        for (Map<String, AtomViewData> aminoAcid : aminoAcidList) {
            if (!aminoAcid.containsKey("CB"))
                continue;

            Point3D cA = aminoAcid.get("CA")
                    .getPosition();
            Point3D cB = aminoAcid.get("CB")
                    .getPosition();

            sidePoints.add(orientation ? cB : reflection(cA, cB));
            orientation = !orientation;
        }

        return sidePoints;
    }

    private Point3D reflection(Point3D middle, Point3D outer) {
        return middle.add(outer.subtract(middle)
                .multiply(-1));
    }

    private void addListener() {
        planes.addListener(viewUpdate());
    }

    private <T extends Node> ListChangeListener<T> viewUpdate() {
        return c -> {
            while (c.next()) {
                c.getRemoved()
                        .forEach(this.getChildren()::remove);
                c.getAddedSubList()
                        .forEach(this.getChildren()::add);
            }
        };
    }

    public void colorize(ColorModeType colorMode) {
        double linesIndexMultiplier = 100.0 / planes.size();
        double atomsIndexMultiplier = backboneAtoms.size() / 100.0;

        for (int i = 0; i < planes.size(); i++) {
            Plane3d plane = planes.get(i);
            int atomIndex = (int) (linesIndexMultiplier * i * atomsIndexMultiplier);
            AtomViewData data = backboneAtoms.get(atomIndex);
            plane.setColor(data.getColor(colorMode));
        }
    }

    @Override
    public Pair<Point3D, Point3D> startPoints() {
        return strands.get(0);
    }

    @Override
    public Pair<Point3D, Point3D> endPoints() {
        Pair<Point3D, Point3D> endPoints = strands.get(strands.size() - 1);

        Point3D direction = endPoints.getKey()
                .subtract(endPoints.getValue())
                .multiply(1.0 / 2.0);

        Point3D left = endPoints.getKey()
                .add(direction);
        Point3D right = endPoints.getValue()
                .subtract(direction);

        return new Pair<>(left, right);
    }

    @Override
    public AtomViewData firstAtom() {
        return backboneAtoms.get(0);
    }

    @Override
    public AtomViewData lastAtom() {
        return backboneAtoms.get(backboneAtoms.size() - 1);
    }
}
