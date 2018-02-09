package models;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class ProteinStructureModel {

    private final String description;
    private final Map<String, SequenceModel> sequences;
    private final String sequence;
    private final Map<Integer, AminoAcidModel> aminoAcids;
    private final Map<Integer, AtomModel> atoms;
    private final Map<Integer, BondModel> bonds;

    ProteinStructureModel(String description, Map<String, SequenceModel> sequences) {
        this.description = description;
        this.sequences = sequences;
        this.aminoAcids = collectAminoAcids();
        this.atoms = collectAtoms();
        this.bonds = collectBonds();
        this.sequence = collectSequence();
    }

    private String collectSequence() {
        return sequences
                .values()
                .stream()
                .map(SequenceModel::getSequence)
                .collect(Collectors.joining());
    }

    private Map<Integer, AminoAcidModel> collectAminoAcids() {
        return sequences.values()
                .stream()
                .map(SequenceModel::getAminoAcids)
                .map(Map::values)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(AminoAcidModel::getId, Function.identity(), (a1, a2) -> a1));
    }

    private Map<Integer, AtomModel> collectAtoms() {
        return sequences.values()
                .stream()
                .map(SequenceModel::getAminoAcids)
                .map(Map::values)
                .flatMap(Collection::stream)
                .map(AminoAcidModel::getAtomNameMap)
                .map(Map::values)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(AtomModel::getId, Function.identity(), (a1, a2) -> a1));
    }

    private Map<Integer, BondModel> collectBonds() {
        return sequences.values()
                .stream()
                .map(SequenceModel::getBonds)
                .map(Map::values)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(BondModel::getId, Function.identity(), (b1, b2) -> b1));
    }

    public String getDescription() {
        return description;
    }

    public String getSequence() {
        return sequence;
    }

    public Map<String, SequenceModel> getSequences() {
        return sequences;
    }

    public Map<Integer, AminoAcidModel> getAminoAcids() {
        return aminoAcids;
    }

    public Map<Integer, AtomModel> getAtoms() {
        return atoms;
    }

    public Map<Integer, BondModel> getBonds() {
        return bonds;
    }
}
