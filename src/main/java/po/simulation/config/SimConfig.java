package po.simulation.config;

public class SimConfig {
    private float spreadSpeed;
    private float panicSpreadChance;
    private int crowdThreshold;
    private int firefighterDelay;

    public SimConfig(float spreadSpeed, float panicSpreadChance, int crowdThreshold, int firefighterDelay) {
        this.spreadSpeed = spreadSpeed;
        this.panicSpreadChance = panicSpreadChance;
        this.crowdThreshold = crowdThreshold;
        this.firefighterDelay = firefighterDelay;
    }

    public static SimConfig defaultConfig() {
        return new SimConfig(0.3f, 0.3f, 3, 10);
    }

    public float getSpreadSpeed() {
        return spreadSpeed;
    }

    public float getPanicSpreadChance() {
        return panicSpreadChance;
    }

    public int getCrowdThreshold() {
        return crowdThreshold;
    }

    public int getFirefighterDelay() {
        return firefighterDelay;
    }

    public void setSpreadSpeed(float spreadSpeed) {
        this.spreadSpeed = spreadSpeed;
    }

    public void setPanicSpreadChance(float panicSpreadChance) {
        this.panicSpreadChance = panicSpreadChance;
    }

    public void setCrowdThreshold(int crowdThreshold) {
        this.crowdThreshold = crowdThreshold;
    }

    public void setFirefighterDelay(int firefighterDelay) {
        this.firefighterDelay = firefighterDelay;
    }

    @Override
    public String toString() {
        return "SimConfig{spreadSpeed=" + spreadSpeed + ", panicSpreadChance=" + panicSpreadChance + ", crowdThreshold=" + crowdThreshold + ", firefighterDelay=" + firefighterDelay + "}";
    }
}