package de.cofinpro.dojo.model;

/**
 * Created by chuff on 28.08.2015.
 */
public class Action {

    private Position position;
    private Type type;

    public enum Type {
        UNCOVER,FLAG,SOLVE
    }

    public Action() {

    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Action{" +
                "position=" + position +
                ", type=" + type +
                '}';
    }
}
