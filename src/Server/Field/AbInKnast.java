package Server.Field;

import Server.Player;

/**
 * Die Klasse AbInKnast repräsentiert ein spezielles Feld im Spiel, das den Spieler ins Gefängnis schickt.
 * Wenn ein Spieler auf diesem Feld landet, wird er verhaftet.
 */
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
