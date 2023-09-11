package com.group17.seg.view.loadairport;

import com.group17.seg.io.AirportXMLParser;
import com.group17.seg.utility.Logger;
import com.group17.seg.utility.Utility;
import com.group17.seg.view.BaseNode;
import com.group17.seg.view.selectairport.SelectAirportView;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

class AirportTitle extends BaseNode {
    AirportTitle(LoadAirportUI loadAirportUI) {
        super(loadAirportUI);
    }

    @Override
    public Node getNode() {
        var titleVBox = new VBox();
        VBox.setVgrow(titleVBox, Priority.NEVER);
        titleVBox.setAlignment(Pos.CENTER);
        titleVBox.setSpacing(2);

        // Button HBox
        var buttonHBox = new HBox(5);
        HBox.setHgrow(buttonHBox, Priority.ALWAYS);
        buttonHBox.setAlignment(Pos.CENTER);

        var backButton = new Button("Back");
        backButton.setOnAction(event -> {
            this.loadAirportUI.airportList.add(0, this.loadAirportUI.airport);
            AirportXMLParser.writeAirportsToXML(this.loadAirportUI.cwd + "/airport-tool/SavedAirports.xml", this.loadAirportUI.airportList);
            this.loadAirportUI.storeObstacles();
            Logger.writeToLogFile(String.format("Airport Saved - %s, Scene Changed to Main Menu", this.loadAirportUI.airport.getName()));
            this.loadAirportUI.nextUI(SelectAirportView.class);
        });
        Utility.setStyle(backButton, "basic-label-button");
        buttonHBox.getChildren().add(backButton);

        var saveButton = new Button("Save");
        saveButton.setOnAction(event -> {
            this.loadAirportUI.airportList.add(0, this.loadAirportUI.airport);
            AirportXMLParser.writeAirportsToXML(this.loadAirportUI.cwd + "/airport-tool/SavedAirports.xml", this.loadAirportUI.airportList);
            this.loadAirportUI.airportList.remove(0);
            this.loadAirportUI.storeObstacles();
            Logger.writeToLogFile(String.format("Airport Saved: %s", this.loadAirportUI.airport.getName()));
        });
        Utility.setStyle(saveButton, "basic-label-button");
        buttonHBox.getChildren().add(saveButton);

        titleVBox.getChildren().add(buttonHBox);

        // Add title text
        var airportLabel = new Label(this.loadAirportUI.airport.getName());
        Utility.setStyle(airportLabel, "airport-name");
        titleVBox.getChildren().add(airportLabel);

        return titleVBox;
    }
}
