import com.group17.seg.model.RunwayId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RunwayIdTest {
    @Test
    public void testRunwayIdAngle() {
        RunwayId runwayId = new RunwayId(27, RunwayId.Position.RIGHT);
        assertEquals(9, runwayId.getAngle());
    }

    @Test
    public void testRunwayIdPosition() {
        RunwayId runwayId = new RunwayId(27, RunwayId.Position.RIGHT);
        assertEquals(RunwayId.Position.LEFT, runwayId.getPosition());
    }
}
