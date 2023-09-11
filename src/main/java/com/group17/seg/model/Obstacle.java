package com.group17.seg.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.xml.bind.annotation.*;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Obstacle {
    //<editor-fold desc="Properties">
    @XmlTransient
    private final DoubleProperty x = new SimpleDoubleProperty();
    @XmlTransient
    private final DoubleProperty y = new SimpleDoubleProperty();
    @XmlTransient
    private final DoubleProperty length = new SimpleDoubleProperty();
    @XmlTransient
    private final DoubleProperty width = new SimpleDoubleProperty();
    @XmlTransient
    private final DoubleProperty height = new SimpleDoubleProperty();
    @XmlTransient
    private final StringProperty name = new SimpleStringProperty();
    //</editor-fold>

    Obstacle() {

    }

    public Obstacle(Obstacle obstacle) {
        this.x.set(obstacle.getX());
        this.y.set(obstacle.getY());
        this.length.set(obstacle.getLength());
        this.width.set(obstacle.getWidth());
        this.height.set(obstacle.getHeight());
        this.name.set(obstacle.getName());
    }

    public Obstacle(double x, double y, double length, double width, double height, String name) {
        this.x.set(x);
        this.y.set(y);
        this.length.set(length);
        this.width.set(width);
        this.height.set(height);
        this.name.set(name);
    }

    /**
     * @return Formatted String for Logging
     */
    public String log() {
        return String.format("Name - %s, x - %.2f, y - %.2f, Length - %.2f, Width - %.2f, Height - %.2f",
                             name.get(),
                             x.get(),
                             y.get(),
                             length.get(),
                             width.get(),
                             height.get());
    }

    public void debug() {
        System.err.println(this.getClass());
        System.err.println(this.getName());
        System.err.println(this.getX());
        System.err.println(this.getY());
        System.err.println(this.getLength());
        System.err.println(this.getWidth());
        System.err.println(this.getHeight());
        System.err.println();
    }

    //<editor-fold desc="Getters and Setters">
    @XmlElement
    public double getX() {
        return x.get();
    }

    public DoubleProperty xProperty() {
        return x;
    }

    public void setX(double x) {
        this.x.set(x);
    }

    @XmlElement
    public double getY() {
        return y.get();
    }

    public DoubleProperty yProperty() {
        return y;
    }

    public void setY(double y) {
        this.y.set(y);
    }

    @XmlElement
    public double getLength() {
        return length.get();
    }

    public DoubleProperty lengthProperty() {
        return length;
    }

    public void setLength(double length) {
        this.length.set(length);
    }

    @XmlElement
    public double getWidth() {
        return width.get();
    }

    public DoubleProperty widthProperty() {
        return width;
    }

    public void setWidth(double width) {
        this.width.set(width);
    }

    @XmlElement
    public double getHeight() {
        return height.get();
    }

    public DoubleProperty heightProperty() {
        return height;
    }

    public void setHeight(double height) {
        this.height.set(height);
    }

    @XmlElement
    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }
    //</editor-fold>
}
