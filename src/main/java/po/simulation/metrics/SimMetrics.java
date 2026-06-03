package po.simulation.metrics;

public class SimMetrics {

    private int evacuatedCount;
    private int deadCount;
    private float avgEvacTime;


    private int totalEvacTicks;

    public SimMetrics() {
        this.evacuatedCount = 0;
        this.deadCount = 0;
        this.avgEvacTime = 0.0f;
        this.totalEvacTicks = 0;
    }


    public void registerEvacuation(int tick) {
        evacuatedCount++;
        totalEvacTicks += tick;
        avgEvacTime = (float) totalEvacTicks / evacuatedCount;
    }


    public void registerDeath() {
        deadCount++;
    }

    public void export() {
        System.out.println("=== Metryki symulacji ===");
        System.out.println("Ewakuowanych: " + evacuatedCount);
        System.out.println("Zabitych:     " + deadCount);
        System.out.printf ("Średni czas ewakuacji: %.1f ticków%n", avgEvacTime);
    }

    public int getEvacuatedCount() { return evacuatedCount; }
    public int getDeadCount()      { return deadCount; }
    public float getAvgEvacTime()  { return avgEvacTime; }

    @Override
    public String toString() {
        return "SimMetrics{evacuated=" + evacuatedCount +
                ", dead=" + deadCount +
                ", avgEvacTime=" + avgEvacTime + "}";
    }
}