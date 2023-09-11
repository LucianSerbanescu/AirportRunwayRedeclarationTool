package com.group17.seg.controller;

import com.group17.seg.io.AirportXMLParser;
import com.group17.seg.model.Airport;
import com.group17.seg.model.AirportList;
import com.group17.seg.nodes.BetterToggleButton;
import com.group17.seg.utility.Grayscale;
import com.group17.seg.utility.Logger;
import com.group17.seg.view.loadairport.LoadAirportUI;
import com.group17.seg.view.selectairport.SelectAirportView;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextInputDialog;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import static com.group17.seg.utility.Logger.cwd;

public class SelectAirportController {
    private final ObservableList<Airport> airportObservableList;
    private final ObjectProperty<Airport> selectedAirport;
    private final SelectAirportView selectAirportView;

    public SelectAirportController(SelectAirportView selectAirportView) {
        this.airportObservableList = FXCollections.observableList(new AirportList());
        this.selectedAirport = new SimpleObjectProperty<>();
        this.selectAirportView = selectAirportView;
        Platform.runLater(this::loadAirportFile);
    }

    public void registerNewAirportButton(Button newAirportButton) {
        newAirportButton.setOnAction(e -> {
            var newAirportPrompt = new TextInputDialog();
            newAirportPrompt.setHeaderText("Enter a Name");
            newAirportPrompt.setTitle("New Airport");
            newAirportPrompt.setGraphic(null);
            Optional<String> airportName = newAirportPrompt.showAndWait();
            if (airportName.isEmpty() || airportName.get().isEmpty()) return;
            var newAirport = new Airport();
            newAirport.setName(airportName.get());
            if (Logger.isLogging()) {
                Logger.writeToLogFile(String.format("New Airport Added: %s", newAirport.getName()));
            }
            this.airportObservableList.add(newAirport);
            this.selectedAirport.set(newAirport);
            this.loadAirport();
        });
    }

    public void registerImportAirportButton(Button importAirportButton) {
        importAirportButton.setOnAction(e -> {
            var stage = new Stage();
            var file = this.selectAirportView.getFileChooser().showOpenDialog(stage);
            if (file == null) return;
            try {
                var importedAirports = AirportXMLParser.parseAirportXML(file);
                if (Logger.isLogging()) {
                    Logger.writeToLogFile(String.format("Airport(s) Imported: %s",
                                                        importedAirports.stream().map(Airport::getName).collect(Collectors.joining(", "))));
                }
                this.airportObservableList.addAll(importedAirports);
                this.storeAirports();
            } catch (JAXBException ex) {
                this.selectAirportView.showError();
            }
        });
    }

    public void registerLoadAirportButton(Button loadAirportButton) {
        loadAirportButton.setOnAction(e -> {
            this.storeAirports();
            this.airportObservableList.remove(this.selectedAirport.get());
            if (Logger.isLogging()) {
                Logger.writeToLogFile(String.format("Loaded Airport: %s", this.selectedAirport.get().getName()));
            }
            this.selectAirportView.showNotification(String.format("Airport Loaded: %s", this.selectedAirport.get().getName()));
            this.loadAirport();
        });
        loadAirportButton.disableProperty().bind(this.selectedAirport.isNull());
    }

    public void registerExportAirportButton(Button exportAirportButton) {
        exportAirportButton.setOnAction(e -> {
            var stage = new Stage();
            var file = this.selectAirportView.getFileChooser().showSaveDialog(stage);
            if (file != null) {
                var exportList = new AirportList(this.selectedAirport.get());
                AirportXMLParser.writeAirportsToXML(String.valueOf(file), exportList);
                if (Logger.isLogging()) {
                    Logger.writeToLogFile(String.format("Airport Exported: %s", this.selectedAirport.get().getName()));
                }
            }
            this.selectAirportView.showNotification("Airport Exported");
        });
        exportAirportButton.disableProperty().bind(this.selectedAirport.isNull());
    }

    public void registerLoggerCheckbox(CheckBox loggerCheckbox) {
        loggerCheckbox.setFocusTraversable(false);
        loggerCheckbox.setSelected(Logger.isLogging());
        loggerCheckbox.setOnAction(e -> {
            Logger.disableLogging(!loggerCheckbox.isSelected());
            if (loggerCheckbox.isSelected()) {
                this.selectAirportView.showNotification("Logger Enabled");
            } else {
                this.selectAirportView.showNotification("Logger Disabled");
            }
        });
    }

    public void registerGrayscaleCheckbox(CheckBox enableGrayscaleCheckbox) {
        this.selectAirportView.getPrimaryStage().getScene().getRoot().setEffect(Grayscale.grayscale);
        enableGrayscaleCheckbox.setFocusTraversable(false);
        enableGrayscaleCheckbox.setSelected(Grayscale.isGrayscale);
        enableGrayscaleCheckbox.setOnAction(e -> {
            if (enableGrayscaleCheckbox.isSelected()) {
                this.selectAirportView.showNotification("Grayscale Enabled");
                Grayscale.setGrayscale(true);
            } else {
                this.selectAirportView.showNotification("Grayscale Disabled");
                Grayscale.setGrayscale(false);
            }
        });
    }

    public void registerAirportToggleButton(BetterToggleButton airportButton, Airport airport) {
        airportButton.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            if (e.getButton() != MouseButton.PRIMARY) return;
            if (e.getClickCount() > 1) {
                this.storeAirports();
                this.airportObservableList.remove(airport);
                this.selectedAirport.set(airport);
                if (Logger.isLogging()) {
                    Logger.writeToLogFile(String.format("Airport Loaded: %s", airport.getName()));
                }
                this.selectAirportView.showNotification(String.format("Airport Loaded: %s", airport.getName()));
                this.loadAirport();
            } else if (airportButton.isSelected()) {
                airportButton.setSelected(false);
                this.selectedAirport.set(null);
            } else {
                airportButton.setSelected(true);
                this.selectedAirport.set(airport);
            }
        });
    }

    public void registerRenameAirportButton(Button renameAirportButton) {
        renameAirportButton.setOnAction(e -> {
            var airportNamePrompt = new TextInputDialog();
            airportNamePrompt.setTitle("New Airport");
            airportNamePrompt.setHeaderText("New Airport Name");
            airportNamePrompt.setGraphic(null);
            Optional<String> airportName = airportNamePrompt.showAndWait();
            if (airportName.isEmpty() || airportName.get().isEmpty()) return;
            if (Logger.isLogging()) {
                Logger.writeToLogFile(String.format("Airport Renamed: %s -> %s", this.selectedAirport.get().getName(), airportName.get()));
            }
            this.selectAirportView.showNotification("Airport Renamed");
            this.selectedAirport.get().setName(airportName.get());
            this.storeAirports();
        });
        renameAirportButton.disableProperty().bind(this.selectedAirport.isNull());
    }

    public void registerDeleteAirportButton(Button deleteAirportButton) {
        deleteAirportButton.setOnAction(e -> {
            if (Logger.isLogging()) {
                Logger.writeToLogFile(String.format("Airport Deleted: %s", this.selectedAirport.get().getName()));
            }
            this.selectAirportView.showNotification(String.format("Airport Deleted: %s", this.selectedAirport.get().getName()));
            this.airportObservableList.remove(this.selectedAirport.get());
            this.selectedAirport.set(null);
            this.storeAirports();
        });
        deleteAirportButton.disableProperty().bind(this.selectedAirport.isNull());
    }

    public void loadAirport() {
        this.saveSettings();
        this.selectAirportView.nextUI(new LoadAirportUI(this.selectAirportView.window,
                                                        this.selectedAirport.get(),
                                                        new AirportList(this.airportObservableList)));
    }

    public void listenToAirportList(ListChangeListener<? super Airport> listener) {
        this.airportObservableList.addListener(listener);
    }

    /**
     * Retrieves the saved airport list, if it doesn't exist, create a new file and copies the DefaultAirports.xml into it.
     */
    public void loadAirportFile() {
        File airportsFile = new File(Logger.cwd + "/airport-tool/SavedAirports.xml");
        if (airportsFile.exists()) {
            try {
                this.airportObservableList.addAll(AirportXMLParser.parseAirportXML(airportsFile));
            } catch (JAXBException e) {
                this.selectAirportView.showError();
            }
        } else {
            var ls = AirportXMLParser.parseAirportXML(getClass().getResourceAsStream("/DefaultAirports.xml"));
            AirportXMLParser.writeAirportsToXML(Logger.cwd + "/airport-tool/SavedAirports.xml", ls);
            this.airportObservableList.addAll(ls);
        }
    }

    public void saveSettings() {
        var settingsFilePath = Path.of(cwd + "/airport-tool/settings.properties");
        try {
            Files.createFile(settingsFilePath);
        } catch (IOException ignored) {
        }
        Properties properties = new Properties(2);
        try {
            properties.setProperty("logging", String.valueOf(Logger.isLogging()));
            properties.setProperty("grayscale", String.valueOf(Grayscale.isGrayscale));
            properties.store(new FileWriter(settingsFilePath.toFile()), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the current list of airports to the SavedAirports.xml file
     */
    public void storeAirports() {
        AirportXMLParser.writeAirportsToXML(Logger.cwd + "/airport-tool/SavedAirports.xml", new AirportList(this.airportObservableList));
    }
}
