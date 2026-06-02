package po.simulation.agent;

import po.simulation.board.Board;

public class AgentFactory {
    private static AgentFactory instance;
    private AgentFactory() {}

    public static synchronized AgentFactory getInstance() {
        if (instance == null) {
            instance = new AgentFactory();
        }
        return instance;
    }

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