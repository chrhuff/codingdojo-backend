package de.cofinpro.dojo.service;

import de.cofinpro.dojo.model.Action;
import de.cofinpro.dojo.model.Cell;
import de.cofinpro.dojo.model.Position;
import de.cofinpro.dojo.model.VisibleCell;
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

//    @Inject
//    private MineService mineService;

/*
    @Inject
    private some persistence backend
*/
/*
    @Context
    private SecurityContext securityContext;
*/
    @PUT
    @Path("/submitAction/{sessid}")
    public Collection<VisibleCell> submitAction( @PathParam("sessid") String sessionId, Action action ) {
        return new ArrayList<VisibleCell>(){{
            add( new VisibleCell( new Cell( new Position(1,1))) );
            add( new VisibleCell( new Cell( new Position(1,1))) );
            add( new VisibleCell( new Cell( new Position(1,1))) );
        }};
    }

    @POST
    @Path("/initGame")
    public String initGame() {
        return "43";
    }

    @GET
    @Path("/highscore")
    public String getHighscore() {
        return "deine mudda";
    }

}
