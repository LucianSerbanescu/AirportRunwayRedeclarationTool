package com.group17.seg.model;

import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"angle", "position"})
public class RunwayId {
    public enum Position {
        LEFT, RIGHT, CENTRE
    }

    private int angle;
    private Position position;

    public RunwayId() {
        this.angle = 0;
    }

    public RunwayId(int angle) {
        this.angle = (angle > 18) ? angle % 18 : angle;
        this.position = null;
    }

    public RunwayId(int angle, Position position) {
        if (angle > 18) {
            this.angle = angle % 18;
            switch (position) {
                case LEFT -> this.position = Position.RIGHT;
                case RIGHT -> this.position = Position.LEFT;
                case CENTRE -> this.position = Position.CENTRE;
            }
        } else {
            this.angle = angle;
            this.position = position;
        }
    }

    public RunwayId(RunwayId runwayId) {
        this.angle = runwayId.getAngle();
        this.position = runwayId.getPosition();
    }

    /**
     * @return String representation of the physical runway, according to its runwayId.
     */
    public String toString() {
        String output = "";
        if (position == null) {
            output = String.format("%1$02d/%2$02d", angle, angle + 18);
        }
        if (position == Position.LEFT) {
            output = String.format("%1$02dL/%2$02dR", angle, angle + 18);
        }
        if (position == Position.CENTRE) {
            output = String.format("%1$02dC/%2$02dC", angle, angle + 18);
        }
        if (position == Position.RIGHT) {
            output = String.format("%1$02dR/%2$02dL", angle, angle + 18);
        }
        return output;
    }

    public String toString(boolean smaller) {
        if (smaller) return this.toStringSmaller();
        return this.toStringLarger();
    }


    public String toStringSmaller() {
        var position = this.getPosition();
        if (position == null) {
            return String.format("%02d", angle);
        } else if (position == Position.LEFT) {
            return String.format("%02dL", angle);
        } else if (position == Position.CENTRE) {
            return String.format("%02dC", angle);
        } else if (position == Position.RIGHT) {
            return String.format("%02dR", angle);
        } else {
            return String.format("%02d", angle);
        }
    }

    public String toStringLarger() {
        var position = this.getPosition();
        if (position == null) {
            return String.format("%02d", angle + 18);
        } else if (position == Position.LEFT) {
            return String.format("%02dR", angle + 18);
        } else if (position == Position.CENTRE) {
            return String.format("%02dC", angle + 18);
        } else if (position == Position.RIGHT) {
            return String.format("%02dL", angle + 18);
        } else {
            return String.format("%02d", angle + 18);
        }
    }

    public int getAngle() {
        return this.angle;
    }

    public String getAngle(boolean smallerRunway) {
        var outputAngle = smallerRunway ? angle : angle + 18;
        return String.format("%02d", outputAngle);
    }

    public Position getPosition() {
        return this.position;
    }


    public void setAngle(int angle) {
        this.angle = angle;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public boolean isReciprocal(RunwayId runwayId) {
        if (this.getAngle() % 18 != runwayId.getAngle() % 18) {
            return false;
        }
        if (this.getPosition() == null && runwayId.getPosition() == null) {
            return true;
        }
        if (this.getPosition() == null || runwayId.getPosition() == null) {
            return false;
        }
        switch (this.getPosition()) {
            case LEFT -> {
                return runwayId.getPosition() == Position.RIGHT;
            }
            case RIGHT -> {
                return runwayId.getPosition() == Position.LEFT;
            }
            case CENTRE -> {
                return runwayId.getPosition() == Position.CENTRE;
            }
            default -> {
                return false;
            }
        }
    }

    public boolean equals(RunwayId runwayId) {
        if (this.angle != runwayId.getAngle()) {
            return false;
        }
        return this.position == runwayId.position;
    }
}
