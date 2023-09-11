package com.group17.seg.model;

import java.util.ArrayList;

public class RunwayLandingOver extends LogicalRunway {
    public RunwayLandingOver() {
        super();
    }

    public RunwayLandingOver(RunwayOriginal originalRunway) {
        super(originalRunway);
    }

    @Override
    public void recalculateValues() {
        var originalRunway = getOriginalRunway();
        var obstacle = this.getObstacle();
        if (obstacle == null) {
            this.setLda(originalRunway.getLda());
            return;
        }
        var newLda = originalRunway.getLda() - (obstacle.getX() + obstacle.getLength() / 2)
                - Math.max(obstacle.getHeight() * slopeRatio, blastRadius) - stripEnd;
        this.setLda(newLda);
        this.setResa(resaMinimum);
    }

    @Override
    public ArrayList<String> getCompareValues() {
        var output = new ArrayList<String>();
        output.add("Landing Over");
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
        var originalRunway = this.getOriginalRunway();
        return String.format(
                " = %.1f - %.1f - 60.0 - %.1f",
                originalRunway.getLda(),
                obstacle.getX() + obstacle.getLength() / 2,
                Math.max(obstacle.getHeight() * 50, resaMinimum)
        );
    }

    @Override
    public String log() {
        return String.format("Landing Over - LDA = %.2f, DT = %.2f", this.getLda(), this.getDisplacedThreshold());
    }
}
