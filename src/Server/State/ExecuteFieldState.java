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
            executeKnast();
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

    public void executeKnast() {
        game.getActivePlayer().sendObject(new Message(MsgType.ASK_KNAST, null));
        String msg = game.getActivePlayer().recieveMessage();

        if (Objects.equals(msg, "ROLL") &&
                ((Knast) game.getActivePlayer().getCurrentField()).getRollAmount(game.getActivePlayer()) < 3) {
            game.askRoll(game.getActivePlayer());
            ((Knast) game.getActivePlayer().getCurrentField()).incrementRollAmount(game.getActivePlayer());
            if (game.getRoll().getPasch()) {
                game.getActivePlayer().setArrested(false);
                System.out.println("Pasch: " + game.getRoll().getPasch());
            }
            else {
                game.getActivePlayer().sendObject(new Message(MsgType.INFO, "Du hast keinen Pasch gewürfelt"));
                return;
            }
        }
        else {
            if (((Knast) game.getActivePlayer().getCurrentField()).getRollAmount(game.getActivePlayer()) >= 3) {
                game.getActivePlayer().sendObject(new Message(MsgType.INFO,
                        "Sie haben bereits 3 mal gewürfelt und müssen jetzt zahlen"));
            }
            ((Knast) game.getActivePlayer().getCurrentField()).payRent(game.getActivePlayer());
            game.getActivePlayer().setArrested(false);
        }

        ((Knast) game.getActivePlayer().getCurrentField()).removeRollAmount(game.getActivePlayer());
        game.getActivePlayer().sendObject(new Message(MsgType.INFO, "Du bist wieder ein freier Mensch"));
        // Roll after getting free
        game.setCurrentGameState(new RollDiceState(game));
        game.getCurrentGameState().execute();
        game.movePlayer();
    }
}
