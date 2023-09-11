package com.group17.seg.view.loadairport;

import com.group17.seg.model.*;
import com.group17.seg.nodes.Arrow;
import com.group17.seg.nodes.BetterToggleButton;
import com.group17.seg.nodes.ResizableRectangle;
import com.group17.seg.nodes.ZoomableScrollPane;
import com.group17.seg.utility.Grayscale;
import com.group17.seg.utility.Utility;
import com.group17.seg.view.BaseNode;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

class RunwayDisplay extends BaseNode {
    private final Airport airport;
    private final SimpleBooleanProperty smallerRunway;
    private final SimpleBooleanProperty topDownView;
    private final SimpleObjectProperty<RunwayId> selectedRunwayId;
    private final SimpleObjectProperty<PhysicalRunway.Runway> selectedView;
    private double lastMouseX;
    private double lastMouseY;

    RunwayDisplay(LoadAirportUI loadAirportUI) {
        super(loadAirportUI);
        this.airport = loadAirportUI.airport;
        this.smallerRunway = loadAirportUI.smallerRunway;
        this.topDownView = loadAirportUI.topDownView;
        this.selectedRunwayId = loadAirportUI.selectedRunwayId;
        this.selectedView = loadAirportUI.selectedView;
    }

    protected double scaleCalculation(double valueToBeChanged, double runwayWidth, double asda) {
        return ((valueToBeChanged / asda) * runwayWidth);
    }

    private double originalLDAPublic = 0;
    private double originalToraPublic = 0;
    private double objectWidthScale = 0;
    private Rectangle globalObject;
    private boolean isRotated = false;
    boolean isActionEnabled = false;


    //show display draw runway
    protected VBox drawRunway(Boolean onlyDisplay, Boolean viewFromSide) {
        // get the physical runway
        PhysicalRunway physicalRunway = this.airport.getPhysicalRunway(this.selectedRunwayId.get());

        // get the current logical runway (either smaller runway or bigger runway, e.g. 09/27)
        LogicalRunwayList runways = this.smallerRunway.get() ? physicalRunway.getSmallerNumberedRunways() : physicalRunway.getLargerNumberedRunways();

        // the logical runway that will be displayed
        LogicalRunway runwayToDisplay = runways.get(this.selectedView.get().ordinal());

        // StackPane containing all the features of the runway
        StackPane rightPanel = new StackPane();
        rightPanel.setAlignment(Pos.CENTER);
        rightPanel.setMinHeight(395);
        rightPanel.setMinWidth(638);
        //rightPanel.setStyle("-fx-background-color: white");

        //Background for the whole right panel
        Rectangle Background = new ResizableRectangle(800, 500);
        Background.setFill(Color.valueOf("#FDF7E9"));
        rightPanel.getChildren().add(Background);

        // add image on the back of the runway
        Image backgroundImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/backgroundRunway.png")));
        ImageView imgV = new ImageView(backgroundImage);
        rightPanel.getChildren().add(imgV);

        // add image on the back of the side on runway
        Image sideOnBackGround = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/sideOnBackground3.png")));
        if (Grayscale.isGrayscale) {
            System.out.println("hey");
            sideOnBackGround = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/sideOnDarker.png")));
        }
        ImageView imgSideOn = new ImageView(sideOnBackGround);

        Image displacedThresholdImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/thresholdImage.png")));
        ImageView imgThreshold = new ImageView(sideOnBackGround);

        // The actual runway rectangle (dark)
        Rectangle runway = new ResizableRectangle(500, 42);
        runway.setY(rightPanel.getHeight() / 2);
        runway.setFill(Color.valueOf("#2D343B"));

        //Rectangle indicators for the program
        //
        // the threshold indicator of the runway
        // shown as a white, thick line on the runway
        Rectangle displayedThresholdIndicator = new ResizableRectangle(2, 42);
        displayedThresholdIndicator.setFill(Color.WHITE);

        // the stop way indicator
        // shown as a yellow, thick line on the runway
        Rectangle stopWayIndicator = new ResizableRectangle(2, 42);
        stopWayIndicator.setFill(Color.YELLOW);
        Text stopWayText = new Text("StopWayText");
        stopWayText.setStyle("runway-name");

        Rectangle stopWayColourBlock = new ResizableRectangle(1, 42);
        stopWayColourBlock.setFill(Color.YELLOW);

        // the factor that the runway will be scaled to the screen
        double todaScale = scaleCalculation(runwayToDisplay.getToda(), runway.getWidth(), runwayToDisplay.getOriginalRunway().getAsda());
        double asdaScale = scaleCalculation(runwayToDisplay.getAsda(), runway.getWidth(), runwayToDisplay.getOriginalRunway().getAsda());
        double toraScale = scaleCalculation(runwayToDisplay.getTora(), runway.getWidth(), runwayToDisplay.getOriginalRunway().getAsda());
        double ldaScale = scaleCalculation(runwayToDisplay.getLda() > 1 ? runwayToDisplay.getLda() : runwayToDisplay.getTora(),
                                           runway.getWidth(),
                                           runwayToDisplay.getOriginalRunway().getAsda());

        // the original numbers of the runway
        double originalTodaScale = scaleCalculation(runwayToDisplay.getOriginalRunway().getToda(),
                                                    runway.getWidth(),
                                                    runwayToDisplay.getOriginalRunway().getAsda());
        double originalAsdaScale = scaleCalculation(runwayToDisplay.getOriginalRunway().getAsda(),
                                                    runway.getWidth(),
                                                    runwayToDisplay.getOriginalRunway().getAsda());
        double originalToraScale = scaleCalculation(runwayToDisplay.getOriginalRunway().getTora(),
                                                    runway.getWidth(),
                                                    runwayToDisplay.getOriginalRunway().getAsda());
        double originalLdaScale = scaleCalculation(runwayToDisplay.getOriginalRunway().getLda(),
                                                   runway.getWidth(),
                                                   runwayToDisplay.getOriginalRunway().getAsda());

        this.originalLDAPublic = originalLdaScale;
        this.originalToraPublic = originalToraScale;


        int x = 20; // equal spacing between text and lines

        // the arrows indicating TODA, TORA, ASDA, LDA on the runway
        Arrow todaArrow = new Arrow(runway.getX(), 0, originalTodaScale, 0);
        todaArrow.setTranslateY(-(6 * x));
        Arrow asdaArrow = new Arrow(runway.getX() + 3, -100, originalAsdaScale, -100);
        asdaArrow.setTranslateY(-(4.5 * x));
        Arrow toraArrow = new Arrow(runway.getX(), 30, originalToraScale, 30);
        toraArrow.setTranslateY(-(3 * x));
        Arrow ldaArrow = new Arrow(runway.getX(), 40, originalLdaScale, 40);
        ldaArrow.setTranslateY((3.5 * x));
        todaArrow.getStyleClass().add("rectangle-for-arrow");


        // Labels on the runway arrows
        Text todaTextDisplay = new Text(String.format("TODA: %.1f", runwayToDisplay.getToda()));
        Text asdaTextDisplay = new Text(String.format("ASDA: %.1f", runwayToDisplay.getAsda()));
        Text toraTextDisplay = new Text(String.format("TORA: %.1f", runwayToDisplay.getTora()));
        Text ldaTextDisplay = new Text(String.format("LDA:" + Math.round(runwayToDisplay.getLda())));


        // Put the texts into the relevant positions on the diagram
        int textTranslation = 213;
        todaTextDisplay.setTranslateY(-6.4 * x);
        todaTextDisplay.setTranslateX(-textTranslation + 3);
        asdaTextDisplay.setTranslateY(-4.9 * x);
        asdaTextDisplay.setTranslateX(-textTranslation + 3);
        toraTextDisplay.setTranslateY(-3.4 * x);
        toraTextDisplay.setTranslateX(-textTranslation + 3);
        ldaTextDisplay.setTranslateY(3.1 * x);
        ldaTextDisplay.setTranslateX(-textTranslation + (originalToraScale - originalLdaScale));

        // Create the rectangles for zebra bar
        Rectangle zebraBar1 = new ResizableRectangle(35, 5);
        zebraBar1.setFill(Color.valueOf("#E5E5E5"));
        Rectangle zebraBar2 = new ResizableRectangle(35, 5);
        zebraBar2.setFill(Color.valueOf("#E5E5E5"));
        Rectangle zebraBar3 = new ResizableRectangle(35, 5);
        zebraBar3.setFill(Color.valueOf("#E5E5E5"));
        Rectangle zebraBar4 = new ResizableRectangle(35, 5);
        zebraBar4.setFill(Color.valueOf("#E5E5E5"));

        // Create vbox for the zebra bar
        VBox zebraBarLeft = new VBox(4);
        VBox.setVgrow(zebraBarLeft, Priority.NEVER);
        zebraBarLeft.setAlignment(Pos.CENTER);
        zebraBarLeft.getChildren().addAll(zebraBar1, zebraBar2, zebraBar3, zebraBar4);
        zebraBarLeft.setMaxWidth(30);
        zebraBarLeft.setMaxHeight(30);

        // set the name for the left runway
        HBox leftRunwayName = new HBox();
        HBox.setHgrow(leftRunwayName, Priority.NEVER);
        leftRunwayName.setMaxHeight(17);
        leftRunwayName.setMaxWidth(10);
        leftRunwayName.setAlignment(Pos.CENTER);
        leftRunwayName.setSpacing(-2);
        var Angle = physicalRunway.getRunwayId().getAngle(this.smallerRunway.get());
        Text leftAngle = new Text(Angle);
        Text leftPosition = new Text();
        try {
            leftPosition = new Text(Character.toString(physicalRunway.getRunwayId().getPosition().toString().toCharArray()[0]));
        } catch (NullPointerException e) {
            //e.printStackTrace();
        }
        leftPosition.setRotate(90);
        leftPosition.getStyleClass().add("runway-position");
        leftPosition.setStyle("-fx-background-color: green");
        leftAngle.setRotate(90);
        leftAngle.getStyleClass().add("runway-name");
        leftAngle.setStyle("-fx-background-color: green");
        leftRunwayName.getChildren().addAll(leftPosition, leftAngle);

        // center line of the runway
        Line centerLine = new Line(0, 0, 0, 0);
        centerLine.setStyle("-fx-stroke: #E5E5E5");
        centerLine.setStartX(-175 + (originalToraScale - originalLdaScale));
        centerLine.setEndX(-249 + (originalToraScale));
        centerLine.getStrokeDashArray().addAll(25d, 10d);
        centerLine.setStrokeWidth(2.0);

        // Add all the previous elements onto the diagram
        rightPanel.getChildren().addAll(runway,
                                        ldaArrow,
                                        todaArrow,
                                        asdaArrow,
                                        toraArrow,
                                        ldaTextDisplay,
                                        toraTextDisplay,
                                        todaTextDisplay,
                                        asdaTextDisplay,
                                        centerLine);

        // Make sure none of the parameters are smaller than 1
        if (runwayToDisplay.getToda() < 1) {
            rightPanel.getChildren().remove(todaArrow);
            todaTextDisplay.setText("No TODA Available");
        }
        if (runwayToDisplay.getAsda() < 1) {
            rightPanel.getChildren().remove(asdaArrow);
            asdaTextDisplay.setText("No ASDA Available");
        }
        if (runwayToDisplay.getTora() < 1) {
            rightPanel.getChildren().remove(toraArrow);
            toraTextDisplay.setText("No TORA Available");
        }

        // the clear way indicator
        Rectangle clearWayIndicator1 = new ResizableRectangle(originalTodaScale - originalAsdaScale, 42);
        clearWayIndicator1.setFill(Color.TRANSPARENT);
        clearWayIndicator1.setStroke(Color.BLACK);
        clearWayIndicator1.setArcHeight(5);
        clearWayIndicator1.setArcWidth(5);


        //Arrow and text for landing directions
        Arrow directionArrow = new Arrow(0, 0, 90, 0);
        Text directionText = new Text("Landing Direction");
        rightPanel.getChildren().addAll(directionText, directionArrow);
        VBox arrowTextDirection = new VBox();
        arrowTextDirection.getChildren().addAll(directionText, directionArrow);

        // Add blast radius arrow and labels
        Arrow blastRadiusArrow = new Arrow(0, 0, scaleCalculation(500, 500, runwayToDisplay.getOriginalRunway().getAsda()), 0);
        blastRadiusArrow.setStroke(Color.WHITE);
        Text blastLabel = new Text("BR: 500");
        blastLabel.setStyle("-fx-fill: white; -fx-font-weight: bold;");
        //blastLabel.setStroke(Color.BLACK);
        //blastLabel.setStrokeWidth(0.7);
        blastLabel.translateYProperty().bind(blastRadiusArrow.translateYProperty().subtract(10));
        blastLabel.translateXProperty().bind(blastRadiusArrow.translateXProperty());
        rightPanel.getChildren().addAll(blastRadiusArrow, blastLabel);

        //Adds arrow and text for resa
        Arrow resaArrow = new Arrow(0, 0, scaleCalculation(240, 500, runwayToDisplay.getOriginalRunway().getAsda()), 0);
        resaArrow.setStroke(Color.WHITE);
        Text resaLabel = new Text("RESA+SE:300");
        Utility.setStyle(resaLabel, "resafont");
        resaLabel.setStyle("-fx-fill: white; -fx-font-weight: bold;");
//        resaLabel.setStroke(Color.BLACK);
//        resaLabel.setStrokeWidth(0.3);
        resaLabel.translateYProperty().bind(resaArrow.translateYProperty().subtract(10));
        resaLabel.translateXProperty().bind(resaArrow.translateXProperty().add(24));
        if (this.selectedView.get() == PhysicalRunway.Runway.LANDINGOVER) {
            resaLabel.translateXProperty().bind(resaArrow.translateXProperty().subtract(24));
        }
        rightPanel.getChildren().addAll(resaArrow, resaLabel);


        clearWayIndicator1.setVisible(originalTodaScale - originalAsdaScale != 0 && todaScale - ldaScale != 0);

        //Vertical lines to represent the different parts
        Line ldaStart = new Line(0, 0, 0, 0);
        //ldaStart.setStyle("-fx-stroke: #E5E5E5");
        ldaStart.setStartY(0);
        ldaStart.setEndY(50);
        ldaStart.getStrokeDashArray().addAll(5d, 5d);
        ldaStart.setStrokeWidth(2);
        Line ldaEnd = new Line(0, 0, 0, 0);
        //ldaEnd.setStyle("-fx-stroke: #E5E5E5");
        ldaEnd.setStartY(0);
        ldaEnd.setEndY(50);
        ldaEnd.getStrokeDashArray().addAll(5d, 5d);
        ldaEnd.setStrokeWidth(2);
        Line toraStart = new Line(0, 0, 0, 0);
        toraStart.setStyle("-fx-stroke: #E5E5E5");
        toraStart.setStartY(0);
        toraStart.setEndY(40);
        toraStart.getStrokeDashArray().addAll(5d, 5d);
        toraStart.setStrokeWidth(2);
        Line toraEnd = new Line(0, 0, 0, 0);
        //toraEnd.setStyle("-fx-stroke: #E5E5E5");
        toraEnd.setStartY(0);
        toraEnd.setEndY(40);
        toraEnd.getStrokeDashArray().addAll(5d, 5d);
        toraEnd.setStrokeWidth(2);
        Line asdaStart = new Line(0, 0, 0, 0);
        //asdaStart.setStyle("-fx-stroke: #E5E5E5");
        asdaStart.setStartY(0);
        asdaStart.setEndY(70);
        asdaStart.getStrokeDashArray().addAll(5d, 5d);
        asdaStart.setStrokeWidth(2);
        Line asdaEnd = new Line(0, 0, 0, 0);
        //asdaEnd.setStyle("-fx-stroke: #E5E5E5");
        asdaEnd.setStartY(0);
        asdaEnd.setEndY(70);
        asdaEnd.getStrokeDashArray().addAll(5d, 5d);
        asdaEnd.setStrokeWidth(2);
        Line todaStart = new Line(0, 0, 0, 0);
        //todaStart.setStyle("-fx-stroke: #E5E5E5");
        todaStart.setStartY(0);
        todaStart.setEndY(100);
        todaStart.getStrokeDashArray().addAll(5d, 5d);
        todaStart.setStrokeWidth(2);
        Line todaEnd = new Line(0, 0, 0, 0);
        //todaEnd.setStyle("-fx-stroke: #E5E5E5");
        todaEnd.setStartY(0);
        todaEnd.setEndY(100);
        todaEnd.getStrokeDashArray().addAll(5d, 5d);
        todaEnd.setStrokeWidth(2);

        // Add elements into the right panel
        //rightPanel.getChildren().add(imgThreshold);
        rightPanel.getChildren().add(displayedThresholdIndicator);
        rightPanel.getChildren().add(stopWayIndicator);
        rightPanel.getChildren().add(zebraBarLeft);
        rightPanel.getChildren().add(leftRunwayName);
        rightPanel.getChildren().add(clearWayIndicator1);
        rightPanel.getChildren().add(ldaStart);
        rightPanel.getChildren().add(ldaEnd);
        rightPanel.getChildren().add(toraStart);
        rightPanel.getChildren().add(toraEnd);
        rightPanel.getChildren().add(asdaStart);
        rightPanel.getChildren().add(asdaEnd);
        rightPanel.getChildren().add(todaStart);
        rightPanel.getChildren().add(todaEnd);
        rightPanel.getChildren().add(stopWayColourBlock);

        //
        String tooSmall = " is too small";


        // place the elements into the relevant positions
        defaultRunwayLayoutCreation(runway,
                                    ldaTextDisplay,
                                    runwayToDisplay,
                                    todaTextDisplay,
                                    asdaTextDisplay,
                                    textTranslation,
                                    toraTextDisplay,
                                    originalToraScale,
                                    originalLdaScale,
                                    originalTodaScale,
                                    originalAsdaScale,
                                    leftRunwayName,
                                    zebraBarLeft,
                                    displayedThresholdIndicator,
                                    stopWayIndicator,
                                    clearWayIndicator1,
                                    todaArrow,
                                    asdaArrow,
                                    toraArrow,
                                    ldaArrow,
                                    centerLine,
                                    ldaStart,
                                    ldaEnd,
                                    toraStart,
                                    toraEnd,
                                    asdaStart,
                                    asdaEnd,
                                    todaStart,
                                    todaEnd,
                                    stopWayColourBlock);

        boolean removeEverything = false;
        // Paint the runway given the selected view
        // (landing over obstacle, takeoff over obstacle, etc.)
        switch (this.selectedView.get()) {
            case LANDINGOVER -> {
                directionText.setText("Landing Direction");
                rightPanel.getChildren().removeAll(todaArrow,
                                                   asdaArrow,
                                                   toraArrow,
                                                   toraTextDisplay,
                                                   todaTextDisplay,
                                                   asdaTextDisplay,
                                                   blastRadiusArrow,
                                                   blastLabel,
                                                   clearWayIndicator1,
                                                   toraStart,
                                                   toraEnd,
                                                   asdaStart,
                                                   asdaEnd,
                                                   todaStart,
                                                   todaEnd,
                                                   stopWayColourBlock);
                LandingOver(ldaTextDisplay,
                            zebraBarLeft,
                            displayedThresholdIndicator,
                            stopWayIndicator,
                            ldaArrow,
                            centerLine,
                            leftRunwayName,
                            resaArrow,
                            runwayToDisplay,
                            1.0,
                            textTranslation,
                            ldaStart,
                            ldaEnd,
                            toraScale,
                            ldaScale);

                // Checks to ensure displaced threshold is fixed when object far away
                if ((((toraScale - ldaScale) + objectWidthScale / 2) < (originalToraScale - originalLdaScale)) || runwayToDisplay.getObstacle() == null) {
                    rightPanel.getChildren().addAll(clearWayIndicator1, stopWayColourBlock);
                    rightPanel.getChildren().removeAll(resaArrow, resaLabel, blastRadiusArrow, blastLabel);
                    defaultRunwayLayoutCreation(runway,
                                                ldaTextDisplay,
                                                runwayToDisplay,
                                                todaTextDisplay,
                                                asdaTextDisplay,
                                                textTranslation,
                                                toraTextDisplay,
                                                originalToraScale,
                                                originalLdaScale,
                                                originalTodaScale,
                                                originalAsdaScale,
                                                leftRunwayName,
                                                zebraBarLeft,
                                                displayedThresholdIndicator,
                                                stopWayIndicator,
                                                clearWayIndicator1,
                                                todaArrow,
                                                asdaArrow,
                                                toraArrow,
                                                ldaArrow,
                                                centerLine,
                                                ldaStart,
                                                ldaEnd,
                                                toraStart,
                                                toraEnd,
                                                asdaStart,
                                                asdaEnd,
                                                todaStart,
                                                todaEnd,
                                                stopWayColourBlock);


                }
                if (runwayToDisplay.getLda() < 1 || objectWidthScale / 2 > ldaScale || (runwayToDisplay.getLda() > 0 && runwayToDisplay.getLda() < 500)) {
                    //Temp design for the no distance available topic
                    removeEverything = true;
                    rightPanel.getChildren().removeAll(ldaArrow,
                                                       stopWayIndicator,
                                                       displayedThresholdIndicator,
                                                       centerLine,
                                                       leftRunwayName,
                                                       zebraBarLeft,
                                                       resaArrow,
                                                       resaLabel,
                                                       clearWayIndicator1, ldaStart, ldaEnd, stopWayColourBlock);

                    ldaTextDisplay.setTranslateX(-textTranslation + (originalToraScale - originalLdaScale));
                    ldaTextDisplay.setText("LDA " + tooSmall);
                }
            }
            case LANDINGTOWARDS -> {
                directionText.setText("Landing Direction");
                rightPanel.getChildren().removeAll(todaArrow,
                                                   asdaArrow,
                                                   toraArrow,
                                                   toraTextDisplay,
                                                   todaTextDisplay,
                                                   asdaTextDisplay,
                                                   blastRadiusArrow,
                                                   blastLabel,
                                                   clearWayIndicator1,
                                                   toraStart,
                                                   toraEnd,
                                                   asdaStart,
                                                   asdaEnd,
                                                   todaStart,
                                                   todaEnd,
                                                   stopWayColourBlock);
                LandingTowards(ldaTextDisplay,
                               zebraBarLeft,
                               displayedThresholdIndicator,
                               stopWayIndicator,
                               ldaArrow,
                               centerLine,
                               leftRunwayName,
                               resaArrow,
                               runwayToDisplay,
                               1.0,
                               textTranslation,
                               ldaStart,
                               ldaEnd,
                               toraScale,
                               ldaScale,
                               originalToraScale,
                               originalLdaScale);


                // Checks to ensure stopway is fixed when object far away
                if (((originalToraScale - originalLdaScale) + ldaScale - objectWidthScale / 2) > (originalToraScale) || runwayToDisplay.getObstacle() == null) {
                    rightPanel.getChildren().addAll(clearWayIndicator1, stopWayColourBlock);
                    rightPanel.getChildren().removeAll(resaArrow, resaLabel, blastRadiusArrow, blastLabel);
                    defaultRunwayLayoutCreation(runway,
                                                ldaTextDisplay,
                                                runwayToDisplay,
                                                todaTextDisplay,
                                                asdaTextDisplay,
                                                textTranslation,
                                                toraTextDisplay,
                                                originalToraScale,
                                                originalLdaScale,
                                                originalTodaScale,
                                                originalAsdaScale,
                                                leftRunwayName,
                                                zebraBarLeft,
                                                displayedThresholdIndicator,
                                                stopWayIndicator,
                                                clearWayIndicator1,
                                                todaArrow,
                                                asdaArrow,
                                                toraArrow,
                                                ldaArrow,
                                                centerLine,
                                                ldaStart,
                                                ldaEnd,
                                                toraStart,
                                                toraEnd,
                                                asdaStart,
                                                asdaEnd,
                                                todaStart,
                                                todaEnd,
                                                stopWayColourBlock);


                }
                //Checks if object overlaps with the displaced threshold then no lda available
                if (runwayToDisplay.getLda() < 1 || objectWidthScale / 2 > ldaScale || (runwayToDisplay.getLda() > 0 && runwayToDisplay.getLda() < 500)) {
                    //Temp design for the no distance available topic
                    removeEverything = true;
                    rightPanel.getChildren().removeAll(ldaArrow,
                                                       stopWayIndicator,
                                                       displayedThresholdIndicator,
                                                       centerLine,
                                                       leftRunwayName,
                                                       zebraBarLeft,
                                                       resaArrow,
                                                       resaLabel,
                                                       blastRadiusArrow,
                                                       blastLabel,
                                                       clearWayIndicator1, ldaStart, ldaEnd, stopWayColourBlock);
                    ldaTextDisplay.setTranslateX(-textTranslation + (originalToraScale - originalLdaScale));
                    ldaTextDisplay.setText("LDA " + tooSmall);
                }
            }
            case TAKEOFFAWAYFROM -> {
                directionText.setText("Take-Off Direction");
                rightPanel.getChildren().removeAll(ldaArrow, ldaTextDisplay, resaArrow, resaLabel, ldaStart, ldaEnd);
                if (!rightPanel.getChildren().contains(displayedThresholdIndicator)) {
                    rightPanel.getChildren().add(displayedThresholdIndicator);
                }
                if (!rightPanel.getChildren().contains(blastRadiusArrow) && runwayToDisplay.getObstacle() != null) {
                    rightPanel.getChildren().addAll(blastRadiusArrow, blastLabel);
                }

                TakingOffAwayFrom(ldaTextDisplay,
                                  toraTextDisplay,
                                  todaTextDisplay,
                                  asdaTextDisplay,
                                  zebraBarLeft,
                                  displayedThresholdIndicator,
                                  stopWayIndicator,
                                  clearWayIndicator1,
                                  centerLine,
                                  leftRunwayName,
                                  toraArrow,
                                  todaArrow,
                                  asdaArrow,
                                  blastRadiusArrow,
                                  runwayToDisplay,
                                  toraScale,
                                  ldaScale,
                                  asdaScale,
                                  todaScale,
                                  objectWidthScale,
                                  originalToraScale,
                                  originalAsdaScale,
                                  originalTodaScale,
                                  textTranslation,
                                  toraStart,
                                  toraEnd,
                                  asdaStart,
                                  asdaEnd,
                                  todaStart,
                                  todaEnd,
                                  stopWayColourBlock);


                if (runwayToDisplay.getTora() > runwayToDisplay.getOriginalRunway().getTora() || runwayToDisplay.getObstacle() == null) {
                    rightPanel.getChildren().removeAll(blastRadiusArrow, blastLabel, resaArrow, resaLabel);
                    defaultRunwayLayoutCreation(runway,
                                                ldaTextDisplay,
                                                runwayToDisplay,
                                                todaTextDisplay,
                                                asdaTextDisplay,
                                                textTranslation,
                                                toraTextDisplay,
                                                originalToraScale,
                                                originalLdaScale,
                                                originalTodaScale,
                                                originalAsdaScale,
                                                leftRunwayName,
                                                zebraBarLeft,
                                                displayedThresholdIndicator,
                                                stopWayIndicator,
                                                clearWayIndicator1,
                                                todaArrow,
                                                asdaArrow,
                                                toraArrow,
                                                ldaArrow,
                                                centerLine,
                                                ldaStart,
                                                ldaEnd,
                                                toraStart,
                                                toraEnd,
                                                asdaStart,
                                                asdaEnd,
                                                todaStart,
                                                todaEnd,
                                                stopWayColourBlock);

                }
                if (((runwayToDisplay.getLda() < 1 || objectWidthScale / 2 > ldaScale) && runwayToDisplay.getObstacle() != null) || (runwayToDisplay.getTora() > 0 && runwayToDisplay.getTora() < 500)) {
                    removeEverything = true;
                    //Temp design for the no distance available topic
                    rightPanel.getChildren().removeAll(asdaArrow,
                                                       toraArrow,
                                                       todaArrow,
                                                       stopWayIndicator,
                                                       displayedThresholdIndicator,
                                                       centerLine,
                                                       leftRunwayName,
                                                       zebraBarLeft,
                                                       blastRadiusArrow,
                                                       blastLabel,
                                                       clearWayIndicator1,
                                                       toraStart,
                                                       toraEnd,
                                                       asdaStart,
                                                       asdaEnd,
                                                       todaStart,
                                                       todaEnd,
                                                       stopWayColourBlock);
                    toraTextDisplay.setText("TORA " + tooSmall);
                    toraTextDisplay.setTranslateX(-textTranslation);
                    asdaTextDisplay.setText("ASDA " + tooSmall);
                    asdaTextDisplay.setTranslateX(-textTranslation);
                    todaTextDisplay.setText("TODA " + tooSmall);
                    todaTextDisplay.setTranslateX(-textTranslation);
                }
            }
            case TAKEOFFTOWARDS -> {
                directionText.setText("Take-Off Direction");
                rightPanel.getChildren().removeAll(ldaArrow, ldaTextDisplay, blastRadiusArrow, blastLabel, ldaStart, ldaEnd, stopWayColourBlock);
                rightPanel.getChildren().addAll(stopWayColourBlock);
                TakingOffTowards(
                        toraTextDisplay,
                        todaTextDisplay,
                        asdaTextDisplay,
                        zebraBarLeft,
                        stopWayIndicator,
                        clearWayIndicator1,
                        centerLine,
                        leftRunwayName,
                        toraArrow,
                        todaArrow,
                        asdaArrow,
                        resaArrow,
                        toraStart,
                        toraEnd,
                        asdaStart,
                        asdaEnd,
                        todaStart,
                        todaEnd,
                        runwayToDisplay,
                        toraScale,
                        ldaScale,
                        asdaScale,
                        todaScale,
                        objectWidthScale,
                        originalAsdaScale,
                        displayedThresholdIndicator

                );
                if (-249 + toraScale - objectWidthScale / 2 > -249 + originalToraScale - objectWidthScale / 2 || runwayToDisplay.getObstacle() == null) {
                    rightPanel.getChildren().removeAll(resaArrow, resaLabel, blastRadiusArrow, blastLabel);
                    defaultRunwayLayoutCreation(runway,
                                                ldaTextDisplay,
                                                runwayToDisplay,
                                                todaTextDisplay,
                                                asdaTextDisplay,
                                                textTranslation,
                                                toraTextDisplay,
                                                originalToraScale,
                                                originalLdaScale,
                                                originalTodaScale,
                                                originalAsdaScale,
                                                leftRunwayName,
                                                zebraBarLeft,
                                                displayedThresholdIndicator,
                                                stopWayIndicator,
                                                clearWayIndicator1,
                                                todaArrow,
                                                asdaArrow,
                                                toraArrow,
                                                ldaArrow,
                                                centerLine,
                                                ldaStart,
                                                ldaEnd,
                                                toraStart,
                                                toraEnd,
                                                asdaStart,
                                                asdaEnd,
                                                todaStart,
                                                todaEnd,
                                                stopWayColourBlock);

                }
                //Checks if object overlaps with the displaced threshold then no lda available
                if (((runwayToDisplay.getLda() < 1 || objectWidthScale / 2 > ldaScale) && runwayToDisplay.getObstacle() != null) || (runwayToDisplay.getTora() > 0 && runwayToDisplay.getTora() < 500)) {
                    removeEverything = true;
                    //Temp design for the no distance available topic
                    rightPanel.getChildren().removeAll(asdaArrow,
                                                       toraArrow,
                                                       todaArrow,
                                                       stopWayIndicator,
                                                       displayedThresholdIndicator,
                                                       centerLine,
                                                       leftRunwayName,
                                                       zebraBarLeft,
                                                       resaArrow,
                                                       resaLabel,
                                                       clearWayIndicator1,
                                                       toraStart,
                                                       toraEnd,
                                                       asdaStart,
                                                       asdaEnd,
                                                       todaStart,
                                                       todaEnd,
                                                       stopWayColourBlock);
                    toraTextDisplay.setText("TORA " + tooSmall);
                    asdaTextDisplay.setText("ASDA " + tooSmall);
                    todaTextDisplay.setText("TODA " + tooSmall);
                }
            }
        }

        VBox display = new VBox();

        // Adding the object to the runway
        if (runwayToDisplay.getObstacle() != null) rightPanel.getChildren().add(addObject(runwayToDisplay));


        // wrap in scroll pane
        ZoomableScrollPane viewport = new ZoomableScrollPane(rightPanel);
        viewport.setPrefSize(rightPanel.getWidth(), 1000);
        rightPanel.prefWidthProperty().bind(viewport.widthProperty().multiply(1.3));
        rightPanel.prefHeightProperty().bind(viewport.heightProperty().multiply(2.5));
        rightPanel.setScaleX(1); // set zoom factor of right panel to 1
        rightPanel.setScaleY(1); // set zoom factor of right panel to 1
        viewport.getStyleClass().clear();
        viewport.setVvalue(0.5);
        viewport.setHvalue(0.5);


        // Add button container
        VBox center = new VBox();
        center.setAlignment(Pos.CENTER);
        HBox buttonBar = new HBox();
        HBox directionAndButtonBar = new HBox(90);
        directionAndButtonBar.setAlignment(Pos.BOTTOM_CENTER);

        // Add buttons
        ToggleGroup group = new ToggleGroup();
        BetterToggleButton topView = new BetterToggleButton("Top View", "toggle-button-runway-selection", group);
        topView.setStyle("-fx-font-size: 12; -fx-min-height: 24px; -fx-min-width: 3cm; -fx-font-family: Arial;");
        BetterToggleButton sideView = new BetterToggleButton("Side view", "toggle-button-runway-selection", group);
        sideView.setStyle("-fx-font-size: 12; -fx-min-height: 24px; -fx-min-width: 3cm; -fx-font-family: Arial;");


        // Add comparison button
        BetterToggleButton compareView = new BetterToggleButton("Compare", "toggle-button-runway-selection", group);
        compareView.setStyle("-fx-font-size: 12; -fx-min-height: 24px; -fx-min-width: 3cm; -fx-font-family: Arial;");
        buttonBar.getChildren().addAll(topView, sideView);
        directionAndButtonBar.setSpacing(40);
        buttonBar.getChildren().add(compareView);


        // add the als and tocs arrows
        Arrow ALS, TOCS;
        Text alsLabel, tocsLabel;
        if (runwayToDisplay.getObstacle() != null) {
            //ALS Arrow
            double StartX = (globalObject.getTranslateX() + globalObject.getWidth() / 2);
            double StartY = (0);
            double EndX = (-249 + (toraScale - ldaScale));
            double EndY = (runwayToDisplay.getObstacle().getHeight());
            ALS = new Arrow(StartX, StartY, EndX, EndY);
            ALS.setStroke(Color.PURPLE);
            ALS.setTranslateX((globalObject.getTranslateX() + globalObject.getWidth() / 2 - 249 + (toraScale - ldaScale)) / 2);
            ALS.setTranslateY(22 - runwayToDisplay.getObstacle().getHeight() / 2);

            // Add als labels
            alsLabel = new Text("ALS");
            alsLabel.setFill(Color.PURPLE);
            alsLabel.translateXProperty().bind(ALS.translateXProperty());
            alsLabel.translateYProperty().bind(ALS.translateYProperty().subtract(10));

            //TOCS Arrow
            double StartXTOCS = (-249 + toraScale);
            double StartYTOCS = (runwayToDisplay.getObstacle().getHeight());
            double EndXTOCS = (globalObject.getTranslateX() - globalObject.getWidth() / 2);
            double EndYTOCS = (0);
            TOCS = new Arrow(StartXTOCS, StartYTOCS, EndXTOCS, EndYTOCS);
            TOCS.setTranslateX((globalObject.getTranslateX() + globalObject.getWidth() / 2 - 249 + toraScale) / 2);
            TOCS.setTranslateY(20 - runwayToDisplay.getObstacle().getHeight() / 2);

            // Add als labels
            tocsLabel = new Text("TOCS");
            tocsLabel.setStyle("-fx-fill: mediumpurple;");
            tocsLabel.translateXProperty().bind(TOCS.translateXProperty());
            tocsLabel.translateYProperty().bind(TOCS.translateYProperty().subtract(10));
        } else {
            ALS = new Arrow(0, 0, 0, 0);
            ALS.setVisible(false);
            alsLabel = new Text("ALS");
            alsLabel.setVisible(false);
            TOCS = new Arrow(0, 0, 0, 0);
            TOCS.setVisible(false);
            tocsLabel = new Text("ALS");
            tocsLabel.setVisible(false);
        }


        boolean finalRemoveEverything = removeEverything;
        sideView.setOnAction(event -> {
            if (sideView.isSelected()) {
                return;
            }
            try {
                center.getChildren().clear();
                center.getChildren().addAll(viewport, directionAndButtonBar);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            compareView.setSelected(false);
            sideView.setSelected(true);
            this.topDownView.set(false);
            try {
                rightPanel.getChildren().remove(rightPanel.lookup("#RedObstacle"));
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            rightPanel
                    .getChildren()
                    .removeAll(leftRunwayName, zebraBarLeft, centerLine, imgV, imgSideOn, ALS, alsLabel, TOCS, tocsLabel, stopWayColourBlock);
            rightPanel.getChildren().addAll(imgSideOn, stopWayColourBlock);
            imgSideOn.toBack();
            Background.toBack();
            if (runwayToDisplay.getObstacle() != null) {
                rightPanel.getChildren().add(addObject(runwayToDisplay));
                if (this.selectedView.get() == PhysicalRunway.Runway.LANDINGOVER && !finalRemoveEverything) {
                    rightPanel.getChildren().addAll(ALS, alsLabel);
                } else if (this.selectedView.get() == PhysicalRunway.Runway.TAKEOFFTOWARDS && !finalRemoveEverything) {
                    rightPanel.getChildren().addAll(TOCS, tocsLabel);
                }
            }
            clearWayIndicator1.setScaleY(0.05);
            clearWayIndicator1.setTranslateY(22);
            clearWayIndicator1.setFill(Color.BLACK);
            runway.setScaleY(0.1);
            runway.setTranslateY(22);
            stopWayColourBlock.setScaleY(0.1);
            stopWayColourBlock.setTranslateY(22);
            stopWayColourBlock.setFill(Color.YELLOW);
            ALS.setStroke(Color.PURPLE);
            alsLabel.setFill(Color.PURPLE);
            if (Grayscale.isGrayscale) {
                stopWayColourBlock.setFill(Color.DARKBLUE);
                alsLabel.setFill(Color.WHITE);
                ALS.setStroke(Color.WHITE);
            }
            displayedThresholdIndicator.setScaleY(0.1);
            displayedThresholdIndicator.setTranslateY(22);
            stopWayIndicator.setScaleY(0.1);
            stopWayIndicator.setTranslateY(22);
            if (finalRemoveEverything) {
                rightPanel.getChildren().removeAll(asdaArrow,
                                                   toraArrow,
                                                   todaArrow,
                                                   stopWayIndicator,
                                                   displayedThresholdIndicator,
                                                   centerLine,
                                                   leftRunwayName,
                                                   zebraBarLeft,
                                                   stopWayColourBlock);
            }

        });
        topView.setOnAction(event -> {
            if (topView.isSelected()) {
                return;
            }
            try {
                center.getChildren().clear();
                center.getChildren().addAll(viewport, directionAndButtonBar);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            rightPanel.getChildren().removeAll(ALS, alsLabel, TOCS, tocsLabel, imgSideOn, stopWayColourBlock);
            topView.setSelected(true);
            compareView.setSelected(false);
            this.topDownView.set(true);
            try {
                rightPanel.getChildren().remove(rightPanel.lookup("#RedObstacle"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            rightPanel.getChildren().addAll(leftRunwayName, zebraBarLeft, centerLine, imgV, stopWayColourBlock);
            if (runwayToDisplay.getObstacle() != null) {
                rightPanel.getChildren().add(addObject(runwayToDisplay));
            }
            imgV.toBack();
            Background.toBack();
            runway.setScaleY(1);
            runway.setTranslateY(0);
            clearWayIndicator1.setFill(Color.TRANSPARENT);
            clearWayIndicator1.setScaleY(1);
            clearWayIndicator1.setTranslateY(0);
            stopWayColourBlock.setScaleY(1);
            stopWayColourBlock.setTranslateY(0);
            stopWayColourBlock.setFill(Color.YELLOW);
            displayedThresholdIndicator.setScaleY(1);
            displayedThresholdIndicator.setTranslateY(0);
            stopWayIndicator.setScaleY(1);
            stopWayIndicator.setTranslateY(0);
            if (finalRemoveEverything) {
                rightPanel.getChildren().removeAll(asdaArrow,
                                                   toraArrow,
                                                   todaArrow,
                                                   stopWayIndicator,
                                                   displayedThresholdIndicator,
                                                   centerLine,
                                                   leftRunwayName,
                                                   zebraBarLeft,
                                                   stopWayColourBlock);
            }


        });
        rightPanel.getChildren().removeAll(leftRunwayName, zebraBarLeft, centerLine, imgV, ALS, alsLabel, TOCS, tocsLabel);
        (this.topDownView.get() ? topView : sideView).fire();

        // Create a Button for rotation
        BetterToggleButton rotateButton = new BetterToggleButton("Compass");
        rotateButton.getStyleClass().add("rotatebutton");
        rotateButton.setPrefWidth(300);

        // Create Rotate object outside the button event
        Rotate rotate = new Rotate(0, 0, 0);

        // Listen for rightPanel's width and height changes
        rightPanel.widthProperty().addListener((observable, oldValue, newValue) -> rotate.setPivotX(newValue.doubleValue() / 2));

        rightPanel.heightProperty().addListener((observable, oldValue, newValue) -> rotate.setPivotY(newValue.doubleValue() / 2));

        AtomicReference<Boolean> clickedPreviously = new AtomicReference<>(true);


        // Update button event
        rotateButton.setOnAction(event -> {
            if (!isRotated) {
                rotateButton.setText("Normalise");
                double angle = (physicalRunway.getRunwayId().getAngle() * 10) - 90;
                rotate.setAngle(angle);

                // Update pivot points when rotating
                rotate.setPivotX(rightPanel.getWidth() / 2);
                rotate.setPivotY(rightPanel.getHeight() / 2);

                rightPanel.getTransforms().clear();
                rightPanel.getTransforms().add(rotate);
                System.out.println(rightPanel.getTransforms());
                isRotated = true;
            } else {
                rotateButton.setText("Compass");
                rightPanel.getTransforms().clear();
                isRotated = false;
                clickedPreviously.set(true);
            }
            rotateButton.setSelected(!rotateButton.isSelected());
        });


        Rotate rotateBy10 = new Rotate(0, 0, 0);

        // Listen for rightPanel's width and height changes for the rotateBy10 object
        rightPanel.widthProperty().addListener((observable, oldValue, newValue) -> rotateBy10.setPivotX(newValue.doubleValue() / 2));
        rightPanel.heightProperty().addListener((observable, oldValue, newValue) -> rotateBy10.setPivotY(newValue.doubleValue() / 2));

        // Create a Button for rotation by 10 degrees
        Button rotateBy10Button = new Button("Rotate");
        rotateBy10Button.getStyleClass().add("rotatebutton");
        rotateBy10Button.setPrefWidth(300);

        // Update button event
        rotateBy10Button.setOnAction(event -> {
            // Add 10 degrees to the current angle of rotation
            if (clickedPreviously.get()) {
                rotateBy10.setAngle(0);
                clickedPreviously.set(false);
            }
            double newAngle = rotateBy10.getAngle() + 10;
            rotateBy10.setAngle(newAngle);

            // Update pivot points when rotating
            rotateBy10.setPivotX(rightPanel.getWidth() / 2);
            rotateBy10.setPivotY(rightPanel.getHeight() / 2);

            // Check if the rotation is already in the transforms list
            if (!rightPanel.getTransforms().contains(rotateBy10)) {
                rightPanel.getTransforms().add(rotateBy10);
            }
        });

        // Create an HBox to hold the Button
        HBox buttonContainer = new HBox();
        buttonContainer.setAlignment(Pos.TOP_LEFT);
        buttonContainer.getChildren().addAll(rotateButton, rotateBy10Button);

        directionAndButtonBar.getChildren().addAll(buttonBar, buttonContainer, arrowTextDirection);

        //rightPanel.getChildren().add(viewport);


        if (!onlyDisplay) {
            center.getChildren().clear();
            center.getChildren().addAll(viewport, directionAndButtonBar);
        } else if (!viewFromSide) {
            center.getChildren().clear();
            center.getChildren().addAll(viewport);
            topView.fire();
            try {
                center.getChildren().remove(directionAndButtonBar);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        } else if (viewFromSide) {
            center.getChildren().clear();
            center.getChildren().addAll(viewport);
            sideView.fire();
            try {
                center.getChildren().remove(directionAndButtonBar);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        compareView.setOnAction(event -> {
            if (compareView.isSelected()) {
                return;
            }
            display.getChildren().clear();
            display.getChildren().addAll(drawRunway(true, false), drawRunway(true, true));
            center.getChildren().clear();
            center.getChildren().addAll(display, directionAndButtonBar);
            compareView.setSelected(true);
        });

        return center;
    }

    //Used to reposition runway display attributes based on a plane landing over an object
    public void LandingOver(
            Text ldaTextDisplay,
            VBox zebraBarLeft,
            Rectangle displayedThresholdIndicator,
            Rectangle stopWayIndicator,
            Arrow ldaArrow,
            Line centerLine,
            HBox leftRunwayName,
            Arrow resaArrow,
            LogicalRunway runwayToDisplay,
            Double multiplier,
            int textTranslation,
            Line ldaStart,
            Line ldaEnd,
            double toraScale,
            double ldaScale) {
        ldaTextDisplay.setTranslateX((-textTranslation + (toraScale - ldaScale) + objectWidthScale / 2) * multiplier);
        zebraBarLeft.setTranslateX((-200 + (toraScale - ldaScale) + objectWidthScale / 2) * multiplier);
        displayedThresholdIndicator.setTranslateX((-249 + (toraScale - ldaScale) + objectWidthScale / 2) * multiplier);
        System.out.println((-249 + (toraScale - ldaScale) + objectWidthScale / 2) * multiplier);
        stopWayIndicator.setTranslateX((-249 + originalToraPublic) * multiplier);
        ldaArrow.setStartX((-249 + (toraScale - ldaScale) + objectWidthScale / 2) * multiplier);
        ldaArrow.setEndX((-249 + (toraScale - ldaScale) + ldaScale) * multiplier);
        ldaArrow.setTranslateX(((displayedThresholdIndicator.getTranslateX() + stopWayIndicator.getTranslateX()) / 2));
        centerLine.setStartX((-175 + (toraScale - ldaScale) + objectWidthScale / 2) * multiplier);
        centerLine.setEndX((-249 + toraScale) * multiplier);
        leftRunwayName.setTranslateX((-232 + (toraScale - ldaScale) + objectWidthScale / 2) * multiplier);
        centerLine.setTranslateX((-215 + (toraScale - ldaScale) + (ldaScale / 2) + objectWidthScale / 4) * multiplier);
        resaArrow.setEndX(scaleCalculation(240, 500, runwayToDisplay.getOriginalRunway().getAsda()) * multiplier);
        resaArrow.setTranslateX((-249 + (originalToraPublic - ldaScale) + objectWidthScale / 2 - (scaleCalculation(240,
                                                                                                                   500,
                                                                                                                   runwayToDisplay
                                                                                                                           .getOriginalRunway()
                                                                                                                           .getAsda())) / 2) * multiplier);
        ldaStart.setTranslateX((-249 + (toraScale - ldaScale) + objectWidthScale / 2) * multiplier);
        ldaEnd.setTranslateX((-249 + toraScale) * multiplier);
        ldaTextDisplay.setText("LDA:" + Math.round(runwayToDisplay.getLda()));
    }

    //Used to reposition runway display attributes based on a plane landing towards an object
    public void LandingTowards(
            Text ldaTextDisplay,
            VBox zebraBarLeft,
            Rectangle displayedThresholdIndicator,
            Rectangle stopWayIndicator,
            Arrow ldaArrow,
            Line centerLine,
            HBox leftRunwayName,
            Arrow resaArrow,
            LogicalRunway runwayToDisplay,
            Double multiplier,
            int textTranslation,
            Line ldaStart,
            Line ldaEnd,
            double toraScale,
            double ldaScale,
            double originalToraScale,
            double originalLdaScale) {
        ldaTextDisplay.setTranslateX(-textTranslation + (originalToraScale - originalLdaScale));
        zebraBarLeft.setTranslateX(-200 + (originalToraScale - originalLdaScale));
        displayedThresholdIndicator.setTranslateX(-249 + (originalToraScale - originalLdaScale)); // White bar
        stopWayIndicator.setTranslateX(-249 + (originalToraScale - originalLdaScale) + ldaScale - objectWidthScale / 2); // Yellow bar
        ldaArrow.setStartX(-249 + (originalToraScale - originalLdaScale));
        ldaArrow.setEndX(-249 + (originalToraScale - originalLdaScale) + ldaScale - objectWidthScale / 2);
        ldaArrow.setTranslateX((displayedThresholdIndicator.getTranslateX() + stopWayIndicator.getTranslateX()) / 2);
        centerLine.setStartX(-175 + (toraScale - ldaScale));
        centerLine.setEndX(-249 + (toraScale) - objectWidthScale / 2);
        leftRunwayName.setTranslateX(-232 + (originalToraScale - originalLdaScale));
        centerLine.setTranslateX(-215 + (originalToraScale - originalLdaScale) + (ldaScale / 2) - objectWidthScale / 4);
        resaArrow.setTranslateX((-249 + (originalToraScale - originalLdaScale) + ldaScale - objectWidthScale / 2) + (scaleCalculation(240,
                                                                                                                                      500,
                                                                                                                                      runwayToDisplay
                                                                                                                                              .getOriginalRunway()
                                                                                                                                              .getAsda())) / 2);
        ldaStart.setTranslateX(-249 + originalToraScale - originalLdaScale);
        ldaEnd.setTranslateX(-249 + (originalToraScale - originalLdaScale) + ldaScale - objectWidthScale / 2);
        ldaTextDisplay.setText("LDA:" + Math.round(runwayToDisplay.getLda()));
    }

    public void TakingOffTowards(
            Text toraTextDisplay,
            Text todaTextDisplay,
            Text asdaTextDisplay,
            VBox zebraBarLeft,
            Rectangle stopWayIndicator,
            Rectangle clearWayIndicator1,
            Line centerLine,
            HBox leftRunwayName,
            Arrow toraArrow,
            Arrow todaArrow,
            Arrow asdaArrow,
            Arrow resaArrow,
            Line toraStart,
            Line toraEnd,
            Line asdaStart,
            Line asdaEnd,
            Line todaStart,
            Line todaEnd,
            LogicalRunway runwayToDisplay,
            double toraScale,
            double ldaScale,
            double asdaScale,
            double todaScale,
            double objectWidthScale,
            double originalAsdaScale,
            Rectangle displacedThreshold) {
        leftRunwayName.setTranslateX(-232);
        zebraBarLeft.setTranslateX(-200);
        stopWayIndicator.setTranslateX(-249 + toraScale - objectWidthScale / 2);
        centerLine.setStartX(-175);
        centerLine.setEndX(-249 + toraScale - objectWidthScale / 2);
        centerLine.setTranslateX(-215 + ((toraScale - ldaScale) / 2) + (ldaScale / 2) - objectWidthScale / 4);
        toraArrow.setStartX(-249);
        toraArrow.setEndX(-249 + toraScale - objectWidthScale / 2);
        toraArrow.setTranslateX(-249 + toraScale / 2 - objectWidthScale / 4);
        toraTextDisplay.setText(String.format("TORA: " + Math.round(runwayToDisplay.getTora())));
        asdaArrow.setStartX(-249);
        asdaArrow.setEndX(-249 + asdaScale - objectWidthScale / 2);
        asdaArrow.setTranslateX(-249 + asdaScale / 2 - objectWidthScale / 4);
        asdaTextDisplay.setText(String.format("ASDA: " + Math.round(runwayToDisplay.getAsda())));
        todaArrow.setStartX(-249);
        todaArrow.setEndX(-249 + todaScale - objectWidthScale / 2);
        todaArrow.setTranslateX(-249 + todaScale / 2 - objectWidthScale / 4);
        todaTextDisplay.setText(String.format("TODA: " + Math.round(runwayToDisplay.getToda())));
        clearWayIndicator1.setWidth(todaScale - asdaScale);
        clearWayIndicator1.setTranslateX(-249 + originalAsdaScale + (todaScale - asdaScale) / 2 + objectWidthScale / 2);
        resaArrow.setTranslateX(-249 + toraScale - objectWidthScale / 2 + (scaleCalculation(240,
                                                                                            500,
                                                                                            runwayToDisplay.getOriginalRunway().getAsda())) / 2);
        toraStart.setTranslateX(-249);
        toraEnd.setTranslateX(-249 + toraScale - objectWidthScale / 2);
        asdaStart.setTranslateX(-249);
        asdaEnd.setTranslateX(-249 + asdaScale);
        todaStart.setTranslateX(-249);
        todaEnd.setTranslateX(-249 + todaScale);
        displacedThreshold.setTranslateX(-249);

    }

    public void TakingOffAwayFrom(
            Text ldaTextDisplay,
            Text toraTextDisplay,
            Text todaTextDisplay,
            Text asdaTextDisplay,
            VBox zebraBarLeft,
            Rectangle displayedThresholdIndicator,
            Rectangle stopWayIndicator,
            Rectangle clearWayIndicator1,
            Line centerLine,
            HBox leftRunwayName,
            Arrow toraArrow,
            Arrow todaArrow,
            Arrow asdaArrow,
            Arrow blastRadiusArrow,
            LogicalRunway runwayToDisplay,
            double toraScale,
            double ldaScale,
            double asdaScale,
            double todaScale,
            double objectWidthScale,
            double originalToraScale,
            double originalAsdaScale,
            double originalTodaScale,
            double textTranslation,
            Line toraStart,
            Line toraEnd,
            Line asdaStart,
            Line asdaEnd,
            Line todaStart,
            Line todaEnd,
            Rectangle stopWayBlock) {
        ldaTextDisplay.setTranslateX(-textTranslation + (originalToraScale - ldaScale));
        zebraBarLeft.setTranslateX(-200 + (originalToraScale - ldaScale));
        displayedThresholdIndicator.setTranslateX(-249 + (originalToraScale - ldaScale)); // White bar
        stopWayIndicator.setTranslateX(-249 + (originalToraScale)); // Yellow bar
        leftRunwayName.setTranslateX(-232 + (originalToraScale - ldaScale));
        centerLine.setStartX(-175 + (toraScale - ldaScale) + objectWidthScale / 2);
        centerLine.setEndX(-249 + (toraScale));
        centerLine.setTranslateX(-215 + (originalToraScale - ldaScale) + (ldaScale / 2) + objectWidthScale / 4);
        toraArrow.setStartX(-249 + (toraScale - ldaScale) + objectWidthScale / 2);
        toraArrow.setEndX(-249 + toraScale - objectWidthScale / 2);
        toraArrow.setTranslateX(((displayedThresholdIndicator.getTranslateX() + stopWayIndicator.getTranslateX()) / 2));
        toraTextDisplay.setTranslateX(-textTranslation + 3 + (originalToraScale - ldaScale) + objectWidthScale / 2);
        toraTextDisplay.setText(String.format("TORA: " + Math.round(runwayToDisplay.getTora())));
        asdaArrow.setStartX(-249 + (toraScale - ldaScale) + objectWidthScale / 2);
        asdaArrow.setEndX(-249 + asdaScale - objectWidthScale / 2);
        asdaArrow.setTranslateX(((displayedThresholdIndicator.getTranslateX() + stopWayIndicator.getTranslateX()) / 2) + ((originalAsdaScale - originalToraScale) / 2));
        asdaTextDisplay.setTranslateX(-textTranslation + 3 + (originalToraScale - ldaScale) + objectWidthScale / 2);
        asdaTextDisplay.setText(String.format("ASDA: " + Math.round(runwayToDisplay.getAsda())));
        todaArrow.setStartX(-249 + (toraScale - ldaScale) + objectWidthScale / 2);
        todaArrow.setEndX(-249 + todaScale - objectWidthScale / 2);
        todaArrow.setTranslateX(((displayedThresholdIndicator.getTranslateX() + stopWayIndicator.getTranslateX()) / 2) + ((originalTodaScale - originalToraScale) / 2));
        todaTextDisplay.setTranslateX(-textTranslation + 3 + (originalToraScale - ldaScale) + objectWidthScale / 2);
        todaTextDisplay.setText(String.format("TODA: " + Math.round(runwayToDisplay.getToda())));
        blastRadiusArrow.setTranslateX(-249 + (originalToraScale - ldaScale) + objectWidthScale / 2
                                               - (scaleCalculation(500, 500, runwayToDisplay.getOriginalRunway().getAsda())) / 2);
        clearWayIndicator1.setWidth(todaScale - asdaScale);
        clearWayIndicator1.setTranslateX(-249 + originalAsdaScale + (todaScale - asdaScale) / 2 + objectWidthScale / 2);
        toraStart.setTranslateX(-249 + (originalToraScale - ldaScale));
        toraEnd.setTranslateX(-249 + originalToraScale);
        asdaStart.setTranslateX(-249 + (originalToraScale - ldaScale));
        asdaEnd.setTranslateX(-249 + originalAsdaScale);
        todaStart.setTranslateX(-249 + (originalToraScale - ldaScale));
        todaEnd.setTranslateX(-249 + originalTodaScale);
        stopWayBlock.setWidth(originalAsdaScale - originalToraScale);
        stopWayBlock.setTranslateX((originalToraScale / 2));
    }

    private void rotateComponents(StackPane rightPanel, double angle) {
        rightPanel.setRotate(angle);
    }

    // Assigns default positions for all runway attributes
    private void defaultRunwayLayoutCreation(
            Rectangle runway,
            Text ldaTextDisplay,
            LogicalRunway runwayToDisplay,
            Text todaTextDisplay,
            Text asdaTextDisplay,
            Integer textTranslation,
            Text toraTextDisplay,
            double originalToraScale,
            double originalLdaScale,
            double originalTodaScale,
            double originalAsdaScale,
            HBox leftRunwayName,
            VBox zebraBarLeft,
            Rectangle displayedThresholdIndicator,
            Rectangle stopWayIndicator,
            Rectangle clearWayIndicator1,
            Arrow todaArrow,
            Arrow asdaArrow,
            Arrow toraArrow,
            Arrow ldaArrow,
            Line centerLine,
            Line ldaStart,
            Line ldaEnd,
            Line toraStart,
            Line toraEnd,
            Line asdaStart,
            Line asdaEnd,
            Line todaStart,
            Line todaEnd,
            Rectangle stopWayBlock) {
        zebraBarLeft.setTranslateX(-200 + (originalToraScale - originalLdaScale));
        toraArrow.setStartX(-249);
        toraArrow.setEndX(-249 + originalToraScale - objectWidthScale / 2);
        toraArrow.setTranslateX(-249 + originalToraScale / 2 - objectWidthScale / 4);
        toraTextDisplay.setText(String.format("TORA: %.1f", runwayToDisplay.getOriginalRunway().getTora()));
        toraTextDisplay.setTranslateX(-textTranslation + 3);
        asdaArrow.setStartX(-249);
        asdaArrow.setEndX(-249 + originalAsdaScale - objectWidthScale / 2);
        asdaArrow.setTranslateX(-249 + originalAsdaScale / 2 - objectWidthScale / 4);
        asdaTextDisplay.setText(String.format("ASDA: %.1f", runwayToDisplay.getOriginalRunway().getAsda()));
        asdaTextDisplay.setTranslateX(-textTranslation + 3);
        todaArrow.setStartX(-249);
        todaArrow.setEndX(-249 + originalTodaScale - objectWidthScale / 2);
        todaArrow.setTranslateX(-249 + originalTodaScale / 2 - objectWidthScale / 4);
        todaTextDisplay.setText(String.format("TODA: %.1f", runwayToDisplay.getOriginalRunway().getToda()));
        todaTextDisplay.setTranslateX(-textTranslation + 3);
        clearWayIndicator1.setWidth(originalTodaScale - originalAsdaScale);
        clearWayIndicator1.setTranslateX(-249 + originalAsdaScale + (originalTodaScale - originalAsdaScale) / 2);
        displayedThresholdIndicator.setTranslateX(-249 + (originalToraScale - originalLdaScale));
        leftRunwayName.setTranslateX(-232 + (originalToraScale - originalLdaScale));
        ldaStart.setTranslateY((35) + 10.5);
        ldaEnd.setTranslateX(-249 + originalLdaScale);
        ldaEnd.setTranslateY((35) + 10.5);
        toraStart.setTranslateX(-249);
        toraStart.setTranslateY((-30) - 10.5);
        toraEnd.setTranslateX(-249 + originalToraScale);
        toraEnd.setTranslateY((-30) - 10.5);
        asdaStart.setTranslateX(-249);
        asdaStart.setTranslateY((-45) - 10.5);
        asdaEnd.setTranslateX(-249 + originalAsdaScale);
        asdaEnd.setTranslateY((-45) - 10.5);
        todaStart.setTranslateX(-249);
        todaStart.setTranslateY((-60) - 10.5);
        todaEnd.setTranslateX(-249 + originalTodaScale);
        todaEnd.setTranslateY((-60) - 10.5);
        stopWayIndicator.setTranslateX(-249 + (originalToraScale));
        centerLine.setStartX(-175 + (originalToraScale - originalLdaScale));
        centerLine.setEndX(-249 + (originalToraScale));
        ldaArrow.setStartX(runway.getX());
        ldaArrow.setEndX(originalLdaScale);
        ldaArrow.setTranslateX(-249 + (originalToraScale - originalLdaScale) + (originalLdaScale / 2));
        centerLine.setTranslateX(-215 + (originalToraScale - originalLdaScale) + (originalLdaScale / 2));
        ldaTextDisplay.setText(String.format("LDA: %.1f", runwayToDisplay.getOriginalRunway().getLda()));
        ldaTextDisplay.setTranslateX(-220 + 6 + (originalToraScale - originalLdaScale) + objectWidthScale / 2);
        clearWayIndicator1.setWidth(originalTodaScale - originalAsdaScale);
        clearWayIndicator1.setTranslateX(-249 + originalAsdaScale + (originalTodaScale - originalAsdaScale) / 2);
        ldaStart.setTranslateX(-249 + (originalToraScale - originalLdaScale));
        ldaEnd.setTranslateX(-249 + originalToraScale);
        stopWayBlock.setWidth(originalAsdaScale - originalToraScale);
        stopWayBlock.setTranslateX((originalToraScale / 2));
    }

    /**
     * Adds an obstacle to the runway
     *
     * @param runway runway that the obstacle will be added to
     * @return a stack pane containing the runway with the obstacle
     */
    protected StackPane addObject(LogicalRunway runway) {
        // get obstacle from runway
        Obstacle obstacle = runway.getObstacle();
        if (obstacle == null)
            return null; // if there are no obstacles, return null
        objectWidthScale = scaleCalculation(Math.min(Math.max(runway.getObstacle().getLength(), runway.getOriginalRunway().getAsda() / 100),
                                                     runway.getOriginalRunway().getAsda() / 10), 500, runway.getOriginalRunway().getAsda());
        double objectLengthScale;
        if (this.topDownView.get()) {
            objectLengthScale = scaleCalculation(Math.min(Math.max(runway.getObstacle().getWidth(), runway.getOriginalRunway().getAsda() / 100),
                                                          runway.getOriginalRunway().getAsda() / 10), 500, runway.getOriginalRunway().getAsda());
        } else {
            objectLengthScale = scaleCalculation(runway.getObstacle().getHeight(), 80, 80);
        }

        // Add rectangle that represents obstacle
        Rectangle object = new ResizableRectangle(objectWidthScale, objectLengthScale);
        object.setId("runway");
        object.setTranslateY(this.topDownView.get() ? -obstacle.getY() : 20 - objectLengthScale / 2);
        object.setFill(Color.RED);
        this.globalObject = object;

        double xCoordScale = scaleCalculation(runway.getObstacle().getX(), 500, runway.getOriginalRunway().getAsda());
        object.setTranslateX(-250 + xCoordScale + (originalToraPublic - originalLDAPublic));
        objectWidthScale = scaleCalculation(runway.getObstacle().getLength(), 500, runway.getOriginalRunway().getAsda());

        // return stack pane containing the obstacle rectangle
        StackPane objectDetails = new StackPane();
        objectDetails.setId("RedObstacle");
        objectDetails.getChildren().addAll(object);
        return objectDetails;
    }

    /**
     * @return RunwayDisplay VBox
     */
    @Override
    public Node getNode() {
        return this.drawRunway(false, false);
    }
}
