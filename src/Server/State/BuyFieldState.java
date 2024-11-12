package Server.State;

import Server.Field.Property.Property;
import Server.Game;
import Server.GameUtilities;

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
                System.out.println("Du hast nicht genügend Geld!");
            }
        }
    }

    private boolean askClient() {
        System.out.println("Willst du dieses Grunstück kaufen?");
        System.out.println(currentProperty.getName());
        System.out.println("Preis: " + currentProperty.getPrice());
        System.out.println("y/n");

        return scanner.next().equalsIgnoreCase("y");
    }
}
