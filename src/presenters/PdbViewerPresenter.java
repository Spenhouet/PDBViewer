package presenters;

import exceptions.*;
import helpers.*;
import javafx.concurrent.*;
import javafx.stage.*;
import javafx.util.*;
import models.*;
import views.*;
import views.choosers.*;
import views.dialogs.*;

import java.io.*;

public class PdbViewerPresenter {

    private final Stage root;
    private final PdbView view;
    private final PdbViewerModel model;

    public PdbViewerPresenter(Stage root, PdbView view) {
        this.root = root;
        this.view = view;
        this.model = new PdbViewerModel();

        new ProteinTablePresenter(view, model);
        new ProteinViewPresenter(view, model);
        new StatisticsPresenter(view, model);

        addBindings();
        addListener();
    }

    private void addBindings() {
        view.getSave()
                .disableProperty()
                .bind(model.proteinStructureProperty()
                        .isNull());

        view.getCountsHBox()
                .visibleProperty()
                .bind(model.proteinStructureProperty()
                        .isNotNull());
    }

    private void addListener() {
        view.getOpen()
                .setOnAction(event -> loadPdbFromFile());
        view.getSave()
                .setOnAction(event -> savePdbFile());

        model.proteinStructureProperty()
                .addListener(listener -> view.getAtomsCount()
                        .textProperty()
                        .set(String.valueOf(model.getProteinStructure()
                                .getAtoms()
                                .size())));

        model.proteinStructureProperty()
                .addListener(listener -> view.getBondsCount()
                        .textProperty()
                        .set(String.valueOf(model.getProteinStructure()
                                .getBonds()
                                .size())));

        model.proteinStructureProperty()
                .addListener(listener -> view.getProteinDescription()
                        .setText(model.getProteinStructure()
                                .getDescription()));

        model.proteinStructureProperty()
                .addListener(listener -> view.getPdbId()
                        .setText(PdbObject.getPdbId()));

        model.proteinStructureProperty()
                .addListener(listener -> view.getTabPane()
                        .getSelectionModel()
                        .select(view.getViewTab()));
    }

    private void loadPdbFromFile() {
        Pair<String, FileReader> pdbFile;
        try {
            pdbFile = openPdbFile();
        } catch (PdbDataReadException e) {
            PdbView.showException(e);
            return;
        }
        if (pdbFile == null)
            return;

        Task<Void> task = model.loadPdbDataFromReader(pdbFile.getValue(), pdbFile.getKey());
        Progress.show(task, view.getProteinViewProgressIndicator(), view.getProteinViewVBox());
        Progress.show(task, view.getStatisticsProgressIndicator(), view.getStatisticsScrollPane());

        task.exceptionProperty()
                .addListener((observable, oldValue, throwable) -> PdbView.showException(throwable));

        new Thread(task).start();
    }

    private Pair<String, FileReader> openPdbFile() throws PdbDataReadException {
        File pdbFile = PdbFileChooser.openFile(root);
        if (pdbFile == null)
            return null;

        String id = pdbFile.getName()
                .substring(0, pdbFile.getName()
                        .length() - 4);
        try {
            return new Pair<>(id, new FileReader(pdbFile.getPath()));
        } catch (IOException e) {
            throw new PdbDataReadException();
        }
    }

    private void savePdbFile() {
        try {
            PdbFileSaver.saveFile(root, PdbObject.asString(), PdbObject.getPdbId());
        } catch (FileSaveException e) {
            PdbView.showException(e);
        }
    }
}