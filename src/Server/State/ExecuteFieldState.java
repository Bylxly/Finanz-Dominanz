package Server.State;

import Server.Field.AbInKnast;
import Server.Field.Property.Knast;
import Server.Field.Property.Property;
import Server.Game;
import Server.Message;
import Server.MsgType;

import java.util.Objects;

public class ExecuteFieldState implements GameState {

    private Game game;

    public ExecuteFieldState(Game game) {
        this.game = game;
    }

    @Override
    public void execute() {
        if (game.getActivePlayer().isArrested()) {
            game.getKnast().executeKnast(game);
        }
        else if (game.getActivePlayer().getCurrentField() instanceof AbInKnast) {
            game.getActivePlayer().getCurrentField().startAction(game.getActivePlayer());
            game.movePlayerToKnast(game.getActivePlayer());
        }
        else if (!game.getActivePlayer().getCurrentField().startAction(game.getActivePlayer())) {
            game.printBoard();
            System.out.println("Der Spieler " + game.getActivePlayer().getName() + " ist bankrott.");
            if (game.getActivePlayer().getCurrentField().getClass().isAssignableFrom(Property.class)
                    && ((Property) game.getActivePlayer().getCurrentField()).getOwner() != game.getActivePlayer()) {
                game.declareBankruptcy(((Property) game.getActivePlayer().getCurrentField()).getOwner());
            }
            else {
                game.declareBankruptcy();
            }
        }
    }
}
