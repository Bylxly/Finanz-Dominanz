package Server.Field.Property;

import Server.GameUtilities;
import Server.Player;

public class Utility extends Property {


    public Utility(String name, int price, int[] rent, int hypothek) {
        super(name, price, rent, hypothek);
    }

    @Override
    public void payRent(Player player) {

    }

    @Override
    public boolean startAction(Player player) {
        if (!hasHypothek()) {
            if (GameUtilities.checkIfEnoughMoney(player, getRent(getAnzahlWerkeOwnedByPlayer(getOwner()) - 1))) {
                payRent(player);
                return true;
            }
            return false;
        }
        return true;
    }

    public int getAnzahlWerkeOwnedByPlayer(Player player) {
        int anzahlWerkeOwnedByPlayer = 0;
        for (Property property : player.getProperties()) {
            if (property instanceof Utility) {
                anzahlWerkeOwnedByPlayer++;
            }
        } return anzahlWerkeOwnedByPlayer;
    }
}
