package po.simulation.model;

/**
 * Możliwe stany agenta w trakcie symulacji ewakuacji.
 */
public enum AgentState {

    /** Agent przebywa w budynku i jest zdolny do ruchu. */
    IN_BUILDING,

    /** Agent dotarł do wyjścia i opuścił budynek. */
    EVACUATED,

    /** Agent został ranny przez ogień lub efekt tłumu. Porusza się z prędkością 0.5. */
    INJURED,

    /** Agent zginął w pożarze. Zostaje usunięty z planszy. */
    DEAD,

    /** Agent jest prowadzony przez altruistę do wyjścia. */
    CARRIED
}