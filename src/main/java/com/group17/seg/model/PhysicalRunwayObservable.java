package com.group17.seg.model;

import javafx.beans.binding.ObjectBinding;

public class PhysicalRunwayObservable extends ObjectBinding<PhysicalRunway> {
    private PhysicalRunway physicalRunway;

    public PhysicalRunwayObservable() {
        this.physicalRunway = null;
    }

    public PhysicalRunwayObservable(PhysicalRunway physicalRunway) {
        if (physicalRunway == null) {
            this.physicalRunway = null;
        } else {
            this.physicalRunway = physicalRunway;
            this.bind(this.physicalRunway.obstacleProperty());
        }
    }

    public void set(PhysicalRunway physicalRunway) {
        if (physicalRunway == null) {
            this.physicalRunway = null;
        } else {
            this.physicalRunway = physicalRunway;
            this.bind(this.physicalRunway.obstacleProperty());
        }
        this.invalidate();
    }
    @Override
    protected PhysicalRunway computeValue() {
        return this.physicalRunway;
    }
}
