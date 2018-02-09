package presenters;

import exceptions.*;
import helpers.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.collections.transformation.*;
import javafx.concurrent.*;
import javafx.event.*;
import javafx.scene.input.*;
import models.*;
import views.*;
import views.dialogs.*;

import java.io.*;
import java.util.*;

import static helpers.PcbIdRequester.FILENAME;

class ProteinTablePresenter {
    private final PdbView view;
    private final PdbViewerModel model;

    private final ObservableList<PdbId> pdbIds = FXCollections.observableArrayList();
    private final FilteredList<PdbId> filteredPdbData;

    ProteinTablePresenter(PdbView view, PdbViewerModel model) {
        this.view = view;
        this.model = model;

        view.getProteinId()
                .setCellValueFactory(cellData -> cellData.getValue()
                        .idProperty());
        view.getProteinClassification()
                .setCellValueFactory(cellData -> cellData.getValue()
                        .classificationProperty());
        view.getProteinTitle()
                .setCellValueFactory(cellData -> cellData.getValue()
                        .titleProperty());

        filteredPdbData = new FilteredList<>(pdbIds, p -> true);
        SortedList<PdbId> sortedData = new SortedList<>(filteredPdbData);
        sortedData.comparatorProperty()
                .bind(view.getProteinTable()
                        .comparatorProperty());

        view.getProteinTable()
                .setItems(sortedData);

        addBindings();
        addListener();

        populatePdbIdTable(false);
    }

    private void addBindings() {
        view.getReloadPdbTable()
                .disableProperty()
                .bind(view.getProteinTable()
                        .disabledProperty());
    }

    private void addListener() {
        view.getProteinTable()
                .addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                    if (event.getClickCount() == 2) {
                        loadPdbDataForSelectedProtein();
                    }
                });

        view.getProteinTable()
                .addEventHandler(KeyEvent.KEY_PRESSED, event -> {
                    if (KeyCode.ENTER == event.getCode()) {
                        loadPdbDataForSelectedProtein();
                    }
                });

        view.getReloadPdbTable()
                .addEventHandler(ActionEvent.ACTION, event -> populatePdbIdTable(true));

        view.getIdFilter()
                .textProperty()
                .addListener(listener -> filterProteinTable());

        view.getClassificationFilter()
                .textProperty()
                .addListener(listener -> filterProteinTable());

        view.getTitleFilter()
                .textProperty()
                .addListener(listener -> filterProteinTable());
    }

    private void populatePdbIdTable(boolean reload) {
        File pdbListFile = new File(System.getProperty("java.io.tmpdir") + FILENAME);
        if (!reload && pdbListFile.exists()) {
            loadPdbIdTableFromFile();
        } else {
            Task<Void> downloadAndSaveList = PcbIdRequester.downloadAndSaveList();
            Progress.show(downloadAndSaveList, view.getProteinListProgressIndicator(), view.getProteinListVBox());
            downloadAndSaveList.setOnSucceeded(event -> loadPdbIdTableFromFile());

            downloadAndSaveList.exceptionProperty()
                    .addListener((observable, oldValue, throwable) -> {
                        view.getProteinListProgressIndicator()
                                .setVisible(false);
                        PdbView.showException(throwable);
                    });

            new Thread(downloadAndSaveList).start();
        }
    }

    private void loadPdbIdTableFromFile() {
        Task<List<PdbId>> retrieveData = PcbIdRequester.retrieveData();
        Progress.show(retrieveData, view.getProteinListProgressIndicator(), view.getProteinListVBox());
        retrieveData.setOnSucceeded(event -> pdbIds.setAll(retrieveData.getValue()));

        retrieveData.exceptionProperty()
                .addListener((observable, oldValue, throwable) -> {
                    view.getProteinListProgressIndicator()
                            .setVisible(false);
                    PdbView.showException(throwable);
                });

        new Thread(retrieveData).start();
    }

    private void filterProteinTable() {
        filteredPdbData.setPredicate(item -> matches(item.idProperty(), view.getIdFilter()
                .textProperty())
                && matches(item.classificationProperty(), view.getClassificationFilter()
                .textProperty()) && matches(item.titleProperty(), view.getTitleFilter()
                .textProperty()));
    }

    private boolean matches(StringProperty item, StringProperty filter) {
        if (filter == null || filter.get() == null || filter.get()
                .isEmpty())
            return true;

        String filterText = filter.get()
                .toLowerCase();
        String itemText = item.get()
                .toLowerCase();

        return itemText.contains(filterText);
    }

    private void loadPdbDataForSelectedProtein() {
        PdbId pdbId = view.getProteinTable()
                .getSelectionModel()
                .getSelectedItem();

        if (pdbId == null)
            return;

        String url = "https://files.rcsb.org/download/" + pdbId.getId() + ".pdb";
        try {
            Task<Void> task = model.loadPdbDataFromReader(Request.getFromURL(url), pdbId.getId());
            Progress.show(task, view.getProteinViewProgressIndicator(), view.getProteinViewVBox());
            Progress.show(task, view.getStatisticsProgressIndicator(), view.getStatisticsScrollPane());
            task.exceptionProperty()
                    .addListener((observable, oldValue, throwable) -> PdbView.showException(throwable));
            new Thread(task).start();
        } catch (IOException e) {
            PdbView.showException(new PdbIdRetrieveException());
        }
    }
}
