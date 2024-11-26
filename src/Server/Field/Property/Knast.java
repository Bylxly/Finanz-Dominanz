package Server.Field.Property;

import Server.GameUtilities;
import Server.Player;

public class Knast extends Property {

    private boolean arrested;

    public Knast(String name, int price, int[] rent, int hypothek) {
        super(name, price, rent, hypothek);
    }

    @Override
    public void payRent(Player player) {

    }

    @Override
    public boolean startAction(Player player) {
        return isArrested();
    }

    public void setArrested(boolean arrest) {
        this.arrested = arrest;
    }

    public boolean isArrested() {
        return arrested;
    }
}
