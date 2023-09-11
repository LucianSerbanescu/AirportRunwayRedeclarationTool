package com.group17.seg.view.loadairport;

import com.group17.seg.model.PhysicalRunway;
import com.group17.seg.model.RunwayId;
import com.group17.seg.nodes.BetterToggleButton;
import com.group17.seg.view.BaseNode;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

class TopButtons extends BaseNode {
    private final SimpleBooleanProperty smallerRunway;
    private final SimpleObjectProperty<PhysicalRunway.Runway> selectedView;
    private final SimpleObjectProperty<RunwayId> selectedRunwayId;

    TopButtons(LoadAirportUI loadAirportUI) {
        super(loadAirportUI);
        this.smallerRunway = loadAirportUI.smallerRunway;
        this.selectedView = loadAirportUI.selectedView;
        this.selectedRunwayId = loadAirportUI.selectedRunwayId;
    }

    /**
     * These buttons are bound to the smallerRunway Boolean property
     *
     * @return HBox with the two runway direction selection buttons
     */
    private HBox runwayButtonsHBox() {
        var runwayButtonsHBox = new HBox();
        runwayButtonsHBox.setAlignment(Pos.CENTER_LEFT);

        var runwaySelectionToggleGroup = new ToggleGroup();

        var smallerRunwayButton = new BetterToggleButton("toggle-button-runway-selection", runwaySelectionToggleGroup, "smallerRunwayButton");
        var largerRunwayButton = new BetterToggleButton("toggle-button-runway-selection", runwaySelectionToggleGroup, "largerRunwayButton");

        smallerRunwayButton.textProperty().bind(Bindings.createStringBinding(() -> {
            var runwayId = this.selectedRunwayId.get();
            if (runwayId == null) {
                return "";
            } else {
                return runwayId.toStringSmaller();
            }
        }, selectedRunwayId));

        largerRunwayButton.textProperty().bind(Bindings.createStringBinding(() -> {
            var runwayId = this.selectedRunwayId.get();
            if (runwayId == null) {
                return "";
            } else {
                return runwayId.toStringLarger();
            }
        }, selectedRunwayId));

        smallerRunwayButton.setSelected(smallerRunway.get());
        largerRunwayButton.setSelected(!smallerRunway.get());

        this.smallerRunway.bind(smallerRunwayButton.selectedProperty());

        smallerRunwayButton.setOnAction(e -> {
            if (!smallerRunwayButton.isSelected()) {
                smallerRunwayButton.setSelected(true);
            }
        });
        largerRunwayButton.setOnAction(e -> {
            if (!largerRunwayButton.isSelected()) {
                largerRunwayButton.setSelected(true);
            }
        });

        runwayButtonsHBox.getChildren().addAll(smallerRunwayButton, largerRunwayButton);

        return runwayButtonsHBox;
    }

    private HBox viewButtonsHBox() {
        var viewButtonsHBox = new HBox();
        viewButtonsHBox.setId("viewButtonsHBox");
        viewButtonsHBox.setAlignment(Pos.CENTER_RIGHT);

        var viewSelectionToggleGroup = new ToggleGroup();
        viewSelectionToggleGroup
                .selectedToggleProperty()
                .addListener((observable, oldValue, newValue) -> selectedView.set((PhysicalRunway.Runway) newValue.getUserData()));

        var landingTowardsButton = new BetterToggleButton("Landing Towards",
                                                          "toggle-button-view-selection",
                                                          viewSelectionToggleGroup,
                                                          "landingTowardsButton");
        var landingOverButton = new BetterToggleButton("Landing Over", "toggle-button-view-selection", viewSelectionToggleGroup, "landingOverButton");
        var takeOffAwayFromButton = new BetterToggleButton("Take-Off Away",
                                                           "toggle-button-view-selection",
                                                           viewSelectionToggleGroup,
                                                           "takeOffAwayFromButton");
        var takeOffTowardsButton = new BetterToggleButton("Take-Off Towards",
                                                          "toggle-button-view-selection",
                                                          viewSelectionToggleGroup,
                                                          "takeOffTowardsButton");

        landingTowardsButton.setUserData(PhysicalRunway.Runway.LANDINGTOWARDS);
        landingOverButton.setUserData(PhysicalRunway.Runway.LANDINGOVER);
        takeOffAwayFromButton.setUserData(PhysicalRunway.Runway.TAKEOFFAWAYFROM);
        takeOffTowardsButton.setUserData(PhysicalRunway.Runway.TAKEOFFTOWARDS);

        switch (selectedView.get()) {
            case LANDINGTOWARDS -> viewSelectionToggleGroup.selectToggle(landingTowardsButton);
            case LANDINGOVER -> viewSelectionToggleGroup.selectToggle(landingOverButton);
            case TAKEOFFAWAYFROM -> viewSelectionToggleGroup.selectToggle(takeOffAwayFromButton);
            case TAKEOFFTOWARDS -> viewSelectionToggleGroup.selectToggle(takeOffTowardsButton);
        }

        viewButtonsHBox.getChildren().addAll(landingTowardsButton, landingOverButton, takeOffAwayFromButton, takeOffTowardsButton);

        viewButtonsHBox.getChildren().forEach(node -> {
            var toggleButton = (BetterToggleButton) node;
            toggleButton.setOnAction(e -> {
                if (!toggleButton.isSelected()) {
                    toggleButton.setSelected(true);
                }
            });
        });
        return viewButtonsHBox;
    }

    @Override
    public Node getNode() {
        var topButtonsGridPane = new GridPane();
        topButtonsGridPane.getStyleClass().clear();
        topButtonsGridPane.setPadding(new Insets(5));

        var runwaySelectionConstraint = new ColumnConstraints();
        var viewSelectionConstraint = new ColumnConstraints();
        runwaySelectionConstraint.setHgrow(Priority.SOMETIMES);
        runwaySelectionConstraint.setHalignment(HPos.LEFT);
        viewSelectionConstraint.setHgrow(Priority.SOMETIMES);
        viewSelectionConstraint.setHalignment(HPos.RIGHT);

        topButtonsGridPane.getColumnConstraints().addAll(runwaySelectionConstraint, viewSelectionConstraint);

        topButtonsGridPane.add(this.runwayButtonsHBox(), 0, 0);
        topButtonsGridPane.add(this.viewButtonsHBox(), 1, 0);

        return topButtonsGridPane;
    }
}
