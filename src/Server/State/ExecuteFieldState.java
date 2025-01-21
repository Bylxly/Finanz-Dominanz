package Server.State;

import Server.Field.AbInKnast;
import Server.Field.Property.Knast;
import Server.Field.Property.Property;
import Server.Game;
import Server.Message;
import Server.MsgType;

import java.util.Objects;

/**
 * Der Zustand, der die Logik ausführt, wenn ein Spieler auf einem Spielfeld landet.
 */
public class ExecuteFieldState implements GameState {

    private Game game;

    /**
     * Konstruktor für ExecuteFieldState.
     * @param game Das aktuelle Spielobjekt.
     */
    public ExecuteFieldState(Game game) {
        this.game = game;
    }

    /**
     * Führt die Spielfeldaktion basierend auf dem aktuellen Feld des aktiven Spielers aus.
     * Behandelt spezielle Szenarien wie Verhaftung, Bankrott oder Spielfeldaktionen.
     */
    @Override
    public void execute() {
        // Wenn der Spieler verhaftet ist, führe die Knastlogik aus
        if (game.getActivePlayer().isArrested()) {
            game.getKnast().executeKnast(game);
        }
        // Wenn der Spieler auf das Feld "AbInKnast" kommt
        else if (game.getActivePlayer().getCurrentField() instanceof AbInKnast) {
            game.getActivePlayer().getCurrentField().startAction(game.getActivePlayer());
            game.movePlayerToKnast(game.getActivePlayer());
        }
        // Standardaktion auf einem Feld
        else if (!game.getActivePlayer().getCurrentField().startAction(game.getActivePlayer())) {
            game.printBoard();
            System.out.println("Der Spieler " + game.getActivePlayer().getName() + " ist bankrott.");
            // Bankrott behandeln, wenn das Grundstück einem anderen Spieler gehört
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
