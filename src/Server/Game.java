package Server;

import Server.Field.AbInKnast;
import Server.Field.Field;
import Server.Field.Property.ColorGroup;
import Server.Field.Property.Property;
import Server.Field.Property.Street;
import Server.Field.Property.Utility;
import Server.Field.Start;
import Server.State.*;

import java.io.Serializable;
import java.net.Socket;
import java.util.*;

public class Game extends Thread implements Serializable {

    // Konstanten
    private final int START_MONEY = 2000;
    private final int BONUS = 200;
    private final int BOARD_SIZE = 40;

    // Attribute
    private List<Player> players;
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
        printBoard();
        startGame();
    }

    private void createBoard() {
        for (int i = 0; i < board.length; i++) {
            if (i == 0) {
                board[i] = new Start("Startfeld", BONUS);
            }
            else if (i == 5) {
                board[i] = new Street("Schillerstraße", 400, 50, new int[]{100, 150, 200, 250, 300, 400}, 50,
                        new ColorGroup("Grün", "\u001B[32m"));
            }
            else if (i == 15) {
                board[i] = new Street("Bayernstraße", 1000, 150, new int[]{200, 350, 500, 650, 800, 1000}, 250,
                        new ColorGroup("Blau", "\u001B[34m"));
            }
            else if (i == 25) {
                board[i] = new Street("Hessenstraße", 500, 60, new int[]{120, 180, 230, 290, 350, 450}, 250,
                        new ColorGroup("Rot", "\u001B[31m"));
            }
            else if (i == 35) {
                board[i] = new Street("Berliner Platz", 200, 25, new int[]{50, 80, 110, 140, 170, 200}, 250,
                        new ColorGroup("Lila", "\u001B[35m"));
            }
            else if (i % 10 == 0) {
                board[i] = new Utility("Wasserwerk Nr. " + i / 10, 100, new int[]{100}, 50);
            }
            else {
                board[i] = new AbInKnast("Feld Nr." + i);
            }
        }
    }

    public void makePlayer(String name, Socket client) {
        Player p = new Player(START_MONEY, name, client);
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
        String msg;
        System.out.println(msg = player.recieveMessage());
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
            do {
                currentGameState = new RollDiceState(this);
                currentGameState.execute();
                movePlayer();

                if (activePlayer.getCurrentField() instanceof Property
                    && !((Property) activePlayer.getCurrentField()).isOwned()) {
                        currentGameState = new BuyFieldState(this);
                }
                else {
                    currentGameState = new ExecuteFieldState(this);
                }
                currentGameState.execute();
            } while (roll.getPasch());

            String msg;
            do {
                activePlayer.sendObject(new Message(MsgType.ASK_NEXT, null));
                msg = activePlayer.recieveMessage();
                switch (msg) {
                    case "BUILD":
                        currentGameState = new BuildState(this);
                        currentGameState.execute();
                        break;
                    case "BANKRUPT":
                        declareBankruptcy();
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
}
