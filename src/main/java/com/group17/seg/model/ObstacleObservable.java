package com.group17.seg.model;

import javafx.beans.binding.ObjectBinding;

import javax.xml.bind.annotation.XmlTransient;

@XmlTransient
public class ObstacleObservable extends ObjectBinding<Obstacle> {
    private Obstacle obstacle;

    public ObstacleObservable() {
        this.obstacle = null;
    }

    public ObstacleObservable(Obstacle obstacle) {
        if (obstacle == null) {
            this.obstacle = null;
        } else {
            this.obstacle = obstacle;
            bind(this.obstacle.xProperty(), this.obstacle.yProperty(), this.obstacle.lengthProperty(), this.obstacle.widthProperty(), this.obstacle.heightProperty(), this.obstacle.nameProperty());
        }
    }

    public ObstacleObservable(double x, double y, double length, double width, double height, String name) {
        this.obstacle = new Obstacle(x, y, length, width, height, name);
        bind(this.obstacle.xProperty(), this.obstacle.yProperty(), this.obstacle.lengthProperty(), this.obstacle.widthProperty(), this.obstacle.heightProperty(), this.obstacle.nameProperty());
    }

    public void set(Obstacle obstacle) {
        if (obstacle == null) {
            this.obstacle = null;
        } else {
            this.obstacle = obstacle;
            this.bind(this.obstacle.xProperty(), this.obstacle.yProperty(), this.obstacle.lengthProperty(), this.obstacle.widthProperty(), this.obstacle.heightProperty(), this.obstacle.nameProperty());
        }
        this.invalidate();
    }

    @Override
    protected Obstacle computeValue() {
        return this.obstacle;
    }
}
