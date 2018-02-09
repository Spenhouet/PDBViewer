package views.graph.elements;

import helpers.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.util.*;
import views.data.*;
import views.graph.shapes.*;
import views.toggle.*;

import java.util.*;
import java.util.stream.*;

public class SplineView extends ProteinShapeView {

    private static final int FILL_FACTOR = 10;

    private final DoubleProperty radius = new SimpleDoubleProperty(5);

    private final ObservableList<Line3d> lines = FXCollections.observableArrayList();

    private final List<AtomViewData> atoms;

    public SplineView(List<Map<String, AtomViewData>> aminoAcidList) {
        this.atoms = aminoAcidList.stream()
                .map(Map::values)
                .map(TreeSet::new)
                .flatMap(TreeSet::stream)
                .filter(AtomViewData::isBackbone)
                .collect(Collectors.toList());

        addListener();

        List<Point3D> points = atoms.stream()
                .map(AtomViewData::getPosition)
                .collect(Collectors.toList());

        createLine(points);
    }

    private void addListener() {
        lines.addListener((ListChangeListener<Line3d>) c -> {
            while (c.next()) {
                c.getRemoved()
                        .forEach(this.getChildren()::remove);
                c.getAddedSubList()
                        .forEach(this.getChildren()::add);
            }
        });
    }

    private void createLine(List<Point3D> points) {
        Spline3D spline3D = new Spline3D(points);

        Point3D lastPoint = null;
        for (Point3D point : (Iterable<Point3D>) spline3D.stream(FILL_FACTOR)::iterator) {
            if (lastPoint != null && point != null) {
                Line3d line3d = new Line3d(lastPoint, point, radius);
                lines.add(line3d);
            }
            lastPoint = point;
        }
    }

    public void colorize(ColorModeType colorMode) {
        double linesIndexMultiplier = 100.0 / lines.size();
        double atomsIndexMultiplier = atoms.size() / 100.0;

        for (int i = 0; i < lines.size(); i++) {
            Line3d line = lines.get(i);
            int atomIndex = (int) (linesIndexMultiplier * i * atomsIndexMultiplier);
            AtomViewData data = atoms.get(atomIndex);
            line.setColor(data.getColor(colorMode));
        }
    }

    public void setRadius(double radius) {
        this.radius.set(radius);
    }

    @Override
    public Pair<Point3D, Point3D> startPoints() {
        Line3d start = lines.get(0);
        return new Pair<>(start.getSource(), start.getSource());
    }

    @Override
    public Pair<Point3D, Point3D> endPoints() {
        Line3d end = lines.get(lines.size() - 1);
        return new Pair<>(end.getTarget(), end.getTarget());
    }

    @Override
    public AtomViewData firstAtom() {
        return atoms.get(0);
    }

    @Override
    public AtomViewData lastAtom() {
        return atoms.get(atoms.size() - 1);
    }
}
