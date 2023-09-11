package com.group17.seg.model;

import java.util.ArrayList;

public class RunwayTakeOffTowards extends LogicalRunway {
    public RunwayTakeOffTowards() {
    }

    public RunwayTakeOffTowards(RunwayOriginal originalRunway) {
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
        var dt = originalRunway.getDisplacedThreshold();
        var obstacleEdge = obstacle.getX() - obstacle.getLength() / 2;
        var safezone = Math.max(obstacle.getHeight() * slopeRatio, resaMinimum);
        var newTora = originalRunway.getDisplacedThreshold() + obstacle.getX() - obstacle.getLength() / 2 - Math.max(
                obstacle.getHeight() * slopeRatio, resaMinimum) - stripEnd;
        newTora = dt + obstacleEdge - safezone - stripEnd;

        this.setTora(Math.min(originalRunway.getTora(), newTora));
        this.setAsda(Math.min(originalRunway.getTora(), newTora));
        this.setToda(Math.min(originalRunway.getTora(), newTora));
        this.setLda(newTora);
        this.setDisplacedThreshold(0);
        this.setResa(resaMinimum);
    }

    @Override
    public ArrayList<String> getCompareValues() {
        var output = new ArrayList<String>();
        output.add("Take-Off Towards");
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

    public String showBreakdownOfToraTodaAsda() {
        var obstacle = this.getObstacle();
        var originalRunway = this.getOriginalRunway();
        return String.format(" = %.1f + %.1f - %.1f - 60.0", originalRunway.getDisplacedThreshold(), obstacle.getX() - obstacle.getLength() / 2,
                             Math.max(obstacle.getHeight() * 50, resaMinimum));
    }

    @Override
    public String log() {
        return String.format("Take-off Towards - TODA = %.2f, ASDA = %.2f, TORA = %.2f", this.getToda(), this.getAsda(), this.getTora());
    }
}
