package exceptions;

public class PdbIdRetrieveException extends Exception {

    public PdbIdRetrieveException() {
        super("There was an error reading the PDB IDs from rcsb.org.");
    }
}
