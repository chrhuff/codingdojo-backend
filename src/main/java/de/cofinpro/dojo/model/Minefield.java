package de.cofinpro.dojo.model;

import java.util.*;

/**
 * Created by chuff on 28.08.2015.
 */
public class Minefield {
    int height, width;

    private Map<Position, Cell> cells;

    /**
     * Create a Minefield with a given number of mines
     * @param width
     * @param height
     * @param numberOfMines
     */
    public Minefield(int width, int height, int numberOfMines) {
        this(width, height);
        setMines(randomize(numberOfMines));
    }

    /**
     * Create a Minefield with a collection of pre-defined mine positions.
     * @param width
     * @param height
     * @param minePositions
     */
    public Minefield(int width, int height, Collection<Position> minePositions) {
        this(width, height);
        setMines(minePositions);
    }

    public Minefield(int width, int height) {
        this.width = width;
        this.height = height;

        this.cells = new HashMap<>();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Position position = new Position(j, i);
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
            Position position = new Position(x, y);
            if (!positionSet.contains(position)) {
                positionSet.add(position);
                minesSet++;
            }
        }

        return positionSet;
    }

    public final void setMines(Collection<Position> minePositions) {
        for (Position position : minePositions) {
            Cell cell = cells.get(position);
            if (cell != null && !cell.isMine()) {
                cell.setMine();
            }
        }
    }

    public Collection<Cell> getCells() {
        return cells.values();
    }

    public Cell getCell(Position position) {
        return cells.get(position);
    }
}
