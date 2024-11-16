package Server.Field.Property;

import Server.GameUtilities;
import Server.Player;

public class Utility extends Property {

    public Utility(String name, int price, int rent, int hypothek) {
        super(name, price, rent, hypothek);
    }

    @Override
    public boolean startAction(Player player) {
        if (GameUtilities.checkIfEnoughMoney(player, getRent())) {
            super.payRent(player);
            return true;
        }
        return false;
    }
}
