package Server.Field;

import Server.Player;

import java.io.Serializable;

public abstract class Field implements Serializable {

    private String name;

    public Field(String name) {
        this.name = name;
    }

    public abstract boolean startAction(Player p);

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Field other = (Field) obj;
        return this.getName().equals(other.getName());
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }


    public String getName() {
        return name;
    }

    public String getUIName() {
        return name;
    }
}
