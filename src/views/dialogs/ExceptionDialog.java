package views.dialogs;

import javafx.scene.control.*;

public class ExceptionDialog extends Alert {

    public ExceptionDialog(Throwable throwable) {
        super(AlertType.ERROR);
        this.setTitle("Error Dialog");
        this.setHeaderText(null);
        this.setContentText(throwable.getLocalizedMessage());
    }


}
