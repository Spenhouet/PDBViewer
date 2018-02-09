package views;

import javafx.application.*;
import javafx.beans.property.*;
import javafx.fxml.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.scene.transform.*;
import models.*;
import selections.*;
import views.dialogs.*;
import views.graph.*;
import views.toggle.*;

public class PdbView {
    private final DoubleProperty mousePosX = new SimpleDoubleProperty(0);
    private final DoubleProperty mousePosY = new SimpleDoubleProperty(0);
    @FXML
    private Pane topPane;
    @FXML
    private Pane bottomPane;
    @FXML
    private Text atomsCount;
    @FXML
    private Text bondsCount;
    @FXML
    private MenuItem save;
    @FXML
    private MenuItem reloadPdbTable;
    @FXML
    private Text proteinDescription;
    @FXML
    private Text pdbId;
    @FXML
    private MenuItem close;
    @FXML
    private MenuItem open;
    @FXML
    private TableView<PdbId> proteinTable;
    @FXML
    private TableColumn<PdbId, String> proteinId;
    @FXML
    private TableColumn<PdbId, String> proteinClassification;
    @FXML
    private TableColumn<PdbId, String> proteinTitle;
    @FXML
    private TextField idFilter;
    @FXML
    private TextField classificationFilter;
    @FXML
    private TextField titleFilter;
    @FXML
    private TextArea proteinSequence;
    @FXML
    private TextArea secondaryStructure;
    @FXML
    private ToggleGroup colorToggleGroup;
    @FXML
    private RadioMenuItem colorCPK;
    @FXML
    private RadioMenuItem colorStructure;
    @FXML
    private RadioMenuItem colorShapely;
    @FXML
    private RadioMenuItem colorSequence;
    @FXML
    private RadioMenuItem colorTemperature;
    @FXML
    private ToggleGroup styleToggleGroup;
    @FXML
    private RadioMenuItem ballAndStickStyle;
    @FXML
    private RadioMenuItem spaceFillingStyle;
    @FXML
    private RadioMenuItem backboneStyle;
    @FXML
    private RadioMenuItem ribbonStyle;
    @FXML
    private RadioMenuItem cartoonStyle;
    @FXML
    private Tab viewTab;
    @FXML
    private PieChart aminoAcidsChart;
    @FXML
    private PieChart atomsChart;
    @FXML
    private PieChart secondaryStructureChart;
    @FXML
    private PieChart sequencesChart;
    @FXML
    private ScrollPane statisticsScrollPane;
    @FXML
    private HBox countsHBox;
    @FXML
    private VBox proteinListVBox;
    @FXML
    private ProgressIndicator proteinListProgressIndicator;
    @FXML
    private VBox proteinViewVBox;
    @FXML
    private ProgressIndicator proteinViewProgressIndicator;
    @FXML
    private ProgressIndicator statisticsProgressIndicator;
    @FXML
    private TabPane tabPane;
    @FXML
    private StackPane proteinViewStackPane;
    @FXML
    private ColorPicker backgroundColorPicker;
    @FXML
    private ChoiceBox<RadioMenuItem> colorModeChoiceBox;
    @FXML
    private ChoiceBox<RadioMenuItem> styleChoiceBox;
    private SubScene proteinViewSubScene;
    private SubScene aminoAcidBoxesSubScene;
    private PerspectiveCamera camera;
    private ProteinView proteinView;

    private StyleSelection visualizationModelSelection;

    public static void showException(Throwable throwable) {
        new ExceptionDialog(throwable).showAndWait();
    }

    @FXML
    private void initialize() {
        this.visualizationModelSelection = new StyleSelection(styleToggleGroup, ballAndStickStyle, spaceFillingStyle, backboneStyle, ribbonStyle, cartoonStyle);
        ColorModeSelection colorModeSelection = new ColorModeSelection(colorToggleGroup, colorCPK, colorStructure, colorShapely, colorSequence, colorTemperature);

        this.proteinView = new ProteinView(colorModeSelection, visualizationModelSelection);

        this.proteinViewSubScene = new SubScene(proteinView, 0, 0, true, SceneAntialiasing.BALANCED);

        this.camera = new PerspectiveCamera(true);
        this.camera.setFarClip(100000);
        this.camera.setNearClip(0.1);
        this.camera.setTranslateZ(-2500);

        this.proteinViewSubScene.setCamera(camera);

        this.bottomPane.getChildren()
                .setAll(proteinViewSubScene);

        this.aminoAcidBoxesSubScene = new SubScene(proteinView.getBoundingBoxes2D(), 0, 0);

        this.topPane.getChildren()
                .add(aminoAcidBoxesSubScene);

        addBindings();
        addListener();
        setShortCuts();
    }

    private void addBindings() {
        proteinViewSubScene.widthProperty()
                .bind(bottomPane.widthProperty());
        proteinViewSubScene.heightProperty()
                .bind(bottomPane.heightProperty());
        aminoAcidBoxesSubScene.widthProperty()
                .bind(bottomPane.widthProperty());
        aminoAcidBoxesSubScene.heightProperty()
                .bind(bottomPane.heightProperty());
    }

    private void addListener() {
        close.setOnAction(event -> close());

        bottomPane.setOnMousePressed(this::saveMousePosition);

        bottomPane.setOnMouseDragged(this::rotate);

        bottomPane.setOnScroll(this::zoom);

        proteinView.styleViewProperty()
                .addListener(listener -> initBoundingBox());
    }

    private void setShortCuts() {
        open.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        save.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        close.setAccelerator(new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN));
    }

    private void initBoundingBox() {
        proteinView.initBoundingBox(getBottomPane(), getProteinView()
                .transformProperty(), getTopPane()
                .widthProperty(), getTopPane()
                .heightProperty(), getCamera()
                .translateZProperty());
    }

    private void saveMousePosition(MouseEvent me) {
        mousePosX.set(me.getSceneX());
        mousePosY.set(me.getSceneY());
    }

    private void rotate(MouseEvent me) {
        double dx = (mousePosX.get() - me.getSceneX());
        double dy = (mousePosY.get() - me.getSceneY());
        if (me.isPrimaryButtonDown()) {
            Point3D axis = new Point3D(-dy, dx, 0);
            double angle = ProteinView.TURN_FACTOR * (new Point2D(dx, dy)).magnitude();

            Rotate rotate = new Rotate(angle, axis);

            Point3D pivot = proteinView.getCenter();
            rotate.setPivotX(pivot.getX());
            rotate.setPivotY(pivot.getY());
            rotate.setPivotZ(pivot.getZ());

            proteinView.addTransform(rotate);
        }
        saveMousePosition(me);
    }

    private void zoom(ScrollEvent event) {
        double delta = event.getDeltaY() * 10.0;

        if (camera.getTranslateZ() + delta >= 0)
            return;

        camera.setTranslateZ(camera.getTranslateZ() + delta);
    }

    private void close() {
        Platform.exit();
    }

    public ProteinView getProteinView() {
        return proteinView;
    }

    private Pane getTopPane() {
        return topPane;
    }

    public Pane getBottomPane() {
        return bottomPane;
    }

    public Text getAtomsCount() {
        return atomsCount;
    }

    public Text getBondsCount() {
        return bondsCount;
    }

    public MenuItem getSave() {
        return save;
    }

    public Text getProteinDescription() {
        return proteinDescription;
    }

    public TableColumn<PdbId, String> getProteinTitle() {
        return proteinTitle;
    }

    public Text getPdbId() {
        return pdbId;
    }

    public MenuItem getOpen() {
        return open;
    }

    private PerspectiveCamera getCamera() {
        return camera;
    }

    public TableView<PdbId> getProteinTable() {
        return proteinTable;
    }

    public TableColumn<PdbId, String> getProteinId() {
        return proteinId;
    }

    public TableColumn<PdbId, String> getProteinClassification() {
        return proteinClassification;
    }

    public TextField getIdFilter() {
        return idFilter;
    }

    public TextField getClassificationFilter() {
        return classificationFilter;
    }

    public TextArea getProteinSequence() {
        return proteinSequence;
    }

    public TextArea getSecondaryStructure() {
        return secondaryStructure;
    }

    public Tab getViewTab() {
        return viewTab;
    }

    public PieChart getAminoAcidsChart() {
        return aminoAcidsChart;
    }

    public PieChart getAtomsChart() {
        return atomsChart;
    }

    public HBox getCountsHBox() {
        return countsHBox;
    }

    public PieChart getSequencesChart() {
        return sequencesChart;
    }

    public PieChart getSecondaryStructureChart() {
        return secondaryStructureChart;
    }

    public TextField getTitleFilter() {
        return titleFilter;
    }

    public VBox getProteinListVBox() {
        return proteinListVBox;
    }

    public ProgressIndicator getProteinListProgressIndicator() {
        return proteinListProgressIndicator;
    }

    public VBox getProteinViewVBox() {
        return proteinViewVBox;
    }

    public ProgressIndicator getProteinViewProgressIndicator() {
        return proteinViewProgressIndicator;
    }

    public ScrollPane getStatisticsScrollPane() {
        return statisticsScrollPane;
    }

    public ProgressIndicator getStatisticsProgressIndicator() {
        return statisticsProgressIndicator;
    }

    public TabPane getTabPane() {
        return tabPane;
    }

    public ObjectProperty<StyleType> getVisualizationModelSelectionProperty() {
        return visualizationModelSelection.styleTypeSelectionProperty();
    }

    public MenuItem getReloadPdbTable() {
        return reloadPdbTable;
    }

    public ToggleGroup getColorToggleGroup() {
        return colorToggleGroup;
    }

    public ToggleGroup getStyleToggleGroup() {
        return styleToggleGroup;
    }

    public StackPane getProteinViewStackPane() {
        return proteinViewStackPane;
    }

    public ColorPicker getBackgroundColorPicker() {
        return backgroundColorPicker;
    }

    public ChoiceBox<RadioMenuItem> getColorModeChoiceBox() {
        return colorModeChoiceBox;
    }

    public ChoiceBox<RadioMenuItem> getStyleChoiceBox() {
        return styleChoiceBox;
    }

}
