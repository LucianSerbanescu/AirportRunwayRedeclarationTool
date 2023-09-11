package com.group17.seg.nodes;

import javafx.event.ActionEvent;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

public class BetterToggleButton extends ToggleButton {
    private Object object;

    public BetterToggleButton() {

    }

    public BetterToggleButton(String name) {
        this.setText(name);
    }

    public BetterToggleButton(String name, String style) {
        this.setText(name);
        this.getStyleClass().setAll(style);
    }

    public BetterToggleButton(String style, ToggleGroup toggleGroup) {
        this.getStyleClass().setAll(style);
        this.setToggleGroup(toggleGroup);
    }

    public BetterToggleButton(String name, String style, ToggleGroup toggleGroup) {
        this.setText(name);
        this.getStyleClass().setAll(style);
        this.setToggleGroup(toggleGroup);
    }

    public BetterToggleButton(String style, ToggleGroup toggleGroup, String id) {
        this.getStyleClass().setAll(style);
        this.setToggleGroup(toggleGroup);
        this.setId(id);
    }

    public BetterToggleButton(String name, String style, ToggleGroup toggleGroup, String id) {
        this.setText(name);
        this.getStyleClass().setAll(style);
        this.setToggleGroup(toggleGroup);
        this.setId(id);
    }

    public Object getObject() {
        return this.object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    @Override
    public void fire() {
        if (this.isDisabled()) {
            return;
        }
        this.fireEvent(new ActionEvent());
    }
}
