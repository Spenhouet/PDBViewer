package helpers;

import javafx.scene.paint.*;

/**
 * CPK Colors based on: http://acces.ens-lyon.fr/biotic/rastop/help/elementtable.htm
 * Radius based on: https://en.wikipedia.org/wiki/Atomic_radius
 */
public enum Atom {
    C("Carbon", 70, Color.rgb(200, 200, 200)),
    O("Oxygen", 60, Color.rgb(240, 0, 0)),
    H("Hydrogen", 25, Color.rgb(255, 255, 255)),
    N("Nitrogen", 65, Color.rgb(143, 143, 255)),
    S("Sulfur", 100, Color.rgb(255, 200, 50)),
    CL("Chlorine", 100, Color.rgb(0, 255, 0)),
    B("Boron", 85, Color.rgb(0, 255, 0)),
    P("Phosphorus", 100, Color.rgb(255, 165, 0)),
    FE("Iron", 140, Color.rgb(255, 165, 0)),
    BA("Barium", 215, Color.rgb(255, 165, 0)),
    NA("Sodium", 180, Color.rgb(0, 0, 255)),
    MG("Magnesium", 150, Color.rgb(34, 139, 34)),
    ZN("Zinc", 134, Color.rgb(165, 42, 42)),
    CU("Copper", 128, Color.rgb(165, 42, 42)),
    NI("Nickel", 124, Color.rgb(165, 42, 42)),
    BR("Bromine", 115, Color.rgb(165, 42, 42)),
    CA("Calcium", 180, Color.rgb(128, 128, 144)),
    MN("Manganese", 127, Color.rgb(128, 128, 144)),
    AL("Aluminium", 143, Color.rgb(128, 128, 144)),
    TI("Titanium", 140, Color.rgb(128, 128, 144)),
    CR("Chromium", 128, Color.rgb(128, 128, 144)),
    AG("Silver", 144, Color.rgb(128, 128, 144)),
    F("Fluorine", 50, Color.rgb(218, 165, 32)),
    SI("Silicon", 111, Color.rgb(218, 165, 32)),
    AU("Gold", 144, Color.rgb(218, 165, 32)),
    I("Iodine", 140, Color.rgb(160, 32, 240)),
    LI("Lithium", 145, Color.rgb(178, 34, 34)),
    HE("Helium", 31, Color.rgb(255, 192, 203)),
    RA("Radium", 215, Color.rgb(255, 20, 147)),
    SR("Strontium", 200, Color.rgb(255, 20, 147)),
    BE("Beryllium", 105, Color.rgb(255, 20, 147)),
    FR("Francium", 260, Color.rgb(255, 20, 147)),
    CS("Caesium", 260, Color.rgb(255, 20, 147)),
    K("Potassium", 220, Color.rgb(255, 20, 147)),
    RB("Rubidium", 235, Color.rgb(255, 20, 147)),
    XE("Xenon", 108, Color.rgb(255, 20, 147)),
    KR("Krypton", 88, Color.rgb(255, 20, 147)),
    AR("Argon", 71, Color.rgb(255, 20, 147)),
    NE("Neon", 38, Color.rgb(255, 20, 147));

    private final String name;
    private final Color color;
    private final int radius;

    Atom(String name, int radius, Color color) {
        this.name = name;
        this.color = color;
        this.radius = radius;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public int getRadius() {
        return radius;
    }
}
