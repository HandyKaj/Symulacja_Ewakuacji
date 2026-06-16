package po.simulation.metrics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SimMetricsTest {

    private SimMetrics metrics;

    @BeforeEach
    void setUp() {
        metrics = new SimMetrics();
    }

    @Test
    void initialValuesAreZero() {
        assertEquals(0, metrics.getEvacuatedCount());
        assertEquals(0, metrics.getDeadCount());
        assertEquals(0.0f, metrics.getAvgEvacTime());
    }

    @Test
    void registerEvacuationIncreasesCount() {
        metrics.registerEvacuation(10);
        assertEquals(1, metrics.getEvacuatedCount());
    }

    @Test
    void registerDeathIncreasesCount() {
        metrics.registerDeath();
        assertEquals(1, metrics.getDeadCount());
    }

    @Test
    void multipleEvacuationsIncreasesCount() {
        metrics.registerEvacuation(10);
        metrics.registerEvacuation(20);
        metrics.registerEvacuation(30);
        assertEquals(3, metrics.getEvacuatedCount());
    }

    @Test
    void multipleDeathsIncreasesCount() {
        metrics.registerDeath();
        metrics.registerDeath();
        assertEquals(2, metrics.getDeadCount());
    }

    @Test
    void avgEvacTimeCalculatedCorrectly() {
        metrics.registerEvacuation(10);
        metrics.registerEvacuation(20);
        // średnia = (10+20)/2 = 15
        assertEquals(15.0f, metrics.getAvgEvacTime());
    }

    @Test
    void avgEvacTimeWithSingleEvacuation() {
        metrics.registerEvacuation(25);
        assertEquals(25.0f, metrics.getAvgEvacTime());
    }
}