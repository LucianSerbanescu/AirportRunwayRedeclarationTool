import com.group17.seg.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PhysicalRunwayTest {
    private PhysicalRunway physicalRunway;
    private RunwayOriginal originalRunway;

    @BeforeEach
    public void setUp() {
        originalRunway = new RunwayOriginal(1000, 900, 1100, 1200);
        physicalRunway = new PhysicalRunway(originalRunway, new RunwayId());
    }

    @Test
    public void testAddObstacleToSmallerNumberedRunway() {
        Obstacle obstacle = new Obstacle(300, 0, 10, 10, 10, "Test Obstacle");
        physicalRunway.addObstacleToSmallerNumberedRunway(obstacle);
        LogicalRunwayList smallerNumberedRunways = physicalRunway.getSmallerNumberedRunways();

        for (LogicalRunway runway : smallerNumberedRunways) {
            assertEquals(obstacle, runway.getObstacle());
        }
    }

    @Test
    public void testAddObstacleToLargerNumberedRunway() {
        Obstacle obstacle = new Obstacle(300, 0, 10, 10, 10, "Test Obstacle");
        physicalRunway.addObstacleToLargerNumberedRunway(obstacle);
        LogicalRunwayList largerNumberedRunways = physicalRunway.getLargerNumberedRunways();

        for (LogicalRunway runway : largerNumberedRunways) {
            assertEquals(obstacle, runway.getObstacle());
        }
    }

    @Test
    public void testConstructorWithOneRunwayOriginal() {
        assertNotNull(physicalRunway.getSmallerNumberedRunways());
        assertNotNull(physicalRunway.getLargerNumberedRunways());
        assertEquals(5, physicalRunway.getSmallerNumberedRunways().size());
        assertEquals(5, physicalRunway.getLargerNumberedRunways().size());
    }

    @Test
    public void testConstructorWithTwoRunwayOriginals() {
        RunwayOriginal largerRunway = new RunwayOriginal(1000, 900, 1100, 1200);
        PhysicalRunway physicalRunway2 = new PhysicalRunway(originalRunway, largerRunway, new RunwayId());

        assertNotNull(physicalRunway2.getSmallerNumberedRunways());
        assertNotNull(physicalRunway2.getLargerNumberedRunways());
        assertEquals(5, physicalRunway2.getSmallerNumberedRunways().size());
        assertEquals(5, physicalRunway2.getLargerNumberedRunways().size());
    }

    @Test
    public void testGetLogicalRunways() {
        LogicalRunwayList smallerRunways = physicalRunway.getLogicalRunways(true);
        LogicalRunwayList largerRunways = physicalRunway.getLogicalRunways(false);

        assertNotNull(smallerRunways);
        assertNotNull(largerRunways);
        assertEquals(5, smallerRunways.size());
        assertEquals(5, largerRunways.size());
    }
}
