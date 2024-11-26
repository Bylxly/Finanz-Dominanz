package Server.State;

import Server.Game;

public class BuildState implements GameState {

    private final Game game;

    public BuildState(Game game) {
        this.game = game;
    }

    @Override
    public void execute() {

    }
}
