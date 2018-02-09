package views.graph.elements;

import javafx.beans.property.*;
import javafx.scene.*;
import javafx.scene.paint.*;
import views.data.*;
import views.graph.shapes.*;
import views.toggle.*;

public class BondView extends Group {

    private final AtomViewData atom1;
    private final AtomViewData atom2;
    private final DoubleProperty radius = new SimpleDoubleProperty(5);
    private final DoubleLine3d line3d;

    public BondView(AtomViewData atom1, AtomViewData atom2) {
        this.atom1 = atom1;
        this.atom2 = atom2;

        line3d = new DoubleLine3d(atom1.getPosition(), atom2.getPosition(), radius);

        this.getChildren()
                .add(line3d);

        this.setCursor(Cursor.HAND);
    }

    public void setRadius(double radius) {
        this.radius.set(radius);
    }

    public AtomViewData getAtom2() {
        return atom2;
    }

    public AtomViewData getAtom1() {
        return atom1;
    }

    public void colorize(ColorModeType colorMode) {
        setColor(atom1.getColor(colorMode), atom2.getColor(colorMode));
    }

    private void setColor(Color colorLeft, Color colorRight) {
        line3d.diffuseColorLeftProperty()
                .set(colorLeft);
        line3d.specularColorLeftProperty()
                .set(colorLeft.brighter());

        line3d.diffuseColorRightProperty()
                .set(colorRight);
        line3d.specularColorRightProperty()
                .set(colorRight.brighter());
    }

}
