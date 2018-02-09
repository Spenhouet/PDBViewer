package helpers;

public enum PdbFileElements {
    HEADER,
    TITLE,
    COMPND,
    SOURCE,
    KEYWDS,
    EXPDTA,
    AUTHOR,
    REVDAT,
    SPRSDE,
    JRNL,
    REMARK,
    DBREF,
    SEQADV,
    SEQRES,
    HET,
    HETNAM,
    FORMUL,
    HELIX(12, 14, 19, 26, 31, 34),
    SHEET(14, 16, 20, 27, 31),
    SSBOND,
    LINK,
    SITE,
    CISPEP,
    CRYST1,
    ORIGX1,
    ORIGX2,
    ORIGX3,
    SCALE1,
    SCALE2,
    SCALE3,
    ATOM(6, 11, 12, 15, 17, 21, 22, 33, 41, 49, 55, 61),
    TER,
    HETATM,
    CONECT,
    MASTER,
    END;

    private final int[] splitPositions;

    PdbFileElements(int... splitPositions) {
        this.splitPositions = splitPositions;
    }

    public static int rank(String element) {
        try {
            return PdbFileElements.valueOf(element)
                    .ordinal();
        } catch (IllegalArgumentException e) {
            return -1;
        }
    }

    public static String[] splitString(String line) {
        String separator = String.valueOf(((char) 7));
        String[] elementSplit = line.split("\\s", 2);

        int[] splitPositions;

        try {
            splitPositions = PdbFileElements.valueOf(elementSplit[0].trim()).splitPositions;
        } catch (IllegalArgumentException e) {
            splitPositions = new int[]{0};
        }

        StringBuilder stringBuilder = new StringBuilder(elementSplit[1]);
        for (int i = splitPositions.length - 1; i >= 0; i--)
            stringBuilder.insert(splitPositions[i], separator);

        String splitAbleLine = elementSplit[0] + separator + " " + stringBuilder.toString();

        return splitAbleLine.split(separator);
    }
}
