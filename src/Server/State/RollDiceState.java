package Server.State;

import Server.Game;

/**
 * Zustand, der das Würfeln im Spiel behandelt.
 * Dieser Zustand wird aufgerufen, wenn ein Spieler an der Reihe ist, die Würfel zu werfen.
 */
public class RollDiceState implements GameState {

    private final Game game;

    /**
     * Konstruktor für RollDiceState.
     * @param game Das aktuelle Spielobjekt.
     */
    public RollDiceState(Game game) {
        this.game = game;
    }

    @Override
    public void execute() {
        game.getRoll().generate();
    }
}
