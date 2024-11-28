package Server.Field;

import Server.Player;

public class AbInKnast extends Field {

    private boolean areYouArrested = false;

    public AbInKnast(String name) {
        super(name);
    }

    @Override
    public boolean startAction(Player p) {
        areYouArrested = true;
        return true;
    }

    public boolean getAreYouArrested() {
        return areYouArrested;
    }
}
