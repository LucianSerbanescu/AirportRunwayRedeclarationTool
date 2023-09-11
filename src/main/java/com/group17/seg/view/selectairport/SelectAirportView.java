package com.group17.seg.view.selectairport;


import com.group17.seg.controller.SelectAirportController;
import com.group17.seg.utility.Notification;
import com.group17.seg.utility.Utility;
import com.group17.seg.view.AppWindow;
import com.group17.seg.view.BaseUI;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.util.Duration;

public class SelectAirportView extends BaseUI {
    //<editor-fold desc="Attributes">
    private final BorderPane mainBorderPane = new BorderPane();
    private final FileChooser fileChooser;
    private final Alert xmlErrorAlert = new Alert(Alert.AlertType.ERROR);
    private final Timeline xmlErrorTimeline = new Timeline();
    private final SelectAirportListPane selectAirportListPane;
    private final SelectAirportMenuButtonsPane selectAirportMenuButtonsPane;
    private Notification notification;
    //</editor-fold>

    /**
     * Set up the default window
     *
     * @param window the window the UI is loading into
     */
    public SelectAirportView(AppWindow window) {
        super(window);
        this.fileChooser = new FileChooser();
        this.fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML", "*.xml"));
        this.xmlErrorAlert.setHeaderText("XML Unmarshalling Error");
        this.xmlErrorAlert.setContentText("The XML file was invalid.");
        this.xmlErrorTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds(5), e -> this.xmlErrorAlert.close()));
        SelectAirportController selectAirportController = new SelectAirportController(this);
        this.selectAirportListPane = new SelectAirportListPane(selectAirportController);
        this.selectAirportMenuButtonsPane = new SelectAirportMenuButtonsPane(selectAirportController);
        Platform.runLater(() -> this.notification = new Notification(this.getPrimaryStage(), 3));
    }

    public void showNotification(String message) {
        this.notification.showNotification(message);
    }

    public void showError() {
        this.xmlErrorAlert.show();
        this.xmlErrorTimeline.play();
    }

    public FileChooser getFileChooser() {
        return this.fileChooser;
    }

    /**
     * Build the layout of the scene
     */
    @Override
    public void build() {
        // Create main pane

        Utility.setStyle(this.mainBorderPane, "window");

        // Sets the Left Pane
        var airportSelectionPane = (StackPane) this.selectAirportListPane.getNode();
        airportSelectionPane.prefWidthProperty().bind(this.mainBorderPane.widthProperty().divide(2));
        this.mainBorderPane.setLeft(airportSelectionPane);

        // Sets the Right Pane
        var airportSelectionButtonsPane = (StackPane) this.selectAirportMenuButtonsPane.getNode();
        airportSelectionButtonsPane.prefWidthProperty().bind(this.mainBorderPane.widthProperty().divide(2));
        this.mainBorderPane.setRight(airportSelectionButtonsPane);

        this.root.getChildren().add(mainBorderPane);
    }
}
