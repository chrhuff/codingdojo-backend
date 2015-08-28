package de.cofinpro.dojo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by chuff on 28.08.2015.
 */
public class Cell {
    protected final Position position;

    @JsonIgnore
    private final CellState state;

    public Cell(Position position) {
        this.position = position;
        this.state = new CellState();
    }

    public int getX() {
        return position.x;
    }

    public int getY() {
        return position.y;
    }

    public boolean isUncovered() {
        return state.uncovered;
    }

    public boolean isFlagged() {
        return state.flagged;
    }

    @JsonIgnore
    public boolean isMine() {
        return state.mine;
    }

    public void setMine() {
        state.mine = true;
    }

    public int getNumber() {
        if (state.uncovered) {
            return state.number;
        }
        return -1;
    }

}
