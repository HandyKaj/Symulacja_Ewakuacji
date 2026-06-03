package po.simulation;

import po.simulation.board.Board;
import po.simulation.board.Cell;
import po.simulation.agent.AgentFactory;
import po.simulation.agent.Agent;
import po.simulation.config.SimConfig;
import po.simulation.model.CellType;
import po.simulation.fire.Fire;

import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        System.out.println("====== FIRE EVACUATION SIMULATION CONFIGURATOR ======");


        System.out.print("Enter number of CALM agents: ");
        int numCalm = scanner.nextInt();

        System.out.print("Enter number of PANICKING agents: ");
        int numPanic = scanner.nextInt();

        System.out.print("Enter number of ALTRUIST agents: ");
        int numAltruist = scanner.nextInt();

        System.out.print("Enter number of INJURED agents: ");
        int numInjured = scanner.nextInt();

        System.out.print("Enter number of FIREFIGHTERS: ");
        int numFirefighters = scanner.nextInt();

        SimConfig config = new SimConfig(0.3f, 0.3f, 3, 10);


        Simulation sim = new Simulation(50, 30, config);
        sim.initialize();
        Board board = sim.getBoard();


        for (int y = 1; y < 20; y++) {
            board.getCell(25, y).setType(CellType.WALL);
        }

        board.getCell(25, 10).setType(CellType.CORRIDOR);


        for (int y = 1; y < 15; y++) {
            board.getCell(35, y).setType(CellType.WALL);
        }

        board.getCell(35, 7).setType(CellType.CORRIDOR);

        board.getCell(48, 15).setType(CellType.EXIT);
        board.getCell(48, 16).setType(CellType.EXIT);


        board.getCell(2, 2).setFire(new Fire(80));
//        board.getCell(25, 15).setFire(new Fire(60));
//        board.getCell(10, 10).setFire(new Fire(50));


        AgentFactory factory = AgentFactory.getInstance();
        int currentId = 1;

        currentId = spawnAgentsRandomly(sim,board, factory, "CALM", numCalm, currentId, random);
        currentId = spawnAgentsRandomly(sim,board, factory, "PANICKING", numPanic, currentId, random);
        currentId = spawnAgentsRandomly(sim,board, factory, "ALTRUIST", numAltruist, currentId, random);
        currentId = spawnAgentsRandomly(sim,board, factory, "INJURED", numInjured, currentId, random);
        currentId = spawnAgentsRandomly(sim,board, factory, "FIREFIGHTER", numFirefighters, currentId, random);




        System.out.println("\n====== SIMULATION STARTED ======");
        board.printBoard();

        sim.run(60);

        System.out.println("\n====== SIMULATION ENDED ======");
        sim.exportMetrics();
        scanner.close();
    }


    private static int spawnAgentsRandomly(Simulation sim,Board board, AgentFactory factory, String type,
                                           int count, int startId, Random random) {
        int id = startId;
        int maxAttempts = 1000;
        String name = type;
        for (int i = 0; i < count; i++) {
            boolean spawned = false;
            int attempts = 0;

            while (!spawned && attempts < maxAttempts) {
                attempts++;
                int rx = random.nextInt(board.getWidth());
                int ry = random.nextInt(board.getHeight());

                Cell targetCell = board.getCell(rx, ry);


                if (targetCell != null && targetCell.isEmpty() && !targetCell.hasFire()
                        && targetCell.getType() != CellType.WALL
                        && targetCell.getType() != CellType.EXIT) {


                    Agent agent = factory.createAgent(type, id, name, board, rx, ry);
                    sim.addAgent(agent);
                    id++;
                    spawned = true;
                }
            }
            if (attempts >= maxAttempts) {
                System.out.println("Warning: Grid is full! Could not spawn all requested " + type);
                break;
            }
        }
        return id;
    }
}
