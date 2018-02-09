package models;

import java.util.concurrent.atomic.*;

public class BondModel {

    private static final AtomicInteger uniqueId = new AtomicInteger(0);
    private final AtomModel atom1;
    private final AtomModel atom2;
    private final boolean isBackbone;
    private final int id;

    BondModel(AtomModel atom1, AtomModel atom2) {
        this.id = uniqueId.getAndIncrement();
        this.atom1 = atom1;
        this.atom2 = atom2;
        this.isBackbone = atom1.isBackbone() && atom2.isBackbone();
    }

    public static void resetUniqueId() {
        uniqueId.set(0);
    }

    public AtomModel getAtom1() {
        return atom1;
    }

    public AtomModel getAtom2() {
        return atom2;
    }

    public int getId() {
        return id;
    }

    public boolean isBackbone() {
        return isBackbone;
    }
}
