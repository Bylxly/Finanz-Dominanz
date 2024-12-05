package Server.Field.Property;

import Server.Field.AbInKnast;
import Server.GameUtilities;
import Server.Message;
import Server.MsgType;
import Server.Player;

import javax.swing.event.ChangeListener;
import java.util.HashMap;
import java.util.Map;

public class Knast extends Property {

    private Map<Player, Integer> rollAmount;

    public Knast(String name, int price, int[] rent, int hypothek) {
        super(name, price, rent, hypothek);
        rollAmount = new HashMap<>();
    }

    @Override
    public void payRent(Player player) {
        if (player.isArrested()) {
            if (getOwner() != null && getOwner() != player) {
                GameUtilities.transferMoney(player, getOwner(), getRent(0));
            }
            else {
                GameUtilities.payBank(player, getRent(0));
            }
        }
        else {
            player.sendObject(new Message(MsgType.INFO, "Sie sind nur zu Besuch im Gefängnis!"));
        }
    }

    @Override
    public boolean startAction(Player p) {
        return false;
    }

    public int getRollAmount(Player player) {
        return rollAmount.get(player);
    }

    public void addRollAmount(Player player, int amount) {
        rollAmount.put(player, amount);
    }

    public void incrementRollAmount(Player player) {
        rollAmount.put(player, getRollAmount(player) + 1);
    }
}
