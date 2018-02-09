package exceptions;

public class PdbDataReadException extends Exception {

    public PdbDataReadException() {
        super("There was an error reading the PDB data.");
    }

}
