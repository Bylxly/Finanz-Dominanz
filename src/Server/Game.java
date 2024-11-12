package Server;

import java.util.ArrayList;
import java.util.List;

public class Game {

    //Konstanten
    private final int START_MONEY = 2000;
    private final int BOARD_SIZE = 8;

    //Attribute
    private List<Player> players;
    private Field[] board;
    private Player activePlayer;
    private Roll roll;

    public Game() {
        players = new ArrayList<Player>();
        board = new Field[BOARD_SIZE];
        createBoard();
        activePlayer = null;
        roll = new Roll();
    }

    private void createBoard() {
        for (int i = 0; i < board.length; i++) {
            if (i == 0) {
                board[i] = new AbInKnast("Startfeld");
            }
            board[i] = new AbInKnast("Feld Nr." + i);
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

    public void move() {
        roll.generate();

        int pos = 0;
        for (Field f : board) {
            if (f == activePlayer.getCurrentField()) {
                break;
            }
            else {pos++;}
        }

        int newPos = pos + roll.getTotal();
        activePlayer.setCurrentField(board[newPos]);
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
            System.out.println(f.getName());
        }

        for (int i = 0; i < 5; i++) {
            System.out.println();
        }
    }
}
