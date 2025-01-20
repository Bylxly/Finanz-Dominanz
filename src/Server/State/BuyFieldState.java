package Server.State;

import Server.Field.Property.Knast;
import Server.Field.Property.Property;
import Server.Game;
import Server.GameUtilities;
import Server.Message;
import Server.MsgType;

import java.util.Objects;

public class BuyFieldState implements GameState {

    private final Game game;
    private Property currentProperty;

    public BuyFieldState(Game game) {
        this.game = game;
        this.currentProperty = (Property) game.getActivePlayer().getCurrentField();
    }

    @Override
    public void execute() {
        game.printBoard();
        if (askClient()) {
            do {
                if (GameUtilities.checkIfEnoughMoney(game.getActivePlayer(), currentProperty.getPrice())) {
                    currentProperty.buy(game.getActivePlayer());
                    break;
                }
                else {
                    game.getActivePlayer().sendObject(new Message(MsgType.INFO, "Du hast nicht genügend Geld!"));
                    if (game.getActivePlayer().showOptions(true)) {
                        game.setCurrentGameState(new AuctionState(game));
                        game.getCurrentGameState().execute();
                        break;
                    }
                }
            } while (true);
        }
        else if (currentProperty instanceof Knast) {
            game.setCurrentGameState(new AuctionState(game));
            game.getCurrentGameState().execute();

            game.setCurrentGameState(new ExecuteFieldState(game));
            game.getCurrentGameState().execute();
        }
        else {
            game.setCurrentGameState(new AuctionState(game));
            game.getCurrentGameState().execute();

        }
    }

    private boolean askClient() {
        game.getActivePlayer().sendObject(new Message(MsgType.ASK_BUY, currentProperty.getName()));

        return Objects.equals(game.getActivePlayer().receiveMessage(), "BUY");
    }
}
