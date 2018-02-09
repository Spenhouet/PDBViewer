package views.graph.elements;

import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.paint.*;
import views.data.*;
import views.graph.shapes.*;
import views.toggle.*;

import java.util.*;

public class RibbonView extends Group {

    private final AtomViewData sourceCa;
    private final AtomViewData targetCa;

    private final Plane3d sourcePlane;
    private final Plane3d targetPlane;

    public RibbonView(Map<String, AtomViewData> aminoAcidAtomsSource, Map<String, AtomViewData> aminoAcidAtomsTarget) {
        this.sourceCa = aminoAcidAtomsSource.get("CA");
        this.targetCa = aminoAcidAtomsTarget.get("CA");

        Point3D sourceCaPoint = this.sourceCa.getPosition();
        Point3D targetCaPoint = this.targetCa.getPosition();

        Point3D sourceCbPoint;
        if (aminoAcidAtomsSource.containsKey("CB")) {
            sourceCbPoint = aminoAcidAtomsSource.get("CB")
                    .getPosition();
        } else {
            sourceCbPoint = aminoAcidAtomsSource.get("C")
                    .getPosition(); //Workaround GLY
        }

        Point3D targetCbPoint;
        if (aminoAcidAtomsTarget.containsKey("CB")) {
            targetCbPoint = aminoAcidAtomsTarget.get("CB")
                    .getPosition();
        } else {
            targetCbPoint = aminoAcidAtomsTarget.get("C")
                    .getPosition(); //Workaround GLY
        }

        Point3D sourceOppositePoint = reflection(sourceCaPoint, sourceCbPoint);
        Point3D targetOppositePoint = reflection(targetCaPoint, targetCbPoint);

        Point3D middle1 = sourceCbPoint.midpoint(targetOppositePoint);
        Point3D middle3 = targetCbPoint.midpoint(sourceOppositePoint);

        this.sourcePlane = new Plane3d(sourceCbPoint, sourceOppositePoint, middle1, middle3);
        this.targetPlane = new Plane3d(targetCbPoint, targetOppositePoint, middle3, middle1);

        this.getChildren()
                .addAll(sourcePlane, targetPlane);
    }

    private Point3D reflection(Point3D middle, Point3D outer) {
        return middle.add(outer.subtract(middle)
                .multiply(-1));
    }

    public void colorize(ColorModeType colorMode) {
        setColor(sourceCa.getColor(colorMode), targetCa.getColor(colorMode));
    }

    private void setColor(Color colorLeft, Color colorRight) {
        sourcePlane.setColor(colorLeft);
        targetPlane.setColor(colorRight);
    }

}
