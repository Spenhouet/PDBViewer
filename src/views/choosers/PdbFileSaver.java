package views.choosers;

import exceptions.*;
import javafx.stage.*;

import java.io.*;

public class PdbFileSaver {

    private PdbFileSaver() {
    }

    public static void saveFile(Window window, String content, String pdbId) throws FileSaveException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save PDB Data");
        fileChooser.setInitialFileName(pdbId + ".pdb");

        File file = fileChooser.showSaveDialog(window);
        if (file != null) {
            try (PrintStream out = new PrintStream(new FileOutputStream(file.getAbsolutePath()))) {
                out.print(content);
            } catch (FileNotFoundException e) {
                throw new FileSaveException();
            }
        }
    }

}
