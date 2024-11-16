package Server.Field;

import Server.Player;

public abstract class Field {

    private String name;

    public Field(String name) {
        this.name = name;
    }

    public abstract boolean startAction(Player p);

    public String getName() {
        return name;
    }
}
