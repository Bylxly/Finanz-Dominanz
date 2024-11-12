package Server;

import Server.Field.AbInKnast;
import Server.Field.Field;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Game {

    //Konstanten
    private final int START_MONEY = 2000;
    private final int BOARD_SIZE = 40;

    //Attribute
    private List<Player> players;
    private Field[] board;
    private Player activePlayer;
    private Roll roll;
    private Scanner scanner;

    public Game() {
        players = new ArrayList<Player>();
        board = new Field[BOARD_SIZE];
        createBoard();
        activePlayer = null;
        roll = new Roll();
        scanner = new Scanner(System.in);
    }

    private void createBoard() {
        for (int i = 0; i < board.length; i++) {
            if (i == 0) {
                board[i] = new AbInKnast("Startfeld");
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
        scanner.nextLine();
    }

    public void move() {
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
        int newPos = pos + roll.getTotal();
        if (newPos >= BOARD_SIZE) {
            newPos -= BOARD_SIZE;
        }
        activePlayer.setCurrentField(board[newPos]);

        // set next player as activePlayer
        if (players.indexOf(activePlayer) == players.size() - 1) {
            activePlayer = players.get(0);
        }
        else {
            activePlayer = players.get(players.indexOf(activePlayer) + 1);
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

        for (Player p : players) {
            System.out.println("Status von Spieler " + p.getName());
            System.out.println("Geld: " + p.getMoney());
            System.out.println("Aktuelles Feld: " + p.getCurrentField().getName());
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
}
