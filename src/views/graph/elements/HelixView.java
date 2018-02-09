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

public class HelixView extends ProteinShapeView {

    private static final int WINDOW_SIZE = 4;
    private static final double WIDTH_FACTOR = 10;
    private static final int HELIX_SMOOTH_FACTOR = 20;
    private static final int WIDTH_SMOOTH_FACTOR = 10;
    private static final int DIRECTION_SMOOTH_FACTOR = 10;

    private final ObservableList<Plane3d> planes = FXCollections.observableArrayList();

    private final List<AtomViewData> backboneAtoms;

    public HelixView(List<Map<String, AtomViewData>> aminoAcidList) {
        addListener();

        backboneAtoms = aminoAcidList.stream()
                .map(Map::values)
                .flatMap(Collection::stream)
                .filter(AtomViewData::isBackbone)
                .collect(Collectors.toList());

        List<Point3D> aminoAcidCenterPoints = createCenterPoints(aminoAcidList);
        List<Point3D> helixPoints = createHelixPoints(aminoAcidCenterPoints);
        List<Point3D> helixCenterPoints = createHelixCenterPoints(aminoAcidCenterPoints);

        if (helixCenterPoints.isEmpty())
            return;

        List<Point3D> helixDirections = createHelixDirections(helixCenterPoints);

        createHelix(helixPoints, helixDirections);
    }

    private void createHelix(List<Point3D> helixPoints, List<Point3D> helixDirections) {
        double helixPointIndexMultiplier = 100.0 / helixPoints.size();
        double helixDirectionIndexMultiplier = helixDirections.size() / 100.0;

        Point3D lastLeft = null;
        Point3D lastRight = null;
        Plane3d lastPlane = null;
        for (int i = 0; i < helixPoints.size(); i++) {
            Point3D helixPoint = helixPoints.get(i);
            int directionIndex = (int) (helixPointIndexMultiplier * i * helixDirectionIndexMultiplier);
            Point3D direction = helixDirections.get(directionIndex);

            Point3D currentLeft = helixPoint.add(direction.multiply(WIDTH_FACTOR));
            Point3D currentRight = helixPoint.add(direction.multiply(-WIDTH_FACTOR));

            if (lastPlane == null) {
                if (lastLeft != null && lastRight != null) {
                    Plane3d plane = new Plane3d(lastLeft, lastRight, currentLeft, currentRight);
                    planes.add(plane);
                    lastPlane = plane;
                }
            } else {
                Plane3d plane = new Plane3d(lastPlane.getCube(), currentLeft, currentRight);
                planes.add(plane);
                lastPlane = plane;
            }

            lastLeft = currentLeft;
            lastRight = currentRight;
        }
    }

    private List<Point3D> createHelixCenterPoints(List<Point3D> aminoAcidCenterPoints) {
        List<Point3D> helixCenterPoints = createHelixCenterPointsRecursive(aminoAcidCenterPoints, WINDOW_SIZE);

        if (helixCenterPoints.isEmpty())
            return helixCenterPoints;

        Spline3D spline3D = new Spline3D(helixCenterPoints);
        return spline3D.stream(WIDTH_SMOOTH_FACTOR)
                .collect(Collectors.toList());
    }

    private List<Point3D> createHelixCenterPointsRecursive(List<Point3D> aminoAcidCenterPoints, int windowSize) {
        if (windowSize <= 0)
            return new ArrayList<>();

        Window window = new Window(windowSize);

        List<Point3D> helixCenterPoints = aminoAcidCenterPoints.stream()
                .map(window::addAndGetAverage)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        if (helixCenterPoints.size() >= 2)
            return helixCenterPoints;

        return createHelixCenterPointsRecursive(aminoAcidCenterPoints, windowSize - 1);
    }

    private List<Point3D> createHelixDirections(List<Point3D> helixCenterPoints) {
        List<Point3D> directions = new ArrayList<>();

        Point3D last = null;
        for (Point3D helixCenterPoint : helixCenterPoints) {
            if (last != null)
                directions.add(helixCenterPoint.subtract(last));

            last = helixCenterPoint;
        }

        Spline3D spline3D = new Spline3D(directions);
        return spline3D.stream(DIRECTION_SMOOTH_FACTOR)
                .collect(Collectors.toList());
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

    private List<Point3D> createHelixPoints(List<Point3D> aminoAcidCenterPoints) {
        Spline3D spline3D = new Spline3D(aminoAcidCenterPoints);
        return spline3D.stream(HELIX_SMOOTH_FACTOR)
                .collect(Collectors.toList());
    }

    private List<Point3D> createCenterPoints(List<Map<String, AtomViewData>> aminoAcidList) {
        return aminoAcidList.stream()
                .map(Map::values)
                .map(this::center)
                .collect(Collectors.toList());
    }

    private Point3D center(Collection<AtomViewData> atomViewData) {
        List<AtomViewData> aminoAcidBackboneAtoms = atomViewData.stream()
                .filter(AtomViewData::isBackbone)
                .collect(Collectors.toList());

        return aminoAcidBackboneAtoms.stream()
                .map(AtomViewData::getPosition)
                .reduce(Point3D::add)
                .map(sum -> sum.multiply(1.0 / aminoAcidBackboneAtoms.size()))
                .orElseThrow(() -> new RuntimeException("This should not be possible"));
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
        Plane3d start = planes.get(0);
        return new Pair<>(start.getSourceLeft(), start.getSourceRight());
    }

    @Override
    public Pair<Point3D, Point3D> endPoints() {
        Plane3d end = planes.get(planes.size() - 1);
        return new Pair<>(end.getTargetLeft(), end.getTargetRight());
    }

    @Override
    public AtomViewData firstAtom() {
        return backboneAtoms.get(0);
    }

    @Override
    public AtomViewData lastAtom() {
        return backboneAtoms.get(backboneAtoms.size() - 1);
    }

    class Window {
        private final int windowSize;
        private final LinkedList<Point3D> points = new LinkedList<>();

        Window(int windowSize) {
            this.windowSize = windowSize;
        }

        Optional<Point3D> addAndGetAverage(Point3D point3D) {
            points.add(point3D);

            if (points.size() > windowSize)
                points.poll();

            return average();
        }

        Optional<Point3D> average() {
            if (points.size() < windowSize)
                return Optional.empty();

            return points.stream()
                    .reduce(Point3D::add)
                    .map(sum -> sum.multiply(1.0 / points.size()));
        }
    }
}
