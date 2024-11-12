package Server.State;

import Server.Game;

public class EndTurnState implements GameState {

    private Game game;

    public EndTurnState(Game game) {
        this.game = game;
    }

    @Override
    public void execute() {
        game.nextPlayer();
    }
}
