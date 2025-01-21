package Server.Field.Property;

import Server.*;

/**
 * Repräsentiert ein Versorgungswerk (Utility) im Spiel.
 * Die Miete wird basierend auf dem Würfelwurf und der Anzahl der besessenen Versorgungswerke berechnet.
 */
public class Utility extends Property {

    private Game game;
    private boolean canPay = true;

    public Utility(String name, int price, int hypothek, Game game) {
        super(name, price, null, hypothek);
        this.game = game;
    }

    /**
     * Berechnet die Miete basierend auf dem Würfelwurf und der Anzahl der besessenen Versorgungswerke.
     */
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

    /**
     * Berechnet die Miete basierend auf dem Würfelwurf und einem Multiplikator.
     */
    public void payRentTimes(Player player, int times) {
        game.askRoll(player);
        int roll = game.getRoll().getTotal();
        if (!GameUtilities.checkIfEnoughMoney(player, roll * times)) {
            canPay = false;
        }
        GameUtilities.transferMoney(player, getOwner(), roll * times);
    }

    /**
     * Startet die Aktion, wenn ein Spieler auf dem Versorgungswerk landet.
     */
    @Override
    public boolean startAction(Player player) {
        if (!hasHypothek()) {
            payRent(player);
        }
        return canPay;
    }

    /**
     * Gibt die Anzahl der Versorgungswerke zurück, die der Besitzer besitzt.
     */
    public int getAnzahlWerkeOwnedByOwner(Player player) {
        int anzahlWerkeOwnedByPlayer = 0;
        for (Property property : player.getProperties()) {
            if (property instanceof Utility) {
                anzahlWerkeOwnedByPlayer++;
            }
        } return anzahlWerkeOwnedByPlayer;
    }

}
