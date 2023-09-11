package com.group17.seg.model;

import javafx.beans.property.SimpleStringProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "Airport")
public class Airport {
    // The list of physical runways in the airport
    private final PhysicalRunwayList physicalRunwayList;

    @XmlTransient
    private final SimpleStringProperty name = new SimpleStringProperty();

    public Airport() {
        this.physicalRunwayList = new PhysicalRunwayList();
    }

    /**
     * Adds an obstacle to a given runway (identified by its runwayID).
     *
     * @param physicalRunway runway that needs to be added
     * @return true if runway is successfully added, false otherwise.
     */
    public boolean addPhysicalRunway(PhysicalRunway physicalRunway) {
        RunwayId id = physicalRunway.getRunwayId();
        for (PhysicalRunway r : this.physicalRunwayList) {
            if (r.getRunwayId().equals(id)) {
                return false;
            }
        }
        this.physicalRunwayList.add(physicalRunway);
        return true;
    }

    /**
     * Adds an obstacle to a given runway (identified by its runwayID).
     *
     * @param id       runway that needs to be added
     * @param smaller  parameter that tells if the runway is viewed form left/right
     * @param obstacle the obstacle that is to be added
     * @return true if runway is successfully added, false otherwise.
     */
    public boolean addObstacle(RunwayId id, boolean smaller, Obstacle obstacle) {
        for (PhysicalRunway runway : this.physicalRunwayList) {
            if (runway.getRunwayId().equals(id)) {
                if (smaller) runway.addObstacleToSmallerNumberedRunway(obstacle);
                else runway.addObstacleToLargerNumberedRunway(obstacle);
                return true;
            }
        }
        return false;
    }

    /**
     * @param runwayId identifies physical runway
     * @return PhysicalRunway
     */
    public PhysicalRunway getPhysicalRunway(RunwayId runwayId) {
        for (var physicalRunway : physicalRunwayList) {
            if (physicalRunway.getRunwayId().equals(runwayId)) return physicalRunway;
        }
        return null;
    }

    public RunwayIdList getRunwayIds() {
        var output = new RunwayIdList();
        this.physicalRunwayList.forEach(runway -> output.add(runway.getRunwayId()));
        return output;
    }

    @XmlElement(name = "PhysicalRunway")
    public PhysicalRunwayList getPhysicalRunwayList() {
        return physicalRunwayList;
    }

    @XmlElement(name = "Name")
    public String getName() {
        return this.name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public SimpleStringProperty nameProperty() {
        return this.name;
    }

    public void debug() {
        this.physicalRunwayList.forEach(physicalRunway -> {
            physicalRunway.getSmallerNumberedRunways().forEach(LogicalRunway::debugRunway);
            physicalRunway.getLargerNumberedRunways().forEach(LogicalRunway::debugRunway);
        });
    }

    public AirportList toAirportList() {
        AirportList output = new AirportList();
        output.add(this);
        return output;
    }
}
