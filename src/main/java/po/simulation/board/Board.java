package po.simulation.board;


import po.simulation.agent.Agent;
import po.simulation.fire.Fire;
import po.simulation.model.CellType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Reprezentuje dwuwymiarową siatkę budynku.
 * Zarządza komórkami, agentami i rozprzestrzenianiem ognia.
 */
public class Board {

    private int width;
    private int height;
    private Cell[][] grid;

    /**
     * Tworzy nową planszę o podanych wymiarach.
     * Wszystkie komórki są domyślnie inicjalizowane jako korytarze.
     *
     * @param width  szerokość planszy (liczba kolumn)
     * @param height wysokość planszy (liczba wierszy)
     */
    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new Cell[width][height];
        initGrid();
    }


    private void initGrid() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[x][y] = new Cell(x, y, CellType.CORRIDOR);
            }
        }
    }

    /**
     * Sprawdza czy podane współrzędne mieszczą się w granicach planszy.
     *
     * @param x współrzędna pozioma
     * @param y współrzędna pionowa
     * @return true jeśli współrzędne są w granicach planszy
     */
    public boolean inBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    /**
     * Sprawdza czy komórka pod podanymi współrzędnymi jest pusta.
     *
     * @param x współrzędna pozioma
     * @param y współrzędna pionowa
     * @return true jeśli komórka nie ma agenta; false jeśli poza planszą
     */
    public boolean isEmpty(int x, int y) {
        if (!inBounds(x, y)) return false;
        return grid[x][y].isEmpty();
    }

    /**
     * Zwraca komórkę pod podanymi współrzędnymi.
     *
     * @param x współrzędna pozioma
     * @param y współrzędna pionowa
     * @return komórka lub null jeśli współrzędne poza planszą
     */
    public Cell getCell(int x, int y) {
        if (!inBounds(x, y)) return null;
        return grid[x][y];
    }

    /**
     * Umieszcza agenta na komórce o podanych współrzędnych.
     *
     * @param agent agent do umieszczenia
     * @param x     współrzędna pozioma
     * @param y     współrzędna pionowa
     */
    public void placeAgent(Agent agent, int x, int y) {
        if (!inBounds(x, y)) return;
        grid[x][y].setAgent(agent);
    }

    /**
     * Przesuwa agenta na nową pozycję — usuwa go z aktualnej komórki
     * i umieszcza w docelowej.
     *
     * @param agent agent do przesunięcia
     * @param newX  nowa współrzędna pozioma
     * @param newY  nowa współrzędna pionowa
     */
    public void moveAgent(Agent agent, int newX, int newY) {
        if (!inBounds(newX, newY)) return;

        // looking for the old agent position by searching the entire board
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (grid[x][y].getAgent() == agent) {
                    grid[x][y].setAgent(null);
                    break;
                }
            }
        }
        grid[newX][newY].setAgent(agent);
    }

    /**
     * Usuwa agenta z planszy — przeszukuje wszystkie komórki i czyści tę,
     * na której agent się znajduje.
     *
     * @param agent agent do usunięcia
     */
    public void removeAgent(Agent agent) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (grid[x][y].getAgent() == agent) {
                    grid[x][y].setAgent(null);
                    return;
                }
            }
        }
    }

    /**
     * Zwraca listę sąsiednich komórek (góra, dół, lewo, prawo).
     * Nie uwzględnia przekątnych ani komórek poza planszą.
     *
     * @param x współrzędna pozioma komórki centralnej
     * @param y współrzędna pionowa komórki centralnej
     * @return lista sąsiednich komórek (maksymalnie 4)
     */
    public List<Cell> getNeighbors(int x, int y) {
        List<Cell> neighbors = new ArrayList<>();
        // four directions: up, down, left, right
        int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};

        for (int[] dir : directions) {
            int nx = x + dir[0];
            int ny = y + dir[1];
            if (inBounds(nx, ny)) {
                neighbors.add(grid[nx][ny]);
            }
        }
        return neighbors;
    }

    /**
     * Rozprzestrzenia ogień co tick — zwiększa intensywność i przenosi na sąsiednie komórki.
     * Ogień nie przechodzi przez ściany.
     */
    public void spreadFire() {
        List<Cell> firecells = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (grid[x][y].hasFire()) {
                    firecells.add(grid[x][y]);
                }
            }
        }

        for (Cell cell : firecells) {
            Fire fire = cell.getFire();
            fire.tick();

            List<Cell> neighbors = getNeighbors(cell.getX(), cell.getY());
            for (Cell neighbor : neighbors) {
                if (neighbor.getType() != CellType.WALL && !neighbor.hasFire()) {
                    if (Math.random() < fire.getSpreadSpeed()) {
                        neighbor.setFire(new Fire(1));
                    }
                }
            }
        }
    }

    /**
     * Oblicza najkrótszą odległość do najbliższego wyjścia algorytmem BFS.
     * Uwzględnia tylko komórki przejezdne.
     *
     * @param startX współrzędna pozioma pozycji startowej
     * @param startY współrzędna pionowa pozycji startowej
     * @return liczba kroków do wyjścia lub -1 jeśli brak dostępnej drogi
     */
    public int distanceToExit(int startX, int startY) {
        if (!inBounds(startX, startY)) return -1;

        boolean[][] visited = new boolean[width][height];
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{startX, startY, 0});
        visited[startX][startY] = true;

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int x = current[0], y = current[1], dist = current[2];

            if (grid[x][y].getType() == CellType.EXIT) return dist;// output found

            for (Cell neighbor : getNeighbors(x, y)) {
                int nx = neighbor.getX(), ny = neighbor.getY();
                if (!visited[nx][ny] && neighbor.isPassable()) {
                    visited[nx][ny] = true;
                    queue.add(new int[]{nx, ny, dist + 1});
                }
            }
        }
        return -1;
    }

    /**
     * Zwraca listę wszystkich agentów aktualnie znajdujących się na planszy.
     *
     * @return lista agentów na planszy
     */
    public List<Agent> getAgents() {
        List<Agent> agents = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (!grid[x][y].isEmpty()) {
                    agents.add(grid[x][y].getAgent());
                }
            }
        }
        return agents;
    }

    public int getWidth()  { return width; }
    public int getHeight() { return height; }

}