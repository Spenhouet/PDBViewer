package views.graph;

import javafx.animation.*;
import javafx.beans.*;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.concurrent.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.transform.*;
import javafx.util.*;
import selections.*;
import views.*;
import views.data.*;
import views.graph.elements.*;
import views.graph.styles.*;
import views.toggle.*;

import java.lang.reflect.*;
import java.util.*;

public class ProteinView extends Group {

    public static final double TURN_FACTOR = 0.4;
    private final ColorModeSelection colorModeSelection;
    private final StyleSelection visualizationModelSelection;
    private List<AtomViewData> atomViewData;
    private List<BondViewData> bondViewData;

    private final ObjectProperty<SelectionModel<AtomView>> aminoAcidSelection = new SimpleObjectProperty<>();

    private final Group boundingBoxes2D = new Group();

    private Duration centerUpdateDuration = Duration.millis(1);
    private final ObjectProperty<Point3D> center = new SimpleObjectProperty<>(Point3D.ZERO);
    private final ObjectProperty<Transform> transform = new SimpleObjectProperty<>(new Rotate());
    private final InvalidationListener centerPositionInvalidation = listener -> center();
    private final ObjectProperty<Style> styleView = new SimpleObjectProperty<>();
    private final InvalidationListener centerPointInvalidation = listener -> updatePivotPoint();

    public ProteinView(ColorModeSelection colorModeSelection, StyleSelection visualizationModelSelection) {
        this.colorModeSelection = colorModeSelection;
        this.visualizationModelSelection = visualizationModelSelection;

        addListener();
    }

    private void addListener() {
        transform.addListener((change, oldTransform, newTransform) -> this.getTransforms()
                .setAll(newTransform));

        styleView
                .addListener(centerPointInvalidation);
        styleView
                .addListener(centerPositionInvalidation);

        colorModeSelection.colorSelectionProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (styleView.isNotNull()
                            .get())
                        styleView.get()
                                .colorModeChange(newValue);
                });

        visualizationModelSelection.styleTypeSelectionProperty()
                .addListener((observable, oldValue, newValue) -> {
                    Task<Style> styleTask = changeStyle(newValue);
                    new Thread(styleTask).start();
                });

        styleView.addListener((observable, oldValue, newValue) -> newValue.colorModeChange(colorModeSelection.getColorSelection()));

        styleView.addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                this.getChildren()
                        .clear();
            } else {
                this.getChildren()
                        .setAll(newValue);
            }
        });
    }

    public Task<Style> setData(List<AtomViewData> atomViewData, List<BondViewData> bondViewData) {
        this.atomViewData = atomViewData;
        this.bondViewData = bondViewData;
        return changeStyle(visualizationModelSelection.getStyleTypeSelection());
    }

    private Task<Style> changeStyle(StyleType styleType) {
        if (this.atomViewData == null || this.bondViewData == null)
            return null;

        Class<? extends Style> visualizationModelClass = styleType.getStyleClass();

        Task<Style> createStyleViewTask = new Task<Style>() {
            @Override
            protected Style call() throws Exception {
                Constructor<? extends Style> modelClassConstructor = visualizationModelClass.getConstructor(List.class, List.class);
                return modelClassConstructor.newInstance(atomViewData, bondViewData);
            }
        };

        createStyleViewTask.valueProperty()
                .addListener((observable, oldValue, style) -> {
                    aminoAcidSelection.set(style.getSelectionModel());
                    styleView.set(style);
                });

        createStyleViewTask.exceptionProperty()
                .addListener(listener -> PdbView.showException(new Exception("Changing the style failed.")));

        return createStyleViewTask;
    }

    private void updatePivotPoint() {
        Bounds bounds = styleView.get()
                .getLayoutBounds();
        double centerX = bounds.getMinX() + (bounds.getWidth() / 2);
        double centerY = bounds.getMinY() + (bounds.getHeight() / 2);
        double centerZ = bounds.getMinZ() + (bounds.getDepth() / 2);
        center.set(new Point3D(centerX, centerY, centerZ));

        transform.set(new Rotate());
    }

    private void center() {
        Point3D centerPoint = this.center.get();
        TranslateTransition transition = new TranslateTransition(centerUpdateDuration, this);
        transition.setToX(-centerPoint.getX());
        transition.setToY(-centerPoint.getY());
        transition.setToZ(-centerPoint.getZ());
        transition.play();

        if (centerUpdateDuration.toMillis() == 0.0)
            centerUpdateDuration = Duration.millis(100);
    }

    public void addTransform(Transform transform) {
        this.transform.set(transform.createConcatenation(this.transform.get()));
    }

    public void initBoundingBox(Pane bottomPane, Observable... properties) {
        BoundingBoxes boundingBoxes = new BoundingBoxes(bottomPane, aminoAcidSelection.get(), properties);
        this.boundingBoxes2D.getChildren()
                .setAll(boundingBoxes.getRectangles());
    }

    public ObjectProperty<Transform> transformProperty() {
        return transform;
    }

    public Group getBoundingBoxes2D() {
        return boundingBoxes2D;
    }

    public Point3D getCenter() {
        return center.get();
    }

    public SelectionModel<AtomView> getAminoAcidSelection() {
        return aminoAcidSelection.get();
    }

    public ObjectProperty<SelectionModel<AtomView>> aminoAcidSelectionProperty() {
        return aminoAcidSelection;
    }

    public ObjectProperty<Style> styleViewProperty() {
        return styleView;
    }
}
