package helpers;

import javafx.scene.paint.*;
import javafx.util.*;

import java.util.*;

/**
 * Naming based on: https://en.wikipedia.org/wiki/DNA_codon_table
 * Shapely Colors based on: http://acces.ens-lyon.fr/biotic/rastop/help/colour.htm#shapelycolours
 */
public enum AminoAcid {
    ALA('A', "Alanine", Color.rgb(140, 255, 140), bond("CA", "CB"), bond("C", "OXT")),
    CYS('C', "Cysteine", Color.rgb(255, 255, 112), bond("CA", "CB"), bond("CB", "SG")),
    ASP('D', "Aspartic acid", Color.rgb(160, 0, 66), bond("CA", "CB"), bond("CB", "CG"), bond("CG", "OD1"), bond("CG", "OD2")),
    GLU('E', "Glutamic acid", Color.rgb(102, 0, 0), bond("CA", "CB"), bond("CB", "CG"), bond("CG", "CD"), bond("CD", "OE1"), bond("CD", "OE2")),
    PHE('F', "Phenylalanine", Color.rgb(83, 76, 66), bond("CA", "CB"), bond("CB", "CG"), bond("CG", "CD1"), bond("CG", "CD2"), bond("CD1", "CE1"), bond("CD2", "CE2"), bond("CE1", "CZ"), bond("CE2", "CZ")),
    GLY('G', "Glycine", Color.rgb(255, 255, 255), bond("C", "OXT")),
    HIS('H', "Histidine", Color.rgb(112, 112, 255), bond("CA", "CB"), bond("CB", "CG"), bond("CG", "ND1"), bond("CG", "CD2"), bond("CD2", "NE2"), bond("NE2", "CE1"), bond("CE1", "ND1")),
    ILE('I', "Isoleucine", Color.rgb(0, 76, 0), bond("CA", "CB"), bond("CB", "CG1"), bond("CB", "CG2"), bond("CG1", "CD1")),
    LYS('K', "Lysine", Color.rgb(71, 71, 184), bond("CA", "CB"), bond("CB", "CG"), bond("CG", "CD"), bond("CD", "CE"), bond("CE", "NZ")),
    LEU('L', "Leucine", Color.rgb(69, 94, 69), bond("CA", "CB"), bond("CB", "CG"), bond("CG", "CD1"), bond("CG", "CD2")),
    MET('M', "Methionine", Color.rgb(184, 160, 66), bond("CA", "CB"), bond("CB", "CG"), bond("CG", "SD"), bond("SD", "CE")),
    ASN('N', "Asparagine", Color.rgb(255, 124, 112), bond("CA", "CB"), bond("CB", "CG"), bond("CG", "OD1"), bond("CG", "ND2")),
    PRO('P', "Proline", Color.rgb(82, 82, 82), bond("CA", "CB"), bond("CB", "CG"), bond("CG", "CD"), bond("CD", "N")),
    GLN('Q', "Glutamine", Color.rgb(255, 76, 76), bond("CA", "CB"), bond("CB", "CG"), bond("CG", "CD"), bond("CD", "OE1"), bond("CD", "NE2")),
    ARG('R', "Arginine", Color.rgb(0, 0, 124), bond("CA", "CB"), bond("CB", "CG"), bond("CG", "CD"), bond("CD", "NE"), bond("NE", "CZ"), bond("CZ", "NH1"), bond("CZ", "NH2")),
    SER('S', "Serine", Color.rgb(255, 112, 66), bond("CA", "CB"), bond("CB", "OG")),
    THR('T', "Threonine", Color.rgb(184, 76, 0), bond("CA", "CB"), bond("CB", "OG1"), bond("CB", "CG2")),
    VAL('V', "Valine", Color.rgb(255, 140, 255), bond("CA", "CB"), bond("CB", "CG1"), bond("CB", "CG2")),
    TRP('W', "Tryptophan", Color.rgb(79, 70, 0), bond("CA", "CB"), bond("CB", "CG"), bond("CG", "CD1"), bond("CG", "CD2"), bond("CD1", "NE1"), bond("NE1", "CE2"), bond("CE2", "CD2"), bond("CD2", "CE3"), bond("CE3", "CZ3"), bond("CE2", "CZ2"), bond("CZ2", "CH2"), bond("CH2", "CZ3")),
    TYR('Y', "Tyrosine", Color.rgb(140, 112, 76), bond("CA", "CB"), bond("CB", "CG"), bond("CG", "CD1"), bond("CG", "CD2"), bond("CD1", "CE1"), bond("CD2", "CE2"), bond("CE1", "CZ"), bond("CE2", "CZ"), bond("CZ", "OH")),
    UNK('U', "Unknown", Color.rgb(124, 233, 255), bond("C", "OXT"));

    private final char code;
    private final String full;
    private final Color color;
    private final List<Pair<String, String>> bonds;

    @SafeVarargs
    AminoAcid(char code, String full, Color color, Pair<String, String>... bonds) {
        this.code = code;
        this.full = full;
        this.color = color;
        this.bonds = new ArrayList<>(Arrays.asList((bonds)));
    }

    private static Pair<String, String> bond(String first, String second) {
        return new Pair<>(first, second);
    }

    public char code() {
        return this.code;
    }

    public String full() {
        return full;
    }

    public Color getColor() {
        return color;
    }

    public List<Pair<String, String>> getBonds() {
        return bonds;
    }
}
