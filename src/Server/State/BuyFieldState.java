package Server.State;

import Server.Field.Property.Property;
import Server.Game;
import Server.GameUtilities;
import Server.Message;
import Server.MsgType;

import java.util.Objects;
import java.util.Scanner;

public class BuyFieldState implements GameState {

    private final Game game;
    private final Scanner scanner;
    private Property currentProperty;

    public BuyFieldState(Game game) {
        this.game = game;
        this.scanner = new Scanner(System.in);
        this.currentProperty = (Property) game.getActivePlayer().getCurrentField();
    }

    @Override
    public void execute() {
        if (askClient()) {
            if (GameUtilities.checkIfEnoughMoney(game.getActivePlayer(), currentProperty.getPrice())) {
                currentProperty.buy(game.getActivePlayer());
            }
            else {
                game.getActivePlayer().sendObject(new Message(MsgType.INFO, "Du hast nicht genügend Geld!"));
            }
        }
    }

    private boolean askClient() {
        game.getActivePlayer().sendObject(new Message(MsgType.ASK_BUY, null));

        return Objects.equals(game.getActivePlayer().recieveMessage(), "BUY");
    }
}
