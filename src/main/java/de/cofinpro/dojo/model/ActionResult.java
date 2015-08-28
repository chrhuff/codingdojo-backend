package de.cofinpro.dojo.model;

import de.cofinpro.dojo.model.VisibleCell;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by chuff on 28.08.2015.
 */
public class ActionResult {

    public enum Status {
        CONTINUE, GAMEOVER, VICTORY
    }

    private final Collection<VisibleCell> visibleCells = new ArrayList<>();

    private final Status status;

    public ActionResult(Collection<VisibleCell> visibleCells, Status status) {
        this.visibleCells.addAll(visibleCells);
        this.status = status;
    }

    public Collection<VisibleCell> getVisibleCells() {
        return visibleCells;
    }

    public Status getStatus() {
        return status;
    }
}
