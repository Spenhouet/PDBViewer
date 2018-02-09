package models;

import javafx.beans.property.*;

public class PdbId {

    private final StringProperty id;
    private final StringProperty classification;
    private final StringProperty title;

    public PdbId(String id, String classification, String title) {
        this(new SimpleStringProperty(id), new SimpleStringProperty(classification), new SimpleStringProperty(title));
    }

    private PdbId(StringProperty id, StringProperty classification, StringProperty title) {
        this.id = id;
        this.classification = classification;
        this.title = title;
    }

    public String getId() {
        return id.get();
    }

    public StringProperty idProperty() {
        return id;
    }

    public StringProperty classificationProperty() {
        return classification;
    }

    public StringProperty titleProperty() {
        return title;
    }
}
