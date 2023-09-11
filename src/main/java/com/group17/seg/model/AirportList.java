package com.group17.seg.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "Airports")
public class AirportList extends ArrayList<Airport> {
    public AirportList() {
        super();
    }

    public AirportList(Airport... airports) {
        this.addAll(List.of(airports));
    }

    public AirportList(List<Airport> airports) {
        this.addAll(airports);
    }

    @XmlElement(name = "Airport")
    public List<Airport> getAirportList() {
        return this;
    }
}
