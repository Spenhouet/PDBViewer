package models;

import helpers.*;
import javafx.geometry.*;

public class AtomModel {

    private final int id;
    private final String name;
    private final int aminoAcidPosition;
    private final String sequenceId;
    private final AminoAcid acid;
    private final Point3D coordinates;
    private final double occupancy;
    private final double temperatureFactor;
    private final Atom element;
    private final boolean isBackbone;
    private int aminoAcidId;

    AtomModel(int id, String name, int aminoAcidPosition, String sequenceId, AminoAcid acid, double x, double y, double z, double occupancy, double temperatureFactor, Atom element) {
        this.id = id;
        this.name = name;
        this.aminoAcidPosition = aminoAcidPosition;
        this.sequenceId = sequenceId;
        this.isBackbone = isBackbone(name);
        this.acid = acid;
        this.coordinates = new Point3D(x, y, z);
        this.occupancy = occupancy;
        this.temperatureFactor = temperatureFactor;
        this.element = element;
    }

    private static boolean isBackbone(String position) {
        return "N".equals(position) || "CA".equals(position) || "C".equals(position);
    }

    public String getName() {
        return name;
    }

    public Point3D getCoordinates() {
        return coordinates;
    }

    public double getOccupancy() {
        return occupancy;
    }

    public Atom getElement() {
        return element;
    }

    public int getId() {
        return id;
    }

    public int getAminoAcidPosition() {
        return aminoAcidPosition;
    }

    public String getSequenceId() {
        return sequenceId;
    }

    public AminoAcid getAcid() {
        return acid;
    }

    public boolean isBackbone() {
        return isBackbone;
    }

    public int getAminoAcidId() {
        return aminoAcidId;
    }

    public void setAminoAcidId(int aminoAcidId) {
        this.aminoAcidId = aminoAcidId;
    }

    public double getTemperatureFactor() {
        return temperatureFactor;
    }
}
