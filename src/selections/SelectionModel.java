package selections;

import javafx.beans.property.*;
import javafx.collections.*;
import javafx.scene.control.*;

import java.util.*;

public class SelectionModel<T> extends MultipleSelectionModel<T> {

    private final ObservableSet<Integer> selectedIndicesSet;
    private final ObservableList<Integer> selectedIndicesList;
    private final ObservableList<T> selectedItemsList;
    private final ObservableMap<Integer, T> idTMap;

    private final BooleanProperty isEmpty;
    private final BooleanProperty allSelected;

    public SelectionModel(ObservableMap<Integer, T> idTMap) {
        this.idTMap = idTMap;
        this.selectedIndicesSet = FXCollections.observableSet();
        this.selectedIndicesList = FXCollections.observableArrayList();
        this.selectedItemsList = FXCollections.observableArrayList();
        this.isEmpty = new SimpleBooleanProperty(true);
        this.allSelected = new SimpleBooleanProperty(false);

        idTMap.addListener((MapChangeListener<Integer, T>) change -> {
            if (change.wasRemoved()) {
                selectedItemsList.remove(change.getValueRemoved());
            }
        });

        selectedIndicesSet.addListener((SetChangeListener<Integer>) c -> {
            if (c.wasAdded()) {
                selectedIndicesList.add(c.getElementAdded());
                selectedItemsList.add(idTMap.get(c.getElementAdded()));
            } else if (c.wasRemoved()) {
                selectedIndicesList.remove(c.getElementRemoved());
                selectedItemsList.remove(idTMap.get(c.getElementRemoved()));
            }
            isEmpty.set(selectedIndicesSet.isEmpty());
            allSelected.set(selectedIndicesSet.size() == idTMap.size());
        });
    }

    @Override
    public ObservableList<Integer> getSelectedIndices() {
        return selectedIndicesList;
    }

    public Set<Integer> getSelectedIndicesSet() {
        return selectedIndicesSet;
    }

    @Override
    public ObservableList<T> getSelectedItems() {
        return selectedItemsList;
    }

    @Override
    public void selectIndices(int index, int... indices) {
        select(index);
        for (int i : indices) {
            select(i);
        }
    }

    @Override
    public void selectAll() {
        idTMap.keySet()
                .forEach(this::select);
    }

    public void selectAll(Set<Integer> indices) {
        if (indices != null)
            indices.forEach(this::select);
    }

    public void clearAndSelectAll(Set<Integer> indices) {
        clearSelection();
        indices.forEach(this::select);
    }

    @Override
    public void clearAndSelect(int index) {
        clearSelection();
        select(index);
    }

    @Override
    public void select(int index) {
        if (idTMap.containsKey(index))
            selectedIndicesSet.add(index);
    }

    @Override
    public void select(T obj) {
        idTMap.entrySet()
                .stream()
                .filter(entry -> entry.getValue()
                        .equals(obj))
                .map(Map.Entry::getKey)
                .findFirst()
                .ifPresent(this::select);
    }

    public void select(List<T> objs) {
        for (T obj : objs) {
            idTMap.entrySet()
                    .stream()
                    .filter(entry -> entry.getValue()
                            .equals(obj))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .ifPresent(this::select);
        }
    }

    @Override
    public void clearSelection(int index) {
        if (selectedIndicesSet.contains(index))
            selectedIndicesSet.remove(index);
    }

    @Override
    public void clearSelection() {
        selectedIndicesSet.clear();
    }

    @Override
    public boolean isSelected(int index) {
        return idTMap.containsKey(index) && selectedIndicesSet.contains(index);
    }

    @Override
    public boolean isEmpty() {
        return isEmpty.get();
    }

    @Override
    public void selectPrevious() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void selectNext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void selectFirst() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void selectLast() {
        throw new UnsupportedOperationException();
    }

    public BooleanProperty isEmptyProperty() {
        return isEmpty;
    }

    public BooleanProperty allSelectedProperty() {
        return allSelected;
    }
}
