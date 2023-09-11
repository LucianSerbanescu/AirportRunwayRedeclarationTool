package com.group17.seg.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
public class ObstacleList extends ArrayList<Obstacle> {

    ObstacleList() {

    }

    public ObstacleList(Obstacle... obstacles) {
        this.addAll(List.of(obstacles));
    }

    @XmlElement(name = "obstacle")
    public List<Obstacle> getObstacleList() {
        return this;
    }
}
