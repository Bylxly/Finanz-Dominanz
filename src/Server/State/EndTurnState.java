package Server.State;

import Server.Game;

/**
 * Zustand, der das Ende eines Spielerzugs behandelt.
 * Dieser Zustand wechselt zum nächsten Spieler, wenn der aktuelle Spieler seinen Zug beendet.
 */
public class EndTurnState implements GameState {

    private Game game;

    /**
     * Konstruktor für EndTurnState.
     * @param game Das aktuelle Spielobjekt.
     */
    public EndTurnState(Game game) {
        this.game = game;
    }

    @Override
    public void execute() {
        game.nextPlayer();
    }
}
