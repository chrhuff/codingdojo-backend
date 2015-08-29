package de.cofinpro.dojo.model;

import java.util.*;

/**
 * Created by chuff on 28.08.2015.
 */
public class Minefield {
    int height, width;

    private Map<Position, Cell> cells;

    private Status status;

    public enum Status {
        CONTINUE, GAMEOVER, VICTORY
    }

    /**
     * Create a Minefield with a given number of mines
     *
     * @param width
     * @param height
     * @param numberOfMines
     */
    public Minefield(int width, int height, int numberOfMines) {
        this(width, height);
        setMines(randomize(numberOfMines));
        setStatus(Status.CONTINUE);
    }

    /**
     * Create a Minefield with a collection of pre-defined mine positions.
     *
     * @param width
     * @param height
     * @param minePositions
     */
    public Minefield(int width, int height, Collection<Position> minePositions) {
        this(width, height);
        setMines(minePositions);
        setStatus(Status.CONTINUE);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    private Minefield(int width, int height) {
        this.width = width;
        this.height = height;

        this.cells = new HashMap<>();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Position position = Position.at(j, i);
                cells.put(position, new Cell(position));
            }
        }
    }

    private Collection<Position> randomize(int numberOfMines) {
        Random random = new Random();
        int minesSet = 0;

        Set<Position> positionSet = new HashSet<>();

        while (minesSet < numberOfMines) {

            int x = random.nextInt(width);
            int y = random.nextInt(height);
            Position position = Position.at(x, y);
            if (!positionSet.contains(position)) {
                positionSet.add(position);
                minesSet++;
            }
        }

        return positionSet;
    }

    public Collection<Cell> getAdjacent(Cell cell) {
        Position position = cell.position;
        List<Cell> adjacent = new ArrayList<>();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int x1 = position.x - j;
                int y1 = position.y - i;
                Position adjacentPos = Position.at(x1, y1);
                Cell adjacentCell = getCell(adjacentPos);
                if (!adjacentPos.equals(position) && null != adjacentCell) {
                    adjacent.add(adjacentCell);
                }
            }
        }
        return adjacent;
    }

    private void setMines(Collection<Position> minePositions) {
        for (Position position : minePositions) {
            Cell cell = cells.get(position);
            if (cell != null && !cell.isMine()) {
                cell.setMine();
            }
        }
        for (Map.Entry<Position, Cell> entry : cells.entrySet()) {
            Cell cell = entry.getValue();
            int number = 0;
            Collection<Cell> adjacentCells = getAdjacent(cell);
            for (Cell adjacent : adjacentCells) {
                if (adjacent != null && adjacent.isMine()) {
                    number++;
                }
            }
            cell.setNumber(number);
        }
    }

    public Collection<Cell> getCells() {
        return cells.values();
    }

    public Cell getCell(Position position) {
        return cells.get(position);
    }

    @Override
    public String toString() {
        String fieldString = "\n";
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                Position pos = Position.at(j, i);
                Cell cell1 = getCell(pos);
                fieldString += "[" + (cell1.isMine() ? "M" : cell1.getNumber()) + "]";
            }
            fieldString += "\n";

        }
        return fieldString;
    }
}
