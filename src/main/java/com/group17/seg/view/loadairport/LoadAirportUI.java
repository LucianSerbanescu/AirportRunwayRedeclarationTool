package com.group17.seg.view.loadairport;

import com.group17.seg.io.ObstacleXMLParser;
import com.group17.seg.model.*;
import com.group17.seg.utility.Grayscale;
import com.group17.seg.utility.Notification;
import com.group17.seg.utility.Utility;
import com.group17.seg.view.AppWindow;
import com.group17.seg.view.BaseUI;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Alert;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.nio.file.Path;
import java.util.Collections;

public class LoadAirportUI extends BaseUI {
    //<editor-fold desc="Attributes">
    protected final BorderPane mainBorderPane;
    protected final Airport airport;
    protected final AirportList airportList;
    protected ObstacleList obstacleList;
    protected final SimpleBooleanProperty smallerRunway;
    protected final SimpleBooleanProperty topDownView;
    protected final SimpleObjectProperty<PhysicalRunway.Runway> selectedView;
    protected final SimpleObjectProperty<RunwayId> selectedRunwayId;
    protected final ObstacleObservable selectedObstacle;
    protected final ObstacleObservable newObstacle;
    protected final PhysicalRunwayObservable selectedPhysicalRunway;
    protected final FileChooser fileChooser;
    protected final String cwd;
    protected final ToggleGroup runwaySelectionToggleGroup;
    protected final ToggleGroup obstacleSelectionToggleGroup;
    private final Alert xmlErrorAlert;
    private final Timeline xmlErrorTimeline;
    private Notification notification;
    //</editor-fold>

    public LoadAirportUI(AppWindow window, Airport airport, AirportList airportList) {
        super(window);
        this.airport = airport;
        this.airportList = airportList;
        this.smallerRunway = new SimpleBooleanProperty(true);
        this.topDownView = new SimpleBooleanProperty(true);
        this.selectedView = new SimpleObjectProperty<>(PhysicalRunway.Runway.LANDINGTOWARDS);
        this.selectedRunwayId = new SimpleObjectProperty<>();
        this.selectedObstacle = new ObstacleObservable();
        this.newObstacle = new ObstacleObservable();
        this.selectedPhysicalRunway = new PhysicalRunwayObservable();
        this.fileChooser = new FileChooser();
        this.fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML", "*.xml"));
        this.cwd = Path.of("").toAbsolutePath().toString();
        this.runwaySelectionToggleGroup = new ToggleGroup();
        this.obstacleSelectionToggleGroup = new ToggleGroup();
        this.mainBorderPane = new BorderPane();
        this.xmlErrorAlert = new Alert(Alert.AlertType.ERROR);
        this.xmlErrorAlert.setHeaderText("XML Unmarshalling Error");
        this.xmlErrorAlert.setContentText("The XML file was invalid.");
        this.xmlErrorTimeline = new Timeline(new KeyFrame(Duration.seconds(5), e -> this.xmlErrorAlert.close()));
        this.loadObstacleXML();
        this.initialise();
        Platform.runLater(() -> this.notification = new Notification(this.getPrimaryStage(), 2));
    }

    public void showNotification(String message) {
        this.notification.showNotification(message);
    }

    private void showError() {
        this.xmlErrorAlert.show();
        this.xmlErrorTimeline.play();
    }

    /**
     * Shows and hides the rightPane according to the argument.
     *
     * @param arg boolean
     */
    void displayRightPane(boolean arg) {
        var rightPane = (BorderPane) this.mainBorderPane.getRight();
        rightPane.setVisible(arg);
        if (arg) rightPane.setCenter(new RunwayDisplay(this).getNode());
    }

    /**
     * Initialises listeners to update the selectedPhysicalRunway when a button is pressed.
     */
    private void initialise() {
        ChangeListener<Object> runwayParameterChangeListener = (observable, oldValue, newValue) -> {
            var selectedRunwayId = this.selectedRunwayId.get();
            if (selectedRunwayId == null) {
                this.selectedPhysicalRunway.set(null);
            } else {
                this.selectedPhysicalRunway.set(this.airport.getPhysicalRunway(selectedRunwayId));
            }
        };
        this.smallerRunway.addListener(runwayParameterChangeListener);
        this.selectedRunwayId.addListener(runwayParameterChangeListener);
        this.selectedView.addListener(runwayParameterChangeListener);
    }

    /**
     * The main method of the LoadAirportUI class. Builds the left and right pane.
     */
    @Override
    public void build() {
        Platform.runLater(() -> this.scene.getRoot().setEffect(Grayscale.grayscale));

        var leftGridPane = new GridPane();
        var rightPane = new BorderPane();
        mainBorderPane.setLeft(leftGridPane);
        mainBorderPane.setRight(rightPane);

        Utility.setStyle(mainBorderPane, "window");
        Utility.setStyle(leftGridPane, "gridpane-loadairport");
        Utility.setStyle(rightPane, "window");

        mainBorderPane.prefWidthProperty().bind(this.root.widthProperty());

        // Left Border Pane Initialisation
        leftGridPane.maxHeightProperty().bind(this.root.heightProperty());
        leftGridPane.setPadding(new Insets(5));
        leftGridPane.setVgap(5);
        leftGridPane.setId("leftGridPane");

        var airportTitleConstraint = new RowConstraints();
        var runwaySelectionConstraint = new RowConstraints();
        var obstacleSelectConstraint = new RowConstraints();
        var gridPaneColumnConstraint = new ColumnConstraints();
        airportTitleConstraint.setVgrow(Priority.NEVER);
        runwaySelectionConstraint.setVgrow(Priority.ALWAYS);
        runwaySelectionConstraint.setValignment(VPos.TOP);
        obstacleSelectConstraint.setVgrow(Priority.ALWAYS);
        obstacleSelectConstraint.setValignment(VPos.BOTTOM);
        gridPaneColumnConstraint.setMaxWidth(275);
        gridPaneColumnConstraint.setMinWidth(275);

        leftGridPane.getRowConstraints().addAll(airportTitleConstraint, runwaySelectionConstraint, obstacleSelectConstraint);
        leftGridPane.getColumnConstraints().add(gridPaneColumnConstraint);

        leftGridPane.add(new AirportTitle(this).getNode(), 0, 0);
        leftGridPane.add(new RunwaySelection(this).getNode(), 0, 1);
        leftGridPane.add(new ObstacleSelection(this).getNode(), 0, 2);

        // Right Border Pane Initialisation
        rightPane.prefWidthProperty().bind(this.root.widthProperty().add(-285));
        rightPane.maxHeightProperty().bind(this.root.heightProperty());
        rightPane.setVisible(false);

        rightPane.setTop(new TopButtons(this).getNode());
        rightPane.setBottom(new RunwayCompare(this).getNode());

        this.root.getChildren().add(mainBorderPane);
    }

    private void loadObstacleXML() {
        this.obstacleList = this.getObstacleList();
    }

    /**
     * Retrieves the saved obstacle list, otherwise creates a new file and copies the DefaultObstacles.xml into it.
     *
     * @return ObstacleList (ArrayList of Obstacles)
     */
    private ObstacleList getObstacleList() {
        File obstaclesFile = new File(cwd + "/airport-tool/SavedObstacles.xml");
        try {
            if (obstaclesFile.exists()) {
                return ObstacleXMLParser.parseObstacleXML(obstaclesFile);
            } else {
                var ls = ObstacleXMLParser.parseObstacleXML(getClass().getResourceAsStream("/DefaultObstacles.xml"));
                ObstacleXMLParser.writeObstaclesToXML(cwd + "/airport-tool/SavedObstacles.xml", ls);
                Collections.reverse(ls);
                return ls;
            }
        } catch (JAXBException e) {
            this.showError();
        }
        return new ObstacleList();
    }

    /**
     * Writes the obstacleList to SavedObstacles.xml
     */
    void storeObstacles() {
        ObstacleXMLParser.writeObstaclesToXML(cwd + "/airport-tool/SavedObstacles.xml", this.obstacleList);
    }
}

