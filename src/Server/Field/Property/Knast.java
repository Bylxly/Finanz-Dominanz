package Server.Field.Property;

import Server.GameUtilities;
import Server.Player;

public class Knast extends Property {

    public Knast(String name, int price, int[] rent, int hypothek) {
        super(name, price, rent, hypothek);
    }

    @Override
    public void payRent(Player player) {

    }

    @Override
    public boolean startAction(Player player) {
        return true;
    }
}
