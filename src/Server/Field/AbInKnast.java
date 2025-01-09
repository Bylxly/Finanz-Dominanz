package Server.Field;

import Server.Player;

public class AbInKnast extends Field {

    public AbInKnast(String name) {
        super(name);
    }

    @Override
    public boolean startAction(Player player) {
        //player.setArrested(true);
        // => Moved to movePlayerToKnast() in Game
        return true;
    }

}
