package po.simulation.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SimConfigTest {

    @Test
    void constructorSetsCorrectValues() {
        SimConfig config = new SimConfig(0.5f, 0.3f, 4, 15);
        assertEquals(0.5f, config.getSpreadSpeed());
        assertEquals(0.3f, config.getPanicSpreadChance());
        assertEquals(4, config.getCrowdThreshold());
        assertEquals(15, config.getFirefighterDelay());
    }

    @Test
    void defaultConfigHasCorrectValues() {
        SimConfig config = SimConfig.defaultConfig();
        assertEquals(0.3f, config.getSpreadSpeed());
        assertEquals(0.3f, config.getPanicSpreadChance());
        assertEquals(3, config.getCrowdThreshold());
        assertEquals(10, config.getFirefighterDelay());
    }

    @Test
    void setSpreadSpeedUpdatesValue() {
        SimConfig config = new SimConfig(0.3f, 0.3f, 3, 10);
        config.setSpreadSpeed(0.6f);
        assertEquals(0.6f, config.getSpreadSpeed());
    }

    @Test
    void setPanicSpreadChanceUpdatesValue() {
        SimConfig config = new SimConfig(0.3f, 0.3f, 3, 10);
        config.setPanicSpreadChance(0.5f);
        assertEquals(0.5f, config.getPanicSpreadChance());
    }

    @Test
    void setCrowdThresholdUpdatesValue() {
        SimConfig config = new SimConfig(0.3f, 0.3f, 3, 10);
        config.setCrowdThreshold(5);
        assertEquals(5, config.getCrowdThreshold());
    }

    @Test
    void setFirefighterDelayUpdatesValue() {
        SimConfig config = new SimConfig(0.3f, 0.3f, 3, 10);
        config.setFirefighterDelay(20);
        assertEquals(20, config.getFirefighterDelay());
    }
}