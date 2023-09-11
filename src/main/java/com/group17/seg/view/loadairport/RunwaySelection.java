package com.group17.seg.view.loadairport;

import com.group17.seg.io.RunwayXMLParser;
import com.group17.seg.model.*;
import com.group17.seg.nodes.BetterToggleButton;
import com.group17.seg.utility.Logger;
import com.group17.seg.utility.Utility;
import com.group17.seg.view.BaseNode;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.PseudoClass;
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

class RunwaySelection extends BaseNode {
    //<editor-fold desc="Attributes">
    private final GridPane leftPane;
    private final ToggleGroup runwaySelectionToggleGroup;
    private final Airport airport;
    private final SimpleObjectProperty<RunwayId> selectedRunwayId;
    private final SimpleObjectProperty<RunwayId> currentlySelectedRunwayId;
    private final ObstacleObservable selectedObstacle;
    private final ObstacleObservable newObstacle;
    private final SimpleObjectProperty<LogicalRunway> leftRunway;
    private final SimpleObjectProperty<LogicalRunway> rightRunway;
    private final PhysicalRunwayObservable physicalRunway;
    private final Alert runwayParameterAlert;
    private final Alert duplicateRunwayBearingAlert;
    private final SimpleBooleanProperty smallerRunway;
    private UnaryOperator<TextFormatter.Change> bearingFilter;
    private UnaryOperator<TextFormatter.Change> doubleFilter;
    private TextField bearingLeft;
    private TextField bearingRight;
    private TextField todaLeft = new TextField();
    private TextField asdaLeft = new TextField();
    private TextField toraLeft = new TextField();
    private TextField ldaLeft = new TextField();
    private TextField todaRight = new TextField();
    private TextField asdaRight = new TextField();
    private TextField toraRight = new TextField();
    private TextField ldaRight = new TextField();
    private final Alert xmlErrorAlert;
    private final Timeline xmlErrorTimeline;
    //</editor-fold>

    RunwaySelection(LoadAirportUI loadAirportUI) {
        super(loadAirportUI);
        this.leftPane = (GridPane) loadAirportUI.mainBorderPane.getLeft();
        this.runwaySelectionToggleGroup = loadAirportUI.runwaySelectionToggleGroup;
        this.airport = loadAirportUI.airport;
        this.selectedRunwayId = loadAirportUI.selectedRunwayId;
        this.currentlySelectedRunwayId = new SimpleObjectProperty<>();
        this.leftRunway = new SimpleObjectProperty<>();
        this.rightRunway = new SimpleObjectProperty<>();
        this.selectedObstacle = loadAirportUI.selectedObstacle;
        this.smallerRunway = loadAirportUI.smallerRunway;
        this.newObstacle = loadAirportUI.newObstacle;
        this.physicalRunway = loadAirportUI.selectedPhysicalRunway;
        this.runwayParameterAlert = new Alert(Alert.AlertType.ERROR);
        this.runwayParameterAlert.setHeaderText("Runway Error");
        this.runwayParameterAlert.setContentText("The runway parameters are not valid.");
        this.duplicateRunwayBearingAlert = new Alert(Alert.AlertType.ERROR, "This runway exists already");
        this.duplicateRunwayBearingAlert.setHeaderText("Duplicate Runway Bearing");
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
        this.bearingFilter = text -> {
            var string = text.getText();
            if (string.matches("[0-9RLCrlc]*")) {
                return text;
            } else if (text.getText().isEmpty()) {
                return text;
            }
            return null;
        };
        this.doubleFilter = text -> {
            var string = text.getText();
            if (string.matches("[0-9.]*")) {
                return text;
            } else if (text.getText().isEmpty()) {
                return text;
            }
            return null;
        };
    }

    private void displayRunwayParameterTable(boolean arg) {
        var node = this.leftPane.lookup("#runwayParameterVBox");
        if (arg) {
            node.setManaged(true);
            node.setVisible(true);
            node.toFront();
            ((Button) this.leftPane.lookup("#saveRunwayButton")).setDefaultButton(true);
        } else {
            node.setManaged(false);
            node.setVisible(false);
            node.toBack();
        }
    }

    private void updateRunwayParameterTable() {
        var runwayId = this.selectedRunwayId.get();
        if (runwayId == null) {
            this.bearingLeft.setText("");
            this.bearingRight.setText("");
            this.todaLeft.setText("");
            this.asdaLeft.setText("");
            this.toraLeft.setText("");
            this.ldaLeft.setText("");
            this.todaRight.setText("");
            this.asdaRight.setText("");
            this.toraRight.setText("");
            this.ldaRight.setText("");
            return;
        }
        var leftRunway = this.physicalRunway.get().getSmallerNumberedRunways().get(0);
        var rightRunway = this.physicalRunway.get().getLargerNumberedRunways().get(0);
        this.bearingLeft.setText(this.selectedRunwayId.get().toStringSmaller());
        this.todaLeft.setText(String.valueOf(leftRunway.getToda()));
        this.asdaLeft.setText(String.valueOf(leftRunway.getAsda()));
        this.toraLeft.setText(String.valueOf(leftRunway.getTora()));
        this.ldaLeft.setText(String.valueOf(leftRunway.getLda()));
        this.bearingRight.setText(this.selectedRunwayId.get().toStringLarger());
        this.todaRight.setText(String.valueOf(rightRunway.getToda()));
        this.asdaRight.setText(String.valueOf(rightRunway.getAsda()));
        this.toraRight.setText(String.valueOf(rightRunway.getTora()));
        this.ldaRight.setText(String.valueOf(rightRunway.getLda()));
    }

    private boolean saveRunwayParameters() {
        var vbox = (VBox) this.leftPane.lookup("#runwayParameterVBox");

        // Check the Left Runway
        var leftBearingString = this.bearingLeft.getText();
        var leftTodaString = this.todaLeft.getText();
        var leftAsdaString = this.asdaLeft.getText();
        var leftToraString = this.toraLeft.getText();
        var leftLdaString = this.ldaLeft.getText();

        boolean leftEmpty = leftTodaString.isEmpty() && leftAsdaString.isEmpty() && leftToraString.isEmpty() & leftLdaString.isEmpty();

        boolean leftError = false;
        if (!leftTodaString.matches("\\d*.?\\d*") || !leftAsdaString.matches("\\d*.?\\d*") || !leftToraString.matches("\\d*.?\\d*") || !leftLdaString.matches(
                "\\d*.?\\d*")) {
            leftError = true;
        } else if (leftTodaString.isEmpty() || leftAsdaString.isEmpty() || leftToraString.isEmpty() || leftLdaString.isEmpty() || leftBearingString.isEmpty()) {
            leftError = true;
        } else if (Double.parseDouble(leftTodaString) < Double.parseDouble(leftAsdaString)) {
            leftError = true;
        } else if (Double.parseDouble(leftAsdaString) < Double.parseDouble(leftToraString)) {
            leftError = true;
        } else if (Double.parseDouble(leftToraString) < Double.parseDouble(leftLdaString)) {
            leftError = true;
        } else if (!leftBearingString.matches("^([1-9]|0[0-9]|1[0-9]|2[0-9]|3[0-6])[LCRlcr]?$")) {
            leftError = true;
        } else if (Double.parseDouble(leftTodaString) > 10000) {
            leftError = true;
        }

        RunwayOriginal leftRunway = null;
        if (!leftError) {
            leftRunway = new RunwayOriginal(Double.parseDouble(leftTodaString),
                                            Double.parseDouble(leftToraString),
                                            Double.parseDouble(leftAsdaString),
                                            Double.parseDouble(leftLdaString));
        }

        // Check the Right Runway
        var rightBearingString = this.bearingRight.getText();
        var rightTodaString = this.todaRight.getText();
        var rightAsdaString = this.asdaRight.getText();
        var rightToraString = this.toraRight.getText();
        var rightLdaString = this.ldaRight.getText();

        boolean rightEmpty = rightTodaString.isEmpty() && rightAsdaString.isEmpty() && rightToraString.isEmpty() & rightLdaString.isEmpty();

        boolean rightError = false;
        if (!rightTodaString.matches("\\d*.?\\d*") || !rightAsdaString.matches("\\d*.?\\d*") || !rightToraString.matches("\\d*.?\\d*") || !rightLdaString.matches(
                "\\d*.?\\d*")) {
            rightError = true;
            System.out.println("1");
        } else if (rightTodaString.isEmpty() || rightAsdaString.isEmpty() || rightToraString.isEmpty() || rightLdaString.isEmpty() || rightBearingString.isEmpty()) {
            rightError = true;
            System.out.println("2");
        } else if (Double.parseDouble(rightTodaString) < Double.parseDouble(rightAsdaString)) {
            rightError = true;
            System.out.println("3");
        } else if (Double.parseDouble(rightAsdaString) < Double.parseDouble(rightToraString)) {
            rightError = true;
            System.out.println("4");
        } else if (Double.parseDouble(rightToraString) < Double.parseDouble(rightLdaString)) {
            rightError = true;
            System.out.println("5");
        } else if (!rightBearingString.matches("^([1-9]|0[0-9]|1[0-9]|2[0-9]|3[0-6])[LCRlcr]?$")) {
            rightError = true;
            System.out.println("6");
        } else if (Double.parseDouble(rightTodaString) > 10000) {
            rightError = true;
        }
        RunwayOriginal rightRunway = null;
        if (!rightError) {
            rightRunway = new RunwayOriginal(Double.parseDouble(rightTodaString),
                                             Double.parseDouble(rightToraString),
                                             Double.parseDouble(rightAsdaString),
                                             Double.parseDouble(rightLdaString));
        }

        PhysicalRunway physicalRunway;
        RunwayId runwayId;

        if (leftEmpty && rightEmpty) {
            this.runwayParameterAlert.showAndWait();
            return false;
        }

        if (leftRunway == null && rightRunway == null) {
            this.runwayParameterAlert.showAndWait();
            System.out.println("1");
            return false;
        }

        if (leftRunway != null && rightRunway != null) {
            runwayId = Utility.parseString(leftBearingString);
            physicalRunway = new PhysicalRunway(leftRunway, rightRunway, runwayId);
            if (!Utility.isReciprocal(leftBearingString, rightBearingString)) {
                this.runwayParameterAlert.showAndWait();
                return false;
            }
        } else if (leftRunway != null && rightEmpty) {
            runwayId = (Utility.parseString(leftBearingString));
            physicalRunway = new PhysicalRunway(leftRunway, runwayId);
        } else if (rightRunway != null && leftEmpty) {
            runwayId = (Utility.parseString(rightBearingString));
            physicalRunway = new PhysicalRunway(rightRunway, runwayId);
        } else {
            System.err.println("It should never reach this: RunwaySelection.java");
            this.runwayParameterAlert.showAndWait();
            return false;
        }
        physicalRunway.setObstacle(this.selectedObstacle.get(), this.loadAirportUI.smallerRunway.get());
        if (this.selectedRunwayId.get() != null && !this.selectedRunwayId.get().equals(runwayId)) {
            for (var id : this.airport.getRunwayIds()) {
                if (runwayId.equals(id)) {
                    this.duplicateRunwayBearingAlert.showAndWait();
                    return false;
                }
            }
        }
        if (this.selectedRunwayId.get() == null) {
            this.airport.addPhysicalRunway(physicalRunway);
            var runwaySelectionList = (VBox) this.leftPane.lookup("#runwaySelectionList");
            this.addNewPhysicalRunwayListItems(new PhysicalRunwayList(physicalRunway), runwaySelectionList);
            this.selectedRunwayId.set(runwayId);
            if (Logger.isLogging()) {
                Logger.writeToLogFile(String.format("New Runway Added: %s", physicalRunway.log()));
            }
        } else {
            var oldRunway = this.airport.getPhysicalRunway(this.selectedRunwayId.get());
            this.airport.getPhysicalRunwayList().remove(oldRunway);
            var runwaySelectionList = (VBox) this.leftPane.lookup("#runwaySelectionList");
            runwaySelectionList.getChildren().remove(this.leftPane.lookup("#" + this.selectedRunwayId.get().toString()));
            this.airport.addPhysicalRunway(physicalRunway);
            this.addNewPhysicalRunwayListItems(new PhysicalRunwayList(physicalRunway), runwaySelectionList);
            this.selectedRunwayId.set(runwayId);
            if (Logger.isLogging()) {
                Logger.writeToLogFile(String.format("Runway Updated:\nOLD - %s\nNEW - %s", oldRunway.log(), physicalRunway.log()));
            }
        }
        this.loadAirportUI.showNotification("Runway Saved");
        return true;
    }

    private void displayObstacleVBox(boolean arg) {
        this.leftPane.lookup("#obstacleVBox").setVisible(arg);
    }

    private void addNewPhysicalRunwayListItems(PhysicalRunwayList physicalRunwayList, VBox runwaySelectionList) {
        for (var runway : physicalRunwayList) {
            var button = new BetterToggleButton(runway.getRunwayId().toString(),
                                                "listItem",
                                                this.runwaySelectionToggleGroup,
                                                runway.getRunwayId().toString());
            button.prefWidthProperty().bind(runwaySelectionList.widthProperty());

            button.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
                if (e.getButton() != MouseButton.PRIMARY) {
                    e.consume();
                }
            });

            button.setOnMouseClicked(e -> {
                if (e.getClickCount() % 2 == 0 && this.selectedRunwayId.get() == runway.getRunwayId()) {
                    this.selectedRunwayId.set(null);
                    this.selectedObstacle.set(null);
                    this.leftRunway.set(new RunwayOriginal(0, 0, 0, 0));
                    this.rightRunway.set(new RunwayOriginal(0, 0, 0, 0));
                    this.loadAirportUI.displayRightPane(false);
                    this.leftPane.lookup("#runwayAddButtonsHBox").toFront();
                    this.displayObstacleVBox(false);
                    button.setSelected(false);
                    if (Logger.isLogging()) {
                        Logger.writeToLogFile(String.format("Runway Deselected: %s", runway.log()));
                    }
                    this.loadAirportUI.showNotification("Runway Deselected");
                    return;
                }
                if (e.getClickCount() % 2 == 0) {
                    this.selectedRunwayId.set(runway.getRunwayId());
                    this.currentlySelectedRunwayId.set(null);
                    this.physicalRunway.set(runway);
                    this.leftRunway.set(runway.getSmallerNumberedRunways().get(0));
                    this.rightRunway.set(runway.getLargerNumberedRunways().get(0));
                    this.selectedObstacle.set(runway.getObstacle(this.smallerRunway.get()));
                    this.loadAirportUI.displayRightPane(true);
                    this.displayObstacleVBox(true);
                    button.setSelected(true);
                    this.newObstacle.set(runway.getObstacle(this.smallerRunway.get()));
                    if (Logger.isLogging()) {
                        Logger.writeToLogFile(String.format("New Runway Selected\n%s", runway.log()));
                    }
                    this.loadAirportUI.showNotification(String.format("%s Selected", runway.getRunwayId().toString()));
                    this.leftPane.lookup("#runwayEditButtonsHBox").toFront();
                    return;
                }
                // If a runway is currently displayed, disable single clicks.
                if (this.selectedRunwayId.get() != null) {
                    return;
                }

                // Deals with single-clicks when nothing is "selected".
                if (button.isSelected()) {
                    this.currentlySelectedRunwayId.set(null);
                    button.setSelected(false);
                } else {
                    this.currentlySelectedRunwayId.set(runway.getRunwayId());
                    button.setSelected(true);
                }
            });
            runwaySelectionList.getChildren().add(0, button);
        }
    }

    private ScrollPane runwayListScrollPane() {
        // Runway Selection List
        var runwaySelectionList = new VBox();
        runwaySelectionList.setAlignment(Pos.TOP_CENTER);
        runwaySelectionList.setFillWidth(true);
        runwaySelectionList.setMaxWidth(Double.MAX_VALUE);
        runwaySelectionList.setId("runwaySelectionList");
        runwaySelectionList.setPadding(new Insets(1));

        this.addNewPhysicalRunwayListItems(this.loadAirportUI.airport.getPhysicalRunwayList(), runwaySelectionList);

        var runwayScrollPane = new ScrollPane(runwaySelectionList);
        Utility.setStyle(runwayScrollPane, "scroll-pane-runway-selection");
        runwayScrollPane.prefHeightProperty().bind(leftPane.heightProperty());
        runwayScrollPane.setId("runwayScrollPane");

        return runwayScrollPane;
    }

    private VBox runwayParameterGridPaneVBox(String name) {
        var nameBox = new VBox();
        var nameLabel = new Label(name);
        Utility.setStyle(nameBox, "runwayParameterNameVBox");
        Utility.setStyle(nameLabel, "runwayParameterLabel");
        nameBox.getChildren().add(nameLabel);
        return nameBox;
    }

    private TextField runwayParameterTextFields(String prompt) {
        var output = new TextField();
        Utility.setStyle(output, "runwayParameterTextField");
        output.setPromptText(prompt);
        output.setTextFormatter(new TextFormatter<>(doubleFilter));
        return output;
    }

    private ArrayList<Node> runwayBearingVBoxes() {
        VBox nameBox = runwayParameterGridPaneVBox("BEARING");

        this.bearingLeft = new TextField();
        Utility.setStyle(bearingLeft, "runwayParameterTextField");
        bearingLeft.setPromptText("09L");
        bearingLeft.setTextFormatter(new TextFormatter<>(bearingFilter));

        this.bearingRight = new TextField();
        Utility.setStyle(bearingRight, "runwayParameterTextField");
        bearingRight.setPromptText("27R");
        bearingRight.setTextFormatter(new TextFormatter<>(bearingFilter));

        bearingLeft.setOnKeyTyped(e -> {
            var newText = Utility.getReciprocal(bearingLeft.getText());
            bearingRight.setText(newText);
        });
        bearingRight.setOnKeyTyped(e -> {
            var newText = Utility.getReciprocal(bearingRight.getText());
            bearingLeft.setText(newText);
        });

        bearingLeft.textProperty().addListener(e -> {
            bearingLeft.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"),
                                                !bearingLeft.getText().isEmpty() && (!bearingLeft.getText().matches(
                                                        "^([1-9]|0[0-9]|1[0-9]|2[0-9]|3[0-6])[LCRlcr]?$") || !Utility.isReciprocal(bearingLeft.getText(),
                                                                                                                                   bearingRight.getText())));
            bearingRight.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"),
                                                 !bearingRight.getText().isEmpty() && (!bearingRight.getText().matches(
                                                         "^([1-9]|0[0-9]|1[0-9]|2[0-9]|3[0-6])[LCRlcr]?$") || !Utility.isReciprocal(bearingLeft.getText(),
                                                                                                                                    bearingRight.getText())));
        });

        bearingRight.textProperty().addListener(e -> {
            bearingLeft.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"),
                                                !bearingLeft.getText().isEmpty() && (!bearingLeft.getText().matches(
                                                        "^([1-9]|0[0-9]|1[0-9]|2[0-9]|3[0-6])[LCRlcr]?$") || !Utility.isReciprocal(bearingLeft.getText(),
                                                                                                                                   bearingRight.getText())));
            bearingRight.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"),
                                                 !bearingRight.getText().isEmpty() && (!bearingRight.getText().matches(
                                                         "^([1-9]|0[0-9]|1[0-9]|2[0-9]|3[0-6])[LCRlcr]?$") || !Utility.isReciprocal(bearingLeft.getText(),
                                                                                                                                    bearingRight.getText())));
        });

        return new ArrayList<>(Arrays.asList(nameBox, bearingLeft, bearingRight));
    }

    private void addListenersToRunwayParameterTextFields(
            Label todaName,
            Label asdaName,
            Label toraName,
            Label ldaName,
            TextField toda,
            TextField asda,
            TextField tora,
            TextField lda) {
        toda.textProperty().addListener(e -> {
            var todaString = toda.getText();
            var asdaString = asda.getText();
            var toraString = tora.getText();
            var ldaString = lda.getText();

            boolean numberError = false;
            if (!todaString.matches("\\d*.?\\d*") || !asdaString.matches("\\d*.?\\d*") || !toraString.matches("\\d*.?\\d*") || !ldaString.matches(
                    "\\d*.?\\d*")) {
                numberError = true;
            } else if (!todaString.isEmpty() && !asdaString.isEmpty() && Double.parseDouble(todaString) < Double.parseDouble(asdaString)) {
                numberError = true;
            } else if (!asdaString.isEmpty() && !toraString.isEmpty() && Double.parseDouble(asdaString) < Double.parseDouble(toraString)) {
                numberError = true;
            } else if (!toraString.isEmpty() && !ldaString.isEmpty() && Double.parseDouble(toraString) < Double.parseDouble(ldaString)) {
                numberError = true;
            } else if (!todaString.isEmpty() && Double.parseDouble(todaString) > 10000) {
                numberError = true;
            }

            todaName.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"),
                                             (!toda.getText().isEmpty() && !toda.getText().matches("([1-9][0-9]*.?[0-9]*)")) || numberError);
            toda.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"),
                                         (!toda.getText().isEmpty() && !toda.getText().matches("([1-9][0-9]*.?[0-9]*)")) || numberError);
            asda.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"),
                                         (!asda.getText().isEmpty() && !asda.getText().matches("([1-9][0-9]*.?[0-9]*)")) || numberError);
            tora.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"),
                                         (!tora.getText().isEmpty() && !tora.getText().matches("([1-9][0-9]*.?[0-9]*)")) || numberError);
            lda.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"),
                                        (!lda.getText().isEmpty() && !lda.getText().matches("([1-9][0-9]*.?[0-9]*)")) || numberError);
        });
        asda.textProperty().addListener(e -> {
            var todaString = toda.getText();
            var asdaString = asda.getText();
            var toraString = tora.getText();
            var ldaString = lda.getText();

            boolean numberError = false;
            if (!todaString.matches("\\d*.?\\d*") || !asdaString.matches("\\d*.?\\d*") || !toraString.matches("\\d*.?\\d*") || !ldaString.matches(
                    "\\d*.?\\d*")) {
                numberError = true;
            } else if (!todaString.isEmpty() && !asdaString.isEmpty() && Double.parseDouble(todaString) < Double.parseDouble(asdaString)) {
                numberError = true;
            } else if (!asdaString.isEmpty() && !toraString.isEmpty() && Double.parseDouble(asdaString) < Double.parseDouble(toraString)) {
                numberError = true;
            } else if (!toraString.isEmpty() && !ldaString.isEmpty() && Double.parseDouble(toraString) < Double.parseDouble(ldaString)) {
                numberError = true;
            }

            asdaName.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"),
                                             (!asda.getText().isEmpty() && !asda.getText().matches("([1-9][0-9]*.?[0-9]*)")) || numberError);
            toda.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"),
                                         (!toda.getText().isEmpty() && !toda.getText().matches("([1-9][0-9]*.?[0-9]*)")) || numberError);
            asda.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"),
                                         (!asda.getText().isEmpty() && !asda.getText().matches("([1-9][0-9]*.?[0-9]*)")) || numberError);
            tora.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"),
                                         (!tora.getText().isEmpty() && !tora.getText().matches("([1-9][0-9]*.?[0-9]*)")) || numberError);
            lda.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"),
                                        (!lda.getText().isEmpty() && !lda.getText().matches("([1-9][0-9]*.?[0-9]*)")) || numberError);
        });
        tora.textProperty().addListener(e -> {
            var todaString = toda.getText();
            var asdaString = asda.getText();
            var toraString = tora.getText();
            var ldaString = lda.getText();

            boolean numberError = false;
            if (!todaString.matches("\\d*.?\\d*") || !asdaString.matches("\\d*.?\\d*") || !toraString.matches("\\d*.?\\d*") || !ldaString.matches(
                    "\\d*.?\\d*")) {
                numberError = true;
            } else if (!todaString.isEmpty() && !asdaString.isEmpty() && Double.parseDouble(todaString) < Double.parseDouble(asdaString)) {
                numberError = true;
            } else if (!asdaString.isEmpty() && !toraString.isEmpty() && Double.parseDouble(asdaString) < Double.parseDouble(toraString)) {
                numberError = true;
            } else if (!toraString.isEmpty() && !ldaString.isEmpty() && Double.parseDouble(toraString) < Double.parseDouble(ldaString)) {
                numberError = true;
            }

            toraName.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"),
                                             (!tora.getText().isEmpty() && !tora.getText().matches("([1-9][0-9]*.?[0-9]*)")) || numberError);
            toda.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"),
                                         (!toda.getText().isEmpty() && !toda.getText().matches("([1-9][0-9]*.?[0-9]*)")) || numberError);
            asda.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"),
                                         (!asda.getText().isEmpty() && !asda.getText().matches("([1-9][0-9]*.?[0-9]*)")) || numberError);
            tora.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"),
                                         (!tora.getText().isEmpty() && !tora.getText().matches("([1-9][0-9]*.?[0-9]*)")) || numberError);
            lda.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"),
                                        (!lda.getText().isEmpty() && !lda.getText().matches("([1-9][0-9]*.?[0-9]*)")) || numberError);
        });
        lda.textProperty().addListener(e -> {
            var todaString = toda.getText();
            var asdaString = asda.getText();
            var toraString = tora.getText();
            var ldaString = lda.getText();

            boolean numberError = false;
            if (!todaString.matches("\\d*.?\\d*") || !asdaString.matches("\\d*.?\\d*") || !toraString.matches("\\d*.?\\d*") || !ldaString.matches(
                    "\\d*.?\\d*")) {
                numberError = true;
            } else if (!todaString.isEmpty() && !asdaString.isEmpty() && Double.parseDouble(todaString) < Double.parseDouble(asdaString)) {
                numberError = true;
            } else if (!asdaString.isEmpty() && !toraString.isEmpty() && Double.parseDouble(asdaString) < Double.parseDouble(toraString)) {
                numberError = true;
            } else if (!toraString.isEmpty() && !ldaString.isEmpty() && Double.parseDouble(toraString) < Double.parseDouble(ldaString)) {
                numberError = true;
            }

            ldaName.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"),
                                            (!lda.getText().isEmpty() && !lda.getText().matches("([1-9][0-9]*.?[0-9]*)")) || numberError);
            toda.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"),
                                         (!toda.getText().isEmpty() && !toda.getText().matches("([1-9][0-9]*.?[0-9]*)")) || numberError);
            asda.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"),
                                         (!asda.getText().isEmpty() && !asda.getText().matches("([1-9][0-9]*.?[0-9]*)")) || numberError);
            tora.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"),
                                         (!tora.getText().isEmpty() && !tora.getText().matches("([1-9][0-9]*.?[0-9]*)")) || numberError);
            lda.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"),
                                        (!lda.getText().isEmpty() && !lda.getText().matches("([1-9][0-9]*.?[0-9]*)")) || numberError);
        });
    }

    private ArrayList<Node> runwayParameterVBoxes() {
        var todaName = this.runwayParameterGridPaneVBox("TODA(m)");
        var asdaName = this.runwayParameterGridPaneVBox("ASDA(m)");
        var toraName = this.runwayParameterGridPaneVBox("TORA(m)");
        var ldaName = this.runwayParameterGridPaneVBox("LDA(m)");

        this.todaLeft = runwayParameterTextFields("TODA");
        this.todaRight = runwayParameterTextFields("TODA");
        this.asdaLeft = runwayParameterTextFields("ASDA");
        this.asdaRight = runwayParameterTextFields("ASDA");
        this.toraLeft = runwayParameterTextFields("TORA");
        this.toraRight = runwayParameterTextFields("TORA");
        this.ldaLeft = runwayParameterTextFields("LDA");
        this.ldaRight = runwayParameterTextFields("LDA");
        this.todaLeft.setId("todaLeft");
        this.todaRight.setId("todaRight");
        this.asdaLeft.setId("asdaLeft");
        this.asdaRight.setId("asdaRight");
        this.toraLeft.setId("toraLeft");
        this.toraRight.setId("toraRight");
        this.ldaLeft.setId("ldaLeft");
        this.ldaRight.setId("ldaRight");

        this.addListenersToRunwayParameterTextFields((Label) todaName.getChildren().get(0),
                                                     (Label) asdaName.getChildren().get(0),
                                                     (Label) toraName.getChildren().get(0),
                                                     (Label) ldaName.getChildren().get(0),
                                                     this.todaLeft,
                                                     this.asdaLeft,
                                                     this.toraLeft,
                                                     this.ldaLeft);
        this.addListenersToRunwayParameterTextFields((Label) todaName.getChildren().get(0),
                                                     (Label) asdaName.getChildren().get(0),
                                                     (Label) toraName.getChildren().get(0),
                                                     (Label) ldaName.getChildren().get(0),
                                                     this.todaRight,
                                                     this.asdaRight,
                                                     this.toraRight,
                                                     this.ldaRight);

        return new ArrayList<>(Arrays.asList(todaName,
                                             this.todaLeft,
                                             this.todaRight,
                                             asdaName,
                                             this.asdaLeft,
                                             this.asdaRight,
                                             toraName,
                                             this.toraLeft,
                                             this.toraRight,
                                             ldaName,
                                             this.ldaLeft,
                                             this.ldaRight));
    }

    private VBox runwayParameterVBox() {
        var runwayParameterVBox = new VBox(5);
        VBox.setVgrow(runwayParameterVBox, Priority.SOMETIMES);
        runwayParameterVBox.setAlignment(Pos.CENTER);
        runwayParameterVBox.setStyle("-fx-background-color: white");
        runwayParameterVBox.setId("runwayParameterVBox");
        runwayParameterVBox.prefWidthProperty().bind(leftPane.widthProperty());

        // GridPane with 8 rows and 2 columns
        var runwayParameterGridPane = new GridPane();
        runwayParameterGridPane.prefHeightProperty().bind(leftPane.heightProperty());
        runwayParameterGridPane.setPadding(new Insets(20, 5, 20, 5));
        runwayParameterGridPane.setVgap(5);
        runwayParameterGridPane.setHgap(5);

        // Row/Column Constraints
        for (int i = 0; i < 5; i++) {
            runwayParameterGridPane.getRowConstraints().add(Utility.rowConstraint());
        }
        for (int i = 0; i < 3; i++) {
            runwayParameterGridPane.getColumnConstraints().add(Utility.columnConstraintRunwaySelection());
        }

        var nodes = this.runwayBearingVBoxes();
        nodes.addAll(this.runwayParameterVBoxes());
        for (int col = 0; col < 3; col++) {
            for (int row = 0; row < 5; row++) {
                var node = nodes.get(row * 3 + col);
                runwayParameterGridPane.add(node, col, row);
            }
        }

        runwayParameterVBox.getChildren().add(runwayParameterGridPane);
        runwayParameterVBox.setVisible(false);
        runwayParameterVBox.setManaged(false);
        return runwayParameterVBox;
    }

    private StackPane runwaySelectionContentsStackPane() {
        var runwaySelectionContentsStackPane = new StackPane();
        runwaySelectionContentsStackPane.setStyle("-fx-background-color: #e5e5e5");
        runwaySelectionContentsStackPane.getChildren().add(this.runwayParameterVBox());
        runwaySelectionContentsStackPane.getChildren().add(this.runwayListScrollPane());
        return runwaySelectionContentsStackPane;
    }

    private HBox runwayAddButtonsHBox() {
        var runwayAddButtonsHBox = this.newSelectionButtonHBox("runwayAddButtonsHBox");

        var addRunwayButton = new Button("New");
        addRunwayButton.setOnAction(e -> {
            this.leftPane.lookup("#runwaySaveButtonsHBox").toFront();
            this.displayRunwayParameterTable(true);
            this.updateRunwayParameterTable();
            this.bearingLeft.requestFocus();
            this.loadAirportUI.showNotification("New Runway");
        });
        runwayAddButtonsHBox.getChildren().add(addRunwayButton);

        var importRunwayButton = new Button("Import");
        importRunwayButton.setOnAction(e -> {
            var stage = new Stage();
            stage.setAlwaysOnTop(true);
            var file = this.loadAirportUI.fileChooser.showOpenDialog(stage);
            if (file == null) return;
            try {
                var runways = RunwayXMLParser.parseRunwayXML(file);
                var leftPane = (GridPane) this.loadAirportUI.mainBorderPane.getLeft();
                var runwaySelectionList = (VBox) leftPane.lookup("#runwaySelectionList");
                this.addNewPhysicalRunwayListItems(runways, runwaySelectionList);
                this.loadAirportUI.airport.getPhysicalRunwayList().addAll(0, runways);
                if (Logger.isLogging()) {
                    Logger.writeToLogFile(String.format("Runway(s) Imported: %s",
                                                        runways.stream().map(PhysicalRunway::log).collect(Collectors.joining("\n"))));
                }
                this.loadAirportUI.showNotification("Import Success");
            } catch (JAXBException ex) {
                this.showError();
            }
        });
        runwayAddButtonsHBox.getChildren().add(importRunwayButton);

        var displayRunwayButton = new Button("Display");
        displayRunwayButton.disableProperty().bind(Bindings.isNull(currentlySelectedRunwayId));
        displayRunwayButton.setOnAction(e -> {
            if (Logger.isLogging()) {
                Logger.writeToLogFile(String.format("New Runway Selected\n%s",
                                                    this.airport.getPhysicalRunway(this.currentlySelectedRunwayId.get()).log()));
            }
            this.selectedRunwayId.set(this.currentlySelectedRunwayId.get());
            this.currentlySelectedRunwayId.set(null);
            this.leftRunway.set(this.airport.getPhysicalRunway(this.selectedRunwayId.get()).getSmallerNumberedRunways().get(0));
            this.rightRunway.set(this.airport.getPhysicalRunway(this.selectedRunwayId.get()).getLargerNumberedRunways().get(0));
            this.leftPane.lookup("#runwayEditButtonsHBox").toFront();
            this.loadAirportUI.displayRightPane(true);
            this.displayObstacleVBox(true);
            this.loadAirportUI.showNotification("Runway Displayed");
        });
        runwayAddButtonsHBox.getChildren().add(displayRunwayButton);

        for (Node child : runwayAddButtonsHBox.getChildren()) {
            var button = (Button) child;
            Utility.setStyle(button, "basic-label-button");
        }

        return runwayAddButtonsHBox;
    }

    private HBox runwayEditButtonsHBox() {
        var runwayEditButtonsHBox = this.newSelectionButtonHBox("runwayEditButtonsHBox");

        var editRunwayButton = new Button("Edit");
        var exportRunwayButton = new Button("Export");
        var hideRunwayButton = new Button("Hide");

        editRunwayButton.setOnAction(e -> {
            this.displayRunwayParameterTable(true);
            this.updateRunwayParameterTable();
            this.leftPane.lookup("#obstacleVBox").setVisible(false);
            this.leftPane.lookup("#runwaySaveButtonsHBox").toFront();
            this.loadAirportUI.displayRightPane(false);
            this.bearingLeft.requestFocus();
            this.loadAirportUI.showNotification("Edit Runway");
        });
        exportRunwayButton.setOnAction(e -> {
            var stage = new Stage();
            stage.setAlwaysOnTop(true);
            var file = this.loadAirportUI.fileChooser.showSaveDialog(stage);
            if (file != null) {
                var physicalRunwayList = new PhysicalRunwayList();
                var runwayToExport = this.loadAirportUI.airport.getPhysicalRunway(this.selectedRunwayId.get());
                physicalRunwayList.add(runwayToExport);
                RunwayXMLParser.writeRunwaysToXML(String.valueOf(file), physicalRunwayList);
                if (Logger.isLogging()) {
                    Logger.writeToLogFile(String.format("Runway Exported:\n%s", runwayToExport.log()));
                }
                this.loadAirportUI.showNotification("Runway Exported");
            }
        });
        hideRunwayButton.setOnAction(e -> {
            if (Logger.isLogging()) {
                Logger.writeToLogFile(String.format("Runway Deselected:\n%s", this.airport.getPhysicalRunway(this.selectedRunwayId.get()).log()));
            }
            this.selectedRunwayId.set(null);
            this.leftRunway.set(new RunwayOriginal(0, 0, 0, 0));
            this.rightRunway.set(new RunwayOriginal(0, 0, 0, 0));
            this.loadAirportUI.displayRightPane(false);
            this.leftPane.lookup("#runwayAddButtonsHBox").toFront();
            this.runwaySelectionToggleGroup.selectToggle(null);
            this.displayObstacleVBox(false);
            this.loadAirportUI.showNotification("Runway Hidden");
        });

        runwayEditButtonsHBox.getChildren().addAll(editRunwayButton, exportRunwayButton, hideRunwayButton);
        for (Node child : runwayEditButtonsHBox.getChildren()) {
            var button = (Button) child;
            Utility.setStyle(button, "basic-label-button");
        }

        return runwayEditButtonsHBox;
    }

    private HBox runwaySaveButtonsHBox() {
        var runwaySaveButtonsHBox = this.newSelectionButtonHBox("runwaySaveButtonsHBox");

        var cancelRunwayButton = new Button("Cancel");
        cancelRunwayButton.setOnAction(e -> {
            if (this.selectedRunwayId.get() == null) {
                this.leftPane.lookup("#runwayAddButtonsHBox").toFront();
                this.loadAirportUI.displayRightPane(false);
            } else {
                this.leftPane.lookup("#runwayEditButtonsHBox").toFront();
                this.loadAirportUI.displayRightPane(true);
                this.displayObstacleVBox(true);
            }
            this.displayRunwayParameterTable(false);
            this.loadAirportUI.showNotification("Runway Parameters Discarded");
        });
        runwaySaveButtonsHBox.getChildren().add(cancelRunwayButton);

        var saveRunwayButton = new Button("Save");
        saveRunwayButton.setId("saveRunwayButton");
        saveRunwayButton.setOnAction(e -> {
            if (!this.saveRunwayParameters()) {
                return;
            }

            var runwayId = this.selectedRunwayId.get();

            ((BetterToggleButton) leftPane.lookup("#" + runwayId.toString())).setSelected(true);
            this.loadAirportUI.displayRightPane(true);
            this.displayObstacleVBox(true);
            this.leftPane.lookup("#runwayEditButtonsHBox").toFront();
            this.displayRunwayParameterTable(false);
        });
        saveRunwayButton.setDefaultButton(true);
        runwaySaveButtonsHBox.getChildren().add(saveRunwayButton);

        for (Node child : runwaySaveButtonsHBox.getChildren()) {
            var button = (Button) child;
            Utility.setStyle(button, "basic-label-button");
        }

        return runwaySaveButtonsHBox;
    }

    private StackPane runwayButtonsStackPane() {
        var runwayButtonsStackPane = new StackPane();

        runwayButtonsStackPane.getChildren().add(this.runwaySaveButtonsHBox());
        runwayButtonsStackPane.getChildren().add(this.runwayEditButtonsHBox());
        runwayButtonsStackPane.getChildren().add(this.runwayAddButtonsHBox());

        return runwayButtonsStackPane;
    }

    @Override
    public Node getNode() {
        var runwaySelectionVBox = new VBox();
        Utility.setStyle(runwaySelectionVBox, "boxes-load-airport-ui");
        VBox.setVgrow(runwaySelectionVBox, Priority.ALWAYS);
        GridPane.setVgrow(runwaySelectionVBox, Priority.ALWAYS);
        runwaySelectionVBox.setId("runwaySelectionVBox");
        runwaySelectionVBox.managedProperty().bind(runwaySelectionVBox.visibleProperty());
        runwaySelectionVBox.prefWidthProperty().bind(leftPane.widthProperty());
        runwaySelectionVBox.maxHeightProperty().bind(leftPane.heightProperty().add(-180));
        runwaySelectionVBox.prefHeightProperty().bind(leftPane.heightProperty());

        // Runways Label
        var runwayTitle = new Label("Runways");
        Utility.setStyle(runwayTitle, "label-runway-selection-title");
        runwayTitle.prefWidthProperty().bind(runwaySelectionVBox.widthProperty());
        runwaySelectionVBox.getChildren().add(runwayTitle);


        runwaySelectionVBox.getChildren().add(this.runwaySelectionContentsStackPane());

        runwaySelectionVBox.getChildren().add(this.runwayButtonsStackPane());

        return runwaySelectionVBox;
    }
}
