package Server;

public abstract class Field {

    private String name;

    public Field(String name) {
        this.name = name;
    }

    public void startAction(Player p){

    }

    public String getName() {
        return name;
    }
}
