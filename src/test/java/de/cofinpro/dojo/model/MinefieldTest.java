package de.cofinpro.dojo.model;

import org.apache.log4j.BasicConfigurator;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MinefieldTest {

    private static final Logger LOG = LoggerFactory.getLogger(MinefieldTest.class);

    @BeforeClass
    public static void initLogging() throws Exception {
        BasicConfigurator.configure();
    }

    @Test
    public void testInitialMinefieldState() {
        Minefield minefield = new Minefield(5, 5, 10);
        Collection<Cell> cells = minefield.getCells();

        Assert.assertEquals(25, cells.size());

        Cell firstCell = cells.iterator().next();

        Assert.assertEquals(0, firstCell.getX());
        Assert.assertEquals(0, firstCell.getY());
        Assert.assertEquals(false, firstCell.isUncovered());
        Assert.assertEquals(false, firstCell.isFlagged());

        int expectedMines = 10;
        int actualMines = 0;

        //assert non-negative coordinates
        for (Cell cell : cells) {
            Assert.assertTrue(cell.getX() >= 0);
            Assert.assertTrue(cell.getX() >= 0);
            if (cell.isMine()) {
                actualMines++;
            }
        }
        Assert.assertEquals(expectedMines, actualMines);

        Cell cell0 = minefield.getCell(Position.at(0,0));
        Assert.assertNotNull(cell0);
        Assert.assertEquals(0, cell0.getX());
        Assert.assertEquals(0, cell0.getY());

        Cell cell = minefield.getCell(Position.at(1, 4));
        Assert.assertNotNull(cell);
        Assert.assertEquals(1, cell.getX());
        Assert.assertEquals(4, cell.getY());

        Cell cell25 = minefield.getCell(Position.at(4,4));
        Assert.assertNotNull(cell25);
        Assert.assertEquals(4, cell25.getX());
        Assert.assertEquals(4, cell25.getY());

        Cell badCell = minefield.getCell(Position.at(5,1));
        Assert.assertNull(badCell);

        Cell badCell2 = minefield.getCell(Position.at(1,5));
        Assert.assertNull(badCell2);

        Cell badCell3 = minefield.getCell(Position.at(6,8));
        Assert.assertNull(badCell3);

        String fieldString = "\n";
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                Position pos = Position.at(j, i);
                Cell cell1 = minefield.getCell(pos);
                fieldString += "[" + (cell1.isMine() ? "M" : cell1.getNumber()) + "]";
            }
            fieldString += "\n";

        }
        LOG.info(fieldString);
    }

    @Test
    public void testCellNumbers() {
        List<Position> mines = new ArrayList<>();
        mines.add(Position.at(0, 1));
        mines.add(Position.at(1, 0));
        mines.add(Position.at(4, 4));

        Minefield minefield = new Minefield(5, 5, mines);
        Cell cell00 = minefield.getCell(Position.at(0,0));
        Assert.assertEquals(2, cell00.getNumber());

        Cell cell34 = minefield.getCell(Position.at(3,4));
        Assert.assertEquals(1, cell34.getNumber());
    }

    @AfterClass
    public static void resetLogging() {
        BasicConfigurator.resetConfiguration();
    }
}