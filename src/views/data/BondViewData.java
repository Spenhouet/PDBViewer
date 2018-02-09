package views.data;

public class BondViewData {

    private final int id;
    private final int sourceAtomId;
    private final int targetAtomId;
    private final boolean backbone;

    public BondViewData(int id, int sourceAtomId, int targetAtomId, boolean backbone) {
        this.id = id;
        this.sourceAtomId = sourceAtomId;
        this.targetAtomId = targetAtomId;
        this.backbone = backbone;
    }

    public int getId() {
        return id;
    }

    public int getSourceAtomId() {
        return sourceAtomId;
    }

    public int getTargetAtomId() {
        return targetAtomId;
    }

    public boolean isBackbone() {
        return backbone;
    }
}
