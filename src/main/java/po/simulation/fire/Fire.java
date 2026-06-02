package po.simulation.fire;


public class Fire {

    private int intensity;      // 0–100
    private float spreadSpeed;
    private int threshold;      // próg blokowania przejścia

    public Fire(int initialIntensity) {
        this.intensity = initialIntensity;
        this.spreadSpeed = 0.3f;
        this.threshold = 50;
    }


    public void tick() {
        intensity = Math.min(100, intensity + (int)(spreadSpeed * 10));
    }


    public void spread() {
        tick();
    }


    public void reduce(int amount) {
        intensity = Math.max(0, intensity - amount);
    }


    public boolean isBlocking() {
        return intensity > threshold;
    }

    public int getIntensity()   { return intensity; }
    public float getSpreadSpeed() { return spreadSpeed; }
    public int getThreshold()   { return threshold; }

    public void setIntensity(int intensity)     { this.intensity = Math.max(0, Math.min(100, intensity)); }
    public void setSpreadSpeed(float speed)     { this.spreadSpeed = speed; }
    public void setThreshold(int threshold)     { this.threshold = threshold; }

    @Override
    public String toString() {
        return "Fire{intensity=" + intensity + ", blocking=" + isBlocking() + "}";
    }
}
