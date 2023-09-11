package com.group17.seg.io;

import com.group17.seg.model.ObstacleList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.InputStream;

public class ObstacleXMLParser {
    public static void writeObstaclesToXML(String url, ObstacleList obstacleList) {
        try {
            JAXBContext context = JAXBContext.newInstance(ObstacleList.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(obstacleList, new File(url));
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param url File location of XML
     * @return List of "Obstacle" objects from the inputted XML
     */
    public static ObstacleList parseObstacleXML(String url) throws JAXBException {
        var file = new File(url);
        return parseFile(file);
    }

    /**
     * @param file File object
     * @return List of "Obstacle" objects from the inputted XML
     */
    public static ObstacleList parseObstacleXML(File file) throws JAXBException {
        return parseFile(file);
    }

    private static ObstacleList parseFile(File file) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(ObstacleList.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());
        return (ObstacleList) unmarshaller.unmarshal(file);
    }

    public static ObstacleList parseObstacleXML(InputStream inputStream) {
        return parseFile(inputStream);
    }

    private static ObstacleList parseFile(InputStream inputStream) {
        try {
            JAXBContext context = JAXBContext.newInstance(ObstacleList.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            unmarshaller.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());
            return (ObstacleList) unmarshaller.unmarshal(inputStream);
        } catch (JAXBException e) {
            e.printStackTrace();
            return new ObstacleList();
        }
    }
}