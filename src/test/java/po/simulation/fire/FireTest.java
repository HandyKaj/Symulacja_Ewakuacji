package po.simulation.fire;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FireTest {

    @Test
    void tickIncreasesIntensity() {
        Fire fire = new Fire(30);
        fire.tick();
        assertTrue(fire.getIntensity() > 30);
    }

    @Test
    void reduceDecreasesIntensity() {
        Fire fire = new Fire(50);
        fire.reduce(20);
        assertEquals(30, fire.getIntensity());
    }

    @Test
    void intensityDoesNotExceed100() {
        Fire fire = new Fire(99);
        fire.tick();
        fire.tick();
        fire.tick();
        assertTrue(fire.getIntensity() <= 100);
    }

    @Test
    void intensityDoesNotGoBelowZero() {
        Fire fire = new Fire(10);
        fire.reduce(50);
        assertEquals(0, fire.getIntensity());
    }

    @Test
    void isBlockingWhenIntensityAboveThreshold() {
        Fire fire = new Fire(60);
        assertTrue(fire.isBlocking());
    }

    @Test
    void isNotBlockingWhenIntensityBelowThreshold() {
        Fire fire = new Fire(30);
        assertFalse(fire.isBlocking());
    }

    @Test
    void initialIntensitySetCorrectly() {
        Fire fire = new Fire(40);
        assertEquals(40, fire.getIntensity());
    }

    @Test
    void setIntensityUpdatesValue() {
        Fire fire = new Fire(30);
        fire.setIntensity(70);
        assertEquals(70, fire.getIntensity());
    }

    @Test
    void setIntensityCannotExceed100() {
        Fire fire = new Fire(30);
        fire.setIntensity(150);
        assertEquals(100, fire.getIntensity());
    }

    @Test
    void setIntensityCannotGoBelowZero() {
        Fire fire = new Fire(30);
        fire.setIntensity(-10);
        assertEquals(0, fire.getIntensity());
    }

    @Test
    void spreadCallsTick() {
        Fire fire = new Fire(30);
        int before = fire.getIntensity();
        fire.spread();
        assertTrue(fire.getIntensity() > before);
    }
}