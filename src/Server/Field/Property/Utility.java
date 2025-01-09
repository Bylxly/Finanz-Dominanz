package Server.Field.Property;

import Server.*;

public class Utility extends Property {

    private Game game;

    public Utility(String name, int price, int hypothek, Game game) {
        super(name, price, null, hypothek);
        this.game = game;
    }

    @Override
    public void payRent(Player player) {
        player.sendObject(new Message(MsgType.INFO, "Roll to determine the amount you need to pay for rent"));
        if (getAnzahlWerkeOwnedByOwner(getOwner()) == 1) {
            payRentTimes(player, 4);
        }
        else if (getAnzahlWerkeOwnedByOwner(getOwner()) == 2) {
            payRentTimes(player, 10);
        }
    }

    public void payRentTimes(Player player, int times) {
        game.askRoll(player);
        int roll = game.getRoll().getTotal();
        GameUtilities.transferMoney(player, getOwner(), roll * times);
    }

    @Override
    public boolean startAction(Player player) {
        if (!hasHypothek()) {
            payRent(player);
        }
        return true;
    }

    public int getAnzahlWerkeOwnedByOwner(Player player) {
        int anzahlWerkeOwnedByPlayer = 0;
        for (Property property : player.getProperties()) {
            if (property instanceof Utility) {
                anzahlWerkeOwnedByPlayer++;
            }
        } return anzahlWerkeOwnedByPlayer;
    }
}
