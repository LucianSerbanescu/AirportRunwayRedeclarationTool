package com.group17.seg.nodes;

import javafx.scene.shape.Rectangle;

public class ResizableRectangle extends Rectangle {
    public ResizableRectangle(double w, double h) {
        super(w, h);
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double minWidth(double height) {
        return 0.0;
    }
}
