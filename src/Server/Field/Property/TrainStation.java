package Server.Field.Property;

import Server.GameUtilities;
import Server.Player;

/**
 * Repräsentiert einen Bahnhof im Spiel.
 * Die Miete hängt von der Anzahl der Bahnhöfe ab, die der Besitzer besitzt.
 */
public class TrainStation extends Property {

    public TrainStation(String name, int price, int[] rent, int hypothek) {
        super(name, price, rent, hypothek);
    }

    /**
     * Gibt die Anzahl der Bahnhöfe zurück, die der Besitzer besitzt.
     */
    public int getAnzahlBahnhöfe() {
        int anzahlDerBesitzendenBahnhöfeEinesSpielers = 0;
        for(Property property : getOwner().getProperties()){
            if(property instanceof TrainStation){
                anzahlDerBesitzendenBahnhöfeEinesSpielers++;
            }
        }
        return anzahlDerBesitzendenBahnhöfeEinesSpielers;
    }

    /**
     * Berechnet die Miete basierend auf der Anzahl der Bahnhöfe.
     */
    @Override
    public void payRent(Player p) {
        if (getOwner() != null && getOwner() != p) {
            GameUtilities.transferMoney(p, getOwner(), getRent(getAnzahlBahnhöfe()));
        }
    }

    /**
     * Berechnet die Miete für Ereigniskarten (doppelte Miete).
     */
    public void payRentCard(Player p) {
        if (getOwner() != null && getOwner() != p) {
            GameUtilities.transferMoney(p, getOwner(), getRent(getAnzahlBahnhöfe()) * 2);
        }
    }

    /**
     * Startet die Aktion, wenn ein Spieler auf dem Bahnhof landet.
     */
    @Override
    public boolean startAction(Player p) {
        if (!hasHypothek()) {
            if (GameUtilities.checkIfEnoughMoney(p, getRent(getAnzahlBahnhöfe()))) {
                payRent(p);
                return true;
            }
            return false;
        }
        return true;
    }
}

