package views.data;

import helpers.*;
import javafx.geometry.*;
import javafx.scene.paint.*;
import views.toggle.*;

import java.util.*;

public class AtomViewData implements Comparable<AtomViewData> {

    private final int id;
    private final String name;
    private final String sequence;
    private final int aminoAcid;
    private final String tooltip;
    private final Point3D position;
    private final boolean backbone;
    private final SecondaryStructure secondaryStructure;
    private final double radius;
    private final Map<ColorModeType, Color> color;

    public AtomViewData(int id, String name, String sequence, int aminoAcid, String tooltip, Point3D position, boolean backbone, SecondaryStructure secondaryStructure, double radius, Map<ColorModeType, Color> color) {
        this.id = id;
        this.name = name;
        this.sequence = sequence;
        this.aminoAcid = aminoAcid;
        this.tooltip = tooltip;
        this.position = position;
        this.backbone = backbone;
        this.secondaryStructure = secondaryStructure;
        this.radius = radius;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public String getTooltip() {
        return tooltip;
    }

    public Point3D getPosition() {
        return position;
    }

    public double getRadius() {
        return radius;
    }

    public Color getColor(ColorModeType colorMode) {
        return color.get(colorMode);
    }

    public boolean isBackbone() {
        return backbone;
    }

    public String getName() {
        return name;
    }

    public int getAminoAcid() {
        return aminoAcid;
    }

    public String getSequence() {
        return sequence;
    }

    public SecondaryStructure getSecondaryStructure() {
        return secondaryStructure;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof AtomViewData && hashCode() == obj.hashCode();
    }

    @Override
    public int compareTo(AtomViewData atom) {
        return id - atom.id;
    }
}
