package helpers;

import javafx.scene.paint.*;

/**
 * Min / Max based on information from: https://pdb101.rcsb.org/learn/guide-to-understanding-pdb-data/dealing-with-coordinates
 */
public class TemperatureFactor {

    private static final double MIN = 10;
    private static final double MAX = 50;

    private TemperatureFactor() {
        //hide constructor
    }

    public static Color color(double temperature) {
        temperature = temperature < MIN ? MIN : temperature;
        temperature = temperature > MAX ? MAX : temperature;
        double hue = Color.BLUE.getHue() + (Color.RED.getHue() - Color.BLUE.getHue()) * (temperature - MIN) / (MAX - MIN);
        return Color.hsb(hue, 1.0, 1.0);
    }
}
