package views.graph;

import javafx.beans.Observable;
import javafx.beans.binding.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import selections.*;
import views.graph.elements.*;

import java.util.*;

/**
 * maintains 2D bounding boxes for 3D node views
 * Daniel Huson, 11.2017
 */
public class BoundingBoxes {
    private final Group rectangles = new Group();
    private final Map<AtomView, Rectangle> node2rectangle = new HashMap<>();

    /**
     * constructor
     *
     * @param properties Other properties on
     */
    BoundingBoxes(Pane bottomPane, SelectionModel<AtomView> nodeSelectionModel, Observable... properties) {
        if (nodeSelectionModel == null)
            return;

        nodeSelectionModel.getSelectedItems()
                .addListener((ListChangeListener<AtomView>) c -> {
                    while (c.next()) {
                        for (AtomView node : c.getRemoved()) {
                            rectangles.getChildren()
                                    .remove(node2rectangle.get(node));
                            node2rectangle.remove(node);
                        }
                        for (AtomView node : c.getAddedSubList()) {
                            final Rectangle rect = createBoundingBoxWithBinding(bottomPane, node, properties);
                            rect.visibleProperty()
                                    .bind(node.visibleProperty());
                            node2rectangle.put(node, rect);
                            rectangles.getChildren()
                                    .add(rect);
                        }
                    }
                });
    }

    /**
     * create a bounding box that is bound to user determined transformations
     */
    private static Rectangle createBoundingBoxWithBinding(Pane pane, AtomView node, final Observable... properties) {
        final Rectangle boundingBox = new Rectangle();
        boundingBox.setStroke(Color.GOLDENROD);
        boundingBox.setStrokeWidth(2);
        boundingBox.setFill(Color.TRANSPARENT);
        boundingBox.setMouseTransparent(true);
        boundingBox.setVisible(true);

        final ObjectBinding<Rectangle> binding = new ObjectBinding<Rectangle>() {
            {
                bind(properties);
                bind(node.translateXProperty());
                bind(node.translateYProperty());
                bind(node.translateZProperty());
            }

            @Override
            protected Rectangle computeValue() {
                return computeRectangle(pane, node);
            }
        };

        binding.addListener((c, o, n) -> {
            boundingBox.setX(n.getX());
            boundingBox.setY(n.getY());
            boundingBox.setWidth(n.getWidth());
            boundingBox.setHeight(n.getHeight());
        });
        boundingBox.setUserData(binding);

        binding.invalidate();
        return boundingBox;
    }

    private static Rectangle computeRectangle(Pane pane, Node node) {
        try {
            final Bounds boundsOnScreen = node.localToScreen(node.getBoundsInLocal());
            final Bounds paneBoundsOnScreen = pane.localToScreen(pane.getBoundsInLocal());
            final double xInScene = boundsOnScreen.getMinX() - paneBoundsOnScreen.getMinX();
            final double yInScene = boundsOnScreen.getMinY() - paneBoundsOnScreen.getMinY();
            return new Rectangle(xInScene, yInScene, boundsOnScreen.getWidth(), boundsOnScreen.getHeight());
        } catch (NullPointerException e) {
            return new Rectangle(0, 0, 0, 0);
        }
    }

    public Group getRectangles() {
        return rectangles;
    }
}
