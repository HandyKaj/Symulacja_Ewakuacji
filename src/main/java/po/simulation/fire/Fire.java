package po.simulation.fire;

/**
 * Reprezentuje ogień na komórce planszy.
 * Intensywność rośnie co tick. Gdy przekracza próg — komórka staje się nieprzejezdna.
 */
public class Fire {
    private int intensity;// 0–100
    private float spreadSpeed;
    private int threshold;

    /**
     * Tworzy nowy ogień z podaną intensywnością początkową.
     * Domyślna prędkość rozprzestrzeniania to 0.1, próg blokowania to 50.
     *
     * @param initialIntensity początkowa intensywność ognia (0–100)
     */
    public Fire(int initialIntensity) {
        this.intensity = initialIntensity;
        this.spreadSpeed = 0.1f;
        this.threshold = 50;
    }

    /** Zwiększa intensywność ognia o jeden tick. Nie przekracza 100. */
    public void tick() {
        intensity = Math.min(100, intensity + (int)(spreadSpeed * 10));
    }

    /** Alias dla {@link #tick()} — używany przy rozprzestrzenianiu ognia. */
    public void spread() {
        tick();
    }

    /**
     * Zmniejsza intensywność ognia — używane przez strażaka.
     * Nie spada poniżej 0.
     */
    public void reduce(int amount) {
        intensity = Math.max(0, intensity - amount);
    }

    /** @return true jeśli ogień blokuje przejście agentów */
    public boolean isBlocking() {
        return intensity > threshold;
    }

    public int getIntensity() {
        return intensity;
    }

    public float getSpreadSpeed() {
        return spreadSpeed;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setIntensity(int intensity) {
        this.intensity = Math.max(0, Math.min(100, intensity));
    }

    public void setSpreadSpeed(float speed) {
        this.spreadSpeed = speed;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public String toString() {
        return "Fire{intensity=" + intensity + ", blocking=" + isBlocking() + "}";
    }
}