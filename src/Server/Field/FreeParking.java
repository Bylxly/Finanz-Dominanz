package Server.Field;

import Server.Player;

/**
 * Die Klasse FreeParking repräsentiert ein Freies-Parken-Feld im Spiel.
 * Wenn ein Spieler auf diesem Feld landet, passiert nichts Besonderes.
 */
public class FreeParking extends Field {

    /**
     * Konstruktor für die Klasse FreeParking.
     *
     * @param name Der Name des Feldes.
     */
    public FreeParking(String name) {
        super(name);
    }

    /**
     * Führt die Aktion aus, wenn ein Spieler auf diesem Feld landet.
     * Es wird keine spezielle Aktion ausgeführt.
     *
     * @param p Der Spieler, der auf dem Feld landet.
     * @return true, da die Aktion erfolgreich ausgeführt wurde.
     */
    @Override
    public boolean startAction(Player p) {
        return true;
    }
}
