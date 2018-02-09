import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.stage.*;
import presenters.*;
import views.*;

public class PdbViewerProgram extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("pdb_viewer.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        PdbView view = loader.getController();
        new PdbViewerPresenter(primaryStage, view);

        primaryStage.setTitle("Protein Data Bank (PDB) Viewer");
        primaryStage.setWidth(Screen.getPrimary()
                .getVisualBounds()
                .getHeight());
        primaryStage.setHeight(Screen.getPrimary()
                .getVisualBounds()
                .getHeight());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
