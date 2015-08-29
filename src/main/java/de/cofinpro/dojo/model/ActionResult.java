package de.cofinpro.dojo.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * Created by chuff on 28.08.2015.
 */
public class ActionResult {

    public enum Status {
        CONTINUE, GAMEOVER, VICTORY
    }

    private final List<VisibleCell> visibleCells = new ArrayList<>();

    private final Status status;

    public ActionResult(List<VisibleCell> visibleCells, Status status) {
        this.visibleCells.addAll(visibleCells);
        this.status = status;
    }

    public Collection<VisibleCell> getVisibleCells() {
        return visibleCells;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public String toString() {
        String result = "ActionResult{" +
                "status=" + status +
                "}\n";

        int width = visibleCells.stream().max(Comparator.comparing(VisibleCell::getX)).get().getX() + 1;
        int height = visibleCells.stream().max(Comparator.comparing(VisibleCell::getY)).get().getY() + 1;

        String[][] fieldView = new String[height][width];
        for (VisibleCell visibleCell : visibleCells) {
            fieldView[visibleCell.getY()][visibleCell.getX()] = "[" + (Boolean.TRUE.equals(visibleCell.isMine()) ? "M" : visibleCell.getNumber()) + "]";
        }

        for (int j = 0; j < width; j++) {
            for (int i = 0; i < height; i++) {
                result += fieldView[j][i];
            }
            result+="\n";
        }

        return result;
    }
}
