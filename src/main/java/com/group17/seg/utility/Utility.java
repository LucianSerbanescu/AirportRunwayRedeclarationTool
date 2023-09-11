package com.group17.seg.utility;

import com.group17.seg.model.RunwayId;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

public class Utility {
    /**
     * @param node  Node to assign a css style class to.
     * @param style String of the style class to assign to the node.
     */
    public static void setStyle(Node node, String style) {
        node.getStyleClass().clear();
        node.getStyleClass().add(style);
        node.getStyleClass().add("text-highlight");
    }

    public static ColumnConstraints columnConstraint() {
        var columnConstraint = new ColumnConstraints();
        columnConstraint.setHalignment(HPos.CENTER);
        columnConstraint.setHgrow(Priority.SOMETIMES);
        return columnConstraint;
    }

    public static ColumnConstraints columnConstraintRunwaySelection() {
        var columnConstraint = new ColumnConstraints();
        columnConstraint.setHalignment(HPos.CENTER);
        columnConstraint.setHgrow(Priority.NEVER);
        return columnConstraint;
    }

    public static ColumnConstraints columnConstraint(HPos hpos, Priority priority, double minWidth, double maxWidth) {
        var output = new ColumnConstraints();
        output.setHalignment(hpos);
        output.setHgrow(priority);
        output.setMinWidth(minWidth);
        output.setMaxWidth(maxWidth);
        return output;
    }

    public static RowConstraints rowConstraint() {
        var rowConstraint = new RowConstraints();
        rowConstraint.setVgrow(Priority.SOMETIMES);
        rowConstraint.setValignment(VPos.CENTER);
        return rowConstraint;
    }

    public static boolean isReciprocal(String runway1, String runway2) {
        if (runway1.equals("") || runway2.equals("")) return true;
        var runway1angle = Integer.parseInt(runway1.replaceAll("\\D+", ""));
        var runway1pos = runway1.replaceAll("\\d+", "");
        var runway2angle = Integer.parseInt(runway2.replaceAll("\\D+", ""));
        var runway2pos = runway2.replaceAll("\\d+", "");
        if (runway1angle % 18 != runway2angle % 18) {
            return false;
        }
        if (runway1pos.equals("") && runway2pos.equals("")) {
            return true;
        }
        if (runway1pos.equals("") || runway2pos.equals("")) {
            return false;
        }
        switch (runway1pos) {
            case "L" -> {
                return runway2pos.equals("R");
            }
            case "R" -> {
                return runway2pos.equals("L");
            }
            case "C" -> {
                return runway2pos.equals("C");
            }
            case "l" -> {
                return runway2pos.equals("r");
            }
            case "r" -> {
                return runway2pos.equals("l");
            }
            case "c" -> {
                return runway2pos.equals("c");
            }
            default -> {
                return false;
            }
        }
    }

    public static String getReciprocal(String original) {
        if (original.equals("")) return "";
        var angle = original.replaceAll("\\D+", "");
        if (angle.equals("")) return "";
        var originalAngle = Integer.parseInt(angle);
        var originalPos = original.replaceAll("\\d+", "");

        String outputString = String.format("%02d", (originalAngle + 17) % 36 + 1);
        if (originalPos.equals("")) {
            return outputString;
        }
        switch (originalPos) {
            case "L" -> outputString += "R";
            case "R" -> outputString += "L";
            case "C" -> outputString += "C";
            case "l" -> outputString += "r";
            case "r" -> outputString += "l";
            case "c" -> outputString += "c";
        }
        return outputString;
    }

    public static RunwayId parseString(String original) {
        var output = new RunwayId();
        if (original.equals("")) return output;
        var angle = original.replaceAll("\\D+", "");
        if (angle.equals("")) return output;
        var originalAngle = Integer.parseInt(angle);
        var originalPos = original.replaceAll("\\d+", "");

        output.setAngle((originalAngle - 1) % 18 + 1);
        if (originalPos.equals("")) {
            return output;
        }
        if (originalAngle > 18) {
            switch (originalPos) {
                case "L", "l" -> output.setPosition(RunwayId.Position.RIGHT);
                case "R", "r" -> output.setPosition(RunwayId.Position.LEFT);
                case "C", "c" -> output.setPosition(RunwayId.Position.CENTRE);
            }
        } else {
            switch (originalPos) {
                case "L", "l" -> output.setPosition(RunwayId.Position.LEFT);
                case "R", "r" -> output.setPosition(RunwayId.Position.RIGHT);
                case "C", "c" -> output.setPosition(RunwayId.Position.CENTRE);
            }
        }
        return output;
    }
}