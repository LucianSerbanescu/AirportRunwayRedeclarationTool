package com.group17.seg.nodes;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;

/**
 * Scroll pane that can be zoomed in/out and dragged around by mouse
 * <br>
 * Source (modified to suit the need for this project):
 * <a href="https://stackoverflow.com/questions/39827911/javafx-8-scaling-zooming-scrollpane-relative-to-mouse-position">Link</a>
 */
public class ZoomableScrollPane extends ScrollPane {

    private double scaleValue;
    private final double zoomIntensity;

    /**
     * target: the target node that will be zoomed in/out
     * zoomNode: wrapper around the target where the zoom operations will act on
     */
    private final Node target;
    private final Node zoomNode;

    public ZoomableScrollPane(Node target) {
        super();
        this.scaleValue = 1;
        this.zoomIntensity = 0.0035;
        this.target = target;
        this.zoomNode = new StackPane(new Group(target));
        this.zoomNode.setOnScroll((event) -> {
            event.consume();
            this.onScroll(event.getDeltaY(), new Point2D(event.getX(), event.getY()));
        });
        this.setContent(this.zoomNode);

        // disable scrollbar and enable mouse drag
        this.setPannable(true);
        this.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // Center align the elements in the scroll pane
        this.setFitToHeight(true);
        this.setFitToWidth(true);

        // initialize scale
        this.updateScale();
    }

    private void updateScale() {
        this.target.setScaleX(this.scaleValue);
        this.target.setScaleY(this.scaleValue);
    }

    private void onScroll(double wheelDelta, Point2D mousePoint) {
        double zoomFactor = Math.exp(wheelDelta * zoomIntensity);

        Bounds innerBounds = this.zoomNode.getLayoutBounds();
        Bounds viewportBounds = this.getViewportBounds();

        // calculate pixel offsets from [0, 1] range
        double valX = this.getHvalue() * (innerBounds.getWidth() - viewportBounds.getWidth());
        double valY = this.getVvalue() * (innerBounds.getHeight() - viewportBounds.getHeight());

        this.scaleValue = Math.max(0.02, Math.min(3, scaleValue * zoomFactor));
        this.updateScale();
        this.layout(); // refresh ScrollPane scroll positions & target bounds

        // convert target coordinates to zoomTarget coordinates
        var posInZoomTarget = target.parentToLocal(zoomNode.parentToLocal(mousePoint)).multiply(1 / target.getScaleX());

        // calculate adjustment of scroll position (pixels)
        Point2D adjustment = target.getLocalToParentTransform().deltaTransform(posInZoomTarget.multiply(zoomFactor - 1));

        // convert back to [0, 1] range
        // (too large/small values are automatically corrected by ScrollPane)
        Bounds updatedInnerBounds = zoomNode.getBoundsInLocal();
        this.setHvalue((valX + adjustment.getX()) / (updatedInnerBounds.getWidth() - viewportBounds.getWidth()));
        this.setVvalue((valY + adjustment.getY()) / (updatedInnerBounds.getHeight() - viewportBounds.getHeight()));
    }
}