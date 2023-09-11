package com.group17.seg.model;

import java.util.ArrayList;

public class RunwayLandingTowards extends LogicalRunway {

    public RunwayLandingTowards() {
    }

    public RunwayLandingTowards(RunwayOriginal originalRunway) {
        super(originalRunway);
    }

    @Override
    public void recalculateValues() {
        var obstacle = this.getObstacle();
        if (obstacle == null) {
            this.setLda(this.getOriginalRunway().getLda());
            return;
        }
        var newLda = Math.min(obstacle.getX() - obstacle.getLength() / 2 - stripEnd - resaMinimum, this.getOriginalRunway().getLda());
        this.setLda(newLda);
        this.setResa(resaMinimum);
    }

    @Override
    public ArrayList<String> getCompareValues() {
        var output = new ArrayList<String>();
        output.add("Landing Towards");
        output.add("N/A");
        output.add("N/A");
        output.add("N/A");
        if (this.getLda() < 0) {
            output.add("Not Usable");
        } else {
            output.add(String.format("%.1f", this.getLda()));
        }
        output.add(String.format("%.1f", this.getDisplacedThreshold()));
        return output;
    }

    public String showBreakdownOfLda() {
        var obstacle = this.getObstacle();
        return String.format(" = %.1f - 60.0 - 240.0 ", obstacle.getX() - obstacle.getLength() / 2);
    }

    @Override
    public String log() {
        return String.format("Landing Towards - LDA = %.2f, DT = %.2f", this.getLda(), this.getDisplacedThreshold());
    }
}
