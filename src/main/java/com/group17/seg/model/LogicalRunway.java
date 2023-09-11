package com.group17.seg.model;

import javafx.beans.property.SimpleDoubleProperty;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;

@XmlType(propOrder = {"toda", "tora", "asda", "lda", "displacedThreshold", "clearway", "stopway", "resa", "obstacle", "originalRunway"})

public abstract class LogicalRunway {
    protected static final double blastRadius = 500;
    protected static final double resaMinimum = 240;
    protected static final double stripEnd = 60;
    protected static final double slopeRatio = 50;
    private final SimpleDoubleProperty toda;
    private final SimpleDoubleProperty asda;
    private final SimpleDoubleProperty tora;
    private final SimpleDoubleProperty lda;
    private final SimpleDoubleProperty displacedThreshold;
    private final SimpleDoubleProperty clearway;
    private final SimpleDoubleProperty stopway;
    private final SimpleDoubleProperty resa;
    private RunwayId runwayId;
    private final ObstacleObservable obstacle;
    private LogicalRunway originalRunway;

    LogicalRunway() {
        this.toda = new SimpleDoubleProperty();
        this.asda = new SimpleDoubleProperty();
        this.tora = new SimpleDoubleProperty();
        this.lda = new SimpleDoubleProperty();
        this.displacedThreshold = new SimpleDoubleProperty();
        this.clearway = new SimpleDoubleProperty();
        this.stopway = new SimpleDoubleProperty();
        this.resa = new SimpleDoubleProperty();
        this.runwayId = new RunwayId();
        this.obstacle = new ObstacleObservable();
    }

    public LogicalRunway(RunwayOriginal originalRunway) {
        this.toda = new SimpleDoubleProperty(originalRunway.getToda());
        this.asda = new SimpleDoubleProperty(originalRunway.getAsda());
        this.tora = new SimpleDoubleProperty(originalRunway.getTora());
        this.lda = new SimpleDoubleProperty(originalRunway.getLda());
        this.displacedThreshold = new SimpleDoubleProperty(originalRunway.getDisplacedThreshold());
        this.clearway = new SimpleDoubleProperty(originalRunway.getClearway());
        this.stopway = new SimpleDoubleProperty(originalRunway.getStopway());
        this.resa = new SimpleDoubleProperty(originalRunway.getResa());
        this.runwayId = originalRunway.getRunwayId();
        this.obstacle = new ObstacleObservable(originalRunway.getObstacle());
        this.originalRunway = originalRunway;
    }

    public LogicalRunway(double toda, double tora, double asda, double lda) {
        this.toda = new SimpleDoubleProperty(toda);
        this.asda = new SimpleDoubleProperty(asda);
        this.tora = new SimpleDoubleProperty(tora);
        this.lda = new SimpleDoubleProperty(lda);
        this.displacedThreshold = new SimpleDoubleProperty(tora - lda);
        this.clearway = new SimpleDoubleProperty(toda - tora);
        this.stopway = new SimpleDoubleProperty(asda - tora);
        this.resa = new SimpleDoubleProperty(0);
        this.obstacle = new ObstacleObservable();
    }

    abstract public void recalculateValues();

    abstract public ArrayList<String> getCompareValues();

    abstract public String log();

    public void debugRunway() {
        System.err.println(this.getClass());
        System.err.printf("TODA: %1$.1f%n", getToda());
        System.err.printf("TORA: %1$.1f%n", getTora());
        System.err.printf("ASDA: %1$.1f%n", getAsda());
        System.err.printf("LDA: %1$.1f%n", getLda());
        System.err.printf("DT: %1$.1f%n", getDisplacedThreshold());
        System.err.printf("CLEARWAY: %1$.1f%n", getClearway());
        System.err.printf("STOPWAY: %1$.1f%n", getStopway());
        System.err.printf("RESA: %1$.1f%n\n", getResa());
    }

    public double getToda() {
        return this.toda.get();
    }

    public double getTora() {
        return this.tora.get();
    }

    public double getAsda() {
        return this.asda.get();
    }

    public double getLda() {
        return this.lda.get();
    }

    public double getDisplacedThreshold() {
        return this.displacedThreshold.get();
    }

    public double getClearway() {
        return this.clearway.get();
    }

    public double getStopway() {
        return this.stopway.get();
    }

    public double getResa() {
        return this.resa.get();
    }

    @XmlTransient
    public RunwayId getRunwayId() {
        return runwayId;
    }

    public Obstacle getObstacle() {
        return this.obstacle.get();
    }

    public LogicalRunway getOriginalRunway() {
        return this.originalRunway;
    }

    public void setToda(double toda) {
        this.toda.set(toda);
    }

    public void setTora(double tora) {
        this.tora.set(tora);
    }

    public void setAsda(double asda) {
        this.asda.set(asda);
    }

    public void setLda(double lda) {
        this.lda.set(lda);
    }

    public void setDisplacedThreshold(double displacedThreshold) {
        this.displacedThreshold.set(displacedThreshold);
    }

    public void setClearway(double clearway) {
        this.clearway.set(clearway);
    }

    public void setStopway(double stopway) {
        this.stopway.set(stopway);
    }

    public void setResa(double resa) {
        this.resa.set(resa);
    }

    public void setRunwayId(RunwayId runwayId) {
        this.runwayId = runwayId;
    }

    public void setObstacle(Obstacle obstacle) {
        this.obstacle.set(obstacle);
    }

    public void setOriginalRunway(LogicalRunway originalRunway) {
        this.originalRunway = originalRunway;
    }

    public static double getBlastRadius() {
        return blastRadius;
    }

    public static double getResaMinimum() {
        return resaMinimum;
    }

    public static double getStripEnd() {
        return stripEnd;
    }

    public static double getSlopeRatio() {
        return slopeRatio;
    }
}
