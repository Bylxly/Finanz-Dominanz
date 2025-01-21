package Server.Field;

import Server.GameUtilities;
import Server.Player;

/**
 * Die Klasse Start repräsentiert das Startfeld im Spiel.
 * Wenn ein Spieler über dieses Feld zieht oder darauf landet, erhält er Geld von der Bank.
 */
public class Start extends Field {

    private final int bonus;

    /**
     * Konstruktor für die Klasse Start.
     *
     * @param name Der Name des Feldes.
     */
    public Start(String name, int bonus) {
        super(name);
        this.bonus = bonus;
    }

    /**
     * Führt die Aktion aus, wenn ein Spieler auf diesem Feld landet.
     * Der Spieler erhält Geld von der Bank.
     *
     * @param p Der Spieler, der auf dem Feld landet.
     * @return true, da die Aktion erfolgreich ausgeführt wurde.
     */
    @Override
    public boolean startAction(Player p) {
        GameUtilities.receiveFromBank(p, bonus);
        return true;
    }

    public int getBonus() {
        return bonus;
    }
}
