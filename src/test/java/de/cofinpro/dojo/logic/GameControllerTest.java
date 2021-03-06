package de.cofinpro.dojo.logic;

import de.cofinpro.dojo.model.*;
import org.apache.log4j.BasicConfigurator;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GameControllerTest {

    private static final Logger LOG = LoggerFactory.getLogger(GameControllerTest.class);

    private GameController controller = new GameController();

    @BeforeClass
    public static void initLogging() {
        BasicConfigurator.configure();
    }

    @Before
    public void setupController() {
        controller.setupMinefields();
    }

    @Test
    public void testRandomizedStart() throws InvalidGameSetupException, InvalidActionException {
        Integer sessionId = controller.startGame(5, 5, 0.25f);

        Action action = new Action();
        action.setPosition(Position.at(0, 0));
        action.setType(Action.Type.UNCOVER);
        ActionResult actionResult = controller.submitAction(sessionId, action);
        Assert.assertNotNull(actionResult);
    }

    @Test(expected = InvalidGameSetupException.class)
    public void testBadMineRatioRandomizedStart() throws InvalidGameSetupException, InvalidActionException {
        Integer sessionId = controller.startGame(5, 5, -0.25f);

        Action action = new Action();
        action.setPosition(Position.at(0, 0));
        action.setType(Action.Type.UNCOVER);
        ActionResult actionResult = controller.submitAction(sessionId, action);
        Assert.assertNotNull(actionResult);
    }

    @Test(expected = InvalidGameSetupException.class)
    public void testBadFieldSizeStart() throws InvalidGameSetupException, InvalidActionException {
        List<Position> mines = new ArrayList<>();
        mines.add(Position.at(1, 2));
        mines.add(Position.at(3, 2));
        mines.add(Position.at(4, 4));

        Integer sessionId = controller.startGame(-1, 15, mines);

        Action action = new Action();
        action.setPosition(Position.at(0, 0));
        action.setType(Action.Type.UNCOVER);
        ActionResult actionResult = controller.submitAction(sessionId, action);
        Assert.assertNotNull(actionResult);
    }

    @Test
    public void testUncoverContinue() throws InvalidActionException, InvalidGameSetupException {

        List<Position> mines = new ArrayList<>();
        mines.add(Position.at(1, 2));
        mines.add(Position.at(3, 2));
        mines.add(Position.at(4, 4));

        Integer sessionId = controller.startGame(5, 5, mines);

        Action action = new Action();
        action.setPosition(Position.at(0, 0));
        action.setType(Action.Type.UNCOVER);
        ActionResult actionResult = controller.submitAction(sessionId, action);
        Assert.assertEquals(Minefield.Status.CONTINUE, actionResult.getStatus());

        Assert.assertEquals(25, actionResult.getVisibleCells().size());
    }

    @Test(expected = InvalidActionException.class)
    public void testUncoverInvalidAction() throws InvalidActionException, InvalidGameSetupException {

        List<Position> mines = new ArrayList<>();
        mines.add(Position.at(1, 2));
        mines.add(Position.at(3, 2));
        mines.add(Position.at(4, 4));

        Integer sessionId = controller.startGame(5, 5, mines);

        Action action = new Action();
        action.setPosition(Position.at(-1, 0));
        action.setType(Action.Type.UNCOVER);
        ActionResult actionResult = controller.submitAction(sessionId, action);

        Assert.fail();
        Assert.assertNotNull(actionResult);
    }

    @Test
    public void testUncoverGameOver() throws InvalidActionException, InvalidGameSetupException {

        List<Position> mines = new ArrayList<>();
        mines.add(Position.at(1, 2));
        mines.add(Position.at(3, 2));
        mines.add(Position.at(4, 4));

        Integer sessionId = controller.startGame(5, 5, mines);

        Action action = new Action();
        action.setPosition(Position.at(1, 2));
        action.setType(Action.Type.UNCOVER);
        ActionResult actionResult = controller.submitAction(sessionId, action);
        Assert.assertEquals(Minefield.Status.GAMEOVER, actionResult.getStatus());
    }

    @Test(expected = InvalidActionException.class)
    public void testBadSessionId() throws InvalidActionException, InvalidGameSetupException {

        List<Position> mines = new ArrayList<>();
        mines.add(Position.at(1, 2));
        mines.add(Position.at(3, 2));
        mines.add(Position.at(4, 4));

        Integer sessionId = controller.startGame(5, 5, mines);

        LOG.info("SessionId was " + sessionId);

        Action action = new Action();
        action.setPosition(Position.at(1, 2));
        action.setType(Action.Type.UNCOVER);
        ActionResult actionResult = controller.submitAction(123213, action);
        Assert.assertEquals(Minefield.Status.GAMEOVER, actionResult.getStatus());
    }

    @Test
    public void testUncoverZero() throws InvalidGameSetupException, InvalidActionException {
        List<Position> mines = new ArrayList<>();
        mines.add(Position.at(1, 2));
        mines.add(Position.at(3, 2));
        mines.add(Position.at(4, 4));

        Integer sessionId = controller.startGame(5, 5, mines);
        Minefield minefield = controller.getMinefield(sessionId);

        Action action = new Action();
        action.setPosition(Position.at(4, 0));
        action.setType(Action.Type.UNCOVER);
        ActionResult actionResult = controller.submitAction(sessionId, action);

        LOG.info(actionResult.toString());
        LOG.info(minefield.toString());

        Assert.assertEquals(Minefield.Status.CONTINUE, actionResult.getStatus());

        Assert.assertEquals(25, actionResult.getVisibleCells().size());

        VisibleCell[][] cells = new VisibleCell[5][5];
        for (VisibleCell cell : actionResult.getVisibleCells()) {
            cells[cell.getX()][cell.getY()] = cell;
        }

        Assert.assertEquals(0, cells[4][0].getNumber());
        Assert.assertEquals(0, cells[3][0].getNumber());
        Assert.assertEquals(1, cells[4][1].getNumber());
    }

    @Test
    public void testSetFlag() throws InvalidGameSetupException, InvalidActionException {
        List<Position> mines = new ArrayList<>();
        mines.add(Position.at(1, 2));
        mines.add(Position.at(3, 2));
        mines.add(Position.at(4, 4));

        Integer sessionId = controller.startGame(5, 5, mines);
        Minefield minefield = controller.getMinefield(sessionId);

        Action action = new Action();
        action.setPosition(Position.at(1, 2));
        action.setType(Action.Type.FLAG);
        ActionResult actionResult = controller.submitAction(sessionId, action);

        LOG.info(actionResult.toString());
        LOG.info(minefield.toString());

        Assert.assertEquals(Minefield.Status.CONTINUE, actionResult.getStatus());

        Assert.assertEquals(25, actionResult.getVisibleCells().size());

        VisibleCell[][] cells = new VisibleCell[5][5];
        for (VisibleCell cell : actionResult.getVisibleCells()) {
            cells[cell.getX()][cell.getY()] = cell;
        }

        Assert.assertEquals(true, cells[1][2].isFlagged());
    }

    @Test(expected = InvalidActionException.class)
    public void testInvalidFlag() throws InvalidGameSetupException, InvalidActionException {
        List<Position> mines = new ArrayList<>();
        mines.add(Position.at(1, 2));
        mines.add(Position.at(3, 2));
        mines.add(Position.at(4, 4));

        Integer sessionId = controller.startGame(5, 5, mines);

        Action action = new Action();
        action.setPosition(Position.at(-1, 2));
        action.setType(Action.Type.FLAG);
        ActionResult actionResult = controller.submitAction(sessionId, action);
        Assert.assertNotNull(actionResult);

        Assert.fail();
    }

    @Test
    public void testToggleFlag() throws InvalidGameSetupException, InvalidActionException {
        List<Position> mines = new ArrayList<>();
        mines.add(Position.at(1, 2));
        mines.add(Position.at(3, 2));
        mines.add(Position.at(4, 4));

        Integer sessionId = controller.startGame(5, 5, mines);
        Minefield minefield = controller.getMinefield(sessionId);

        Action action = new Action();
        action.setPosition(Position.at(1, 2));
        action.setType(Action.Type.FLAG);
        ActionResult actionResult = controller.submitAction(sessionId, action);

        Assert.assertEquals(Minefield.Status.CONTINUE, actionResult.getStatus());
        Assert.assertEquals(25, actionResult.getVisibleCells().size());

        VisibleCell[][] cells = new VisibleCell[5][5];
        for (VisibleCell cell : actionResult.getVisibleCells()) {
            cells[cell.getX()][cell.getY()] = cell;
        }

        Assert.assertEquals(true, cells[1][2].isFlagged());

        LOG.info(actionResult.toString());

        ActionResult actionResult2 = controller.submitAction(sessionId, action);

        Assert.assertEquals(Minefield.Status.CONTINUE, actionResult2.getStatus());
        Assert.assertEquals(25, actionResult2.getVisibleCells().size());

        VisibleCell[][] cells2 = new VisibleCell[5][5];
        for (VisibleCell cell : actionResult.getVisibleCells()) {
            cells2[cell.getX()][cell.getY()] = cell;
        }

        Assert.assertEquals(false, cells[1][2].isFlagged());


        LOG.info(actionResult2.toString());
        LOG.info(minefield.toString());


    }

    @Test
    public void testNOOPAction()throws InvalidGameSetupException, InvalidActionException {
        List<Position> mines = new ArrayList<>();
        mines.add(Position.at(1, 2));
        mines.add(Position.at(3, 2));
        mines.add(Position.at(4, 4));

        Integer sessionId = controller.startGame(5, 5, mines);
        Minefield minefield = controller.getMinefield(sessionId);

        Action action = new Action();
        action.setType(Action.Type.NOOP);
        ActionResult actionResult = controller.submitAction(sessionId, action);

        Assert.assertEquals( Minefield.Status.CONTINUE, actionResult.getStatus() );
        Assert.assertEquals(25, actionResult.getVisibleCells().size());
        Assert.assertEquals(minefield, controller.getMinefield(sessionId));

    }

    @Test
    public void testSolveAction() throws InvalidActionException, InvalidGameSetupException {
        List<Position> mines = new ArrayList<>();
        mines.add(Position.at(0, 0));
        mines.add(Position.at(3, 2));
        mines.add(Position.at(4, 4));

        Integer sessionId = controller.startGame(5, 5, mines);
        Minefield minefield = controller.getMinefield(sessionId);

        Action uncoverAction = new Action();
        uncoverAction.setPosition(Position.at(1, 1));
        uncoverAction.setType(Action.Type.UNCOVER);
        ActionResult unconverResult = controller.submitAction(sessionId, uncoverAction);

        Action flagAction = new Action();
        flagAction.setPosition(Position.at(0,0));
        flagAction.setType(Action.Type.FLAG);
        ActionResult flagResult = controller.submitAction(sessionId, flagAction);

        LOG.info(flagResult.toString());

        Action action = new Action();
        action.setPosition(Position.at(1, 1));
        action.setType(Action.Type.SOLVE);
        ActionResult actionResult = controller.submitAction(sessionId, action);

        Assert.assertEquals(Minefield.Status.CONTINUE, actionResult.getStatus());
        Assert.assertEquals(25, actionResult.getVisibleCells().size());

        VisibleCell[][] cells = new VisibleCell[5][5];
        for (VisibleCell cell : actionResult.getVisibleCells()) {
            cells[cell.getX()][cell.getY()] = cell;
        }

        LOG.info(actionResult.toString());
        LOG.info(minefield.toString());

        Assert.assertEquals(true, cells[0][0].isFlagged());

        Assert.assertEquals(1, cells[1][0].getNumber());
        Assert.assertEquals(1, cells[1][1].getNumber());
        Assert.assertEquals(0, cells[1][2].getNumber());

        Assert.assertEquals(1, cells[0][1].getNumber());
        Assert.assertEquals(0, cells[0][2].getNumber());

        Assert.assertEquals(0, cells[2][0].getNumber());
        Assert.assertEquals(1, cells[2][1].getNumber());
        Assert.assertEquals(1, cells[2][2].getNumber());
    }

    @Test
    public void testSolveGameover() throws InvalidActionException, InvalidGameSetupException {
        List<Position> mines = new ArrayList<>();
        mines.add(Position.at(0, 0));
        mines.add(Position.at(3, 2));
        mines.add(Position.at(4, 4));

        Integer sessionId = controller.startGame(5, 5, mines);
        Minefield minefield = controller.getMinefield(sessionId);

        Action uncoverAction = new Action();
        uncoverAction.setPosition(Position.at(1, 1));
        uncoverAction.setType(Action.Type.UNCOVER);
        ActionResult unconverResult = controller.submitAction(sessionId, uncoverAction);

        Action flagAction = new Action();
        flagAction.setPosition(Position.at(1, 0));
        flagAction.setType(Action.Type.FLAG);
        ActionResult flagResult = controller.submitAction(sessionId, flagAction);

        LOG.info(flagResult.toString());

        Action action = new Action();
        action.setPosition(Position.at(1, 1));
        action.setType(Action.Type.SOLVE);
        ActionResult actionResult = controller.submitAction(sessionId, action);

        Assert.assertEquals(Minefield.Status.GAMEOVER, actionResult.getStatus());
        Assert.assertEquals(25, actionResult.getVisibleCells().size());

        VisibleCell[][] cells = new VisibleCell[5][5];
        for (VisibleCell cell : actionResult.getVisibleCells()) {
            cells[cell.getX()][cell.getY()] = cell;
        }

        LOG.info(actionResult.toString());
        LOG.info(minefield.toString());

        Assert.assertEquals(Boolean.TRUE, cells[0][0].isMine());

        Assert.assertEquals(-1, cells[1][0].getNumber());
        Assert.assertEquals(1, cells[1][1].getNumber());
        Assert.assertEquals(0, cells[1][2].getNumber());

        Assert.assertEquals(1, cells[0][1].getNumber());
        Assert.assertEquals(0, cells[0][2].getNumber());

        Assert.assertEquals(0, cells[2][0].getNumber());
        Assert.assertEquals(1, cells[2][1].getNumber());
        Assert.assertEquals(1, cells[2][2].getNumber());
    }

    @Test
    public void testVictory() throws InvalidActionException, InvalidGameSetupException {
        List<Position> mines = new ArrayList<>();
        mines.add(Position.at(0, 0));
        mines.add(Position.at(1, 1));
        mines.add(Position.at(2, 2));

        Integer sessionId = controller.startGame(3, 3, mines);
        Minefield minefield = controller.getMinefield(sessionId);

        Action uncover = new Action();
        uncover.setType(Action.Type.UNCOVER);

        uncover.setPosition(Position.at(0, 1));
        controller.submitAction(sessionId, uncover);
        Assert.assertEquals(minefield.getStatus(), Minefield.Status.CONTINUE);
        uncover.setPosition(Position.at(0, 2));
        controller.submitAction(sessionId, uncover);
        Assert.assertEquals(minefield.getStatus(), Minefield.Status.CONTINUE);
        uncover.setPosition(Position.at(1, 0));
        controller.submitAction(sessionId, uncover);
        Assert.assertEquals(minefield.getStatus(), Minefield.Status.CONTINUE);
        uncover.setPosition(Position.at(1, 2));
        controller.submitAction(sessionId, uncover);
        Assert.assertEquals(minefield.getStatus(), Minefield.Status.CONTINUE);
        uncover.setPosition(Position.at(2, 0));
        controller.submitAction(sessionId, uncover);
        Assert.assertEquals(minefield.getStatus(), Minefield.Status.CONTINUE);
        uncover.setPosition(Position.at(2, 1));
        controller.submitAction(sessionId, uncover);
        Assert.assertEquals(minefield.getStatus(), Minefield.Status.VICTORY);

    }

    @Test
    public void testVictoryWithSolve() throws InvalidActionException, InvalidGameSetupException {
        List<Position> mines = new ArrayList<>();
        mines.add(Position.at(0, 0));
        mines.add(Position.at(1, 1));
        mines.add(Position.at(2, 2));

        Integer sessionId = controller.startGame(3, 3, mines);
        Minefield minefield = controller.getMinefield(sessionId);

        Action uncover = new Action();
        uncover.setType(Action.Type.UNCOVER);

        uncover.setPosition(Position.at(0, 1));
        controller.submitAction(sessionId, uncover);
        Assert.assertEquals(minefield.getStatus(), Minefield.Status.CONTINUE);
        uncover.setPosition(Position.at(0, 2));
        controller.submitAction(sessionId, uncover);
        Assert.assertEquals(minefield.getStatus(), Minefield.Status.CONTINUE);
        uncover.setPosition(Position.at(1, 2));
        controller.submitAction(sessionId, uncover);
        Assert.assertEquals(minefield.getStatus(), Minefield.Status.CONTINUE);
        uncover.setPosition(Position.at(2, 1));
        controller.submitAction(sessionId, uncover);
        Assert.assertEquals(minefield.getStatus(), Minefield.Status.CONTINUE);

        Action flag = new Action();
        flag.setType(Action.Type.FLAG);
        flag.setPosition(Position.at(0, 0));
        controller.submitAction(sessionId, flag);
        Assert.assertEquals(minefield.getStatus(), Minefield.Status.CONTINUE);
        flag.setPosition(Position.at(1, 1));
        controller.submitAction(sessionId, flag);
        Assert.assertEquals(minefield.getStatus(), Minefield.Status.CONTINUE);
        flag.setPosition(Position.at(2, 2));
        controller.submitAction(sessionId, flag);
        Assert.assertEquals(minefield.getStatus(), Minefield.Status.CONTINUE);

        Action solve = new Action();
        solve.setType(Action.Type.SOLVE);
        solve.setPosition(Position.at(2, 1));
        ActionResult ar = controller.submitAction(sessionId, solve);
        Assert.assertEquals(ar.getStatus(), Minefield.Status.VICTORY);

    }

    @Test
    public void testIgnoreActionsOnVictoryMinfield() throws InvalidActionException, InvalidGameSetupException {
        List<Position> mines = new ArrayList<>();
        mines.add(Position.at(0, 0));
        mines.add(Position.at(1, 1));
        mines.add(Position.at(2, 2));

        Integer sessionId = controller.startGame(3, 3, mines);
        Minefield minefield = controller.getMinefield(sessionId);

        Action uncover = new Action();
        uncover.setType(Action.Type.UNCOVER);

        uncover.setPosition(Position.at(0, 1));
        controller.submitAction(sessionId, uncover);
        Assert.assertEquals(minefield.getStatus(), Minefield.Status.CONTINUE);
        uncover.setPosition(Position.at(0, 2));
        controller.submitAction(sessionId, uncover);
        Assert.assertEquals(minefield.getStatus(), Minefield.Status.CONTINUE);
        uncover.setPosition(Position.at(1, 0));
        controller.submitAction(sessionId, uncover);
        Assert.assertEquals(minefield.getStatus(), Minefield.Status.CONTINUE);
        uncover.setPosition(Position.at(1, 2));
        controller.submitAction(sessionId, uncover);
        Assert.assertEquals(minefield.getStatus(), Minefield.Status.CONTINUE);
        uncover.setPosition(Position.at(2, 0));
        controller.submitAction(sessionId, uncover);
        Assert.assertEquals(minefield.getStatus(), Minefield.Status.CONTINUE);
        uncover.setPosition(Position.at(2, 1));
        controller.submitAction(sessionId, uncover);
        Assert.assertEquals(minefield.getStatus(), Minefield.Status.VICTORY);

        Collection<Cell> cellsBeforeUnwantedAction = controller.getMinefield(sessionId).getCells();
        controller.submitAction( sessionId, uncover );
        Assert.assertArrayEquals(cellsBeforeUnwantedAction.toArray(), controller.getMinefield(sessionId).getCells().toArray());
    }

    @Test
    public void testIgnoreActionsOnGameOverMinfield() throws InvalidActionException, InvalidGameSetupException {
        List<Position> mines = new ArrayList<>();
        mines.add(Position.at(0, 0));
        mines.add(Position.at(1, 1));
        mines.add(Position.at(2, 2));

        Integer sessionId = controller.startGame(3, 3, mines);
        Minefield minefield = controller.getMinefield(sessionId);

        Action uncover = new Action();
        uncover.setType(Action.Type.UNCOVER);

        uncover.setPosition(Position.at(0, 0));
        controller.submitAction(sessionId, uncover);
        Assert.assertEquals(minefield.getStatus(), Minefield.Status.GAMEOVER);

        Collection<Cell> cellsBeforeUnwantedAction = controller.getMinefield(sessionId).getCells();
        controller.submitAction(sessionId, uncover);
        Assert.assertArrayEquals( cellsBeforeUnwantedAction.toArray() , controller.getMinefield(sessionId).getCells().toArray() );
    }

    @AfterClass
    public static void resetLogging() {
        BasicConfigurator.resetConfiguration();
    }

}