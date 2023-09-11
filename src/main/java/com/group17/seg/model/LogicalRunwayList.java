package com.group17.seg.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

@XmlRootElement(name = "LogicalRunways")
public class LogicalRunwayList extends ArrayList<LogicalRunway> {

    @XmlElement(name = "LogicalRunway")
    public LogicalRunwayList getLogicalRunwayList() {
        return this;
    }

    public Obstacle getObstacle() {
        return this.get(PhysicalRunway.Runway.ORIGINAL.ordinal()).getObstacle();
    }
}
