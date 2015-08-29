package de.cofinpro.dojo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by chuff on 28.08.2015.
 */
public class VisibleCell {

    @JsonIgnore
    private final Cell underlyingCell;

    private boolean mineRevealed;

    public VisibleCell(Cell underlyingCell) {
        this(underlyingCell, false);
    }

    public VisibleCell(Cell underlyingCell, boolean mineRevealed) {
        this.underlyingCell = underlyingCell;
        this.mineRevealed = mineRevealed;
    }

    public int getNumber() {
        if (underlyingCell.isUncovered()) {
            return underlyingCell.getNumber();
        }
        return -1;
    }

    public Boolean isMine() {
        if (mineRevealed) {
            return underlyingCell.isMine();
        }
        return null;
    }

    public Integer getX() {
        return underlyingCell.getX();
    }

    public Integer getY() {
        return underlyingCell.getY();
    }

    public boolean isFlagged() {
        return underlyingCell.isFlagged();
    }

}
