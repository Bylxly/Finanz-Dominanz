package Server.State;

import Server.Game;

public class ExecuteFieldState implements GameState {

    private Game game;

    public ExecuteFieldState(Game game) {
        this.game = game;
    }

    @Override
    public void execute() {
        game.getActivePlayer().getCurrentField().startAction(game.getActivePlayer());
    }
}
