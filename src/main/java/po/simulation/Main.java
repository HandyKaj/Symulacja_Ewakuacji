package po.simulation;

import po.simulation.board.Board;
import po.simulation.board.Cell;
import po.simulation.agent.AgentFactory;
import po.simulation.agent.Agent;
import po.simulation.engine.Engine;
import po.simulation.model.CellType;
import po.simulation.fire.Fire;

import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        System.out.println("====== FIRE EVACUATION SIMULATION CONFIGURATOR ======");

        // Setting up agents from console
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

        //---------------------------------------------============------------------------------------
        //board
        int width = 50;
        int height = 30;
        Board board = new Board(width, height);

        for (int y = 0; y < height; y++) {
            board.getCell(0, y).setType(CellType.WALL);
            board.getCell(width - 1, y).setType(CellType.WALL);
        }
        for (int x = 0; x < width; x++) {
            board.getCell(x, 0).setType(CellType.WALL);
            board.getCell(x, height - 1).setType(CellType.WALL);
        }
        //---------------------------------------------================-------------------------

        board.getCell(width-2, 30/2).setType(CellType.EXIT);
        board.getCell(width-2, 30/2+1).setType(CellType.EXIT);

        //set fire
        board.getCell(2, 2).setFire(new Fire(40));

        //start THE FACTORY
        AgentFactory factory = AgentFactory.getInstance();
        int currentId = 1;

        currentId = spawnAgentsRandomly(board, factory, "CALM", numCalm, currentId, random);
        currentId = spawnAgentsRandomly(board, factory, "PANICKING", numPanic, currentId, random);
        currentId = spawnAgentsRandomly(board, factory, "ALTRUIST", numAltruist, currentId, random);
        currentId = spawnAgentsRandomly(board, factory, "INJURED", numInjured, currentId, random);
        currentId = spawnAgentsRandomly(board, factory, "FIREFIGHTER", numFirefighters, currentId, random);
        // 6. Initialize Sequential Simulation Engine
        Engine engine = new Engine(board);

        // 7. Start Simulation Run Loop
        System.out.println("\n====== SIMULATION STARTED ======");
        board.printBoard();

        int totalTicks = 60;
        for (int tick = 1; tick <= totalTicks; tick++) {
            System.out.println("\n--- TICK " + tick + " ---");

            // Execute sequential single-threaded updates
            engine.runSingleTick();

            // Print visual grid representation
            board.printBoard();

            // Check termination conditions
            if (board.getAgents().isEmpty()) {
                System.out.println("\n[Simulation Finished]: All agents evacuated or died.");
                break;
            }

            try {
                Thread.sleep(500); // 500ms delay per frame
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Simulation interrupted.");
                break;
            }
        }

        System.out.println("\n====== SIMULATION ENDED ======");
        scanner.close();
    }




    private static int spawnAgentsRandomly(Board board, AgentFactory factory, String type,
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

                // rules for agents
                if (targetCell != null && targetCell.isEmpty() && !targetCell.hasFire()
                        && targetCell.getType() != CellType.WALL
                        && targetCell.getType() != CellType.EXIT) {

                    // creating with factory agents
                    Agent agent = factory.createAgent(type, id, name,board, rx, ry);
                    board.placeAgent(agent, rx, ry);

                    id++;
                    spawned = true;
                }
            }
            if (attempts >= maxAttempts) {
                System.out.println("Warning: Grid is full! Could not spawn all requested " + type );
                break;
            }
        }
        return id;
    }
}