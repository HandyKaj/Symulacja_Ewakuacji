package po.simulation.board;


import po.simulation.agent.Agent;
import po.simulation.fire.Fire;
import po.simulation.model.CellType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Board {

    private int width;
    private int height;
    private Cell[][] grid;

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


    public boolean inBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }


    public boolean isEmpty(int x, int y) {
        if (!inBounds(x, y)) return false;
        return grid[x][y].isEmpty();
    }


    public Cell getCell(int x, int y) {
        if (!inBounds(x, y)) return null;
        return grid[x][y];
    }


    public void placeAgent(Agent agent, int x, int y) {
        if (!inBounds(x, y)) return;
        grid[x][y].setAgent(agent);
    }


    public void moveAgent(Agent agent, int newX, int newY) {
        if (!inBounds(newX, newY)) return;

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


    public List<Cell> getNeighbors(int x, int y) {
        List<Cell> neighbors = new ArrayList<>();
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

    public int distanceToExit(int startX, int startY) {
        if (!inBounds(startX, startY)) return -1;

        boolean[][] visited = new boolean[width][height];
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{startX, startY, 0});
        visited[startX][startY] = true;

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int x = current[0], y = current[1], dist = current[2];

            if (grid[x][y].getType() == CellType.EXIT) return dist;

            for (Cell neighbor : getNeighbors(x, y)) {
                int nx = neighbor.getX(), ny = neighbor.getY();
                if (!visited[nx][ny] && neighbor.isPassable()) {
                    visited[nx][ny] = true;
                    queue.add(new int[]{nx, ny, dist + 1});
                }
            }
        }
        return -1; // brak drogi do wyjścia
    }

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


    public void printBoard() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Cell cell = grid[x][y];
                if (cell.hasFire())
                    System.out.print("\u001B[31mF \u001B[0m"); // красный
                else if (!cell.isEmpty()) {
                    char c = cell.getAgent().getDisplayChar();
                    switch (c) {
                        case 'C' -> System.out.print("\u001B[32mC \u001B[0m"); // зелёный
                        case 'P' -> System.out.print("\u001B[35mP \u001B[0m"); // фиолетовый
                        case 'A' -> System.out.print("\u001B[34mA \u001B[0m"); // синий
                        case 'I' -> System.out.print("\u001B[33mI \u001B[0m"); // жёлтый
                        case 'S' -> System.out.print("\u001B[36mS \u001B[0m"); // голубой
                        default  -> System.out.print("\u001B[37m" + c + " \u001B[0m");
                    }
                }
                else if (cell.getType() == CellType.WALL)   System.out.print("\u001B[90m# \u001B[0m"); // серый
                else if (cell.getType() == CellType.EXIT)   System.out.print("\u001B[92mE \u001B[0m"); // ярко-зелёный
                else if (cell.getType() == CellType.ROOM)   System.out.print(". ");
                else System.out.print("  ");
            }
            System.out.println();
        }
    }
}