package po.simulation.agent;

import po.simulation.board.Board;

/**
 * Fabryka agentów — tworzy konkretne typy agentów na podstawie nazwy typu.
 * Implementuje wzorzec Singleton (jedna instancja) i Factory (tworzenie obiektów).
 */
public class AgentFactory {
    private static AgentFactory instance;
    private AgentFactory() {}

    /**
     * Zwraca jedyną instancję fabryki (wzorzec Singleton).
     * Metoda jest synchronizowana dla bezpieczeństwa wątkowego.
     *
     * @return instancja AgentFactory
     */
    public static synchronized AgentFactory getInstance() {
        if (instance == null) {
            instance = new AgentFactory();
        }
        return instance;
    }

    /**
     * Tworzy agenta podanego typu.
     *
     * @param type typ agenta: "CALM", "PANICKING", "ALTRUIST", "INJURED", "FIREFIGHTER"
     * @param id   unikalny identyfikator
     * @param name nazwa agenta
     * @param board plansza symulacji
     * @param x    pozycja pozioma
     * @param y    pozycja pionowa
     * @return nowy agent odpowiedniego typu
     * @throws IllegalArgumentException jeśli podany typ nie istnieje
     */
    public Agent createAgent(String type, int id, String name, Board board, int x, int y) {
        switch (type.toUpperCase()) {
            case "CALM":
                return new Calm(id, name ,board, x, y);
            case "PANICKING":
                return new Panicking(id, name ,board, x, y);
            case "ALTRUIST":
                return new Altruist(id, name ,board, x, y);
            case "INJURED":
                return new Injured(id, name ,board, x, y);
            case "FIREFIGHTER":
                return new Firefighter(id, name ,board, x, y);
            default:
                throw new IllegalArgumentException("Missing agent type: " + type);
        }
    }
}