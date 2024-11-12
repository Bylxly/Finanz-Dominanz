package Server.State;

import Server.Game;

public class RollDiceState implements GameState {

    private final Game game;

    public RollDiceState(Game game) {
        this.game = game;
    }

    @Override
    public void execute() {
        game.getRoll().generate();
    }
}
