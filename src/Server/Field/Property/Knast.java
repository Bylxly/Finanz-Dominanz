package Server.Field.Property;

import Server.*;
import Server.State.RollDiceState;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Repräsentiert das Gefängnis (Knast) im Spiel.
 * Spieler können hier festgehalten werden und müssen entweder würfeln oder zahlen, um freizukommen.
 */
public class Knast extends Property {

    private Map<Player, Integer> rollAmount; // Speichert, wie oft ein Spieler bereits gewürfelt hat.

    public Knast(String name, int price, int[] rent, int hypothek) {
        super(name, price, rent, hypothek);
        rollAmount = new HashMap<>();
    }

    /**
     * Zieht Spieler die Miete ab, wenn er zahlen muss / will.
     */
    @Override
    public void payRent(Player player) {
        if (player.isArrested()) {
            if (getOwner() != null && getOwner() != player) {
                GameUtilities.transferMoney(player, getOwner(), getRent(0));
            }
            else if (getOwner() != player) {
                GameUtilities.payBank(player, getRent(0));
            }
        }
        else {
            player.sendObject(new Message(MsgType.INFO, "Sie sind nur zu Besuch im Gefängnis!"));
        }
    }

    /**
     * Startet die Aktion, wenn ein Spieler im Gefängnis ist.
     */
    @Override
    public boolean startAction(Player player) {
        player.sendObject(new Message(MsgType.INFO, "Sie sind nur zu Besuch im Gefängnis!"));
        return true;
    }

    /**
     * Führt die Gefängnislogik aus: Spieler können würfeln oder zahlen, um freizukommen.
     */
    public void executeKnast(Game game) {
        game.getActivePlayer().sendObject(new Message(MsgType.ASK_KNAST, null));
        String msg = game.getActivePlayer().receiveMessage();

        if (Objects.equals(msg, "ROLL") && this.getRollAmount(game.getActivePlayer()) < 3) {
            game.askRoll(game.getActivePlayer());
            this.incrementRollAmount(game.getActivePlayer());
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
            if (this.getRollAmount(game.getActivePlayer()) >= 3) {
                game.getActivePlayer().sendObject(new Message(MsgType.INFO,
                        "Sie haben bereits 3 mal gewürfelt und müssen jetzt zahlen"));
            }
            this.payRent(game.getActivePlayer());
            game.getActivePlayer().setArrested(false);
        }

        this.removeRollAmount(game.getActivePlayer());
        game.getActivePlayer().sendObject(new Message(MsgType.INFO, "Du bist wieder ein freier Mensch"));
        // Roll after getting free
        game.setCurrentGameState(new RollDiceState(game));
        game.getCurrentGameState().execute();
        game.movePlayer();
        game.setBuildOrExecuteState();
    }

    public int getRollAmount(Player player) {
        return rollAmount.get(player);
    }

    public void addRollAmount(Player player, int amount) {
        rollAmount.put(player, amount);
    }

    public void incrementRollAmount(Player player) {
        rollAmount.put(player, getRollAmount(player) + 1);
    }

    public void removeRollAmount(Player player) {
        rollAmount.remove(player);
    }
}
