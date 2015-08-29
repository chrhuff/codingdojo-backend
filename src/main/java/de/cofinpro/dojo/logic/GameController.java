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

    private Map<Integer, Minefield> minefields;

    @PostConstruct
    protected void setupMinefields() {
        minefields = new HashMap<>();

        //developer backdoor
        Minefield minefield = new Minefield(10, 10, 20);
        this.minefields.put( 42 , minefield );
    }

    public Integer startGame(int width, int height, float mineRatio) throws InvalidGameSetupException {
        int sessionId = generateSessionId();

        if (width < 0 || height < 0 || mineRatio < 0 || mineRatio > 1) {
            throw new InvalidGameSetupException("Invalid game setup!");
        }

        int numberOfMines = Math.round((height * width) * mineRatio);
        Minefield minefield = new Minefield(width, height, numberOfMines);

        this.minefields.put(sessionId, minefield);

        return sessionId;
    }

    Integer startGame(int width, int height, List<Position> minePositions) throws InvalidGameSetupException {
        int sessionId = generateSessionId();

        int fieldSize = width * height;
        if (width < 0 || height < 0 || minePositions.size() < fieldSize * 0.1 || minePositions.size() > (fieldSize * 0.9)) {
            throw new InvalidGameSetupException("Invalid game setup!");
        }

        Minefield minefield = new Minefield(width, height, minePositions);

        this.minefields.put(sessionId, minefield);

        return sessionId;
    }

    private int generateSessionId() {
        Random random = new Random();
        int sessionId = random.nextInt();
        while (minefields.containsKey(sessionId)) {
            sessionId = random.nextInt();
        }
        return sessionId;
    }

    protected Minefield getMinefield(int sessionId) {
        return minefields.get(sessionId);
    }

    public ActionResult submitAction(Integer sessionId, Action action) throws InvalidActionException {

        Minefield.Status status = Minefield.Status.CONTINUE;

        Minefield minefield = getMinefield(sessionId);
        if (minefield == null) {
            throw new InvalidActionException("Session not found", action);
        }
        if (action.getType() != Action.Type.NOOP) {
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
                            status = Minefield.Status.GAMEOVER;
                        } else {
                            LOG.info("Empty cell!");
                            uncoverCell(minefield, selectedCell);
                        }
                    }
                    break;
                case FLAG:

                    LOG.info("Flag action at " + position.toString());

                    if (!selectedCell.isUncovered()) {
                        selectedCell.toggleFlag();
                    } else {
                        throw new InvalidActionException("Cannot flag/unflag uncovered cell at position " + position, action);
                    }

                    break;
                case SOLVE:
                    break;
            }
        }
        else {
            LOG.debug("NOOP called for sessid " + sessionId );
        }
        Collection<Cell> cells = minefield.getCells();
        minefield.setStatus(status); //sets the last known status

        return new ActionResult(cells.stream().map(VisibleCell::new).collect(Collectors.toList()), status);
    }

    private void uncoverCell(Minefield minefield, Cell selectedCell) {
        if (!selectedCell.isUncovered()) {
            selectedCell.uncover();
            if (selectedCell.getNumber() == 0) {
                //uncover adjacent cells, now!
                for (Cell adjacentCell : minefield.getAdjacent(selectedCell)) {
                    uncoverCell(minefield, adjacentCell);
                }

            }
        }
    }
}
