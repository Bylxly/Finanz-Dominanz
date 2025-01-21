package Server.Field;

import Server.GameUtilities;
import Server.Player;

public class Tax extends Field{

    private final int amount;

    public Tax(String name, int amount) {
        super(name);
        this.amount = amount;
    }

    @Override
    public boolean startAction(Player p) {
        GameUtilities.payBank(p, amount);
        return true;
    }

    public int getAmount() {
        return amount;
    }
}
