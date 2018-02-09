package views.graph.elements;

import javafx.beans.property.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import views.data.*;
import views.toggle.*;

public class AtomView extends Group {

    private final ObjectProperty<Color> diffuseColor = new SimpleObjectProperty<>();
    private final ObjectProperty<Color> specularColor = new SimpleObjectProperty<>();
    private final AtomViewData data;

    public AtomView(AtomViewData atomViewData, double radius) {
        Sphere atom = new Sphere();

        this.getChildren()
                .add(atom);

        this.data = atomViewData;
        Point3D position = atomViewData.getPosition();
        this.setTranslateX(position.getX());
        this.setTranslateY(position.getY());
        this.setTranslateZ(position.getZ());

        PhongMaterial material = new PhongMaterial();
        material.diffuseColorProperty()
                .bind(this.diffuseColor);
        material.specularColorProperty()
                .bind(this.specularColor);
        atom.setMaterial(material);
        atom.setRadius(radius);

        Tooltip.install(atom, new Tooltip(atomViewData.getTooltip()));

        this.setCursor(Cursor.HAND);
    }

    public int getAtomId() {
        return data.getId();
    }

    private void setColor(Color color) {
        this.diffuseColor.set(color);
        this.specularColor.set(color.brighter());
    }

    public void colorize(ColorModeType colorMode) {
        setColor(data.getColor(colorMode));
    }

    public AtomViewData getData() {
        return data;
    }
}
