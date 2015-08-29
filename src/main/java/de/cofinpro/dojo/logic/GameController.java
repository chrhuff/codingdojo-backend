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
        if (action.getType() != Action.Type.NOOP && minefield.getStatus() == Minefield.Status.CONTINUE ) {
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
                            selectedCell.uncover();
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

                    LOG.info("Solve action at " + position.toString());


                    if (selectedCell.isUncovered()) {

                        try {
                            Collection<Cell> adjacent = minefield.getAdjacent(selectedCell);
                            long numberOfFlags = adjacent.stream().filter(c -> c.isFlagged()).count();
                            if (numberOfFlags == selectedCell.getNumber()) {
                                adjacent.stream().filter(c -> !c.isUncovered() && !c.isFlagged()).forEach(c -> uncoverCell(minefield, c));
                            }
                        } catch (MineUncoveredException e) {
                            LOG.error(e.getMessage());
                            status = Minefield.Status.GAMEOVER;
                        }
                    }

                    break;
            }
        }
        else {
            LOG.debug("Either minefield was finished or NOOP called for sessid " + sessionId );
            status = minefield.getStatus();
        }
        Collection<Cell> cells = minefield.getCells();
        minefield.setStatus(status); //sets the last known status

        checkVictory(minefield);

        return new ActionResult(cells.stream().map(VisibleCell::new).collect(Collectors.toList()), status);
    }

    private void checkVictory( Minefield minefield) {
        if (minefield.getStatus() == Minefield.Status.CONTINUE) {
            Collection<Cell> cells = minefield.getCells();
            //if all fields without mine are uncovered -> VICTORY
            Iterator<Cell> it = cells.iterator();
            while ( it.hasNext() ) {
                Cell cell = it.next();
                if (!cell.isUncovered() && !cell.isMine()) {
                    return;
                }
            }
            minefield.setStatus(Minefield.Status.VICTORY);
        }
    }

    private void uncoverCell(Minefield minefield, Cell selectedCell) {
        if (!selectedCell.isUncovered()) {
            selectedCell.uncover();
            if (selectedCell.isMine()) {
                throw new MineUncoveredException(selectedCell);
            }
            if (selectedCell.getNumber() == 0) {
                //uncover unflagged adjacent cells, now!
                minefield.getAdjacent(selectedCell).stream().filter(c -> !c.isFlagged()).forEach(c -> uncoverCell(minefield, c));
            }
        }
    }

    private class MineUncoveredException extends RuntimeException {
        public MineUncoveredException(Cell selectedCell) {
            super("Mine uncovered at position: " + selectedCell.getX() + ", " + selectedCell.getY());
        }
    }
}
