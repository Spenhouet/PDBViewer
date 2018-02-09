package views.dialogs;

import javafx.concurrent.*;
import javafx.scene.*;
import javafx.scene.control.*;

public class Progress {

    private Progress() {
        //hide constructor
    }

    public static void show(Task task, ProgressIndicator progressIndicator, Node... nodes) {
        for (Node node : nodes)
            node.setDisable(true);

        progressIndicator.setVisible(true);

        progressIndicator.setProgress(-1F);
        progressIndicator.progressProperty()
                .bind(task.progressProperty());

        task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, event -> {
            for (Node node : nodes)
                node.setDisable(false);

            progressIndicator.setVisible(false);
            progressIndicator.progressProperty()
                    .unbind();
        });
    }

    public static void showSpinner(Task task, ProgressIndicator progressIndicator, Node... nodes) {
        for (Node node : nodes)
            node.setDisable(true);

        progressIndicator.setVisible(true);

        progressIndicator.setProgress(-1F);

        task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, event -> {
            for (Node node : nodes)
                node.setDisable(false);

            progressIndicator.setVisible(false);
        });
    }

}
