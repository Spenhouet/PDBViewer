package views.graph.elements;

import javafx.geometry.*;
import javafx.scene.*;
import javafx.util.*;
import views.graph.shapes.*;
import views.toggle.*;

public class ConnectionView extends Group {

    private final ProteinShapeView lastProteinShapeView;
    private final ProteinShapeView currentProteinShapeView;

    private final Plane3d plane;

    public ConnectionView(ProteinShapeView lastProteinShapeView, ProteinShapeView currentProteinShapeView) {
        this.lastProteinShapeView = lastProteinShapeView;
        this.currentProteinShapeView = currentProteinShapeView;

        Pair<Point3D, Point3D> endPoints = lastProteinShapeView.endPoints();
        Pair<Point3D, Point3D> startPoints = currentProteinShapeView.startPoints();

        this.plane = new Plane3d(endPoints.getKey(), endPoints.getValue(), startPoints.getKey(), startPoints.getValue());

        this.getChildren()
                .add(plane);
    }

    public void colorize(ColorModeType colorMode) {
        if (lastProteinShapeView instanceof SplineView) {
            plane.setColor(currentProteinShapeView.firstAtom()
                    .getColor(colorMode));
        } else if (currentProteinShapeView instanceof SplineView) {
            plane.setColor(lastProteinShapeView.lastAtom()
                    .getColor(colorMode));
        } else {
            plane.setColor(lastProteinShapeView.lastAtom()
                    .getColor(colorMode));
        }
    }
}
