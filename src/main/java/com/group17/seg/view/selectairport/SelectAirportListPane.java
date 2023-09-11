package com.group17.seg.view.selectairport;

import com.group17.seg.controller.SelectAirportController;
import com.group17.seg.model.Airport;
import com.group17.seg.nodes.BetterToggleButton;
import com.group17.seg.utility.Utility;
import com.group17.seg.view.BaseNodeNew;
import javafx.collections.ListChangeListener;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class SelectAirportListPane implements BaseNodeNew {
    private final SelectAirportController selectAirportController;

    public SelectAirportListPane(SelectAirportController selectAirportController) {
        this.selectAirportController = selectAirportController;
    }

    private VBox contentVBox() {
        // The VBox that will be returned
        var contentVBox = new VBox(15);
        VBox.setVgrow(contentVBox, Priority.ALWAYS);
        Utility.setStyle(contentVBox, "vbox-select-airport-left");

        // The VBox Title
        var airportSelectionTitleLabel = new Label("Select Airport");
        Utility.setStyle(airportSelectionTitleLabel, "label-airport-selection-title");
        contentVBox.getChildren().add(airportSelectionTitleLabel);

        // List of ToggleButtons that correspond to the List of Airports
        var airportSelectionList = new VBox();
        var airportSelectionListToggleGroup = new ToggleGroup();
        this.selectAirportController.listenToAirportList((ListChangeListener<? super Airport>) c -> {
            while (c.next()) {
                for (var airport : c.getAddedSubList()) {
                    var airportButton = new BetterToggleButton("listItem", airportSelectionListToggleGroup);
                    airportButton.setObject(airport);
                    airportButton.textProperty().bind(airport.nameProperty());
                    airportButton.prefWidthProperty().bind(airportSelectionList.widthProperty());
                    airportSelectionList.getChildren().add(0, airportButton);
                    this.selectAirportController.registerAirportToggleButton(airportButton, airport);
                }
                for (var airport : c.getRemoved()) {
                    airportSelectionList.getChildren().removeIf(node -> {
                        if (!(node instanceof BetterToggleButton button)) return false;
                        return button.getObject() == airport;
                    });
                }
            }
        });
        var airportSelectionScrollPane = new ScrollPane();
        airportSelectionScrollPane.setFitToWidth(true);
        airportSelectionScrollPane.minHeightProperty().bind(contentVBox.heightProperty().multiply(0.5));
        airportSelectionScrollPane.maxHeightProperty().bind(contentVBox.heightProperty().multiply(0.5));
        airportSelectionScrollPane.setContent(airportSelectionList);

        contentVBox.getChildren().add(airportSelectionScrollPane);

        // Rename and Delete Buttons
        var renameDeleteHBox = new HBox(20);
        Utility.setStyle(renameDeleteHBox, "hbox-rename-delete");

        var renameAirportButton = new Button("Rename");
        var deleteAirportButton = new Button("Delete");

        Utility.setStyle(renameAirportButton, "button-mainmenu-delete");
        Utility.setStyle(deleteAirportButton, "button-mainmenu-delete");

        this.selectAirportController.registerRenameAirportButton(renameAirportButton);
        this.selectAirportController.registerDeleteAirportButton(deleteAirportButton);

        renameDeleteHBox.getChildren().addAll(deleteAirportButton, renameAirportButton);

        contentVBox.getChildren().add(renameDeleteHBox);
        return contentVBox;
    }

    /**
     * @return Node
     */
    @Override
    public Node getNode() {
        var stackPane = new StackPane();

        var contentsVBox = this.contentVBox();
        stackPane.getChildren().add(contentsVBox);

        return stackPane;
    }
}
