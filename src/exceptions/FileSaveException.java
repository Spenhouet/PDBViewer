package exceptions;

public class FileSaveException extends Exception {

    public FileSaveException() {
        super("An error appeared while saving the PDB data. Please try again.");
    }

}
