package com.group17.seg.utility;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Objects;

public class Notification {
    private final Popup notification = new Popup();
    private final Timeline timelineMain = new Timeline();
    private final Timeline timelineFade = new Timeline();
    private final Stage stage;
    private final SimpleStringProperty message = new SimpleStringProperty();
    private final SimpleDoubleProperty width = new SimpleDoubleProperty();

    public Notification(Stage stage, double duration) {
        this.stage = stage;
        this.initialise(Objects.requireNonNull(getClass().getResource("/icons/green.png")).toExternalForm(), duration);
    }

    public Notification(Stage stage, String iconUrl, double duration) {
        this.stage = stage;
        this.initialise(iconUrl, duration);
    }

    private void showNotification() {
        this.notification.show(this.stage);
        this.timelineFade.stop();
        this.timelineMain.stop();
        this.timelineMain.play();
        this.notification.setX(this.getXPos() - this.notification.getWidth());
        this.notification.setY(this.getYPos() + this.notification.getHeight() + 20);
    }

    public void showNotification(String message) {
        this.message.set(message);
        this.showNotification();
    }

    private void initialise(String iconUrl, double duration) {
        var root = new HBox();
        root.setStyle("-fx-background-radius: 8px; -fx-background-color: rgba(60, 63, 65, 0.95);");
        root.setEffect(new DropShadow(5, 3, 3, Color.BLACK));

        if (iconUrl != null) {
            ImageView icon = new ImageView(new Image(iconUrl));
            icon.setFitWidth(32);
            icon.setFitHeight(32);
            HBox.setMargin(icon, new Insets(5));
            root.getChildren().add(icon);
        }

        VBox content = new VBox();
        content.setMinWidth(150);
        content.setMaxWidth(150);
        content.setPrefHeight(35);
        HBox.setMargin(content, new Insets(5));
        HBox.setHgrow(content, Priority.ALWAYS);
        root.getChildren().add(content);

        Label titleLabel = new Label();
        titleLabel.setWrapText(true);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");

        titleLabel.textProperty().bind(this.message);
        content.getChildren().add(titleLabel);

        this.stage.xProperty().addListener(e -> this.notification.setX(this.getXPos() - this.notification.getWidth()));
        this.stage.yProperty().addListener(e -> this.notification.setY(this.getYPos() + this.notification.getHeight() + 20));

        KeyValue kv1 = new KeyValue(root.opacityProperty(), 0);
        KeyValue kv2 = new KeyValue(root.opacityProperty(), 1);
        KeyFrame kf1 = new KeyFrame(Duration.ZERO, kv1);
        KeyFrame kf2 = new KeyFrame(Duration.millis(250), kv2);
        this.timelineMain.getKeyFrames().addAll(kf1, kf2);
        this.timelineMain.setOnFinished(e -> {
            KeyValue kv3 = new KeyValue(root.opacityProperty(), 1);
            KeyValue kv4 = new KeyValue(root.opacityProperty(), 0);
            KeyFrame kf3 = new KeyFrame(Duration.ZERO, kv3);
            KeyFrame kf4 = new KeyFrame(Duration.millis(250), kv4);
            this.timelineFade.getKeyFrames().addAll(kf3, kf4);
            this.timelineFade.setDelay(Duration.seconds(duration));
            this.timelineFade.setOnFinished(e2 -> this.notification.hide());
            this.timelineFade.play();
        });

        this.notification.getContent().add(root);
    }

    private double getXPos() {
        var x0 = this.stage.getX();
        var width = this.stage.getWidth();
        //System.out.println(x0 + width);
        return x0 + width;
    }

    private double getYPos() {
        return this.stage.getY();
    }
}
