package Server.State;

import Server.Game;

public class ExecuteFieldState implements GameState {

    private Game game;

    public ExecuteFieldState(Game game) {
        this.game = game;
    }

    @Override
    public void execute() {
        if (!game.getActivePlayer().getCurrentField().startAction(game.getActivePlayer())) {
            game.printBoard();
            System.out.println("Der Spieler " + game.getActivePlayer().getName() + " ist bankrott.");
            game.declareBankruptcy();
        }
    }
}
