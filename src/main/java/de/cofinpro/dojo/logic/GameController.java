package de.cofinpro.dojo.logic;

import de.cofinpro.dojo.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by chuff on 28.08.2015.
 */
@ApplicationScoped
public class GameController {

    private static final Logger LOG = LoggerFactory.getLogger(GameController.class);

    private Map<Integer, Minefield> minefield;

    @PostConstruct
    protected void setupMinefields() {
        minefield = new HashMap<>();
    }

    public Integer startGame(int width, int height, float mineRatio) throws InvalidGameSetupException {
        int sessionId = generateSessionId();

        if (width < 0 || height < 0 || mineRatio < 0 || mineRatio > 1) {
            throw new InvalidGameSetupException("Invalid game setup!");
        }

        int numberOfMines = Math.round((height * width) * mineRatio);
        Minefield minefield = new Minefield(width, height, numberOfMines);

        this.minefield.put(sessionId, minefield);

        return sessionId;
    }

    public Integer startGame(int width, int height, List<Position> minePositions) throws InvalidGameSetupException {
        int sessionId = generateSessionId();

        int fieldSize = width * height;
        if (width < 0 || height < 0 || minePositions.size() < fieldSize * 0.1 || minePositions.size() > (fieldSize * 0.9)) {
            throw new InvalidGameSetupException("Invalid game setup!");
        }

        Minefield minefield = new Minefield(width, height, minePositions);

        this.minefield.put(sessionId, minefield);

        return sessionId;
    }

    private int generateSessionId() {
        Random random = new Random();
        int sessionId = random.nextInt();
        while (minefield.containsKey(sessionId)) {
            sessionId = random.nextInt();
        }
        return sessionId;
    }

    private Minefield getMinefield(int sessionId) {
        return minefield.get(sessionId);
    }

    public ActionResult submitAction(Integer sessionId, Action action) throws InvalidActionException {

        ActionResult.Status status = ActionResult.Status.CONTINUE;

        Minefield minefield = getMinefield(sessionId);
        if (minefield == null) {
            throw new InvalidActionException("Session not found", action);
        }

        Position position = action.getPosition();
        Cell selectedCell = minefield.getCell(position);

        if (selectedCell == null) {
            throw new InvalidActionException("Invalid position: " + position, action);
        }

        switch (action.getType()) {
            case UNCOVER:

                LOG.info("Uncover action at " + position.toString());

                if (!selectedCell.isUncovered()) {
                    if (selectedCell.isMine()) {
                        LOG.error("Game over!");
                        status = ActionResult.Status.GAMEOVER;
                    } else {
                        LOG.info("Empty cell!");
                        selectedCell.uncover();
                    }
                }
                break;
            case FLAG:
                break;
            case SOLVE:
                break;
        }
        Collection<Cell> cells = minefield.getCells();

        return new ActionResult(cells.stream().map(VisibleCell::new).collect(Collectors.toList()), status);
    }
}
