package po.simulation.metrics;

/**
 * Zbiera i przechowuje metryki symulacji ewakuacji.
 * Śledzi liczbę ewakuowanych i zabitych agentów oraz średni czas ewakuacji.
 */
public class SimMetrics {
    private int evacuatedCount;
    private int deadCount;
    private float avgEvacTime;
    private int totalEvacTicks;

    /**
     * Tworzy nowy obiekt metryk z zerowymi wartościami początkowymi.
     */
    public SimMetrics() {
        this.evacuatedCount = 0;
        this.deadCount = 0;
        this.avgEvacTime = 0.0f;
        this.totalEvacTicks = 0;
    }

    /**
     * Rejestruje ewakuację agenta i aktualizuje średni czas ewakuacji.
     *
     * @param tick numer ticku w którym agent się ewakuował
     */
    public void registerEvacuation(int tick) {
        evacuatedCount++;
        totalEvacTicks += tick;
        avgEvacTime = (float) totalEvacTicks / evacuatedCount;
    }

    /** Rejestruje śmierć agenta. */
    public void registerDeath() {
        deadCount++;
    }

    /** @return łączna liczba ewakuowanych agentów */
    public int getEvacuatedCount() { return evacuatedCount; }

    /** @return łączna liczba zabitych agentów */
    public int getDeadCount()      { return deadCount; }

    /** @return średni czas ewakuacji w tickach */
    public float getAvgEvacTime()  { return avgEvacTime; }

    @Override
    public String toString() {
        return "SimMetrics{evacuated=" + evacuatedCount +
                ", dead=" + deadCount +
                ", avgEvacTime=" + avgEvacTime + "}";
    }
}