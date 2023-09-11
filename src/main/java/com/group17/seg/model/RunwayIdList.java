package com.group17.seg.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RunwayIdList extends ArrayList<RunwayId> {
    public RunwayIdList(RunwayId... runwayIds) {
        super();
        if (runwayIds.length > 0) {
            this.addAll(Arrays.asList(runwayIds));
        }
    }

    public List<RunwayId> getPhysicalRunwayList() {
        return this;
    }
}
