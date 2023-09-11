package com.group17.seg.view.loadairport;

import com.group17.seg.io.ObstacleXMLParser;
import com.group17.seg.model.Obstacle;
import com.group17.seg.model.ObstacleList;
import com.group17.seg.model.ObstacleObservable;
import com.group17.seg.model.PhysicalRunwayObservable;
import com.group17.seg.nodes.BetterToggleButton;
import com.group17.seg.utility.Logger;
import com.group17.seg.utility.Utility;
import com.group17.seg.view.BaseNode;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.PseudoClass;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

class ObstacleSelection extends BaseNode {
    //<editor-fold desc="Attributes">
    private final GridPane leftPane;
    private final SimpleBooleanProperty smallerRunway;
    private final ObstacleObservable selectedObstacle;
    private final ObstacleObservable currentlySelectedObstacle;
    private final ObstacleObservable newObstacle;
    private final PhysicalRunwayObservable selectedPhysicalRunway;
    private final ToggleGroup obstacleSelectionToggleGroup;
    private final Alert obstacleParameterAlert;
    private UnaryOperator<TextFormatter.Change> doubleFilter;
    private UnaryOperator<TextFormatter.Change> signedDoubleFilter;
    private final Alert xmlErrorAlert;
    private final Timeline xmlErrorTimeline;
    //</editor-fold>

    ObstacleSelection(LoadAirportUI loadAirportUI) {
        super(loadAirportUI);
        this.leftPane = (GridPane) loadAirportUI.mainBorderPane.getLeft();
        this.smallerRunway = loadAirportUI.smallerRunway;
        this.selectedObstacle = loadAirportUI.selectedObstacle;
        this.currentlySelectedObstacle = new ObstacleObservable();
        this.newObstacle = loadAirportUI.newObstacle;
        this.selectedPhysicalRunway = loadAirportUI.selectedPhysicalRunway;
        this.obstacleSelectionToggleGroup = loadAirportUI.obstacleSelectionToggleGroup;
        this.obstacleParameterAlert = new Alert(Alert.AlertType.ERROR);
        this.obstacleParameterAlert.setHeaderText("Obstacle Error");
        this.obstacleParameterAlert.setContentText("The obstacle is invalid.");
        this.xmlErrorAlert = new Alert(Alert.AlertType.ERROR);
        this.xmlErrorAlert.setHeaderText("XML Unmarshalling Error");
        this.xmlErrorAlert.setContentText("The XML file was invalid.");
        this.xmlErrorTimeline = new Timeline(new KeyFrame(Duration.seconds(5), e -> this.xmlErrorAlert.close()));
        PseudoClass error = PseudoClass.getPseudoClass("error");
        this.initialise();
    }

    private void showError() {
        this.xmlErrorAlert.show();
        this.xmlErrorTimeline.play();
    }

    private void initialise() {
        // Create the Filters
        this.doubleFilter = text -> {
            var string = text.getText();
            if (string.matches("[0-9.]*")) {
                return text;
            } else if (text.getText().isEmpty()) {
                return text;
            }
            return null;
        };
        this.signedDoubleFilter = text -> {
            var string = text.getText();
            if (string.matches("[\\-0-9.]*")) {
                return text;
            } else if (text.getText().isEmpty()) {
                return text;
            }
            return null;
        };
        this.newObstacle.addListener(e -> {
            if (this.newObstacle.get() == null) {
                this.leftPane.lookup("#obstacleAddButtonsHBox").toFront();
                this.obstacleSelectionToggleGroup.selectToggle(null);
                return;
            }
            var obstacleListVBox = (VBox) this.leftPane.lookup("#obstacleListVBox");
            for (var button : obstacleListVBox.lookupAll("#" + this.newObstacle.get().getName().replaceAll("\\s", ""))) {
                obstacleListVBox.getChildren().remove(button);
            }
            this.addToggleButtonToObstacleList(new ObstacleList(this.newObstacle.get()), obstacleListVBox);
            try {
                ((BetterToggleButton) obstacleListVBox.lookup("#" + this.newObstacle.get().getName().replaceAll("\\s", ""))).setSelected(true);
            } catch (Exception err) {
                err.printStackTrace();
            }
            this.selectedObstacle.set(this.newObstacle.get());
            this.currentlySelectedObstacle.set(null);
            this.leftPane.lookup("#obstacleEditButtonsHBox").toFront();
        });
    }

    /**
     * Updates the values in the TextFields of the Obstacle Parameter table. Call this function when the "New"
     * or "Edit" obstacle button is used.
     */
    private void updateObstacleParameterTable() {
        var name = (TextField) this.leftPane.lookup("#nameTextField");
        var x = (TextField) this.leftPane.lookup("#xTextField");
        var y = (TextField) this.leftPane.lookup("#yTextField");
        var length = (TextField) this.leftPane.lookup("#lengthTextField");
        var width = (TextField) this.leftPane.lookup("#widthTextField");
        var height = (TextField) this.leftPane.lookup("#heightTextField");

        var textFields = new ArrayList<>(Arrays.asList(x, y, length, width, height, name));
        var obstacle = this.selectedObstacle.get();
        if (obstacle == null) {
            textFields.forEach(textField -> textField.setText(""));
            return;
        }

        if (x.getText().isEmpty()) {
            x.setText(String.valueOf(obstacle.getX()));
        }
        if (y.getText().isEmpty()) {
            y.setText(String.valueOf(obstacle.getY()));
        }
        name.setText(obstacle.getName());
        length.setText(String.valueOf(obstacle.getLength()));
        width.setText(String.valueOf(obstacle.getWidth()));
        height.setText(String.valueOf(obstacle.getHeight()));
    }

    /**
     * Creates a new obstacle from the numbers in the Obstacle Parameter table, and creates a ToggleButton with it.
     * If the name exists already, replaces the existing button.
     *
     * @return True if it has succeeded.
     */
    private boolean saveObstacleParameters() {
        var xString = ((TextField) this.leftPane.lookup("#xTextField")).getText();
        var yString = ((TextField) this.leftPane.lookup("#yTextField")).getText();
        var lengthString = ((TextField) this.leftPane.lookup("#lengthTextField")).getText();
        var widthString = ((TextField) this.leftPane.lookup("#widthTextField")).getText();
        var heightString = ((TextField) this.leftPane.lookup("#heightTextField")).getText();
        var nameString = ((TextField) this.leftPane.lookup("#nameTextField")).getText();

        // Guards to check the strings are in the right format
        String[] obstacleParameters = new String[]{xString, yString, lengthString, widthString, heightString, nameString};
        // Checks that all TextFields are filled in.
        for (var parameter : obstacleParameters) {
            if (parameter.isEmpty()) {
                this.obstacleParameterAlert.setContentText("Some parameters were not filled.");
                return false;
            }
        }

        // Validates the Inputs
        if (!xString.matches("(-?\\d+\\.?\\d*)(?<!-\\d)")) {
            this.obstacleParameterAlert.setContentText("The X value is the wrong format.");
            return false;
        } else if (!yString.matches("(-?\\d+\\.?\\d*)(?<!-\\d)")) {
            this.obstacleParameterAlert.setContentText("The Y value is the wrong format.");
            return false;
        } else if (!lengthString.matches("(\\d*.?\\d*)")) {
            this.obstacleParameterAlert.setContentText("The length value is the wrong format.");
            return false;
        } else if (!widthString.matches("(\\d*.?\\d*)")) {
            this.obstacleParameterAlert.setContentText("The width value is the wrong format.");
            return false;
        } else if (!heightString.matches("(\\d*.?\\d*)")) {
            this.obstacleParameterAlert.setContentText("The height value is the wrong format.");
            return false;
        }

        var xValue = Double.parseDouble(xString);
        var yValue = Double.parseDouble(yString);
        var lengthValue = Double.parseDouble(lengthString);
        var widthValue = Double.parseDouble(widthString);
        var heightValue = Double.parseDouble(heightString);

        // Validates the Obstacle will affect the recalculations
        // Validation for Landing Over
        if (xValue < 0 && Math.min(heightValue * 50 + 60, 310) < Math.abs(xValue) - lengthValue / 2) {
            this.obstacleParameterAlert.setContentText("The x coordinate is too far off the runway.");
            return false;
        }
        // Validation for Take Off Away From
        else if (xValue < 0 && 500 < Math.abs(xValue)) {
            this.obstacleParameterAlert.setContentText("The x coordinate is too far off the runway.");
            return false;
        }
        // Validation for Take Off Towards
        else if (xValue > 0 && Math.min(heightValue * 50 + 60,
                                        310) < Math.abs(xValue) - lengthValue / 2 - this.selectedPhysicalRunway.get()
                                                                                                               .getLogicalRunways(this.smallerRunway.get())
                                                                                                               .get(
                                                                                                                       0)
                                                                                                               .getTora()) {
            this.obstacleParameterAlert.setContentText("The x coordinate is too far off the runway.");
            return false;
        }
        // Validation for Landing Towards
        else if (xValue > 0 && 300 < Math.abs(xValue) - lengthValue / 2 - this.selectedPhysicalRunway.get()
                                                                                                     .getLogicalRunways(this.smallerRunway.get())
                                                                                                     .get(
                                                                                                             0)
                                                                                                     .getTora()) {
            this.obstacleParameterAlert.setContentText("The x coordinate is too far off the runway.");
            return false;
        }
        // Validation for the Y position
        else if (Math.abs(yValue) - widthValue / 2 > 75) {
            this.obstacleParameterAlert.setContentText("The y coordinate is too far off the runway.");
            return false;
        }

        Obstacle newObstacle = new Obstacle(xValue, yValue, lengthValue, widthValue, heightValue, nameString);
        this.selectedPhysicalRunway.get().setObstacle(newObstacle, this.smallerRunway.get());
        this.selectedObstacle.set(newObstacle);

        var obstacleListVBox = (VBox) this.leftPane.lookup("#obstacleListVBox");
        try {
            obstacleListVBox.getChildren().remove(obstacleListVBox.lookup("#" + nameString.replaceAll("\\s", "")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            this.addToggleButtonToObstacleList(new ObstacleList(newObstacle), obstacleListVBox);
            ((BetterToggleButton) obstacleListVBox.lookup("#" + nameString.replaceAll("\\s", ""))).setSelected(true);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        if (Logger.isLogging()) {
            Logger.writeToLogFile(String.format("Obstacle Added to Runway: %s", newObstacle.log()));
        }
        this.loadAirportUI.showNotification(String.format("%s Saved Successfully", nameString));
        return true;
    }

    /**
     * @param arg Boolean to decide to display the entire Runway VBox or not.
     */
    private void displayRunwayVBox(boolean arg) {
        this.leftPane.lookup("#runwaySelectionVBox").setVisible(arg);
    }

    /**
     * Creates buttons from the obstacleList and adds it to the provided VBox.
     *
     * @param obstacleList          List of Obstacle Objects
     * @param obstacleSelectionList VBox to add the button to
     */
    private void addToggleButtonToObstacleList(ObstacleList obstacleList, VBox obstacleSelectionList) {
        for (var obstacle : obstacleList) {
            var button = new BetterToggleButton(obstacle.getName(),
                                                "listItem",
                                                this.obstacleSelectionToggleGroup,
                                                obstacle.getName().replaceAll("\\s", ""));
            button.prefWidthProperty().bind(obstacleSelectionList.widthProperty());

            button.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
                if (e.getButton() != MouseButton.PRIMARY) {
                    e.consume();
                }
            });

            button.setOnMouseClicked(e -> {
                // Handles double click if the selected object is the one on the runway
                if (e.getClickCount() % 2 == 0 && this.selectedObstacle.get() == obstacle) {
                    Logger.writeToLogFile(String.format("Obstacle Removed from Runway: %s", this.selectedObstacle.get().log()));
                    this.selectedPhysicalRunway.get().setObstacle(null, this.smallerRunway.get());
                    this.selectedObstacle.set(null);
                    this.selectedObstacle.invalidate();
                    this.leftPane.lookup("#obstacleAddButtonsHBox").toFront();
                    button.setSelected(false);
                    this.loadAirportUI.showNotification("Obstacle Deselected");
                }
                // Handles double click to replace the current runway object
                else if (e.getClickCount() % 2 == 0) {
                    if (this.selectedObstacle.get() != null) {
                        obstacle.setX(this.selectedObstacle.get().getX());
                        obstacle.setY(this.selectedObstacle.get().getY());
                    }
                    this.selectedPhysicalRunway.get().setObstacle(obstacle, this.smallerRunway.get());
                    this.selectedObstacle.set(obstacle);
                    this.currentlySelectedObstacle.set(null);
                    this.leftPane.lookup("#obstacleEditButtonsHBox").toFront();
                    if (Logger.isLogging()) {
                        Logger.writeToLogFile(String.format("Obstacle Added to Runway: %s", obstacle.log()));
                    }
                    this.loadAirportUI.showNotification(String.format("Displaying %s at (%.0f, %.0f)",
                                                                      obstacle.getName(),
                                                                      obstacle.getX(),
                                                                      obstacle.getY()));
                    button.setSelected(true);
                }
                // If an obstacle is currently selected and on the runway, disable single clicks.
                else if (this.selectedObstacle.get() != null) {
                    this.currentlySelectedObstacle.set(null);
                    e.consume();
                }
                // Deals with single-clicks when nothing is "selected".
                else if (button.isSelected()) {
                    this.currentlySelectedObstacle.set(null);
                    button.setSelected(false);
                } else {
                    this.currentlySelectedObstacle.set(obstacle);
                    button.setSelected(true);
                }
            });
            obstacleSelectionList.getChildren().add(0, button);
        }
    }

    /**
     * @return Label that says "Obstacle"
     */
    private Label obstacleTableTitle() {
        var obstacleTitle = new Label();
        Utility.setStyle(obstacleTitle, "label-runway-selection-title");
        obstacleTitle.prefWidthProperty().bind(leftPane.widthProperty());
        obstacleTitle.textProperty().bind(Bindings.createStringBinding(() -> {
            var selectedObstacle = this.selectedObstacle.get();
            if (selectedObstacle == null) {
                return "Obstacle";
            } else {
                return selectedObstacle.getName();
            }
        }, selectedObstacle));
        return obstacleTitle;
    }

    private ScrollPane obstacleListScrollPane() {
        // Obstacle Selection List
        var obstacleListVBox = new VBox();
        obstacleListVBox.setAlignment(Pos.TOP_CENTER);
        obstacleListVBox.setMaxWidth(Double.MAX_VALUE);
        obstacleListVBox.setFillWidth(true);
        obstacleListVBox.setId("obstacleListVBox");
        obstacleListVBox.setPadding(new Insets(1));

        this.addToggleButtonToObstacleList(this.loadAirportUI.obstacleList, obstacleListVBox);

        // Obstacle Selection ScrollPane
        var obstacleListScrollPane = new ScrollPane(obstacleListVBox);
        Utility.setStyle(obstacleListScrollPane, "scroll-pane-runway-selection");
        obstacleListScrollPane.prefHeightProperty().bind(leftPane.heightProperty());
        obstacleListScrollPane.setId("obstacleListScrollPane");

        return obstacleListScrollPane;
    }

    private VBox obstacleParameterVBox() {
        var obstacleParameterVBox = new VBox(5);
        VBox.setVgrow(obstacleParameterVBox, Priority.SOMETIMES);
        obstacleParameterVBox.setId("obstacleParameterVBox");
        obstacleParameterVBox.managedProperty().bind(obstacleParameterVBox.visibleProperty());
        obstacleParameterVBox.prefWidthProperty().bind(leftPane.widthProperty());
        obstacleParameterVBox.setVisible(false);

        // GridPane with 8 rows and 2 columns
        var obstacleParameterGridPane = new GridPane();
        obstacleParameterGridPane.prefHeightProperty().bind(leftPane.heightProperty());
        obstacleParameterGridPane.setPadding(new Insets(20, 10, 20, 10));
        obstacleParameterGridPane.setStyle("-fx-background-color: white");
        obstacleParameterGridPane.setHgap(10);
        obstacleParameterGridPane.setVgap(5);

        // Row Constraints
        for (int i = 0; i < 8; i++) {
            obstacleParameterGridPane.getRowConstraints().add(Utility.rowConstraint());
        }

        // Column Constraints
        var columnConstraintLeft = new ColumnConstraints();
        var columnConstraintRight = new ColumnConstraints();
        columnConstraintLeft.setHgrow(Priority.SOMETIMES);
        columnConstraintLeft.setHalignment(HPos.CENTER);
        columnConstraintRight.setHgrow(Priority.SOMETIMES);
        columnConstraintRight.setHalignment(HPos.CENTER);
        obstacleParameterGridPane.getColumnConstraints().addAll(columnConstraintLeft, columnConstraintRight);

        // Name
        var nameLabel = new Label("NAME");
        Utility.setStyle(nameLabel, "label-obstacle-parameter-gridpane-small");
        var nameTextField = new TextField();
        Utility.setStyle(nameTextField, "textfield-obstacle-parameter-gridpane");
        nameTextField.setId("nameTextField");

        // Name VBox
        var nameVBox = new VBox();
        nameVBox.setSpacing(5);
        nameVBox.setAlignment(Pos.CENTER);
        nameVBox.getChildren().addAll(nameLabel, nameTextField);
        obstacleParameterGridPane.add(nameVBox, 0, 0, 2, 1);

        // Position
        var positionLabel = new Label("POSITION");
        Utility.setStyle(positionLabel, "label-obstacle-parameter-gridpane-small");
        obstacleParameterGridPane.add(positionLabel, 0, 1, 2, 1);

        var positionXHBox = new HBox();
        positionXHBox.setAlignment(Pos.CENTER);
        var xLabel = new Label("X(m):");
        Utility.setStyle(xLabel, "label-x-y");
        var xTextField = new TextField();
        Utility.setStyle(xTextField, "textfield-obstacle-parameter-gridpane");
        xTextField.setId("xTextField");
        xTextField.setTextFormatter(new TextFormatter<>(signedDoubleFilter));
        xTextField.textProperty().addListener(e -> {
            xTextField.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"),
                                               !xTextField.getText().isEmpty() && !xTextField.getText().matches(
                                                       "(-?\\d+\\.?\\d*)(?<!-\\d)"));
            xLabel.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"),
                                           !xTextField.getText().isEmpty() && !xTextField.getText().matches(
                                                   "(-?\\d+\\.?\\d*)(?<!-\\d)"));
        });


        positionXHBox.getChildren().addAll(xLabel, xTextField);
//        obstacleParameterGridPane.add(positionXHBox, 0, 2);

        var positionYHBox = new HBox();
        positionYHBox.setAlignment(Pos.CENTER);
        var yLabel = new Label("Y(m):");
        Utility.setStyle(yLabel, "label-x-y");
        var yTextField = new TextField();
        Utility.setStyle(yTextField, "textfield-obstacle-parameter-gridpane");
        yTextField.setId("yTextField");
        yTextField.setTextFormatter(new TextFormatter<>(signedDoubleFilter));
        yTextField.textProperty().addListener(e -> {
            yTextField.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"),
                                               !yTextField.getText().isEmpty() && !yTextField.getText()
                                                                                             .matches(
                                                                                                     "(-?\\d+\\.?\\d*)(?<!-\\d)"));
            yLabel.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"),
                                           !yTextField.getText().isEmpty() && !yTextField.getText()
                                                                                         .matches(
                                                                                                 "(-?\\d+\\.?\\d*)(?<!-\\d)"));
        });
        positionYHBox.getChildren().addAll(yLabel, yTextField);

        var vboxXY = new VBox();
        vboxXY.setSpacing(10);
        vboxXY.getChildren().add(positionXHBox);
        vboxXY.getChildren().add(positionYHBox);


        obstacleParameterGridPane.add(vboxXY, 0, 2, 2, 1);

        obstacleParameterVBox.getChildren().add(obstacleParameterGridPane);

        var positionExplainLabel = new Label();
        positionExplainLabel.setWrapText(true);
        positionExplainLabel.setText("X = HORIZONTAL DISTANCE FROM DT\nY = VERTICAL DISTANCE FROM CENTERLINE");
        Utility.setStyle(positionExplainLabel, "label-obstacle-parameter-gridpane-smaller");

        obstacleParameterGridPane.add(positionExplainLabel, 0, 3, 2, 1);

        // Properties
        var propertiesLabel = new Label("PROPERTIES");
        Utility.setStyle(propertiesLabel, "label-obstacle-parameter-gridpane-small");
        obstacleParameterGridPane.add(propertiesLabel, 0, 4, 2, 1);

        var lengthLabel = new Label("LENGTH(m):");
        Utility.setStyle(lengthLabel, "label-obstacle-parameter-gridpane");
        obstacleParameterGridPane.add(lengthLabel, 0, 5);
        var lengthTextField = new TextField();
        Utility.setStyle(lengthTextField, "textfield-obstacle-parameter-gridpane");
        lengthTextField.setId("lengthTextField");
        lengthTextField.setTextFormatter(new TextFormatter<>(doubleFilter));
        lengthTextField.textProperty().addListener(e -> {
            lengthTextField.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"),
                                                    !lengthTextField.getText()
                                                                    .isEmpty() && !lengthTextField.getText()
                                                                                                  .matches(
                                                                                                          "(\\d*.?\\d*)"));
            lengthLabel.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"),
                                                !lengthTextField.getText()
                                                                .isEmpty() && !lengthTextField.getText()
                                                                                              .matches(
                                                                                                      "(\\d*.?\\d*)"));
        });

        obstacleParameterGridPane.add(lengthTextField, 1, 5);

        var widthLabel = new Label("WIDTH(m):");
        Utility.setStyle(widthLabel, "label-obstacle-parameter-gridpane");
        obstacleParameterGridPane.add(widthLabel, 0, 6);
        var widthTextField = new TextField();
        Utility.setStyle(widthTextField, "textfield-obstacle-parameter-gridpane");
        widthTextField.setId("widthTextField");
        widthTextField.setTextFormatter(new TextFormatter<>(doubleFilter));
        widthTextField.textProperty().addListener(e -> {
            widthTextField.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"),
                                                   !widthTextField.getText()
                                                                  .isEmpty() && !widthTextField.getText()
                                                                                               .matches(
                                                                                                       "(\\d*.?\\d*)"));
            widthLabel.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"),
                                               !widthTextField.getText()
                                                              .isEmpty() && !widthTextField.getText()
                                                                                           .matches(
                                                                                                   "(\\d*.?\\d*)"));
        });

        obstacleParameterGridPane.add(widthTextField, 1, 6);

        var heightLabel = new Label("HEIGHT(m):");
        Utility.setStyle(heightLabel, "label-obstacle-parameter-gridpane");
        obstacleParameterGridPane.add(heightLabel, 0, 7);
        var heightTextField = new TextField();
        Utility.setStyle(heightTextField, "textfield-obstacle-parameter-gridpane");
        heightTextField.setId("heightTextField");
        heightTextField.setTextFormatter(new TextFormatter<>(doubleFilter));
        heightTextField.textProperty().addListener(e -> {
            heightTextField.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"),
                                                    !heightTextField.getText()
                                                                    .isEmpty() && !heightTextField.getText()
                                                                                                  .matches(
                                                                                                          "(\\d*.?\\d*)"));
            heightLabel.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"),
                                                !heightTextField.getText()
                                                                .isEmpty() && !heightTextField.getText()
                                                                                              .matches(
                                                                                                      "(\\d*.?\\d*)"));
        });
        obstacleParameterGridPane.add(heightTextField, 1, 7);

        return obstacleParameterVBox;
    }

    private StackPane obstacleSelectionContentsStackPane() {
        var obstacleParameterStackPane = new StackPane();
        obstacleParameterStackPane.getChildren().add(this.obstacleListScrollPane());
        obstacleParameterStackPane.getChildren().add(this.obstacleParameterVBox());
        return obstacleParameterStackPane;
    }

    private HBox obstacleAddButtonsHBox() {
        var obstacleAddButtonsHBox = this.newSelectionButtonHBox("obstacleAddButtonsHBox");

        var addObstacleButton = new Button("New");
        addObstacleButton.setOnAction(e -> {
            this.leftPane.lookup("#saveButtonsHBox").toFront();
            this.leftPane.lookup("#obstacleParameterVBox").setVisible(true);
            ((Button) this.leftPane.lookup("#saveObstacleButton")).setDefaultButton(true);
            this.updateObstacleParameterTable();
            this.displayRunwayVBox(false);
            this.leftPane.lookup("#nameTextField").requestFocus();
            this.loadAirportUI.showNotification("New Obstacle");
        });
        obstacleAddButtonsHBox.getChildren().add(addObstacleButton);

        var importObstacleButton = new Button("Import");
        importObstacleButton.setOnAction(e -> {
            var stage = new Stage();
            stage.setAlwaysOnTop(true);
            var file = this.loadAirportUI.fileChooser.showOpenDialog(stage);
            if (file == null) return;
            try {
                var obstacles = ObstacleXMLParser.parseObstacleXML(file);
                var obstacleSelectionList = (VBox) this.leftPane.lookup("#obstacleListVBox");
                this.addToggleButtonToObstacleList(obstacles, obstacleSelectionList);
                this.loadAirportUI.obstacleList.addAll(0, obstacles);
                Logger.writeToLogFile(String.format("Obstacle(s) Imported: %s",
                                                    obstacles.stream().map(Obstacle::log).collect(Collectors.joining(", "))));
                this.loadAirportUI.showNotification(String.format("%d Obstacle%s Imported", obstacles.size(), (obstacles.size() != 0) ? "s" : ""));
            } catch (NullPointerException | JAXBException ex) {
                this.showError();
            }
        });
        obstacleAddButtonsHBox.getChildren().add(importObstacleButton);

        var displayObstacleButton = new Button("Display");
        displayObstacleButton.disableProperty().bind(this.currentlySelectedObstacle.isNull());
        displayObstacleButton.setOnAction(e -> {
            this.selectedPhysicalRunway.get().setObstacle(this.currentlySelectedObstacle.get(), this.smallerRunway.get());
            this.selectedObstacle.set(this.currentlySelectedObstacle.get());
            this.currentlySelectedObstacle.set(null);
            this.leftPane.lookup("#obstacleEditButtonsHBox").toFront();
            Logger.writeToLogFile(String.format("Obstacle Added to Runway: %s", this.selectedObstacle.get().log()));
            this.loadAirportUI.showNotification(String.format("Displaying %s at (%.0f, %.0f)",
                                                              this.selectedObstacle.get().getName(),
                                                              this.selectedObstacle.get().getX(),
                                                              this.selectedObstacle.get().getY()));
        });
        obstacleAddButtonsHBox.getChildren().add(displayObstacleButton);

        for (Node child : obstacleAddButtonsHBox.getChildren()) {
            var button = (Button) child;
            Utility.setStyle(button, "basic-label-button");
        }
        return obstacleAddButtonsHBox;
    }

    private HBox obstacleEditButtonsHBox() {
        var obstacleEditButtonsHBox = this.newSelectionButtonHBox("obstacleEditButtonsHBox");

        var editObstacleButton = new Button("Edit");
        editObstacleButton.setOnAction(e -> {
            this.leftPane.lookup("#saveButtonsHBox").toFront();
            this.leftPane.lookup("#obstacleParameterVBox").setVisible(true);
            this.updateObstacleParameterTable();
            this.displayRunwayVBox(false);
            ((Button) this.leftPane.lookup("#saveObstacleButton")).setDefaultButton(true);
            this.leftPane.lookup("#nameTextField").requestFocus();
        });
        obstacleEditButtonsHBox.getChildren().add(editObstacleButton);

        var exportObstacleButton = new Button("Export");
        exportObstacleButton.setOnAction(e -> {
            var stage = new Stage();
            stage.setAlwaysOnTop(true);
            var file = this.loadAirportUI.fileChooser.showSaveDialog(stage);
            if (file != null) {
                var obstacleList = new ObstacleList();
                obstacleList.add(this.selectedObstacle.get());
                ObstacleXMLParser.writeObstaclesToXML(String.valueOf(file), obstacleList);
                Logger.writeToLogFile(String.format("Obstacle Exported: %s", this.selectedObstacle.get().log()));
                this.loadAirportUI.showNotification(String.format("%s Exported", this.selectedObstacle.get().getName()));
            }
        });
        obstacleEditButtonsHBox.getChildren().add(exportObstacleButton);

        var removeObstacleButton = new Button("Remove");
        removeObstacleButton.setOnAction(e -> {
            if (Logger.isLogging()) {
                Logger.writeToLogFile(String.format("Obstacle Removed from Runway: %s", this.selectedObstacle.get().log()));
            }
            this.loadAirportUI.showNotification(String.format("%s Removed from Runway", this.selectedObstacle.get().getName()));
            this.selectedPhysicalRunway.get().setObstacle(null, this.smallerRunway.get());
            this.selectedObstacle.set(null);
            this.leftPane.lookup("#obstacleAddButtonsHBox").toFront();
            this.obstacleSelectionToggleGroup.selectToggle(null);
        });
        obstacleEditButtonsHBox.getChildren().add(removeObstacleButton);

        for (Node child : obstacleEditButtonsHBox.getChildren()) {
            var button = (Button) child;
            Utility.setStyle(button, "basic-label-button");
        }

        return obstacleEditButtonsHBox;
    }

    private HBox saveButtonsHBox() {
        var saveButtonsHBox = this.newSelectionButtonHBox("saveButtonsHBox");

        var cancelObstacleButton = new Button("Cancel");
        cancelObstacleButton.setOnAction(e -> {
            if (this.selectedObstacle.get() == null) {
                this.leftPane.lookup("#obstacleAddButtonsHBox").toFront();
            } else {
                this.leftPane.lookup("#obstacleEditButtonsHBox").toFront();
            }
            this.leftPane.lookup("#obstacleParameterVBox").setVisible(false);
            this.displayRunwayVBox(true);
            this.loadAirportUI.showNotification("Obstacle Parameters Discarded");
        });
        saveButtonsHBox.getChildren().add(cancelObstacleButton);

        var saveObstacleButton = new Button("Save");
        saveObstacleButton.setId("saveObstacleButton");
        saveObstacleButton.setOnAction(e -> {
            if (!this.saveObstacleParameters()) {
                System.out.println("ALERT");
                obstacleParameterAlert.showAndWait();
                return;
            }
            this.leftPane.lookup("#obstacleEditButtonsHBox").toFront();
            this.leftPane.lookup("#obstacleParameterVBox").setVisible(false);
            this.displayRunwayVBox(true);
        });
        saveButtonsHBox.getChildren().add(saveObstacleButton);

        for (Node child : saveButtonsHBox.getChildren()) {
            var button = (Button) child;
            Utility.setStyle(button, "basic-label-button");
        }

        return saveButtonsHBox;
    }

    private StackPane obstacleButtonsStackPane() {
        var obstacleButtonsStackPane = new StackPane();
        obstacleButtonsStackPane.getChildren().add(this.saveButtonsHBox());
        obstacleButtonsStackPane.getChildren().add(this.obstacleEditButtonsHBox());
        obstacleButtonsStackPane.getChildren().add(this.obstacleAddButtonsHBox());
        return obstacleButtonsStackPane;
    }

    @Override
    public Node getNode() {
        var obstacleVBox = new VBox();
        Utility.setStyle(obstacleVBox, "boxes-load-airport-ui");
        VBox.setVgrow(obstacleVBox, Priority.ALWAYS);
        GridPane.setVgrow(obstacleVBox, Priority.ALWAYS);
        obstacleVBox.setId("obstacleVBox");
        obstacleVBox.managedProperty().bind(obstacleVBox.visibleProperty());
        obstacleVBox.prefWidthProperty().bind(leftPane.widthProperty());
        obstacleVBox.maxHeightProperty().bind(leftPane.heightProperty());
        obstacleVBox.prefHeightProperty().bind(leftPane.heightProperty());

        obstacleVBox.setVisible(false);

        obstacleVBox.getChildren().add(this.obstacleTableTitle());
        obstacleVBox.getChildren().add(this.obstacleSelectionContentsStackPane());
        obstacleVBox.getChildren().add(this.obstacleButtonsStackPane());

        return obstacleVBox;
    }
}
