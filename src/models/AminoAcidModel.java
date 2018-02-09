package models;

import helpers.*;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

public class AminoAcidModel {

    private static final AtomicInteger uniqueId = new AtomicInteger(0);
    private final int id;
    private final Integer position;
    private final AminoAcid component;
    private final Map<Integer, AtomModel> atomIdMap;
    private final Map<String, AtomModel> atomNameMap;
    private final List<BondModel> bonds;
    private final String sequenceId;
    private SecondaryStructure secondaryStructureElement;

    AminoAcidModel(Integer position, String sequenceId, AminoAcid component, Map<Integer, AtomModel> atomIdMap) {
        this.id = uniqueId.getAndIncrement();
        this.position = position;
        this.sequenceId = sequenceId;
        this.component = component;
        this.atomIdMap = atomIdMap;
        this.atomNameMap = createAtomNameMap(atomIdMap.values());
        this.bonds = createStandardAminoAcidBonds();
        this.bonds.addAll(createExtraAminoAcidBonds());

        atomIdMap.values()
                .forEach(atom -> atom.setAminoAcidId(this.id));
    }

    public static void resetUniqueId() {
        uniqueId.set(0);
    }

    private List<BondModel> createStandardAminoAcidBonds() {
        List<BondModel> bondModels = new ArrayList<>();

        if (hasAtoms("N", "CA"))
            bondModels.add(new BondModel(atomNameMap.get("N"), atomNameMap.get("CA")));
        if (hasAtoms("CA", "C"))
            bondModels.add(new BondModel(atomNameMap.get("CA"), atomNameMap.get("C")));
        if (hasAtoms("C", "O"))
            bondModels.add(new BondModel(atomNameMap.get("C"), atomNameMap.get("O")));

        return bondModels;
    }

    private List<BondModel> createExtraAminoAcidBonds() {
        return component.getBonds()
                .stream()
                .filter(bond -> hasAtoms(bond.getKey(), bond.getValue()))
                .map(bond -> new BondModel(atomNameMap.get(bond.getKey()), atomNameMap.get(bond.getValue())))
                .collect(Collectors.toList());
    }

    private boolean hasAtoms(String position1, String position2) {
        return atomNameMap.containsKey(position1) && atomNameMap.containsKey(position2);
    }

    private Map<String, AtomModel> createAtomNameMap(Collection<AtomModel> atoms) {
        return atoms.stream()
                .collect(Collectors.toMap(AtomModel::getName, Function.identity(), (a1, a2) -> a1.getOccupancy() > a2.getOccupancy() ? a1 : a2));
    }

    public AminoAcid getComponent() {
        return component;
    }

    public Map<Integer, AtomModel> getAtomIdMap() {
        return atomIdMap;
    }

    public Map<String, AtomModel> getAtomNameMap() {
        return atomNameMap;
    }

    public List<BondModel> getBonds() {
        return bonds;
    }

    public SecondaryStructure getSecondaryStructureElement() {
        return secondaryStructureElement;
    }

    public void setSecondaryStructureElement(SecondaryStructure secondaryStructureElement) {
        this.secondaryStructureElement = secondaryStructureElement;
    }

    public Integer getPosition() {
        return position;
    }

    public String getSequenceId() {
        return sequenceId;
    }

    public int getId() {
        return id;
    }
}
