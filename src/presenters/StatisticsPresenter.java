package presenters;

import javafx.scene.chart.*;
import models.*;
import views.*;

import java.util.*;

import static java.util.stream.Collectors.*;

class StatisticsPresenter {

    private final PdbView view;
    private final PdbViewerModel model;

    StatisticsPresenter(PdbView view, PdbViewerModel model) {
        this.view = view;
        this.model = model;

        addListener();
    }

    private void addListener() {
        model.proteinStructureProperty()
                .addListener(listener -> initAminoAcidChart());
        model.proteinStructureProperty()
                .addListener(listener -> initAtomsChart());
        model.proteinStructureProperty()
                .addListener(listener -> initSecondaryStructureChart());
        model.proteinStructureProperty()
                .addListener(listener -> initSequencesChart());
    }

    private void initAminoAcidChart() {
        Set<PieChart.Data> aminoAcids = model.getProteinStructure()
                .getAtoms()
                .values()
                .stream()
                .collect(groupingBy(AtomModel::getAcid, counting()))
                .entrySet()
                .stream()
                .map(entry -> new PieChart.Data(entry.getKey()
                        .full(), entry.getValue()))
                .collect(toSet());

        view.getAminoAcidsChart()
                .getData()
                .clear();

        view.getAminoAcidsChart()
                .getData()
                .addAll(aminoAcids);
    }

    private void initAtomsChart() {
        Set<PieChart.Data> atoms = model.getProteinStructure()
                .getAtoms()
                .values()
                .stream()
                .collect(groupingBy(AtomModel::getElement, counting()))
                .entrySet()
                .stream()
                .map(entry -> new PieChart.Data(entry.getKey()
                        .getName(), entry.getValue()))
                .collect(toSet());

        view.getAtomsChart()
                .getData()
                .clear();

        view.getAtomsChart()
                .getData()
                .addAll(atoms);
    }

    private void initSecondaryStructureChart() {
        Set<PieChart.Data> secondaryStructure = model.getProteinStructure()
                .getAminoAcids()
                .values()
                .stream()
                .collect(groupingBy(AminoAcidModel::getSecondaryStructureElement, counting()))
                .entrySet()
                .stream()
                .map(entry -> new PieChart.Data(entry.getKey()
                        .name(), entry.getValue()))
                .collect(toSet());

        view.getSecondaryStructureChart()
                .getData()
                .clear();

        view.getSecondaryStructureChart()
                .getData()
                .addAll(secondaryStructure);
    }

    private void initSequencesChart() {
        Set<PieChart.Data> sequences = model.getProteinStructure()
                .getAminoAcids()
                .values()
                .stream()
                .collect(groupingBy(AminoAcidModel::getSequenceId, counting()))
                .entrySet()
                .stream()
                .map(entry -> new PieChart.Data(entry.getKey(), entry.getValue()))
                .collect(toSet());

        view.getSequencesChart()
                .getData()
                .clear();

        view.getSequencesChart()
                .getData()
                .addAll(sequences);
    }

}
