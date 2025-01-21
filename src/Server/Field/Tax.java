package Server.Field;

import Server.GameUtilities;
import Server.Player;

/**
 * Die Klasse Tax repräsentiert ein Steuerfeld im Spiel.
 * Wenn ein Spieler auf diesem Feld landet, muss er eine bestimmte Steuer an die Bank zahlen.
 */
public class Tax extends Field {

    private final int amount;

    /**
     * Konstruktor für die Klasse Tax.
     *
     * @param name   Der Name des Feldes.
     * @param amount Der Betrag, der als Steuer gezahlt werden muss.
     */
    public Tax(String name, int amount) {
        super(name);
        this.amount = amount;
    }

    /**
     * Führt die Aktion aus, wenn ein Spieler auf diesem Feld landet.
     * Der Spieler zahlt die Steuer an die Bank.
     *
     * @param p Der Spieler, der auf dem Feld landet.
     * @return true, da die Aktion erfolgreich ausgeführt wurde.
     */
    @Override
    public boolean startAction(Player p) {
        GameUtilities.payBank(p, amount);
        return true;
    }

    /**
     * Gibt den Steuerbetrag zurück.
     *
     * @return Der Steuerbetrag.
     */
    public int getAmount() {
        return amount;
    }
}
