package com.group17.seg.view;

import com.group17.seg.model.*;
import com.group17.seg.utility.Grayscale;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class CalcBreakdownUI extends BaseUI {
    private final BorderPane mainPane;
    private final PhysicalRunway runway;
    private PhysicalRunway.Runway selectedView;
    private boolean smallerRunway;

    /**
     * @param window        Window passed in to display the breakdown in
     * @param runway        Physical Runway to get the numbers from
     * @param selectedView  Boolean to know the initial display
     * @param smallerRunway Runway enum to know the initial display
     */
    public CalcBreakdownUI(AppWindow window, PhysicalRunway runway, PhysicalRunway.Runway selectedView, boolean smallerRunway) {
        super(window);
        this.runway = runway;
        this.selectedView = selectedView;
        this.smallerRunway = smallerRunway;
        this.mainPane = new BorderPane();
        this.mainPane.getStyleClass().addAll("window", "BreakDownText");
        this.root.getChildren().add(mainPane);
    }

    /**
     * Constructs a GridPane to display the calc breakdown and sets it as the center pane.
     */
    private void displayCalcBreakdownGridPane() {
        var calcBreakdownGridPane = new GridPane();
        calcBreakdownGridPane.getStyleClass().add("gridpane-calc-breakdown-ui");
        calcBreakdownGridPane.setVgap(20);
        calcBreakdownGridPane.setHgap(10);
        calcBreakdownGridPane.setPadding(new Insets(100, 0, 0, 0));
//

        var variablesLeft = new Label("Variables Altered:");
        var equationsLeft = new Label("Equations:");
        var calculationsLeft = new Label("Calculations:");
        var variablesRight = new Label();
        var equationsRight = new Label();
        var calculationsRight = new Label();

        variablesRight.setId("variablesRight");
        equationsRight.setId("equationsRight");
        calculationsRight.setId("calculationsRight");

        calcBreakdownGridPane.add(variablesLeft, 1, 0);
        calcBreakdownGridPane.add(equationsLeft, 1, 2);
        calcBreakdownGridPane.add(calculationsLeft, 1, 4);
        calcBreakdownGridPane.add(variablesRight, 2, 0);
        calcBreakdownGridPane.add(equationsRight, 2, 2);
        calcBreakdownGridPane.add(calculationsRight, 2, 4);

        for (Node child : calcBreakdownGridPane.getChildren()) {
            var label = (Label) child;
            label.getStyleClass().clear();
            label.getStyleClass().add("label-calc-breakdown-gridpane");
        }

        //calcBreakdownGridPane.setGridLinesVisible(true);

        var firstLineSeparator = new Separator(Orientation.HORIZONTAL);
        firstLineSeparator.setPrefHeight(3);
        firstLineSeparator.setPrefWidth(Double.MAX_VALUE);
        firstLineSeparator.getStyleClass().clear();
        firstLineSeparator.setStyle("-fx-background-color: lightgray");
        calcBreakdownGridPane.add(firstLineSeparator, 1, 1, 2, 1);

        var secondLineSeparator = new Separator(Orientation.HORIZONTAL);
        secondLineSeparator.setPrefHeight(3);
        secondLineSeparator.setPrefWidth(Double.MAX_VALUE);
        secondLineSeparator.getStyleClass().clear();
        secondLineSeparator.setStyle("-fx-background-color: lightgray");
        calcBreakdownGridPane.add(secondLineSeparator, 1, 3, 2, 1);

        var leftPadding = new ColumnConstraints();
        var calcBreakdownHeader = new ColumnConstraints(170);
        var calcBreakdownContent = new ColumnConstraints(660);
        var rightPadding = new ColumnConstraints();
        leftPadding.setHgrow(Priority.SOMETIMES);
        rightPadding.setHgrow(Priority.SOMETIMES);
        calcBreakdownHeader.setHalignment(HPos.RIGHT);
        calcBreakdownContent.setHalignment(HPos.LEFT);

        calcBreakdownGridPane.getColumnConstraints().addAll(leftPadding, calcBreakdownHeader, calcBreakdownContent, rightPadding);

        this.mainPane.setCenter(calcBreakdownGridPane);
    }

    /**
     * Looks up relevant labels by ID then replaces the text contents.
     * Termination point of view updating chain.
     *
     * @param variables    Formatted string of variables changed
     * @param equations    Formatted string of equations involved
     * @param calculations Formatted string of calculation breakdown
     */
    private void updateGridPaneValues(String variables, String equations, String calculations) {
        var gridPane = (GridPane) this.mainPane.getCenter();
        var variablesLabel = (Label) gridPane.lookup("#variablesRight");
        var equationsLabel = (Label) gridPane.lookup("#equationsRight");
        var calculationsLabel = (Label) gridPane.lookup("#calculationsRight");

        variablesLabel.setText(variables);
        equationsLabel.setText(equations);
        calculationsLabel.setText(calculations);
    }

    /**
     * Case to format the LandingTowards view button case to be passed to updateGridPaneValues()
     */
    private void updateGridPaneLandingTowards() {
        var runwayToDisplay = (RunwayLandingTowards) this.runway.getLogicalRunways(this.smallerRunway).get(selectedView.ordinal());

        String bonusString = "";
        if (runwayToDisplay.getLda() < 0) {
            bonusString = "(Runway is unusable for landing)";
        }

        var variablesString = "LDA";
        var equationsString = String.format("%-4s = %s", "LDA", "Distance of Obstacle from DT - Strip End - RESA");
        var calculationsString = String.format("%-4s%s\n %5s %-8.1f %s",
                                               "LDA",
                                               runwayToDisplay.showBreakdownOfLda(),
                                               "=",
                                               runwayToDisplay.getLda(),
                                               bonusString);

        this.updateGridPaneValues(variablesString, equationsString, calculationsString);
    }

    /**
     * Case to format the LandingOver view button case to be passed to updateGridPaneValues()
     */
    private void updateGridPaneLandingOver() {
        var runwayToDisplay = (RunwayLandingOver) this.runway.getLogicalRunways(this.smallerRunway).get(selectedView.ordinal());

        String bonusString = "";
        if (runwayToDisplay.getLda() < 0) {
            bonusString = "(Runway is unusable for landing)";
        }

        var variablesString = "LDA";
        var equationsString = String.format("%-4s = %s", "LDA", "Original LDA Distance - Obstacle Distance from DT - Strip End - RESA");
        var calculationsString = String.format("%-4s%s\n %5s %-8.1f %s",
                                               "LDA",
                                               runwayToDisplay.showBreakdownOfLda(),
                                               "=",
                                               runwayToDisplay.getLda(),
                                               bonusString);

        this.updateGridPaneValues(variablesString, equationsString, calculationsString);
    }

    /**
     * Case to format the TakeOffAwayFrom view button case to be passed to updateGridPaneValues()
     */
    private void updateGridPaneTakeOffAwayFrom() {
        var runwayToDisplay = (RunwayTakeOffAwayFrom) this.runway.getLogicalRunways(this.smallerRunway).get(selectedView.ordinal());

        String bonusString = "";
        String bonusToda = "";
        String bonusAsda = "";
        if (runwayToDisplay.getTora() < 0) {
            bonusString = "(Runway can not be used for take-off)";
        } else {
            if (runwayToDisplay.getToda() > runwayToDisplay.getTora()) {
                bonusToda = "(Adjusted to utilise existing TODA)";
            }
            if (runwayToDisplay.getAsda() > runwayToDisplay.getTora()) {
                bonusAsda = "(Adjusted to utilise existing ASDA)";
            }
        }

        var variablesString = "TODA, ASDA, and TORA";
        var equationsString = String.format("%-4s = %s\n%-4s = %s\n%-4s = %s",
                                            "TODA",
                                            "Original TODA - Blast Protection Value - Distance of Object from DT - DT",
                                            "ASDA",
                                            "Original ASDA - Blast Protection Value - Distance of Object from DT - DT",
                                            "TORA",
                                            "Original TORA - Blast Protection Value - Distance of Object from DT - DT");
        var calculationsString = String.format("%-4s%s\n %5s %-8.1f %s %s\n%-4s%s\n %5s %-8.1f %s %s\n%-4s%s\n %5s %-8.1f %s",
                                               "TODA",
                                               runwayToDisplay.showBreakdownOfToda(),
                                               "=",
                                               runwayToDisplay.getToda(),
                                               bonusString,
                                               bonusToda,
                                               "ASDA",
                                               runwayToDisplay.showBreakdownOfAsda(),
                                               "=",
                                               runwayToDisplay.getAsda(),
                                               bonusString,
                                               bonusAsda,
                                               "TORA",
                                               runwayToDisplay.showBreakdownOfTora(),
                                               "=",
                                               runwayToDisplay.getTora(),
                                               bonusString);

        this.updateGridPaneValues(variablesString, equationsString, calculationsString);
    }

    /**
     * Case to format the TakeOffTowards view button case to be passed to updateGridPaneValues()
     */
    private void updateGridPaneTakeOffTowards() {
        var runwayToDisplay = (RunwayTakeOffTowards) this.runway.getLogicalRunways(this.smallerRunway).get(selectedView.ordinal());

        String bonusString = "";
        String bonusToda = "";
        String bonusAsda = "";
        if (runwayToDisplay.getTora() < 0) {
            bonusString = "(Runway can not be used for take-off)";
        } else {
            if (runwayToDisplay.getAsda() > runwayToDisplay.getTora()) {
                bonusAsda = "(Adjusted to utilise existing ASDA)";
            }
            if (runwayToDisplay.getToda() > runwayToDisplay.getTora()) {
                bonusToda = "(Adjusted to utilise existing TODA)";
            }
        }

        var variablesString = "TODA, ASDA, and TORA";
        var equationsString = String.format("%-4s = %s\n%-4s = %s\n%-4s = %s",
                                            "TODA",
                                            "DT + Obstacle Distance from DT - (RESA or Obstacle Height * 50) - Strip End",
                                            "ASDA",
                                            "DT + Obstacle Distance from DT - (RESA or Obstacle Height * 50) - Strip End",
                                            "TORA",
                                            "DT + Obstacle Distance from DT - (RESA or Obstacle Height * 50) - Strip End");
        var calculationsString = String.format("%-4s%s\n %5s %-8.1f %s %s\n%-4s%s\n %5s %-8.1f %s %s\n%-4s%s\n %5s %-8.1f %s",
                                               "TODA",
                                               runwayToDisplay.showBreakdownOfToraTodaAsda(),
                                               "=",
                                               runwayToDisplay.getToda(),
                                               bonusString,
                                               bonusToda,
                                               "ASDA",
                                               runwayToDisplay.showBreakdownOfToraTodaAsda(),
                                               "=",
                                               runwayToDisplay.getAsda(),
                                               bonusString,
                                               bonusAsda,
                                               "TORA",
                                               runwayToDisplay.showBreakdownOfToraTodaAsda(),
                                               "=",
                                               runwayToDisplay.getTora(),
                                               bonusString);

        this.updateGridPaneValues(variablesString, equationsString, calculationsString);
    }

    /**
     * Method that the view buttons call to update the GridPane that displays the breakdown.
     */
    private void updateBreakdownTable() {
        switch (this.selectedView) {
            case LANDINGTOWARDS -> this.updateGridPaneLandingTowards();
            case LANDINGOVER -> this.updateGridPaneLandingOver();
            case TAKEOFFAWAYFROM -> this.updateGridPaneTakeOffAwayFrom();
            case TAKEOFFTOWARDS -> this.updateGridPaneTakeOffTowards();
        }
    }

    /**
     * Creates and shows the top buttons HBox on the top pane.
     */
    private void displayTopButtons() {
        var topButtonsHBox = new HBox();
        var topButtonsToggleGroup = new ToggleGroup();

        var landingTowardsButton = new ToggleButton("Landing Towards");
        landingTowardsButton.setId("LANDINGTOWARDS");
        var landingOverButton = new ToggleButton("Landing Over");
        landingOverButton.setId("LANDINGOVER");
        var takeOffAwayFromButton = new ToggleButton("Take-Off Away From");
        takeOffAwayFromButton.setId("TAKEOFFAWAYFROM");
        var takeOffTowardsButton = new ToggleButton("Take-Off Towards");
        takeOffTowardsButton.setId("TAKEOFFTOWARDS");

        topButtonsHBox.getChildren().addAll(landingTowardsButton, landingOverButton, takeOffAwayFromButton, takeOffTowardsButton);

        for (Node node : topButtonsHBox.getChildren()) {
            var button = (ToggleButton) node;
            button.getStyleClass().clear();
            button.getStyleClass().add("toggle-button-breakdown-view-selection");
            button.setMaxWidth(Double.MAX_VALUE);
            button.setToggleGroup(topButtonsToggleGroup);
            HBox.setHgrow(button, Priority.ALWAYS);
            button.setOnAction(e -> {
                if (topButtonsToggleGroup.getSelectedToggle() == null) {
                    button.fire();
                    return;
                }
                this.selectedView = PhysicalRunway.Runway.valueOf(button.getId());
                this.updateBreakdownTable();
            });
        }

        this.mainPane.setTop(topButtonsHBox);
        this.initialiseTopButtons();
    }

    /**
     * Creates and shows the bottom buttons HBox on the bottom pane.
     */
    private void displayBottomButtons() {
        var bottomButtonsHBox = new HBox();
        bottomButtonsHBox.setAlignment(Pos.CENTER);
        bottomButtonsHBox.setPadding(new Insets(10));
        bottomButtonsHBox.setSpacing(200);

        var runwaySelectionHBox = new HBox();
        var runwaySelectionToggleGroup = new ToggleGroup();

        var smallerRunwayButton = new ToggleButton(String.valueOf(runway.getRunwayId().toString().split("/")[0]));
        smallerRunwayButton.setId("true");
        var largerRunwayButton = new ToggleButton(String.valueOf(runway.getRunwayId().toString().split("/")[1]));
        largerRunwayButton.setId("false");

        runwaySelectionHBox.getChildren().addAll(smallerRunwayButton, largerRunwayButton);

        for (Node node : runwaySelectionHBox.getChildren()) {
            var button = (ToggleButton) node;
            button.getStyleClass().clear();
            button.getStyleClass().add("toggle-button-breakdown-runway-selection");
            button.setPadding(new Insets(5));
            button.setToggleGroup(runwaySelectionToggleGroup);
            button.setOnAction(e -> {
                if (runwaySelectionToggleGroup.getSelectedToggle() == null) {
                    button.fire();
                    return;
                }
                this.smallerRunway = Boolean.parseBoolean(button.getId());
                this.updateBreakdownTable();
            });
        }

        var closeButton = new Button("Close");
        closeButton.setAlignment(Pos.BOTTOM_RIGHT);
        closeButton.getStyleClass().add("basic-label-button");

        closeButton.setOnAction(e -> ((Stage) closeButton.getScene().getWindow()).close());

        bottomButtonsHBox.getChildren().addAll(runwaySelectionHBox, closeButton);

        this.mainPane.setBottom(bottomButtonsHBox);
        this.initialiseBottomButtons();
    }

    /**
     * Prefires the top ToggleButtons so the correct ones are pressed.
     */
    private void initialiseTopButtons() {
        var buttonHBox = (HBox) mainPane.getTop();
        ((ToggleButton) buttonHBox.getChildren().get(selectedView.ordinal() - 1)).fire();
    }

    /**
     * Prefires the bottom ToggleButtons so the correct ones are pressed.
     */
    private void initialiseBottomButtons() {
        var bottomHBox = (HBox) mainPane.getBottom();
        var buttonHBox = (HBox) bottomHBox.getChildren().get(0);
        if (smallerRunway) {
            ((ToggleButton) buttonHBox.getChildren().get(0)).fire();
        } else {
            ((ToggleButton) buttonHBox.getChildren().get(1)).fire();
        }
    }

    /**
     * Build the layout of the scene
     */
    @Override
    public void build() {
        Platform.runLater(() -> this.scene.getRoot().setEffect(Grayscale.grayscale));
        this.displayCalcBreakdownGridPane();
        this.displayTopButtons();
        this.displayBottomButtons();
    }
}
