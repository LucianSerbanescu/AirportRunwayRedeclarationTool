package com.group17.seg.model;

import java.util.ArrayList;

public class RunwayTakeOffAwayFrom extends LogicalRunway {
    public RunwayTakeOffAwayFrom() {

    }

    public RunwayTakeOffAwayFrom(RunwayOriginal originalRunway) {
        super(originalRunway);
    }

    @Override
    public void recalculateValues() {
        var originalRunway = this.getOriginalRunway();
        var obstacle = this.getObstacle();
        if (obstacle == null) {
            this.setToda(originalRunway.getToda());
            this.setAsda(originalRunway.getAsda());
            this.setTora(originalRunway.getTora());
            return;
        }
        var relevantObstacleEdge = obstacle.getX() + obstacle.getLength() / 2;
        var newToda = originalRunway.getToda() - blastRadius - relevantObstacleEdge - originalRunway.getDisplacedThreshold();
        var newAsda = originalRunway.getAsda() - blastRadius - relevantObstacleEdge - originalRunway.getDisplacedThreshold();
        var newTora = originalRunway.getTora() - blastRadius - relevantObstacleEdge - originalRunway.getDisplacedThreshold();
        this.setToda(Math.min(originalRunway.getToda(), newToda));
        this.setTora(Math.min(originalRunway.getTora(), newTora));
        this.setAsda(Math.min(originalRunway.getAsda(), newAsda));
        this.setLda(Math.min(originalRunway.getTora(), newTora));
        this.setDisplacedThreshold(0);
    }

    @Override
    public ArrayList<String> getCompareValues() {
        var output = new ArrayList<String>();
        output.add("Take-Off Away From");
        if (this.getTora() < 0) {
            output.add("Not Usable");
            output.add("Not Usable");
            output.add("Not Usable");
        } else {
            output.add(String.format("%.1f", this.getToda()));
            output.add(String.format("%.1f", this.getAsda()));
            output.add(String.format("%.1f", this.getTora()));
        }
        output.add("N/A");
        output.add("N/A");
        return output;
    }

    public String showBreakdownOfToda() {
        var obstacle = this.getObstacle();
        var relevantObstacleEdge = obstacle.getX() + obstacle.getLength() / 2;
        var originalRunway = this.getOriginalRunway();
        return String.format(" = %.1f - 500.0 - %.1f - %.1f", originalRunway.getToda(), relevantObstacleEdge,
                             originalRunway.getDisplacedThreshold());
    }

    public String showBreakdownOfAsda() {
        var obstacle = this.getObstacle();
        var relevantObstacleEdge = obstacle.getX() + obstacle.getLength() / 2;
        var originalRunway = this.getOriginalRunway();
        return String.format(" = %.1f - 500.0 - %.1f - %.1f", originalRunway.getAsda(), relevantObstacleEdge,
                             originalRunway.getDisplacedThreshold());
    }

    public String showBreakdownOfTora() {
        var obstacle = this.getObstacle();
        var relevantObstacleEdge = obstacle.getX() + obstacle.getLength() / 2;
        var originalRunway = this.getOriginalRunway();
        return String.format(" = %.1f - 500.0 - %.1f - %.1f", originalRunway.getTora(), relevantObstacleEdge,
                             originalRunway.getDisplacedThreshold());
    }

    @Override
    public String log() {
        return String.format("Take-off Away From - TODA = %.2f, ASDA = %.2f, TORA = %.2f", this.getToda(), this.getAsda(), this.getTora());
    }
}
