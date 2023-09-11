package com.group17.seg.view;

import com.group17.seg.utility.Utility;
import com.group17.seg.view.loadairport.LoadAirportUI;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

public abstract class BaseNode {
    protected final LoadAirportUI loadAirportUI;

    public BaseNode(LoadAirportUI loadAirportUI) {
        this.loadAirportUI = loadAirportUI;
    }

    public abstract Node getNode();

    public HBox newSelectionButtonHBox(String id) {
        var output = new HBox(5);
        Utility.setStyle(output, "hbox-selection-buttons");
        output.setId(id);
        return output;
    }
}
