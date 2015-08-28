package de.cofinpro.dojo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

/**
 * Order REST Service for accessing minesweeper backend
 *
 */
@Path("/minesweeper")
@Produces("application/json")
@Consumes("application/json")
public class MinesweeperRESTService {

    private Logger LOG = LoggerFactory.getLogger(getClass());

/*
    @Inject
    private some persistence backend
*/

    @Context
    private SecurityContext securityContext;

    @POST
    @Path("/submitAction")
    public Boolean submitAction( String action ) {
        return false;
    }

    @GET
    public String getHighscore() {
        return "deine mudda";
    }

}
