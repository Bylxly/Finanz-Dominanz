package Server.Field.Property;

import Server.Player;

public class Utility extends Property {

    public Utility(String name, int price, int rent, int hypothek) {
        super(name, price, rent, hypothek);
    }

    @Override
    public void startAction(Player player) {
        super.payRent(player);
    }
}
