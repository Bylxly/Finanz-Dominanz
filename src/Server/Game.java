package Server;

import Server.Field.AbInKnast;
import Server.Field.DummyField;
import Server.Field.Field;
import Server.Field.Property.*;
import Server.Field.Start;
import Server.State.*;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.*;
import java.util.List;

public class Game extends Thread implements Serializable {

    // Konstanten
    private final int START_MONEY = 2000;
    private final int BONUS = 200;
    private final int BOARD_SIZE = 40;

    // Attribute
    private volatile List<Player> players;
    private Field[] board;
    private Player activePlayer;
    private Roll roll;
    private GameState currentGameState;

    public Game() {
        players = new ArrayList<Player>();
        board = new Field[BOARD_SIZE];
        createBoard();
        activePlayer = null;
        roll = new Roll();
        currentGameState = null;
    }

    @Override
    public void run() {
        //TODO: LobbyState
        while (true) {
            if (players.size() >= 2) {
                printBoard();
                startGame();
            }
        }
    }

    private void createBoard() {
        ColorGroup purple = new ColorGroup("purple", "\u001B[35m");
        ColorGroup cyan = new ColorGroup("Cyan", "\u001B[96m");
        ColorGroup magenta = new ColorGroup("Magenta", "\u001B[95m");
        ColorGroup orange = new ColorGroup("Orange", "\u001B[33m");
        ColorGroup red = new ColorGroup("Red", "\u001B[31m");
        ColorGroup yellow = new ColorGroup("Yellow", "\u001B[93m");
        ColorGroup green = new ColorGroup("Green", "\u001B[32m");
        ColorGroup blue = new ColorGroup("Blue", "\u001B[34m");

        for (int i = 0; i < board.length; i++) {
            switch (i) {
                case 0:
                    board[i] = new Start("Startfeld", BONUS);
                    break;
                case 1:
                    board[i] = new Street("Badstraße", 60, 50, new int[]{2, 10, 30, 90, 160, 250}, 30, purple);
                    break;
                case 3:
                    board[i] = new Street("Turmstraße", 60, 50 , new int[]{4, 20, 60, 180, 320, 450}, 30, purple);
                    break;
                case 5:
                    board[i] = new TrainStation("Südbahnhof", 200, new int[]{25, 50, 100, 200}, 100);
                    break;
                case 6:
                    board[i] = new Street("Chausseestraße", 100, 50, new int[]{6, 30, 90, 270, 400, 550}, 50, cyan);
                    break;
                case 8:
                    board[i] = new Street("Elisenstraße", 100, 50, new int[]{6, 30, 90, 270, 400, 550}, 50, cyan);
                    break;
                case 9:
                    board[i] = new Street("Poststraße", 120, 50, new int[]{8, 40, 100, 300, 450, 600}, 60, cyan);
                    break;
                case 10:
                    board[i] = new Knast("Knast", 1000, new int[]{50}, 500);
                    break;
                case 11:
                    board[i] = new Street("Seestraße", 140, 100, new int[]{10, 50, 150, 450, 625, 750}, 70, magenta);
                    break;
                case 12:
                    board[i] = new Utility("Elektrizitätswerk", 150, new int[]{50, 75}, 75);
                    break;
                case 13:
                    board[i] = new Street("Hafenstraße", 140, 100, new int[]{10, 50, 150, 450, 625, 750}, 70, magenta);
                    break;
                case 14:
                    board[i] = new Street("Neue Straße", 160, 100, new int[]{12, 60, 180, 500, 700, 900}, 80, magenta);
                    break;
                case 15:
                    board[i] = new TrainStation("Westbahnhof", 200, new int[]{25, 50, 100, 200}, 100);
                    break;
                case 16:
                    board[i] = new Street("Münchner Straße", 180, 100, new int[]{14, 70, 200, 550, 750, 950}, 90, orange);
                    break;
                case 18:
                    board[i] = new Street("Wiener Straße", 180, 100, new int[]{14, 70, 200, 550, 750, 950}, 90, orange);
                    break;
                case 19:
                    board[i] = new Street("Berliner Straße", 200, 100, new int[]{16, 80, 220, 600, 800, 1000}, 100, orange);
                    break;
                case 21:
                    board[i] = new Street("Theaterstraße", 220, 150, new int[]{18, 90, 250, 700, 875, 1050}, 110, red);
                    break;
                case 23:
                    board[i] = new Street("Museumstraße", 220, 150, new int[]{18, 90, 250, 700, 875, 1050}, 110, red);
                    break;
                case 24:
                    board[i] = new Street("Opernplatz", 240, 150, new int[]{20, 100, 300, 750, 925, 1100}, 120, red);
                    break;
                case 25:
                    board[i] = new TrainStation("Nordbahnhof", 200, new int[]{25, 50, 100, 200}, 100);
                    break;
                case 26:
                    board[i] = new Street("Lessingstraße", 260, 150, new int[]{22, 110, 330, 800, 975, 1150}, 130, yellow);
                    break;
                case 27:
                    board[i] = new Street("Schillerstraße", 260, 150, new int[]{22, 110, 330, 800, 975, 1150}, 130, yellow);
                    break;
                case 28:
                    board[i] = new Utility("Wasserwerk", 150, new int[]{50, 75}, 75);
                    break;
                case 29:
                    board[i] = new Street("Goethestraße", 280, 150, new int[]{24, 120, 360, 850, 1025, 1200}, 140, yellow);
                    break;
                case 30:
                    board[i] = new AbInKnast("Ab in den Knast");
                    break;
                case 31:
                    board[i] = new Street("Rathausplatz", 300, 200, new int[]{26, 130, 390, 900, 1100, 1275}, 150, green);
                    break;
                case 32:
                    board[i] = new Street("Hauptstraße", 300, 200, new int[]{26, 130, 390, 900, 1100, 1275}, 150, green);
                    break;
                case 34:
                    board[i] = new Street("Bahnhofstraße", 320, 200, new int[]{28, 150, 450, 1000, 1200, 1400}, 160, green);
                    break;
                case 35:
                    board[i] = new TrainStation("Hauptbahnhof", 200, new int[]{25, 50, 100, 200}, 100);
                    break;
                case 37:
                    board[i] = new Street("Parkstraße", 350, 200, new int[]{35, 175, 500, 1100, 1300, 1500}, 175, blue);
                    break;
                case 39:
                    board[i] = new Street("Schlossallee", 400, 200, new int[]{50, 200, 600, 1400, 1700, 2000}, 200, blue);
                    break;
                default:
                    board[i] = new DummyField("Feld Nr." + i);
                    break;
            }
        }
    }

    public void makePlayer(String name, Socket client, ObjectOutputStream out, BufferedReader in) {
        Player p = new Player(START_MONEY, name, client, out, in);
        players.add(p);
        if (activePlayer == null) {
            activePlayer = p;
        }
        p.setCurrentField(board[0]);
        p.sendObject(this);
    }

    public void askRoll(Player player) {
        boolean check;
        player.sendObject(new Message(MsgType.ASK_ROLL, null));
        String msg = player.recieveMessage();
        check = Objects.equals(msg, "ROLL");
        if (!check){
            throw new RuntimeException("Reply not allowed");
        }
        else {
            roll.generate();
        }
    }

    public void movePlayer() {
            askRoll(activePlayer);

            // get array position of Player
            int pos = 0;
            for (Field f : board) {
                if (f == activePlayer.getCurrentField()) {
                    break;
                } else {
                    pos++;
                }
            }

            // move player to new field
            int newPos = (pos + roll.getTotal()) % BOARD_SIZE;
            activePlayer.setCurrentField(board[newPos]);

            int timesAroundField = (int) Math.floor((double) (pos + roll.getTotal()) / BOARD_SIZE);
            if (!(activePlayer.getCurrentField() instanceof Start) && timesAroundField >= 1) {
                GameUtilities.receiveFromBank(getActivePlayer(), BONUS * timesAroundField);
            }
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

    public void declareBankruptcy() {
        for (Property property : activePlayer.getProperties()) {
            property.setOwner(null);
        }
        players.remove(activePlayer);
    }

    public void startGame() {
        Map<Player, Integer> rolledResults = new HashMap<>();

        // let player roll and save results in map
        for (Player player : players) {
            askRoll(player);
            int roll_result = roll.getTotal();
            rolledResults.put(player, roll_result);
            System.out.println(player.getName() + " hat " + roll_result + " gewürfelt.");
        }

        // sort players after roll results
        players.sort((p1, p2) -> rolledResults.get(p2).compareTo(rolledResults.get(p1)));

        // set new active player
        activePlayer = players.get(0);

        while (true) {
            int paschAnzahl = 0;
            do {
                if (paschAnzahl == 3){
                    activePlayer.setArrested(true);
                    movePlayerToKnast(activePlayer);
                    break;
                }
                currentGameState = new RollDiceState(this);
                currentGameState.execute();

                if (!activePlayer.isArrested()) {
                    movePlayer();
                }

                if (activePlayer.getCurrentField() instanceof Property
                    && !((Property) activePlayer.getCurrentField()).isOwned()) {
                        currentGameState = new BuyFieldState(this);
                }
                else {
                    currentGameState = new ExecuteFieldState(this);
                }
                currentGameState.execute();
                printBoard();

                if (roll.getPasch()) {
                    ++paschAnzahl;
                } else {
                    paschAnzahl = 0; // Paschzähler zurücksetzen, wenn kein Pasch gewürfelt wird
                }
            } while (roll.getPasch() && !activePlayer.isArrested());

            String msg;
            do {
                activePlayer.sendObject(new Message(MsgType.ASK_NEXT, null));
                msg = activePlayer.recieveMessage();
                switch (msg) {
                    case "BUILD":
                        currentGameState = new BuildState(this);
                        currentGameState.execute();
                        break;
                    case "MORTGAGE":
                        currentGameState = new HypothekState(this);
                        currentGameState.execute();
                        break;
                    case "LIFT":
                        currentGameState = new LiftMortgageState(this);
                        currentGameState.execute();
                        break;
                    case "TRADE":
                        currentGameState = new TradeState(this);
                        currentGameState.execute();
                        break;
                    case "BANKRUPT":
                        declareBankruptcy();
                        msg = "END";
                        break;
                    case "END":
                        break;
                    default:
                        throw new RuntimeException("Reply not allowed.");
                }
            } while (!msg.equals("END"));
            currentGameState = new EndTurnState(this);

            currentGameState.execute();
            printBoard();

            // ends game if only one player is left
            if (players.size() <= 1) {
                return;
            }
        }
    }


    public void printBoard() {
        for (Player player : players) {
            player.sendObject(this);
        }
    }

    public void movePlayerToKnast(Player player) {
        for (Field f : board) {
            if (f instanceof Knast) {
                player.setCurrentField(f);
            }
        }
    }

    public Roll getRoll() {
        return roll;
    }

    public Player getActivePlayer() {
        return activePlayer;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Field[] getBoard() {
        return board;
    }

    public void setCurrentGameState(GameState currentGameState) {
        this.currentGameState = currentGameState;
    }

    public GameState getCurrentGameState() {
        return currentGameState;
    }
}
