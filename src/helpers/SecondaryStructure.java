package helpers;

import javafx.scene.paint.*;

/**
 * Structure Colors based on: http://acces.ens-lyon.fr/biotic/rastop/help/colour.htm#structurecolours
 */
public enum SecondaryStructure {
    HELIX('H', Color.rgb(240, 0, 128)),
    SHEET('E', Color.rgb(255, 255, 0)),
    NONE(' ', Color.rgb(255, 255, 255));

    private final Color color;
    private final char code;

    SecondaryStructure(char code, Color color) {
        this.code = code;
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public char getCode() {
        return code;
    }
}
