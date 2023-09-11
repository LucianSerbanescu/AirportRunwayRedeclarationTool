package com.group17.seg.view.selectairport;

import com.group17.seg.controller.SelectAirportController;
import com.group17.seg.utility.Utility;
import com.group17.seg.view.BaseNodeNew;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.Objects;

public class SelectAirportMenuButtonsPane implements BaseNodeNew {
    private final SelectAirportController selectAirportController;

    public SelectAirportMenuButtonsPane(SelectAirportController selectAirportController) {
        this.selectAirportController = selectAirportController;
    }

    private VBox getContentsVBox() {
        var contentsVBox = new VBox(15);
        VBox.setVgrow(contentsVBox, Priority.ALWAYS);
        Utility.setStyle(contentsVBox, "vbox-select-airport-right");

        // The VBox Title
        var airportSelectionTitleLabel = new javafx.scene.control.Label("Runway Redeclaration Tool");
        airportSelectionTitleLabel.setWrapText(true);
        Utility.setStyle(airportSelectionTitleLabel, "title");
        contentsVBox.getChildren().add(airportSelectionTitleLabel);

        // Button GridPane
        var buttonGridPane = new GridPane();
        Utility.setStyle(buttonGridPane, "gridpane-select-airport-selection-buttons");
        buttonGridPane.setHgap(20);
        buttonGridPane.setVgap(20);

        var newAirportButton = new Button("New Airport");
        var loadAirportButton = new Button("Load Airport");
        var importAirportButton = new Button("Import Airport");
        var exportAirportButton = new Button("Export Airport");

        newAirportButton.setGraphic(new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/newAirportPNG.png")))));
        importAirportButton.setGraphic(new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/importAirportPNG.png")))));
        loadAirportButton.setGraphic(new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/loadAirportPNG.png")))));
        exportAirportButton.setGraphic(new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/exportAirportPNG.png")))));

        Utility.setStyle(newAirportButton, "buttons-select-airport");
        Utility.setStyle(importAirportButton, "buttons-select-airport");
        Utility.setStyle(loadAirportButton, "buttons-select-airport");
        Utility.setStyle(exportAirportButton, "buttons-select-airport");

        buttonGridPane.add(newAirportButton, 0, 0);
        buttonGridPane.add(loadAirportButton, 1, 0);
        buttonGridPane.add(importAirportButton, 0, 1);
        buttonGridPane.add(exportAirportButton, 1, 1);

        this.selectAirportController.registerNewAirportButton(newAirportButton);
        this.selectAirportController.registerLoadAirportButton(loadAirportButton);
        this.selectAirportController.registerImportAirportButton(importAirportButton);
        this.selectAirportController.registerExportAirportButton(exportAirportButton);

        contentsVBox.getChildren().add(buttonGridPane);

        // Logger Checkbox
        var loggerCheckbox = new CheckBox("Enable Logging");
        this.selectAirportController.registerLoggerCheckbox(loggerCheckbox);
        contentsVBox.getChildren().add(loggerCheckbox);

        var enableGrayscaleCheckbox = new CheckBox("Enable Grayscale");
        Platform.runLater(() -> this.selectAirportController.registerGrayscaleCheckbox(enableGrayscaleCheckbox));
        contentsVBox.getChildren().add(enableGrayscaleCheckbox);

        return contentsVBox;
    }

    /**
     * @return StackPane of the Buttons
     */
    @Override
    public Node getNode() {
        var stackPane = new StackPane();
        stackPane.getChildren().add(this.getContentsVBox());
        return stackPane;
    }
}
