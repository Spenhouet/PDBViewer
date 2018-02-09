package selections;

import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.scene.control.*;
import sun.reflect.generics.reflectiveObjects.*;
import views.toggle.*;

public class ColorModeSelection {

    private final ToggleGroup toggleGroup;
    private final RadioMenuItem cpk;
    private final RadioMenuItem structure;
    private final RadioMenuItem shapely;
    private final RadioMenuItem sequence;
    private final RadioMenuItem temperature;
    private final ObjectProperty<ColorModeType> colorSelection = new SimpleObjectProperty<>();
    private final ChangeListener<Toggle> toggleChangeListener = (observable, oldValue, selectedToggle) -> update(selectedToggle);

    public ColorModeSelection(ToggleGroup toggleGroup, RadioMenuItem cpk, RadioMenuItem structure, RadioMenuItem shapely, RadioMenuItem sequence, RadioMenuItem temperature) {
        this.toggleGroup = toggleGroup;
        this.cpk = cpk;
        this.structure = structure;
        this.shapely = shapely;
        this.sequence = sequence;
        this.temperature = temperature;

        addListener();
        colorSelection.set(getColorModeForSelection(toggleGroup.getSelectedToggle()));
    }

    private void addListener() {
        toggleGroup.selectedToggleProperty()
                .addListener(toggleChangeListener);
    }

    private void update(Toggle selectedToggle) {
        if (selectedToggle != null)
            colorSelection.set(getColorModeForSelection(selectedToggle));
    }

    private ColorModeType getColorModeForSelection(Toggle selectedToggle) {
        if (cpk.equals(selectedToggle))
            return ColorModeType.CPK;

        if (structure.equals(selectedToggle))
            return ColorModeType.STRUCTURE;

        if (shapely.equals(selectedToggle))
            return ColorModeType.SHAPELY;

        if (sequence.equals(selectedToggle))
            return ColorModeType.SEQUENCE;

        if (temperature.equals(selectedToggle))
            return ColorModeType.TEMPERATURE;

        throw new NotImplementedException();
    }

    public ColorModeType getColorSelection() {
        return colorSelection.get();
    }

    public ObjectProperty<ColorModeType> colorSelectionProperty() {
        return colorSelection;
    }
}
