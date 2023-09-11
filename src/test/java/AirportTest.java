import com.group17.seg.model.Airport;
import com.group17.seg.model.PhysicalRunway;
import com.group17.seg.model.RunwayId;
import com.group17.seg.model.RunwayOriginal;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AirportTest {

    @Test
    public void testAddPhysicalRunway() {
        Airport airport = new Airport();
        RunwayId runwayId = new RunwayId(9, RunwayId.Position.LEFT);
        RunwayOriginal runwayOriginal = new RunwayOriginal(3000, 3000, 3000, 3000);
        PhysicalRunway physicalRunway = new PhysicalRunway(runwayOriginal, runwayId);
        assertTrue(airport.addPhysicalRunway(physicalRunway), "Physical runway should be added successfully.");
    }

    @Test
    public void testAirportName() {
        String airportName = "London Heathrow";
        Airport airport = new Airport();
        airport.setName(airportName);
        assertEquals(airportName, airport.getName());
    }
}
