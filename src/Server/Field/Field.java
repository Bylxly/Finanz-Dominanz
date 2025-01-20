package Server.Field;

import Server.Player;

import java.io.Serializable;

public abstract class Field implements Serializable {

    private String name;

    public Field(String name) {
        this.name = name;
    }

    public abstract boolean startAction(Player p);


    public String getName() {
        return name;
    }

    public String getUIName() {
        return name;
    }
}
