package de.cofinpro.dojo.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by chuff on 28.08.2015.
 */
public class Minefield {
    int height, width;

    private Map<Position, Cell> cells;

    public Minefield(int width, int height, int numberOfMines) {
        this.width = width;
        this.height = height;

        this.cells = new HashMap<>();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Position position = new Position(j, i);
                cells.put(position, new Cell(position));
            }
        }
        randomize(numberOfMines);
    }

    private void randomize(int numberOfMines) {
        Random random = new Random();
        int minesSet = 0;
        while (minesSet < numberOfMines) {

            int x = random.nextInt(width);
            int y = random.nextInt(height);
            Position position = new Position(x, y);
            Cell cell = cells.get(position);
            if (cell != null && !cell.isMine()) {
                cell.setMine();
                minesSet++;
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
