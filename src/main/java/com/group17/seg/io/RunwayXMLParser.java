package com.group17.seg.io;

import com.group17.seg.model.PhysicalRunwayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public class RunwayXMLParser {
    public static void writeRunwaysToXML(String url, PhysicalRunwayList physicalRunwayList) {
        try {
            JAXBContext context = JAXBContext.newInstance(PhysicalRunwayList.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(physicalRunwayList, new File(url));
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public static PhysicalRunwayList parseRunwayXML(String url) throws JAXBException {
        var file = new File(url);
        return parseFile(file);
    }

    public static PhysicalRunwayList parseRunwayXML(File file) throws JAXBException {
        return parseFile(file);
    }

    private static PhysicalRunwayList parseFile(File file) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(PhysicalRunwayList.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (PhysicalRunwayList) unmarshaller.unmarshal(file);
    }
}
