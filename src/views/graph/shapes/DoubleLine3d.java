package views.graph.shapes;

import javafx.beans.property.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.transform.*;

public class DoubleLine3d extends Group {

    private final Point3D source;
    private final Point3D target;
    private final ObjectProperty<Color> diffuseColorLeft = new SimpleObjectProperty<>();
    private final ObjectProperty<Color> specularColorLeft = new SimpleObjectProperty<>();
    private final ObjectProperty<Color> diffuseColorRight = new SimpleObjectProperty<>();
    private final ObjectProperty<Color> specularColorRight = new SimpleObjectProperty<>();
    private final DoubleProperty radius;
    private final Cylinder cylinderLeft;
    private final Cylinder cylinderRight;
    private final DoubleProperty length;

    public DoubleLine3d(Point3D source, Point3D target, DoubleProperty radius) {
        this.source = source;
        this.target = target;
        this.radius = radius;
        this.cylinderLeft = new Cylinder();
        this.cylinderRight = new Cylinder();
        this.length = new SimpleDoubleProperty(0);

        setMaterial();

        bind();

        updateCylinderRotation();

        this.getChildren()
                .addAll(cylinderLeft, cylinderRight);
    }

    /**
     * Calculation from here: http://netzwerg.ch/blog/2015/03/22/javafx-3d-line/
     */
    private void updateCylinderRotation() {
        Point3D yAxis = new Point3D(0, 1, 0);
        Point3D diff = target.subtract(source);
        length.set(diff.magnitude());

        Point3D mid = target.midpoint(source);

        Point3D midLeft = mid.midpoint(source);
        Point3D midRight = target.midpoint(mid);

        Translate moveToLeftMidpoint = new Translate(midLeft.getX(), midLeft.getY(), midLeft.getZ());
        Translate moveToRightMidpoint = new Translate(midRight.getX(), midRight.getY(), midRight.getZ());

        Point3D axisOfRotation = diff.crossProduct(yAxis);
        double angle = Math.acos(diff.normalize()
                .dotProduct(yAxis));
        Rotate rotateAroundCenter = new Rotate(-Math.toDegrees(angle), axisOfRotation);

        cylinderLeft.getTransforms()
                .setAll(moveToLeftMidpoint, rotateAroundCenter);
        cylinderRight.getTransforms()
                .setAll(moveToRightMidpoint, rotateAroundCenter);
    }

    private void setMaterial() {
        PhongMaterial materialLeft = new PhongMaterial();
        materialLeft.diffuseColorProperty()
                .bind(this.diffuseColorLeft);
        materialLeft.specularColorProperty()
                .bind(this.specularColorLeft);
        cylinderLeft.setMaterial(materialLeft);

        PhongMaterial materialRight = new PhongMaterial();
        materialRight.diffuseColorProperty()
                .bind(this.diffuseColorRight);
        materialRight.specularColorProperty()
                .bind(this.specularColorRight);
        cylinderRight.setMaterial(materialRight);
    }

    private void bind() {
        cylinderLeft.radiusProperty()
                .bind(radius);
        cylinderRight.radiusProperty()
                .bind(radius);

        cylinderLeft.heightProperty()
                .bind(length.divide(2));
        cylinderRight.heightProperty()
                .bind(length.divide(2));
    }

    public ObjectProperty<Color> diffuseColorLeftProperty() {
        return diffuseColorLeft;
    }

    public ObjectProperty<Color> specularColorLeftProperty() {
        return specularColorLeft;
    }

    public ObjectProperty<Color> diffuseColorRightProperty() {
        return diffuseColorRight;
    }

    public ObjectProperty<Color> specularColorRightProperty() {
        return specularColorRight;
    }
}
