package po.simulation.model;

/**
 * Typy komórek na planszy symulacji.
 */
public enum CellType {

    /** Pokój lub biuro — miejsce startowe agentów, ruch możliwy. */
    ROOM,

    /** Korytarz — główna trasa ewakuacji, ruch możliwy. */
    CORRIDOR,

    /** Wyjście awaryjne — agent wchodzący jest ewakuowany i usuwany z planszy. */
    EXIT,

    /** Wyjście awaryjne — agent wchodzący jest ewakuowany i usuwany z planszy. */
    WALL
}