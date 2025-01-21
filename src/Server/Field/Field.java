package Server.Field;

import Server.Player;

import java.io.Serializable;

/**
 * Die abstrakte Klasse Field ist die Basisklasse für alle Felder im Spiel.
 * Jedes Feld hat einen Namen und kann eine spezielle Aktion ausführen, wenn ein Spieler darauf landet.
 */
public abstract class Field implements Serializable {

    private String name;

    /**
     * Konstruktor für die Klasse Field.
     *
     * @param name Der Name des Feldes.
     */
    public Field(String name) {
        this.name = name;
    }

    /**
     * Abstrakte Methode, die die Aktion des Feldes definiert.
     * Muss von jeder Unterklasse implementiert werden.
     *
     * @param p Der Spieler, der auf dem Feld landet.
     * @return true, wenn die Aktion erfolgreich ausgeführt wurde.
     */
    public abstract boolean startAction(Player p);


    public String getName() {
        return name;
    }

    public String getUIName() {
        return name;
    }
}
