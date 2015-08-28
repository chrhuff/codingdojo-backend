package de.cofinpro.dojo.logic;

import de.cofinpro.dojo.model.Action;

/**
 * Created by chuff on 28.08.2015.
 */
public class InvalidActionException extends Exception {

    public InvalidActionException(String message, Action action) {
        super(message + ": " + action);
    }
}
