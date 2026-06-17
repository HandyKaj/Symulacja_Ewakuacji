package po.simulation;

import po.simulation.agent.AgentFactory;
import po.simulation.board.Board;
import po.simulation.board.Cell;
import po.simulation.config.SimConfig;
import po.simulation.fire.Fire;
import po.simulation.model.CellType;
import po.simulation.ui.ConfigPanel;
import po.simulation.ui.SimPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.List;

/**
 * Punkt wejścia aplikacji — uruchamia interfejs graficzny symulacji
 * w oparciu o Swing. Zarządza przełączaniem między panelem konfiguracji
 * i panelem aktywnej symulacji.
 */
public class Main {
    private final JFrame frame = new JFrame("Symulacja Ewakuacji Pożarowej");
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(cardLayout);

    private final List<Integer> panicHistory = new ArrayList<>();
    private final Map<String, int[]> typeStats = new LinkedHashMap<>();

    /**
     * Tworzy okno aplikacji i panel konfiguracji jako pierwszy widoczny ekran.
     */
    public Main() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1400, 860);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(Color.decode("#0f1117"));
        frame.addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {}
        });

        ConfigPanel configPanel = new ConfigPanel(this::startSimulation);
        mainPanel.add(configPanel, "CONFIG");
        mainPanel.setBackground(Color.decode("#0f1117"));
        frame.add(mainPanel);
        cardLayout.show(mainPanel, "CONFIG");
    }

    /**
     * Tworzy nową symulację na podstawie liczby agentów wybranych przez użytkownika
     * i przełącza widok na panel symulacji.
     *
     * @param counts tablica liczby agentów [calm, panic, altruist, injured, firefighter]
     */
    private void startSimulation(int[] counts) {
        Simulation sim = buildSimulation(counts[0], counts[1], counts[2], counts[3], counts[4]);
        panicHistory.clear();
        typeStats.clear();
        for (String t : new String[]{"Calm","Panicking","Altruist","Injured","Firefighter"})
            typeStats.put(t, new int[]{0, 0});

        SimPanel simPanel = new SimPanel(sim, panicHistory, typeStats,
                () -> cardLayout.show(mainPanel, "CONFIG"));

        // delete the previous simulation panel (if any) before adding a new one
        if (mainPanel.getComponentCount() > 1) mainPanel.remove(1);
        mainPanel.add(simPanel, "SIM");
        mainPanel.revalidate();
        cardLayout.show(mainPanel, "SIM");
    }

    /** Wyświetla okno aplikacji. */
    public void show() { frame.setVisible(true); }


    /**
     * Tworzy i konfiguruje nową symulację: planszę, ściany, wyjście, ognisko pożaru
     * oraz rozmieszcza agentów losowo na dostępnych komórkach.
     *
     * @param calm  liczba spokojnych agentów
     * @param panic liczba panikujących agentów
     * @param alt   liczba altruistów
     * @param inj   liczba rannych
     * @param ff    liczba strażaków
     * @return gotowa do uruchomienia symulacja
     */
    public static Simulation buildSimulation(int calm, int panic, int alt, int inj, int ff) {
        Random rng = new Random();
        SimConfig config = new SimConfig(0.1f, 0.3f, 3, 10);
        Simulation sim = new Simulation(120, 70, config);
        sim.initialize();
        Board board = sim.getBoard();
        int W = board.getWidth(), H = board.getHeight();

        // we clean the board to the corridors before manually setting the layout of the building
        for (int x=0;x<W;x++) for (int y=0;y<H;y++) board.getCell(x,y).setType(CellType.CORRIDOR);
        for (int x=0;x<W;x++) { board.getCell(x,0).setType(CellType.WALL); board.getCell(x,H-1).setType(CellType.WALL); }
        for (int y=0;y<H;y++) { board.getCell(0,y).setType(CellType.WALL); board.getCell(W-1,y).setType(CellType.WALL); }

        for (int x=1;x<40;x++) {
            if (x < 18 || x > 22) board.getCell(x,50).setType(CellType.WALL);
        }
        for (int y=1;y<H-1;y++) if (y<24||y>40) board.getCell(40,y).setType(CellType.WALL);
        for (int y=1;y<H-1;y++) if (y<45||y>55) board.getCell(60,y).setType(CellType.WALL);
        for (int x=60;x<W-1;x++) if (x<90||x>100) { board.getCell(x,55).setType(CellType.WALL); board.getCell(x,45).setType(CellType.WALL); }
        for (int y=1;y<45;y++) if (y<22||y>24) board.getCell(80,y).setType(CellType.WALL);

        // wide output on the right edge
        for (int y=20;y<=35;y++) board.getCell(W-1,y).setType(CellType.EXIT);

        // random position of the fire centre, with margin from the walls
        int fx=rng.nextInt(W-2)+1, fy=rng.nextInt(H-2)+1;
        board.getCell(fx,fy).setFire(new Fire(80));

        AgentFactory factory = AgentFactory.getInstance();
        int id = 1;
        id = spawn(sim, board, factory, "CALM",        calm,  id, rng);
        id = spawn(sim, board, factory, "PANICKING",   panic, id, rng);
        id = spawn(sim, board, factory, "ALTRUIST",    alt,   id, rng);
        id = spawn(sim, board, factory, "INJURED",     inj,   id, rng);
        spawn(sim, board, factory, "FIREFIGHTER", ff,    id, rng);

        return sim;
    }

    /**
     * Tworzy podaną liczbę agentów danego typu i rozmieszcza ich losowo
     * na wolnych, przejezdnych komórkach planszy.
     *
     * @param sim     symulacja do której dodajemy agentów
     * @param board   plansza na której szukamy wolnych komórek
     * @param factory fabryka tworząca agentów
     * @param type    typ agenta do utworzenia
     * @param count   liczba agentów do utworzenia
     * @param startId numer ID od którego zaczynamy numerację
     * @param rng     generator liczb losowych
     * @return numer ID następnego wolnego identyfikatora
     */
    private static int spawn(Simulation sim, Board board, AgentFactory factory,
                             String type, int count, int startId, Random rng) {
        int id = startId;
        for (int i=0;i<count;i++) {
            boolean ok = false;
            for (int a=0;a<1000&&!ok;a++) {
                int rx=rng.nextInt(board.getWidth()), ry=rng.nextInt(board.getHeight());
                Cell c = board.getCell(rx,ry);
                if (c!=null && c.isEmpty() && !c.hasFire()
                        && c.getType()!=CellType.WALL && c.getType()!=CellType.EXIT) {
                    sim.addAgent(factory.createAgent(type, id++, type, board, rx, ry));
                    ok=true;
                }
            }
        }
        return id;
    }

    /** Punkt startowy aplikacji — uruchamia GUI w wątku Swing (Event Dispatch Thread). */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().show());
    }
}