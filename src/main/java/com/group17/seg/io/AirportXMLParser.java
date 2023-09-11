package com.group17.seg.io;

import com.group17.seg.model.AirportList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.InputStream;

public class AirportXMLParser {
    public static void writeAirportsToXML(String url, AirportList airportList) {
        try {
            JAXBContext context = JAXBContext.newInstance(AirportList.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            File file = new File(url);
            marshaller.marshal(airportList, file);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public static void writeAirportsToXML(File file, AirportList airportList) {
        try {
            JAXBContext context = JAXBContext.newInstance(AirportList.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(airportList, file);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public static AirportList parseAirportXML(String url) throws JAXBException {
        var file = new File(url);
        return parseFile(file);
    }

    public static AirportList parseAirportXML(File file) throws JAXBException {
        return parseFile(file);
    }

    private static AirportList parseFile(File file) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(AirportList.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());
        return (AirportList) unmarshaller.unmarshal(file);
    }

    public static AirportList parseAirportXML(InputStream inputStream) {
        return parseFile(inputStream);
    }

    private static AirportList parseFile(InputStream inputStream) {
        try {
            JAXBContext context = JAXBContext.newInstance(AirportList.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            unmarshaller.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());
            return (AirportList) unmarshaller.unmarshal(inputStream);
        } catch (JAXBException e) {
            e.printStackTrace();
            return new AirportList();
        }
    }
}
