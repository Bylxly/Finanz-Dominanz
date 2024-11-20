package Server.Field;

import Server.GameUtilities;
import Server.Player;

public class Start extends Field{

    private final int bonus;

    public Start(String name, int bonus) {
        super(name);
        this.bonus = bonus;
    }

    @Override
    public boolean startAction(Player p) {
        GameUtilities.receiveFromBank(p, bonus);
        return true;
    }

    public int getBonus() {
        return bonus;
    }
}
