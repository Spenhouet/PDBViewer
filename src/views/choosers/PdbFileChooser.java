package views.choosers;

import javafx.stage.*;

import java.io.*;

public class PdbFileChooser {

    private PdbFileChooser() {
    }

    public static File openFile(Window window) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Protein Data Bank (PDB)", "*.pdb");
        fileChooser.getExtensionFilters()
                .add(extensionFilter);
        fileChooser.setTitle("Open PDB Data");
        return fileChooser.showOpenDialog(window);
    }

}
