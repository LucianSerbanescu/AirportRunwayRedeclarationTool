import com.group17.seg.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LogicalRunwayTest {

    private RunwayLandingOver runwayLandingOver;
    private RunwayLandingTowards runwayLandingTowards;
    private RunwayTakeOffAwayFrom runwayTakeOffAwayFrom;
    private RunwayTakeOffTowards runwayTakeOffTowards;
    private Obstacle obstacle;
    private RunwayOriginal originalRunway;

    @BeforeEach
    public void setUp() {
        originalRunway = new RunwayOriginal(3000, 3000, 3000, 3000);

        double obstacleX = 500;
        double obstacleY = 100;

        obstacle = new Obstacle(obstacleX, obstacleY, 200, 100, 50, "rock");

        runwayLandingOver = new RunwayLandingOver(originalRunway);
        runwayLandingTowards = new RunwayLandingTowards(originalRunway);
        runwayTakeOffAwayFrom = new RunwayTakeOffAwayFrom(originalRunway);
        runwayTakeOffTowards = new RunwayTakeOffTowards(originalRunway);

        runwayLandingOver.setObstacle(obstacle);
        runwayLandingTowards.setObstacle(obstacle);
        runwayTakeOffAwayFrom.setObstacle(obstacle);
        runwayTakeOffTowards.setObstacle(obstacle);
    }


    private void addObstacleToOriginalRunways(Obstacle obstacle) {
        runwayLandingOver.getOriginalRunway().setObstacle(obstacle);
        runwayLandingTowards.getOriginalRunway().setObstacle(obstacle);
        runwayTakeOffAwayFrom.getOriginalRunway().setObstacle(obstacle);
        runwayTakeOffTowards.getOriginalRunway().setObstacle(obstacle);
    }


    @Test
    public void testRunwayLandingOverRecalculateValues() {
        runwayLandingOver.recalculateValues();

        double expectedLda =
                originalRunway.getLda() - (obstacle.getX() + obstacle.getLength() / 2) - Math.max(obstacle.getHeight() * LogicalRunway.getSlopeRatio(),
                                                                                                               LogicalRunway.getBlastRadius()) - LogicalRunway.getStripEnd();
        assertEquals(expectedLda, runwayLandingOver.getLda());
        assertEquals(LogicalRunway.getResaMinimum(), runwayLandingOver.getResa());
    }

    @Test
    public void testRunwayLandingTowardsRecalculateValues() {
        runwayLandingTowards.recalculateValues();

        double expectedLda = obstacle.getX() - obstacle.getLength() / 2 - LogicalRunway.getStripEnd() - LogicalRunway.getResaMinimum();
        assertEquals(expectedLda, runwayLandingTowards.getLda());
        assertEquals(LogicalRunway.getResaMinimum(), runwayLandingTowards.getResa());
    }

    @Test
    public void testRunwayTakeOffAwayFromRecalculateValues() {
        runwayTakeOffAwayFrom.recalculateValues();

        double relevantObstacleEdge = obstacle.getX() + obstacle.getLength() / 2;
        double expectedToda =
                originalRunway.getToda() - LogicalRunway.getBlastRadius() - relevantObstacleEdge - originalRunway.getDisplacedThreshold();
        double expectedTora =
                originalRunway.getTora() - LogicalRunway.getBlastRadius() - relevantObstacleEdge - originalRunway.getDisplacedThreshold();
        double expectedAsda =
                originalRunway.getAsda() - LogicalRunway.getBlastRadius() - relevantObstacleEdge - originalRunway.getDisplacedThreshold();

        assertEquals(expectedToda, runwayTakeOffAwayFrom.getToda());
        assertEquals(expectedTora, runwayTakeOffAwayFrom.getTora());
        assertEquals(expectedAsda, runwayTakeOffAwayFrom.getAsda());
    }

    @Test
    public void testRunwayTakeOffTowardsRecalculateValues() {
        runwayTakeOffTowards.recalculateValues();

        double expectedTora =
                originalRunway.getDisplacedThreshold() + obstacle.getX() - obstacle.getLength() / 2 - Math.max(obstacle.getHeight() * LogicalRunway.getSlopeRatio(),
                                                                                                                             LogicalRunway.getResaMinimum()) - LogicalRunway.getStripEnd();
        assertEquals(expectedTora, runwayTakeOffTowards.getTora());
        assertEquals(expectedTora, runwayTakeOffTowards.getAsda());
        assertEquals(expectedTora, runwayTakeOffTowards.getToda());
        assertEquals(LogicalRunway.getResaMinimum(), runwayTakeOffTowards.getResa());
    }
}

