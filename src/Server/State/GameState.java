package Server.State;

import java.io.Serializable;


/**
 * Schnittstelle für die Zustände im Spiel.
 * Jeder Zustand implementiert diese Schnittstelle und definiert spezifische Aktionen,
 * die in diesem Zustand ausgeführt werden sollen.
 */
public interface GameState extends Serializable {

    /**
     * Führt die Hauptaktion des Zustands aus.
     * Diese Methode wird von den konkreten Zustandsklassen implementiert.
     */
    public void execute();
}
