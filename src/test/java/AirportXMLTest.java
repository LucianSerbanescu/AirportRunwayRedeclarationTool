import com.group17.seg.model.Airport;
import com.group17.seg.model.AirportList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AirportXMLTest {
    private Airport airport;

    @BeforeEach
    void setUp() {
        // Create an airport object with the necessary properties
        airport = new Airport();
        airport.setName("Test Airport");
        // Add other properties if needed, like runways or other elements

        // Create an AirportList and add the airport to it
        AirportList airportList = new AirportList();
        airportList.add(airport);

        // Save the airportList object to an XML file
        try {
            JAXBContext context = JAXBContext.newInstance(AirportList.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(airportList, new File("testAirportList.xml"));
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testAirportXML() {
        // Load the airportList object from the XML file
        AirportList loadedAirportList = null;
        try {
            JAXBContext context = JAXBContext.newInstance(AirportList.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            loadedAirportList = (AirportList) unmarshaller.unmarshal(new File("testAirportList.xml"));
        } catch (JAXBException e) {
            e.printStackTrace();
        }

        // Remove the test XML file after loading the airportList
        File file = new File("testAirportList.xml");
        if (file.exists()) {
            var deleted = file.delete();
        }

        // Check if the loaded airportList is not null and has at least one airport
        assertNotNull(loadedAirportList, "Loaded airport list should not be null");
        assertEquals(1, loadedAirportList.size(), "Loaded airport list should contain 1 airport");

        // Compare the properties of the original and loaded airport objects
        Airport loadedAirport = loadedAirportList.get(0);
        assertEquals(airport.getName(), loadedAirport.getName(), "Airport names should be the same");
        // Add other property comparisons if needed, like runways or other elements
    }
}