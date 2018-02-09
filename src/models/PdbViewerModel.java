package models;

import exceptions.*;
import helpers.*;
import javafx.beans.property.*;
import javafx.concurrent.*;
import javafx.scene.control.*;
import views.*;

import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class PdbViewerModel {

    private final ObjectProperty<ProteinStructureModel> proteinStructureProperty = new SimpleObjectProperty<>();

    public PdbViewerModel() {
        //empty constructor
    }

    private void loadPdbDataToModel() {
        ProteinStructureModel proteinStructureModel = createProteinStructureModel();
        createSecondaryStructureMap(proteinStructureModel);
        proteinStructureProperty.set(proteinStructureModel);
    }

    private void createSecondaryStructureMap(ProteinStructureModel proteinStructureModel) {
        Map<String, Set<IndexRange>> helixSet = PdbObject.getHelixSet();
        Map<String, Set<IndexRange>> sheetSet = PdbObject.getSheetSet();

        proteinStructureModel.getAminoAcids()
                .values()
                .forEach(aminoAcidModel -> {
                    if (contain(helixSet.get(aminoAcidModel.getSequenceId()), aminoAcidModel.getPosition())) {
                        aminoAcidModel.setSecondaryStructureElement(SecondaryStructure.HELIX);
                    } else if (contain(sheetSet.get(aminoAcidModel.getSequenceId()), aminoAcidModel.getPosition())) {
                        aminoAcidModel.setSecondaryStructureElement(SecondaryStructure.SHEET);
                    } else {
                        aminoAcidModel.setSecondaryStructureElement(SecondaryStructure.NONE);
                    }
                });
    }

    private boolean contain(Set<IndexRange> ranges, int index) {
        return ranges != null && !ranges.isEmpty() && ranges.stream()
                .anyMatch(range -> range.getStart() <= index && index <= range.getEnd());
    }

    private ProteinStructureModel createProteinStructureModel() {
        BondModel.resetUniqueId();
        AminoAcidModel.resetUniqueId();

        Map<String, SequenceModel> sequences = PdbObject.getGroupedAtomData()
                .entrySet()
                .stream()
                .map(this::createSequenceModel)
                .collect(Collectors.toMap(SequenceModel::getId, Function.identity(), (s1, s2) -> s1));

        return new ProteinStructureModel(PdbObject.getDescriptor(), sequences);
    }

    private SequenceModel createSequenceModel(Map.Entry<String, Map<Integer, Map<String, List<String[]>>>> sequence) {
        Map<Integer, AminoAcidModel> aminoAcidModels = sequence.getValue()
                .entrySet()
                .stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(Map.Entry::getValue)
                .map(Map::values)
                .flatMap(Collection::stream)
                .filter(tokens -> tokens.size() > 0)
                .map(this::createAminoAcidModel)
                .collect(Collectors.toMap(AminoAcidModel::getId, Function.identity(), (a1, a2) -> a1));

        return new SequenceModel(sequence.getKey(), aminoAcidModels);
    }

    private AminoAcidModel createAminoAcidModel(List<String[]> aminoAcid) {
        try {
            Map<Integer, AtomModel> atomModels = aminoAcid.stream()
                    .map(atom -> {
                        int id = Integer.parseInt(atom[1].trim());
                        String position = atom[2].trim();
                        AminoAcid acid = AminoAcid.valueOf(atom[4].trim());
                        String sequenceId = atom[5].trim();
                        int aminoAcidId = Integer.parseInt(atom[6].trim());
                        double x = Double.parseDouble(atom[8].trim());
                        double y = Double.parseDouble(atom[9].trim());
                        double z = Double.parseDouble(atom[10].trim());
                        double occupancy = Double.parseDouble(atom[11].trim());
                        double temperatureFactor = Double.parseDouble(atom[12].trim());
                        Atom element = Atom.valueOf(atom[13].trim());
                        return new AtomModel(id, position, aminoAcidId, sequenceId, acid, x, y, z, occupancy, temperatureFactor, element);
                    })
                    .collect(Collectors.toMap(AtomModel::getId, Function.identity(), (a1, a2) -> a1));

            AminoAcid acid = atomModels.values()
                    .stream()
                    .map(AtomModel::getAcid)
                    .findAny()
                    .orElseThrow(PdbDataReadException::new);

            Integer position = atomModels.values()
                    .stream()
                    .map(AtomModel::getAminoAcidPosition)
                    .findAny()
                    .orElseThrow(PdbDataReadException::new);

            String sequenceId = atomModels.values()
                    .stream()
                    .map(AtomModel::getSequenceId)
                    .findAny()
                    .orElseThrow(PdbDataReadException::new);

            return new AminoAcidModel(position, sequenceId, acid, atomModels);
        } catch (PdbDataReadException e) {
            PdbView.showException(e);
        }

        return null;
    }

    public Task<Void> loadPdbDataFromReader(Reader reader, String pdbId) {
        Task<Void> task = PdbObject.loadPdb(reader, pdbId);
        task.setOnSucceeded(listener -> loadPdbDataToModel());

        return task;
    }

    public ObjectProperty<ProteinStructureModel> proteinStructureProperty() {
        return proteinStructureProperty;
    }

    public ProteinStructureModel getProteinStructure() {
        return proteinStructureProperty.get();
    }

}
