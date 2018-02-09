package views.graph.elements;

import javafx.geometry.*;
import javafx.scene.*;
import javafx.util.*;
import views.data.*;

public abstract class ProteinShapeView extends Group {

    public abstract Pair<Point3D, Point3D> startPoints();

    public abstract Pair<Point3D, Point3D> endPoints();

    public abstract AtomViewData firstAtom();

    public abstract AtomViewData lastAtom();

}
