package views.graph.shapes;

import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;

public class Cube3d extends Group {

    private final PhongMaterial material = new PhongMaterial();
    private final Point3D p0;
    private final Point3D p1;
    private final Point3D p2;
    private final Point3D p3;
    private final Point3D p4;
    private final Point3D p5;
    private final Point3D p6;
    private final Point3D p7;

    /**
     * ........● ―  ― ●
     * ........ | Back  |
     * ● ― ― ● ―  ― ● ― ― ● ―  ― ●
     * |  Left  | Top   | Right | Bottom |
     * ● ― ― ● ―  ― ● ― ― ● ―  ― ●
     * ........ | Front |
     * ........● ―  ― ●
     * <p>
     * ........ 4 ―  ― 6
     * ........ |    ╱  |
     * ........ | ╱     |
     * 4 ―  ― 0 ―  ―  2 ―  ― 6 ―  ― 4
     * |    ╱  |    ╱  |    ╱  |    ╱  |
     * | ╱     | ╱     | ╱     | ╱     |
     * 5 ―  ― 1 ―  ―  3 ―  ― 7 ―  ― 5
     * ........ |    ╱  |
     * ........ | ╱     |
     * ........ 5 ―  ― 7
     */
    public Cube3d(Point3D p0, Point3D p1, Point3D p2, Point3D p3, Point3D p4, Point3D p5, Point3D p6, Point3D p7) {
        this.p0 = p0;
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.p4 = p4;
        this.p5 = p5;
        this.p6 = p6;
        this.p7 = p7;

        MeshView meshView = new MeshView(createTriangleMesh());
        meshView.setMaterial(material);

        this.getChildren()
                .add(meshView);
    }

    private TriangleMesh createTriangleMesh() {
        TriangleMesh mesh = new TriangleMesh();

        float[] currentPoints = {
                (float) p0.getX(), (float) p0.getY(), (float) p0.getZ(), //0
                (float) p1.getX(), (float) p1.getY(), (float) p1.getZ(), //1
                (float) p2.getX(), (float) p2.getY(), (float) p2.getZ(), //2
                (float) p3.getX(), (float) p3.getY(), (float) p3.getZ(), //3
                (float) p4.getX(), (float) p4.getY(), (float) p4.getZ(), //4
                (float) p5.getX(), (float) p5.getY(), (float) p5.getZ(), //5
                (float) p6.getX(), (float) p6.getY(), (float) p6.getZ(), //6
                (float) p7.getX(), (float) p7.getY(), (float) p7.getZ(), //7
        };

        int[] currentFaces = {
                //Top:
                0, 0, 1, 0, 2, 0,
                1, 0, 3, 0, 2, 0,
                //Bottom:
                6, 0, 7, 0, 4, 0,
                7, 0, 5, 0, 4, 0,
                //Right:
                2, 0, 3, 0, 6, 0,
                3, 0, 7, 0, 6, 0,
                //Left:
                4, 0, 5, 0, 0, 0,
                5, 0, 1, 0, 0, 0,
                //Back:
                4, 0, 0, 0, 6, 0,
                0, 0, 2, 0, 6, 0,
                //Front:
                1, 0, 5, 0, 3, 0,
                5, 0, 7, 0, 3, 0,
        };

        int[] faceSmoothingGroups = {1, 1, 2, 2, 4, 4, 8, 8, 16, 16, 32, 32};

        float[] texCoords = {0, 0};

        mesh.getPoints()
                .setAll(currentPoints);
        mesh.getFaces()
                .setAll(currentFaces);
        mesh.getFaceSmoothingGroups()
                .setAll(faceSmoothingGroups);
        mesh.getTexCoords()
                .setAll(texCoords);
        return mesh;
    }

    public void setColor(Color color) {
        material.setDiffuseColor(color);
        material.setSpecularColor(color.brighter());
    }

    public PhongMaterial getMaterial() {
        return material;
    }

    public Point3D getP0() {
        return p0;
    }

    public Point3D getP1() {
        return p1;
    }

    public Point3D getP2() {
        return p2;
    }

    public Point3D getP3() {
        return p3;
    }

    public Point3D getP4() {
        return p4;
    }

    public Point3D getP5() {
        return p5;
    }

    public Point3D getP6() {
        return p6;
    }

    public Point3D getP7() {
        return p7;
    }
}
