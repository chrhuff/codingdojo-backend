package de.cofinpro.dojo.model;

import javafx.geometry.Pos;
import org.apache.log4j.BasicConfigurator;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

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

        Cell cell0 = minefield.getCell(new Position(0,0));
        Assert.assertNotNull(cell0);
        Assert.assertEquals(0, cell0.getX());
        Assert.assertEquals(0, cell0.getY());

        Cell cell = minefield.getCell(new Position(1, 4));
        Assert.assertNotNull(cell);
        Assert.assertEquals(1, cell.getX());
        Assert.assertEquals(4, cell.getY());

        Cell cell25 = minefield.getCell(new Position(4,4));
        Assert.assertNotNull(cell25);
        Assert.assertEquals(4, cell25.getX());
        Assert.assertEquals(4, cell25.getY());

        Cell badCell = minefield.getCell(new Position(5,1));
        Assert.assertNull(badCell);

        Cell badCell2 = minefield.getCell(new Position(1,5));
        Assert.assertNull(badCell2);

        Cell badCell3 = minefield.getCell(new Position(6,8));
        Assert.assertNull(badCell3);

        String fieldString = "\n";
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                Position pos = new Position(j, i);
                Cell cell1 = minefield.getCell(pos);
                fieldString += (cell1.isMine() ? "m" : "_");
                fieldString += "("+cell1.getX() + ";"+cell1.getY()+")";
            }
            fieldString += "\n";

        }
        LOG.info(fieldString);
    }

    @AfterClass
    public static void resetLogging() {
        BasicConfigurator.resetConfiguration();
    }
}