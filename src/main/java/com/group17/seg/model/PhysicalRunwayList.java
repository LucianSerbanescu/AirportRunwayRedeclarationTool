package com.group17.seg.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@XmlRootElement
public class PhysicalRunwayList extends ArrayList<PhysicalRunway> {
    public PhysicalRunwayList() {
        super();
    }

    public PhysicalRunwayList(int initialCapacity) {
        super(initialCapacity);
    }

    public PhysicalRunwayList(PhysicalRunway... physicalRunways) {
        super();
        if (physicalRunways.length > 0) {
            this.addAll(Arrays.asList(physicalRunways));
        }
    }

    @XmlElement
    public List<PhysicalRunway> getPhysicalRunwayList() {
        return this;
    }
}
