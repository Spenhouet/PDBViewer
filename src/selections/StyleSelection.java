package selections;

import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.scene.control.*;
import sun.reflect.generics.reflectiveObjects.*;
import views.toggle.*;

public class StyleSelection {

    private final ToggleGroup toggleGroup;
    private final RadioMenuItem ballAndStick;
    private final RadioMenuItem spaceFilling;
    private final RadioMenuItem backbone;
    private final RadioMenuItem ribbon;
    private final RadioMenuItem cartoon;

    private final ObjectProperty<StyleType> styleTypeSelection = new SimpleObjectProperty<>();
    private final ChangeListener<Toggle> toggleChangeListener = (observable, oldValue, selectedToggle) -> update(selectedToggle);

    public StyleSelection(ToggleGroup toggleGroup, RadioMenuItem ballAndStick, RadioMenuItem spaceFilling, RadioMenuItem backbone, RadioMenuItem ribbon, RadioMenuItem cartoon) {
        this.toggleGroup = toggleGroup;
        this.ballAndStick = ballAndStick;
        this.spaceFilling = spaceFilling;
        this.backbone = backbone;
        this.ribbon = ribbon;
        this.cartoon = cartoon;

        addListener();
        styleTypeSelection.set(getStyleTypeForSelection(toggleGroup.getSelectedToggle()));
    }

    private void addListener() {
        toggleGroup.selectedToggleProperty()
                .addListener(toggleChangeListener);
    }

    private void update(Toggle selectedToggle) {
        if (selectedToggle != null)
            styleTypeSelection.set(getStyleTypeForSelection(selectedToggle));
    }

    private StyleType getStyleTypeForSelection(Toggle selectedToggle) {
        if (ballAndStick.equals(selectedToggle))
            return StyleType.BALL_AND_STICK;

        if (spaceFilling.equals(selectedToggle))
            return StyleType.SPACE_FILLING;

        if (backbone.equals(selectedToggle))
            return StyleType.BACKBONE;

        if (ribbon.equals(selectedToggle))
            return StyleType.RIBBON;

        if (cartoon.equals(selectedToggle))
            return StyleType.CARTOON;

        throw new NotImplementedException();
    }

    public StyleType getStyleTypeSelection() {
        return styleTypeSelection.get();
    }

    public ObjectProperty<StyleType> styleTypeSelectionProperty() {
        return styleTypeSelection;
    }
}
