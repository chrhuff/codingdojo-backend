package de.cofinpro.dojo.logic;

import de.cofinpro.dojo.model.Action;
import de.cofinpro.dojo.model.ActionResult;
import de.cofinpro.dojo.model.Position;
import org.apache.log4j.BasicConfigurator;
import org.junit.*;

import java.util.ArrayList;
import java.util.List;

public class GameControllerTest {

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
        List<Position> mines = new ArrayList<>();
        mines.add(new Position(1, 2));
        mines.add(new Position(3, 2));
        mines.add(new Position(4, 4));

        Integer sessionId = controller.startGame(5, 5, 0.25f);

        Action action = new Action();
        action.setPosition(new Position(0, 0));
        action.setType(Action.Type.UNCOVER);
        ActionResult actionResult = controller.submitAction(sessionId, action);
        Assert.assertNotNull(actionResult);
    }

    @Test(expected = InvalidGameSetupException.class)
    public void testBadMineRatioRandomizedStart() throws InvalidGameSetupException, InvalidActionException {
        List<Position> mines = new ArrayList<>();
        mines.add(new Position(1, 2));
        mines.add(new Position(3, 2));
        mines.add(new Position(4, 4));

        Integer sessionId = controller.startGame(5, 5, -0.25f);

        Action action = new Action();
        action.setPosition(new Position(0, 0));
        action.setType(Action.Type.UNCOVER);
        ActionResult actionResult = controller.submitAction(sessionId, action);
        Assert.assertNotNull(actionResult);
    }

    @Test(expected = InvalidGameSetupException.class)
    public void testBadFieldSizeStart() throws InvalidGameSetupException, InvalidActionException {
        List<Position> mines = new ArrayList<>();
        mines.add(new Position(1, 2));
        mines.add(new Position(3, 2));
        mines.add(new Position(4, 4));

        Integer sessionId = controller.startGame(-1, 15, mines);

        Action action = new Action();
        action.setPosition(new Position(0, 0));
        action.setType(Action.Type.UNCOVER);
        ActionResult actionResult = controller.submitAction(sessionId, action);
        Assert.assertNotNull(actionResult);
    }

    @Test
    public void testUncoverContinue() throws InvalidActionException, InvalidGameSetupException {

        List<Position> mines = new ArrayList<>();
        mines.add(new Position(1, 2));
        mines.add(new Position(3, 2));
        mines.add(new Position(4, 4));

        Integer sessionId = controller.startGame(5, 5, mines);

        Action action = new Action();
        action.setPosition(new Position(0, 0));
        action.setType(Action.Type.UNCOVER);
        ActionResult actionResult = controller.submitAction(sessionId, action);
        Assert.assertEquals(ActionResult.Status.CONTINUE, actionResult.getStatus());

        Assert.assertEquals(25, actionResult.getVisibleCells().size());
    }

    @Test(expected = InvalidActionException.class)
    public void testUncoverInvalidAction() throws InvalidActionException, InvalidGameSetupException {

        List<Position> mines = new ArrayList<>();
        mines.add(new Position(1, 2));
        mines.add(new Position(3, 2));
        mines.add(new Position(4, 4));

        Integer sessionId = controller.startGame(5, 5, mines);

        Action action = new Action();
        action.setPosition(new Position(-1, 0));
        action.setType(Action.Type.UNCOVER);
        ActionResult actionResult = controller.submitAction(sessionId, action);

        Assert.fail();
        Assert.assertNotNull(actionResult);
    }

    @Test
    public void testUncoverGameOver() throws InvalidActionException, InvalidGameSetupException {

        List<Position> mines = new ArrayList<>();
        mines.add(new Position(1, 2));
        mines.add(new Position(3, 2));
        mines.add(new Position(4, 4));

        Integer sessionId = controller.startGame(5, 5, mines);

        Action action = new Action();
        action.setPosition(new Position(1, 2));
        action.setType(Action.Type.UNCOVER);
        ActionResult actionResult = controller.submitAction(sessionId, action);
        Assert.assertEquals(ActionResult.Status.GAMEOVER, actionResult.getStatus());
    }

    @Test(expected = InvalidActionException.class)
    public void testBadSessionId() throws InvalidActionException, InvalidGameSetupException {

        List<Position> mines = new ArrayList<>();
        mines.add(new Position(1, 2));
        mines.add(new Position(3, 2));
        mines.add(new Position(4, 4));

        Integer sessionId = controller.startGame(5, 5, mines);

        Action action = new Action();
        action.setPosition(new Position(1, 2));
        action.setType(Action.Type.UNCOVER);
        ActionResult actionResult = controller.submitAction(123213, action);
        Assert.assertEquals(ActionResult.Status.GAMEOVER, actionResult.getStatus());
    }

    @AfterClass
    public static void resetLogging() {
        BasicConfigurator.resetConfiguration();
    }

}