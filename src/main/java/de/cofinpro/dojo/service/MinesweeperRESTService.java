package de.cofinpro.dojo.service;

import de.cofinpro.dojo.logic.GameController;
import de.cofinpro.dojo.logic.InvalidActionException;
import de.cofinpro.dojo.logic.InvalidGameSetupException;
import de.cofinpro.dojo.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Order REST Service for accessing minesweeper backend
 *
 */
@Path("/minesweeper")
@Produces("application/json")
@Consumes("application/json")
public class MinesweeperRESTService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    private GameController gameController;

/*
    @Inject
    private some persistence backend
*/
/*
    @Context
    private SecurityContext securityContext;
*/
    @PUT
    @Path("/{sessid}")
    public ActionResult submitAction( @PathParam("sessid") Integer sessionId, Action action ) {
        try {
            ActionResult result = gameController.submitAction(sessionId, action);
            return result;
        } catch (InvalidActionException e) {
            logger.warn("Could NOT process action. " + e.getLocalizedMessage());
        } finally {
        }
        return null;
    }

    @POST
    public Integer initGame( InitGameRequest request ) {
        try {
            int id = gameController.startGame(request.getWidth(), request.getHeight(), request.getMineRatio());
            logger.info("New game with id <" + id + "> started.");
            return id;
        } catch (InvalidGameSetupException e) {
            logger.warn("Could NOT start new game. " + e.getLocalizedMessage() );
        } finally {
        }
        return null;
    }

    @GET
    @Path("/{sessid}")
    public ActionResult getCurrentGameState(@PathParam("sessid") Integer sessionId) {
        Action noop = new Action();
        noop.setType(Action.Type.NOOP);
        try {
            return gameController.submitAction(sessionId,noop);
        } catch (InvalidActionException e) {
            logger.warn("Could NOT retrieve. " + e.getLocalizedMessage());
        } finally {
        }
        return null;
    }

    @GET
    @Path("/highscore")
    public String getHighscore() {
        return "deine mudda";
    }

}
