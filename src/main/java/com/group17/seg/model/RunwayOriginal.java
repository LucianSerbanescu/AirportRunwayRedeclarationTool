package com.group17.seg.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

@XmlRootElement
public class RunwayOriginal extends LogicalRunway {
    RunwayOriginal() {
    }

    public RunwayOriginal(double toda, double tora, double asda, double lda) {
        super(toda, tora, asda, lda);
    }

    @Override
    public void recalculateValues() {
    }

    @Override
    public ArrayList<String> getCompareValues() {
        var output = new ArrayList<String>();
        output.add(String.format("%.1f", this.getToda()));
        output.add(String.format("%.1f", this.getAsda()));
        output.add(String.format("%.1f", this.getTora()));
        output.add(String.format("%.1f", this.getLda()));
        output.add(String.format("%.1f", this.getDisplacedThreshold()));
        return output;
    }

    @Override
    public String log() {
        return String.format("Original Runway - TODA = %.2f, ASDA = %.2f, TORA = %.2f, LDA = %.2f, DT = %.2f",
                             this.getToda(),
                             this.getAsda(),
                             this.getTora(),
                             this.getLda(),
                             this.getDisplacedThreshold());
    }
}
