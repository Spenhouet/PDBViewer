package views.graph.shapes;

import javafx.beans.property.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.transform.*;

public class Line3d extends Group {

    private final Point3D source;
    private final Point3D target;
    private final ObjectProperty<Color> diffuseColor = new SimpleObjectProperty<>();
    private final ObjectProperty<Color> specularColor = new SimpleObjectProperty<>();
    private final DoubleProperty radius;
    private final Cylinder cylinder;
    private final DoubleProperty length;

    public Line3d(Point3D source, Point3D target, DoubleProperty radius) {
        this.source = source;
        this.target = target;
        this.radius = radius;
        this.cylinder = new Cylinder();
        this.length = new SimpleDoubleProperty(0);

        setMaterial();

        bind();

        updateCylinderRotation();

        this.getChildren()
                .add(cylinder);
    }

    /**
     * Calculation from here: http://netzwerg.ch/blog/2015/03/22/javafx-3d-line/
     */
    private void updateCylinderRotation() {
        Point3D yAxis = new Point3D(0, 1, 0);
        Point3D diff = target.subtract(source);
        length.set(diff.magnitude());

        Point3D mid = target.midpoint(source);

        Translate moveToMidpoint = new Translate(mid.getX(), mid.getY(), mid.getZ());

        Point3D axisOfRotation = diff.crossProduct(yAxis);
        double angle = Math.acos(diff.normalize()
                .dotProduct(yAxis));
        Rotate rotateAroundCenter = new Rotate(-Math.toDegrees(angle), axisOfRotation);

        cylinder.getTransforms()
                .setAll(moveToMidpoint, rotateAroundCenter);
    }

    private void setMaterial() {
        PhongMaterial materialRight = new PhongMaterial();
        materialRight.diffuseColorProperty()
                .bind(this.diffuseColor);
        materialRight.specularColorProperty()
                .bind(this.specularColor);
        cylinder.setMaterial(materialRight);
    }

    private void bind() {
        cylinder.radiusProperty()
                .bind(radius);
        cylinder.heightProperty()
                .bind(length);
    }

    public void setColor(Color color) {
        diffuseColor.set(color);
        specularColor.set(color.brighter());
    }

    public Point3D getSource() {
        return source;
    }

    public Point3D getTarget() {
        return target;
    }
}
