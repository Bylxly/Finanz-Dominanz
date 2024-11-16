package Server;

import Server.Field.AbInKnast;
import Server.Field.Field;
import Server.Field.Property.Property;
import Server.Field.Property.Utility;
import Server.State.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Game {

    //Konstanten
    private final int START_MONEY = 2000;
    private final int BOARD_SIZE = 10;

    //Attribute
    private List<Player> players;
    private Field[] board;
    private Player activePlayer;
    private Roll roll;
    private Scanner scanner;

    private GameState currentGameState;

    public Game() {
        players = new ArrayList<Player>();
        board = new Field[BOARD_SIZE];
        createBoard();
        activePlayer = null;
        roll = new Roll();
        scanner = new Scanner(System.in);
        currentGameState = null;
    }

    private void createBoard() {
        for (int i = 0; i < board.length; i++) {
            if (i == 0) {
                board[i] = new AbInKnast("Startfeld");
            }
            if (i % 4 == 0) {
                board[i] = new Utility("Wasserwerk Nr. " + i / 4, 100, 50, 50);
            }
            else {
                board[i] = new AbInKnast("Feld Nr." + i);
            }
        }
    }

    public void makePlayer(String name) {
        Player p = new Player(START_MONEY, name);
        players.add(p);
        if (activePlayer == null) {
            activePlayer = p;
        }
        p.setCurrentField(board[0]);
    }

    public void getPlayerInput() {
        scanner.reset();
        scanner.nextLine();
    }

    public void movePlayer() {
        System.out.println("Spieler " + activePlayer.getName() + " ist am Zug");
        getPlayerInput();
        roll.generate();

        // get array position of Player
        int pos = 0;
        for (Field f : board) {
            if (f == activePlayer.getCurrentField()) {
                break;
            }
            else {pos++;}
        }

        // move player to new field
        int newPos = (pos + roll.getTotal()) % BOARD_SIZE;
        activePlayer.setCurrentField(board[newPos]);
    }

    public void nextPlayer() {
        // set next player as activePlayer
        if (players.indexOf(activePlayer) == players.size() - 1) {
            activePlayer = players.get(0);
        }
        else {
            activePlayer = players.get(players.indexOf(activePlayer) + 1);
        }
    }

    public void startGame() {
        while (true) {
            currentGameState = new RollDiceState(this);
            currentGameState.execute();
            movePlayer();

            if (activePlayer.getCurrentField() instanceof Property
                    && !((Property) activePlayer.getCurrentField()).isOwned()) {
                currentGameState = new BuyFieldState(this);
            } else {
                currentGameState = new ExecuteFieldState(this);
            }
            currentGameState.execute();

            currentGameState = new EndTurnState(this);
            currentGameState.execute();
            printBoard();
        }
    }

    // Temporäre Methode für Debug Zwecke
    public void printBoard() {
        System.out.println("Status:");
        System.out.println("Spieleranzahl: " + players.size());
        System.out.println("Felderanzahl: " + board.length);
        System.out.println("nächster Spieler: " + activePlayer.getName());
        System.out.println("Augenanzahl vom letzten Wurf: " + roll.getNumber1() + "+" + roll.getNumber2() + "=" + roll.getTotal());

        System.out.println();
        System.out.println();

        for (Player player : players) {
            System.out.println("Status von Spieler " + player.getName());
            System.out.println("Geld: " + player.getMoney());
            System.out.print("Felder im Besitz: ");
            if (player.getProperties().isEmpty()) {
                System.out.println("keine");
            }
            else {
                for (Property property : player.getProperties()) {
                    if (player.getProperties().indexOf(property) == 0) {
                        System.out.print(property.getName());
                    }
                    else {
                        System.out.print(", " + property.getName());
                    }
                }
                System.out.println();
            }
            System.out.println("Aktuelles Feld: " + player.getCurrentField().getName());
            System.out.println();
        }

        System.out.println();
        System.out.println();

        System.out.println("Status vom Spielbrett");
        for (Field f : board) {
            int playerAmount = 0;
            for (Player p : players) {
                if (f == p.getCurrentField() && playerAmount == 0) {
                    System.out.print(f.getName() + " <-- " + p.getName());
                    playerAmount++;
                }
                else if (f == p.getCurrentField()) {
                    System.out.print(", " + p.getName());
                    playerAmount++;
                }
            }
            if (playerAmount == 0) {
                System.out.print(f.getName());
            }
            System.out.println();
        }

        for (int i = 0; i < 5; i++) {
            System.out.println();
        }
    }

    public Roll getRoll() {
        return roll;
    }

    public Player getActivePlayer() {
        return activePlayer;
    }
}
