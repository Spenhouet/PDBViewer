package views.graph.styles;

import javafx.collections.*;
import javafx.scene.*;
import selections.*;
import views.graph.elements.*;
import views.toggle.*;

public abstract class Style extends Group {

    static void updateGroup(MapChangeListener.Change<? extends Integer, ? extends Group> change, Group group) {
        if (change.wasAdded()) {
            group.getChildren()
                    .add(change.getValueAdded());
        } else if (change.wasRemoved()) {
            group.getChildren()
                    .remove(change.getValueRemoved());
        }
    }

    public abstract void colorModeChange(ColorModeType colorMode);

    public abstract SelectionModel<AtomView> getSelectionModel();

}
