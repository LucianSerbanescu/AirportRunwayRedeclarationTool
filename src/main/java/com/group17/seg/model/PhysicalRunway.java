package com.group17.seg.model;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "PhysicalRunway")
@XmlSeeAlso({RunwayOriginal.class, RunwayLandingOver.class, RunwayLandingTowards.class, RunwayTakeOffAwayFrom.class, RunwayTakeOffTowards.class})
@XmlType(propOrder = {
        "runwayId",
        "smallerNumberedRunways",
        "largerNumberedRunways",
        "obstacle"
})
public class PhysicalRunway {
    public enum Runway {
        ORIGINAL, LANDINGTOWARDS, LANDINGOVER, TAKEOFFAWAYFROM, TAKEOFFTOWARDS;

        /**
         * @param i 0 ORIGINAL, 1 LANDINGTOWARDS, 2 LANDINGOVER, 3 TAKEOFFAWAYFROM, 4 TAKEOFFTOWARDS
         * @return Runway Enum
         */
        public static Runway fromInt(int i) {
            return switch (i) {
                case 0 -> ORIGINAL;
                case 1 -> LANDINGTOWARDS;
                case 2 -> LANDINGOVER;
                case 3 -> TAKEOFFAWAYFROM;
                case 4 -> TAKEOFFTOWARDS;
                default -> throw new IllegalStateException("Unexpected value: " + i);
            };
        }
    }

    private RunwayId runwayId = new RunwayId();
    private final LogicalRunwayList smallerNumberedRunways = new LogicalRunwayList();
    private final LogicalRunwayList largerNumberedRunways = new LogicalRunwayList();
    private final ObstacleObservable obstacle = new ObstacleObservable();

    PhysicalRunway() {

    }

    /**
     * This allows for the creation of a physicalRunway with only one of the logical runways.
     *
     * @param logicalRunway singular logical runway
     */
    public PhysicalRunway(RunwayOriginal logicalRunway, RunwayId runwayId) {
        this.runwayId = runwayId;
        logicalRunway.setRunwayId(runwayId);
        var focusedRunways = smallerNumberedRunways;
        var unfocusedRunways = largerNumberedRunways;
        if (logicalRunway.getRunwayId().getAngle() > 18) {
            focusedRunways = largerNumberedRunways;
            unfocusedRunways = smallerNumberedRunways;
        }
        focusedRunways.add(logicalRunway);
        focusedRunways.add(new RunwayLandingTowards(logicalRunway));
        focusedRunways.add(new RunwayLandingOver(logicalRunway));
        focusedRunways.add(new RunwayTakeOffAwayFrom(logicalRunway));
        focusedRunways.add(new RunwayTakeOffTowards(logicalRunway));
        // If no runway parameters are specified, the mirror takes the values of the main runway's tora.
        var tora = logicalRunway.getTora();
        var mirrorRunway = new RunwayOriginal(tora, tora, tora, tora);
        unfocusedRunways.add(mirrorRunway);
        unfocusedRunways.add(new RunwayLandingTowards(mirrorRunway));
        unfocusedRunways.add(new RunwayLandingOver(mirrorRunway));
        unfocusedRunways.add(new RunwayTakeOffAwayFrom(mirrorRunway));
        unfocusedRunways.add(new RunwayTakeOffTowards(mirrorRunway));
    }

    /**
     * This allows for the creation of a physical runway with 2 logical runways.
     *
     * @param logicalRunway1 logical runway 1
     * @param logicalRunway2 logical runway 2
     */
    public PhysicalRunway(RunwayOriginal logicalRunway1, RunwayOriginal logicalRunway2, RunwayId runwayId) {
        this.runwayId = runwayId;
        logicalRunway1.setRunwayId(runwayId);
        logicalRunway2.setRunwayId(runwayId);
        var smallerRunway = logicalRunway1;
        var largerRunway = logicalRunway2;
        if (logicalRunway1.getRunwayId().getAngle() > 18) {
            smallerRunway = logicalRunway2;
            largerRunway = logicalRunway1;
        }
        smallerNumberedRunways.add(smallerRunway);
        smallerNumberedRunways.add(new RunwayLandingTowards(smallerRunway));
        smallerNumberedRunways.add(new RunwayLandingOver(smallerRunway));
        smallerNumberedRunways.add(new RunwayTakeOffAwayFrom(smallerRunway));
        smallerNumberedRunways.add(new RunwayTakeOffTowards(smallerRunway));

        largerNumberedRunways.add(largerRunway);
        largerNumberedRunways.add(new RunwayLandingTowards(largerRunway));
        largerNumberedRunways.add(new RunwayLandingOver(largerRunway));
        largerNumberedRunways.add(new RunwayTakeOffAwayFrom(largerRunway));
        largerNumberedRunways.add(new RunwayTakeOffTowards(largerRunway));
    }

    /**
     * This function also recalculates the runway values for all runways.
     *
     * @param obstacle obstacle relative to smaller numbered runway
     */
    public void addObstacleToSmallerNumberedRunway(Obstacle obstacle) {
        var smallerRunway = smallerNumberedRunways.get(0);
        var largerRunway = largerNumberedRunways.get(0);
        var mirroredObstacle = createMirroredObstacle(obstacle, smallerRunway, largerRunway);
        smallerNumberedRunways.forEach(runway -> {
            runway.setObstacle(obstacle);
            runway.recalculateValues();
        });
        largerNumberedRunways.forEach(runway -> {
            runway.setObstacle(mirroredObstacle);
            runway.recalculateValues();
        });
    }

    /**
     * This function also recalculates the runway values for all runways.
     *
     * @param obstacle obstacle relative to the larger numbered runway
     */
    public void addObstacleToLargerNumberedRunway(Obstacle obstacle) {
        var smallerRunway = smallerNumberedRunways.get(0);
        var largerRunway = largerNumberedRunways.get(0);
        var mirroredObstacle = createMirroredObstacle(obstacle, largerRunway, smallerRunway);
        smallerNumberedRunways.forEach(runway -> {
            runway.setObstacle(mirroredObstacle);
            runway.recalculateValues();
        });
        largerNumberedRunways.forEach(runway -> {
            runway.setObstacle(obstacle);
            runway.recalculateValues();
        });
    }

    public void setObstacle(Obstacle obstacle, boolean smallerRunway) {
        this.obstacle.set(obstacle);
        if (obstacle == null) {
            this.smallerNumberedRunways.forEach(runway -> {
                runway.setObstacle(null);
                runway.recalculateValues();
            });
            this.largerNumberedRunways.forEach(runway -> {
                runway.setObstacle(null);
                runway.recalculateValues();
            });
            return;
        }
        if (smallerRunway) {
            this.addObstacleToSmallerNumberedRunway(obstacle);
        } else {
            this.addObstacleToLargerNumberedRunway(obstacle);
        }
    }

    private Obstacle createMirroredObstacle(Obstacle obstacle, LogicalRunway active, LogicalRunway reciprocal) {
        return new Obstacle(active.getTora() - active.getDisplacedThreshold() - obstacle.getX() - reciprocal.getDisplacedThreshold(),
                (-1) * obstacle.getY(), obstacle.getLength(), obstacle.getWidth(), obstacle.getHeight(), obstacle.getName());
    }

    @XmlElement(name = "SmallerRunway")
    public LogicalRunwayList getSmallerNumberedRunways() {
        return smallerNumberedRunways;
    }

    @XmlElement(name = "LargerRunway")
    public LogicalRunwayList getLargerNumberedRunways() {
        return largerNumberedRunways;
    }

    @XmlElement(name = "RunwayId")
    public RunwayId getRunwayId() {
        return runwayId;
    }

    public void setRunwayId(RunwayId runwayId) {
        this.runwayId = runwayId;
    }

    public Obstacle getObstacle(boolean smallerRunway) {
        if (smallerRunway) {
            return this.getSmallerNumberedRunways().getObstacle();
        }
        return this.getLargerNumberedRunways().getObstacle();
    }

    @XmlElement(name = "Obstacle")
    public Obstacle getObstacle() {
        return obstacle.get();
    }

    public ObstacleObservable obstacleProperty() {
        return this.obstacle;
    }

    void setObstacle(Obstacle obstacle) {
        this.obstacle.set(obstacle);
    }

    public LogicalRunwayList getLogicalRunways(boolean smallerRunway) {
        if (smallerRunway) {
            return this.getSmallerNumberedRunways();
        }
        return this.getLargerNumberedRunways();
    }

    public String log() {
        try {
            var smallerRunway = this.getSmallerNumberedRunways().get(0);
            var largerRunway = this.getLargerNumberedRunways().get(0);
            return String.format("ID - %s, TODA - %.2f, ASDA - %.2f, TORA - %.2f, LDA - %.2f\nID - %s, TODA - %.2f, ASDA - %.2f, TORA - %.2f, LDA - %.2f", runwayId.toString().split("/")[0], smallerRunway.getToda(), smallerRunway.getAsda(), smallerRunway.getTora(), smallerRunway.getLda(), runwayId.toString().split("/")[1], largerRunway.getToda(), largerRunway.getAsda(), largerRunway.getTora(), largerRunway.getLda());
        } catch (IndexOutOfBoundsException e) {
            return "ERROR: NO PHYSICAL RUNWAY";
        }
    }

    public void debug() {
        smallerNumberedRunways.forEach(LogicalRunway::debugRunway);
        largerNumberedRunways.forEach(LogicalRunway::debugRunway);
    }
}