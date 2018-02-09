package helpers;

import javafx.collections.*;
import javafx.scene.control.*;
import javafx.util.*;

import java.util.stream.*;

public class RadioMenuItemSync {

    private RadioMenuItemSync() {
        //hide constructor
    }

    public static void bind(ToggleGroup toggleGroup, ChoiceBox<RadioMenuItem> choiceBox) {
        choiceBox.setConverter(new StringConverter<RadioMenuItem>() {
            @Override
            public String toString(RadioMenuItem object) {
                return object.getText();
            }

            @Override
            public RadioMenuItem fromString(String string) {
                return toggleGroup.getToggles()
                        .stream()
                        .map(RadioMenuItem.class::cast)
                        .filter(toggle -> toggle.getText()
                                .equals(string))
                        .findAny()
                        .orElse((RadioMenuItem) toggleGroup.getSelectedToggle());
            }
        });

        choiceBox.getItems()
                .addAll(toggleGroup.getToggles()
                        .stream()
                        .map(RadioMenuItem.class::cast)
                        .collect(Collectors.toCollection(FXCollections::observableArrayList)));

        choiceBox.getSelectionModel()
                .select((RadioMenuItem) toggleGroup.getSelectedToggle());

        choiceBox.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null && oldValue != newValue) {
                        toggleGroup.selectToggle(newValue);
                    }
                });

        toggleGroup.selectedToggleProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null && oldValue != newValue)
                        choiceBox.getSelectionModel()
                                .select((RadioMenuItem) newValue);
                });
    }
}
