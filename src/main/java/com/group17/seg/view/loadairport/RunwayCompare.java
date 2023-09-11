package com.group17.seg.view.loadairport;

import com.group17.seg.model.*;
import com.group17.seg.utility.Logger;
import com.group17.seg.utility.Utility;
import com.group17.seg.view.AppWindow;
import com.group17.seg.view.BaseNode;
import com.group17.seg.view.CalcBreakdownUI;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.group17.seg.utility.Logger.exportViewFileName;

class RunwayCompare extends BaseNode {
    //<editor-fold desc="Attributes">
    private final VBox runwayCompareVBox;
    private final SimpleBooleanProperty smallerRunway;
    private final SimpleObjectProperty<PhysicalRunway.Runway> selectedView;
    private final SimpleObjectProperty<RunwayId> selectedRunwayId;
    private final ObstacleObservable selectedObstacle;
    private final PhysicalRunwayObservable selectedPhysicalRunway;
    private final SimpleObjectProperty<LogicalRunway> originalRunway;
    private final SimpleObjectProperty<LogicalRunway> recalculatedRunway;
    private final Alert exportConfirmation;
    private final Timeline exportTimeline;
    private final Desktop desktop;
    //</editor-fold>

    RunwayCompare(LoadAirportUI loadAirportUI) {
        super(loadAirportUI);
        this.runwayCompareVBox = new VBox(5);
        this.smallerRunway = loadAirportUI.smallerRunway;
        this.selectedView = loadAirportUI.selectedView;
        this.selectedRunwayId = loadAirportUI.selectedRunwayId;
        this.selectedObstacle = loadAirportUI.selectedObstacle;
        this.selectedPhysicalRunway = loadAirportUI.selectedPhysicalRunway;
        this.originalRunway = new SimpleObjectProperty<>();
        this.recalculatedRunway = new SimpleObjectProperty<>();
        this.exportConfirmation = new Alert(Alert.AlertType.INFORMATION);
        this.exportConfirmation.setHeaderText("Export Success");
        this.exportConfirmation.setContentText("Current view exported successfully.");
        this.exportTimeline = new Timeline(new KeyFrame(Duration.seconds(5), e -> this.exportConfirmation.close()));
        this.desktop = Desktop.getDesktop();
        this.initialise();
    }

    private void showConfirmation() {
        this.exportConfirmation.showAndWait();
        this.exportTimeline.play();
    }

    private void initialise() {
        ChangeListener<Object> compareTableListener = (observable, oldValue, newValue) -> {
            var view = this.selectedView.get();
            var physicalRunway = this.selectedPhysicalRunway.get();
            if (physicalRunway == null) {
                this.originalRunway.set(null);
                this.recalculatedRunway.set(null);
            } else {
                this.originalRunway.set(physicalRunway.getLogicalRunways(this.smallerRunway.get()).get(0));
                this.recalculatedRunway.set(physicalRunway.getLogicalRunways(this.smallerRunway.get()).get(this.selectedView.get().ordinal()));
            }
            if (newValue != null) {
                loadAirportUI.displayRightPane(true);
            }
            System.err.println(observable.toString());
        };
        ChangeListener<Object> drawRunwayListener = (observable, oldValue, newValue) -> {
            var view = this.selectedView.get();
            var physicalRunway = this.selectedPhysicalRunway.get();
            loadAirportUI.displayRightPane(physicalRunway != null);
            System.err.println(observable.toString());
        };
        this.smallerRunway.addListener(compareTableListener);
        this.selectedView.addListener(compareTableListener);
        this.selectedRunwayId.addListener(compareTableListener);
        this.selectedObstacle.addListener(compareTableListener);
        this.selectedObstacle.addListener(drawRunwayListener);
        this.selectedPhysicalRunway.addListener(drawRunwayListener);
        this.recalculatedRunway.addListener(e -> {
            if (this.recalculatedRunway.get() != null)
                Logger.writeToLogFile(String.format("Runway Parameters Updated: %s", this.recalculatedRunway.get().log()));
        });
    }

    private VBox labelledVBox(Label label, String style) {
        var output = new VBox();
        Utility.setStyle(output, style);
        output.getChildren().add(label);
        return output;
    }

    private VBox runwayNumberVBox() {
        var runwayNumberLabel = new Label();
        runwayNumberLabel.getStyleClass().setAll("label-compare-table-firstrow");
        runwayNumberLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            var runwayId = this.selectedRunwayId.get();
            if (runwayId == null) {
                return "";
            }
            if (this.smallerRunway.get()) {
                return runwayId.toStringSmaller();
            } else {
                return runwayId.toStringLarger();
            }
        }, this.smallerRunway, this.selectedRunwayId));
        var runwayNumberVBox = this.labelledVBox(runwayNumberLabel, "vbox-compare-table-firstrow");
        runwayNumberVBox.getStyleClass().add("vbox-compare-table-topleft");
        return runwayNumberVBox;
    }

    private List<VBox> runwayCategoryVBoxes() {
        var categoryNames = new String[]{"TODA", "ASDA", "TORA", "LDA", "DT"};
        List<VBox> output = new ArrayList<>();
        for (int i = 0; i < categoryNames.length; i++) {
            VBox vbox;
            var label = new Label();
            label.setText(categoryNames[i]);
            if (i % 2 == 0) {
                Utility.setStyle(label, "label-compare-table-even");
                vbox = this.labelledVBox(label, "vbox-compare-table-even");
            } else {
                Utility.setStyle(label, "label-compare-table-odd");
                vbox = this.labelledVBox(label, "vbox-compare-table-odd");
            }
            if (i == 4) {
                vbox.getStyleClass().add("vbox-compare-table-bottomleft");
            }
            output.add(vbox);
        }
        return output;
    }

    private List<VBox> originalRunwayParameterVBoxes() {
        List<VBox> output = new ArrayList<>();

        var originalLabel = new Label("Original");
        Utility.setStyle(originalLabel, "label-compare-table-firstrow");
        output.add(this.labelledVBox(originalLabel, "vbox-compare-table-firstrow"));

        for (int i = 0; i < 5; i++) {
            VBox vbox;
            var label = new Label();
            int index = i;
            label.textProperty().bind(Bindings.createStringBinding(() -> {
                var originalRunway = this.originalRunway.get();
                if (originalRunway == null) return "";
                return originalRunway.getCompareValues().get(index);
            }, this.originalRunway, this.selectedObstacle));
            if (i % 2 == 0) {
                Utility.setStyle(label, "label-compare-table-even");
                vbox = this.labelledVBox(label, "vbox-compare-table-even");
            } else {
                Utility.setStyle(label, "label-compare-table-odd");
                vbox = this.labelledVBox(label, "vbox-compare-table-odd");
            }
            output.add(vbox);
        }
        return output;
    }

    private List<VBox> recalculatedRunwayParameterVBoxes() {
        List<VBox> output = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            VBox vbox;
            var label = new Label();
            int index = i;
            label.textProperty().bind(Bindings.createStringBinding(() -> {
                var recalculatedRunway = this.recalculatedRunway.get();
                if (recalculatedRunway == null) return "";
                return recalculatedRunway.getCompareValues().get(index);
            }, this.recalculatedRunway, this.selectedObstacle));
            if (i == 0) {
                Utility.setStyle(label, "label-compare-table-firstrow");
                vbox = this.labelledVBox(label, "vbox-compare-table-firstrow");
                vbox.getStyleClass().add("vbox-compare-table-topright");
            } else if (i % 2 == 1) {
                Utility.setStyle(label, "label-compare-table-even");
                vbox = this.labelledVBox(label, "vbox-compare-table-even");
            } else {
                Utility.setStyle(label, "label-compare-table-odd");
                vbox = this.labelledVBox(label, "vbox-compare-table-odd");
            }
            if (i == 5) vbox.getStyleClass().add("vbox-compare-table-bottomright");
            output.add(vbox);
        }
        return output;
    }

    private GridPane runwayCompareGridPane() {
        var runwayCompareGridPane = new GridPane();
        Utility.setStyle(runwayCompareGridPane, "gridpane-runway-compare");

        var constraintLeft = new ColumnConstraints(100, 100, 100, Priority.NEVER, HPos.CENTER, true);
        var constraintMiddle = new ColumnConstraints(200, 200, 200, Priority.NEVER, HPos.CENTER, true);
        var constraintRight = new ColumnConstraints(200, 200, 200, Priority.NEVER, HPos.CENTER, true);
        runwayCompareGridPane.getColumnConstraints().addAll(constraintLeft, constraintMiddle, constraintRight);

        // Add VBoxes to the GridPane
        runwayCompareGridPane.add(this.runwayNumberVBox(), 0, 0);

        var runwayCategoryVBoxes = this.runwayCategoryVBoxes();
        for (int i = 0; i < runwayCategoryVBoxes.size(); i++) {
            runwayCompareGridPane.add(runwayCategoryVBoxes.get(i), 0, i + 1);
        }

        var originalRunwayVBoxes = this.originalRunwayParameterVBoxes();
        for (int i = 0; i < originalRunwayVBoxes.size(); i++) {
            runwayCompareGridPane.add(originalRunwayVBoxes.get(i), 1, i);
        }

        var recalculatedRunwayParameterVBoxes = this.recalculatedRunwayParameterVBoxes();
        for (int i = 0; i < recalculatedRunwayParameterVBoxes.size(); i++) {
            runwayCompareGridPane.add(recalculatedRunwayParameterVBoxes.get(i), 2, i);
        }

        return runwayCompareGridPane;
    }

    private Button displayCalcBreakdownButton() {
        var displayCalcBreakdownButton = new Button("View Calculation Breakdown");
        Utility.setStyle(displayCalcBreakdownButton, "button-compare-table-show-breakdown");
        displayCalcBreakdownButton.prefWidthProperty().bind(this.runwayCompareVBox.widthProperty().multiply(0.75));
        displayCalcBreakdownButton.disableProperty().bind(selectedObstacle.isNull());

        displayCalcBreakdownButton.setOnAction(e -> {
            var stage = new Stage();
            var newWindow = new AppWindow(stage, 1050, 650);
            var physicalRunway = this.loadAirportUI.airport.getPhysicalRunway(this.selectedRunwayId.get());
            newWindow.loadUI(new CalcBreakdownUI(newWindow, physicalRunway, this.selectedView.get(), this.smallerRunway.get()));
            stage.show();
        });

        return displayCalcBreakdownButton;
    }

    private Button exportSelectedViewButton() {
        var exportSelectedViewButton = new Button("Export View");
        Utility.setStyle(exportSelectedViewButton, "button-compare-table-show-breakdown");
        exportSelectedViewButton.prefWidthProperty().bind(this.runwayCompareVBox.widthProperty().multiply(0.25));

        var exportSelectedViewContextMenu = new ContextMenu();
        var exportTXT = new MenuItem("Export as TXT");
        var exportPNG = new MenuItem("Export as PNG");
        var exportJPG = new MenuItem("Export as JPG");
        var exportGIF = new MenuItem("Export as GIF");
        var openFolder = new MenuItem("Open Folder");

        exportTXT.setOnAction(e -> {
            var currentView = new ArrayList<String>();
            if (this.selectedObstacle.get() == null) {
                var originalRunway = this.originalRunway.get();
                currentView.add(String.format("Airport - %s", this.loadAirportUI.airport.getName()));
                currentView.add(String.format("Current Runway - %s", this.selectedRunwayId.get().toString()));
                currentView.add(String.format("Current Logical Runway - %s", this.selectedRunwayId.get().toString(this.smallerRunway.get())));
                currentView.add("Current Obstacle - N/A");
                currentView.add(String.format(originalRunway.log()));
            } else {
                var runways = this.selectedPhysicalRunway.get().getLogicalRunways(this.smallerRunway.get());
                currentView.add(String.format("Airport - %s", this.loadAirportUI.airport.getName()));
                currentView.add(String.format("Current Runway - %s", this.selectedRunwayId.get().toString()));
                currentView.add(String.format("Current Logical Runway - %s", this.selectedRunwayId.get().toString(this.smallerRunway.get())));
                currentView.add(String.format("Current Obstacle - %s",
                                              this.selectedPhysicalRunway.get().getObstacle(this.smallerRunway.get()).log()));
                runways.forEach(runway -> currentView.add(runway.log()));
            }
            Logger.exportCurrentView(currentView);
            var folder = new File(Logger.exportedViewFolderPath + "/calculations");
            try {
                this.desktop.open(folder);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        exportPNG.setOnAction(e -> {
            // Write scene to file
            try {
                var bi = SwingFXUtils.fromFXImage(currentSceneToImage(), null);
                var f = new File(String.format("%s%s", exportViewFileName, ".png"));
                f.createNewFile();
                ImageIO.write(bi, "png", f);
                var folder = new File(Logger.exportedViewFolderPath + "/screenshots");
                this.desktop.open(folder);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        exportJPG.setOnAction(e -> {
            // Write scene to file
            try {
                var bi = SwingFXUtils.fromFXImage(currentSceneToImage(), null);
                var f = new File(String.format("%s%s", exportViewFileName, ".jpg"));
                var width = bi.getWidth();
                var height = bi.getHeight();
                var pixels = new int[width * height];
                bi.getRGB(0, 0, width, height, pixels, 0, width);
                var newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                newImage.setRGB(0, 0, width, height, pixels, 0, width);
                f.createNewFile();
                ImageIO.write(newImage, "jpg", f);
                var folder = new File(Logger.exportedViewFolderPath + "/screenshots");
                this.desktop.open(folder);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        exportGIF.setOnAction(e -> {
            // Write scene to file
            try {
                var bi = SwingFXUtils.fromFXImage(currentSceneToImage(), null);
                var f = new File(String.format("%s%s", exportViewFileName, ".gif"));
                f.createNewFile();
                ImageIO.write(bi, "gif", f);
                var folder = new File(Logger.exportedViewFolderPath + "/screenshots");
                this.desktop.open(folder);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        openFolder.setOnAction(e -> {
            try {
                var folder = new File(Logger.exportedViewFolderPath);
                this.desktop.open(folder);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        exportSelectedViewContextMenu.getItems().addAll(exportTXT, exportPNG, exportJPG, exportGIF, openFolder);

        exportSelectedViewButton.setOnMouseClicked(e -> {
            exportSelectedViewContextMenu.show(exportSelectedViewButton, e.getScreenX(), e.getScreenY());
        });

        return exportSelectedViewButton;
    }

    private WritableImage currentSceneToImage() {
        int width = (int) this.loadAirportUI.getPrimaryStage().getScene().getWidth();
        int height = (int) this.loadAirportUI.getPrimaryStage().getScene().getHeight();
        var writableImage = new WritableImage(width, height);
        this.loadAirportUI.window.getPrimaryStage().getScene().snapshot(writableImage);

        return writableImage;
    }

    private HBox runwayCompareButtonsHBox() {
        var runwayCompareButtonsHBox = new HBox(10);

        runwayCompareButtonsHBox.getChildren().add(this.displayCalcBreakdownButton());
        runwayCompareButtonsHBox.getChildren().add(this.exportSelectedViewButton());

        return runwayCompareButtonsHBox;
    }

    @Override
    public Node getNode() {
        this.runwayCompareVBox.setId("runwayCompareVBox");
        this.runwayCompareVBox.setPadding(new Insets(5));

        this.runwayCompareVBox.getChildren().add(this.runwayCompareGridPane());
        this.runwayCompareVBox.getChildren().add(this.runwayCompareButtonsHBox());

        return this.runwayCompareVBox;
    }
}
