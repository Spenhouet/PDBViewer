package views.graph.shapes;

import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.paint.*;

public class Plane3d extends Group {

    private static final int DEPTH = 1;

    private final PhongMaterial material = new PhongMaterial();
    private final Cube3d cube;

    private final Point3D sourceLeft;
    private final Point3D sourceRight;
    private final Point3D targetLeft;
    private final Point3D targetRight;

    /**
     * ⏺ ― ― ⏺       0 ―  ― 2
     * |    ╱ |        |    ╱ |
     * | ╱    |        | ╱    |
     * ⏺ ― ― ⏺       1 ―  ― 3
     */
    public Plane3d(Point3D p0, Point3D p1, Point3D p2, Point3D p3) {
        this.sourceLeft = p0;
        this.sourceRight = p1;
        this.targetLeft = p2;
        this.targetRight = p3;

        Point3D direction = calculateDirectionChange(p0, p1, p2, p3);

        Point3D cubeP0 = p0.add(direction);
        Point3D cubeP1 = p1.add(direction);
        Point3D cubeP2 = p2.add(direction);
        Point3D cubeP3 = p3.add(direction);

        Point3D cubeP4 = p0.subtract(direction);
        Point3D cubeP5 = p1.subtract(direction);
        Point3D cubeP6 = p2.subtract(direction);
        Point3D cubeP7 = p3.subtract(direction);

        Cube3d cube3d = new Cube3d(cubeP0, cubeP1, cubeP2, cubeP3, cubeP4, cubeP5, cubeP6, cubeP7);

        cube3d.getMaterial()
                .diffuseColorProperty()
                .bind(material.diffuseColorProperty());
        cube3d.getMaterial()
                .specularColorProperty()
                .bind(material.specularColorProperty());

        this.cube = cube3d;

        this.getChildren()
                .add(cube3d);
    }

    /**
     * ⏺ ― ― ⏺       0 ―  ― 2
     * |    ╱ |        |    ╱ |
     * | ╱    |        | ╱    |
     * ⏺ ― ― ⏺       1 ―  ― 3
     */
    public Plane3d(Cube3d cube, Point3D p2, Point3D p3) {
        this.sourceLeft = cube.getP2()
                .midpoint(cube.getP6());
        this.sourceRight = cube.getP3()
                .midpoint(cube.getP7());
        this.targetLeft = p2;
        this.targetRight = p3;

        Point3D direction = calculateDirectionChange(sourceLeft, sourceRight, p2, p3);

        Point3D cubeP0 = cube.getP2();
        Point3D cubeP1 = cube.getP3();
        Point3D cubeP2 = p2.add(direction);
        Point3D cubeP3 = p3.add(direction);

        Point3D cubeP4 = cube.getP6();
        Point3D cubeP5 = cube.getP7();
        Point3D cubeP6 = p2.subtract(direction);
        Point3D cubeP7 = p3.subtract(direction);

        Cube3d cube3d = new Cube3d(cubeP0, cubeP1, cubeP2, cubeP3, cubeP4, cubeP5, cubeP6, cubeP7);

        cube3d.getMaterial()
                .diffuseColorProperty()
                .bind(material.diffuseColorProperty());
        cube3d.getMaterial()
                .specularColorProperty()
                .bind(material.specularColorProperty());

        this.cube = cube3d;

        this.getChildren()
                .add(cube3d);
    }

    /**
     * ⏺ ― ― ⏺       0 ―  ― 2
     * |    ╱ |        |    ╱ |
     * | ╱    |        | ╱    |
     * ⏺ ― ― ⏺       1 ―  ― 3
     */
    private static Point3D calculateDirectionChange(Point3D p0, Point3D p1, Point3D p2, Point3D p3) {
        Point3D direction1 = calculateDirectionTriangle(p0, p1, p2);
        Point3D direction2 = calculateDirectionTriangle(p3, p2, p1);
        Point3D average = calculateAverageDirection(direction1, direction2);
        Point3D normalized = average.normalize();
        return normalized.multiply(DEPTH);
    }

    /**
     * ⏺ ― ― ⏺       0 ―  ― 2
     * |    ╱          |    ╱
     * | ╱             | ╱
     * ⏺               1
     */
    private static Point3D calculateDirectionTriangle(Point3D p0, Point3D p1, Point3D p2) {
        return p1.subtract(p0)
                .crossProduct(p2.subtract(p0));
    }

    private static Point3D calculateAverageDirection(Point3D direction1, Point3D direction2) {
        return direction1.add(direction2)
                .multiply(1.0 / 2.0);
    }

    public Cube3d getCube() {
        return cube;
    }

    public void setColor(Color color) {
        material.setDiffuseColor(color);
        material.setSpecularColor(color.brighter());
    }

    public Point3D getSourceLeft() {
        return sourceLeft;
    }

    public Point3D getSourceRight() {
        return sourceRight;
    }

    public Point3D getTargetLeft() {
        return targetLeft;
    }

    public Point3D getTargetRight() {
        return targetRight;
    }
}
