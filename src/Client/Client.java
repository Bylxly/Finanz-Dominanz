package Client;

import Server.Field.Field;
import Server.Field.Property.Property;
import Server.Game;
import Server.Message;
import Server.MsgType;
import Server.Player;
import Server.State.GameState;

import processing.core.PApplet;

import java.io.*;
import java.net.Socket;

public class Client {
    private Socket serverSocket;
    private int playerID;
    private boolean isConnected;
    private GameState currentGameState;
    private boolean isTurn;
    private Draw draw; // GUI handler
    private int money;
    private int figureID;
    private boolean gameOver;
    private BufferedReader reader;
    private PrintWriter writer;
    private ObjectInputStream objectReader;
    private Game game;

    public Client() {
        this.isConnected = false;
        this.isTurn = false;
        this.gameOver = false;
        this.money = 0;
        this.draw = new Draw(this);
    }

    public void connectToServer() {
        try {
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter server IP address: ");
            String ipAddress = consoleReader.readLine();
            if (ipAddress.isEmpty()) {
                ipAddress = "localhost";
            }

            System.out.print("Enter server port: ");
            String portInput = consoleReader.readLine();
            int port = portInput.isEmpty() ? 5555 : Integer.parseInt(portInput);

            serverSocket = new Socket(ipAddress, port);
            this.isConnected = true;
            System.out.println("Connected to the server at " + ipAddress + ":" + port);

            writer = new PrintWriter(new OutputStreamWriter(serverSocket.getOutputStream()), true);
            objectReader = new ObjectInputStream(serverSocket.getInputStream());

            new Thread(this::readGameUpdates).start();

            startGUI();

        } catch (IOException e) {
            System.out.println("Error connecting to the server: " + e.getMessage());
        }
    }

    private void startGUI() {
        Thread guiThread = new Thread(() -> {
            String[] processingArgs = {"Monopoly GUI"};
            PApplet.runSketch(processingArgs, draw);
        });
        guiThread.start();
    }

    private void readGameUpdates() {
        try {
            while (isConnected && !serverSocket.isClosed()) {
                Object obj = objectReader.readObject();
                if (obj instanceof Game) {
                    updateGame((Game) obj);
                } else if (obj instanceof Message) {
                    processMessage((Message) obj);
                } else {
                    System.out.println("Received unknown object type: " + obj.getClass().getName());
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error reading game updates: " + e.getMessage());
        }
    }

    private synchronized void updateGame(Game updatedGame) {
        this.game = updatedGame;

        if (draw != null) {
            draw.updateGameState(updatedGame);
        }

        printBoard();
    }

    private void processMessage(Message message) {
        MsgType type = message.messageType();
        String content = message.message();

        switch (type) {
            case INFO:
                System.out.println("Server info: " + content);
                break;
            default:
                try {
                    Action.ServerMessage serverMessage = Action.ServerMessage.valueOf(type.name());
                    serverMessage.execute(this, message);
                } catch (IllegalArgumentException e) {
                    System.out.println("Unknown or unhandled message type: " + type);
                }
                break;
        }
    }

    public void sendAction(Action action) {
        if (isConnected) {
            writer.println(action.toString());
        }
    }

    public int getFieldIndex(Field field) {
        if (game == null || game.getBoard() == null) {
            System.out.println("Game or board is not initialized.");
            return -1;
        }
        Field[] board = game.getBoard();
        for (int i = 0; i < board.length; i++) {
            if (board[i].equals(field)) {
                return i;
            }
        }
        // Field not found
        System.out.println("Field not found on the board: " + field.getName());
        return -1;
    }

    public void disconnect() {
        try {
            if (isConnected) {
                if (reader != null) {
                    reader.close();
                }
                if (writer != null) {
                    writer.close();
                }
                if (objectReader != null) {
                    objectReader.close();
                }
                if (serverSocket != null) {
                    serverSocket.close();
                }
                isConnected = false;
                System.out.println("Disconnected from the server.");
            }
        } catch (IOException e) {
            System.out.println("Error disconnecting: " + e.getMessage());
        }
    }

    // For testing purposes: Console-based board representation
    public void printBoard() {
        System.out.println("Status:");
        System.out.println("Spieleranzahl: " + game.getPlayers().size());
        System.out.println("Felderanzahl: " + game.getBoard().length);
        System.out.println("nächster Spieler: " + game.getActivePlayer().getName());
        System.out.println("Augenanzahl vom letzten Wurf: " +
                game.getRoll().getNumber1() + "+" + game.getRoll().getNumber2() + "=" + game.getRoll().getTotal());

        System.out.println();

        for (Player player : game.getPlayers()) {
            System.out.println("Status von Spieler " + player.getName());
            System.out.println("Geld: " + player.getMoney());
            System.out.print("Felder im Besitz: ");
            if (player.getProperties().isEmpty()) {
                System.out.println("keine");
            } else {
                for (Property property : player.getProperties()) {
                    if (player.getProperties().indexOf(property) == 0) {
                        System.out.print(property.getName());
                    } else {
                        System.out.print(", " + property.getName());
                    }
                }
                System.out.println();
            }
            System.out.println("Aktuelles Feld: " + player.getCurrentField().getName());
            System.out.println();
        }

        System.out.println("Status vom Spielbrett");
        for (Field f : game.getBoard()) {
            int playerAmountOnField = 0;
            for (Player p : game.getPlayers()) {
                if (f == p.getCurrentField() && playerAmountOnField == 0) {
                    System.out.print(f.getName() + " <-- " + p.getName());
                    playerAmountOnField++;
                } else if (f == p.getCurrentField()) {
                    System.out.print(", " + p.getName());
                    playerAmountOnField++;
                }
            }
            if (playerAmountOnField == 0) {
                System.out.print(f.getName());
            }
            System.out.println();
        }

        System.out.println();
    }

    // Getter and Setter methods
    public GameState getGameState() {
        return currentGameState;
    }

    public void updateLocalState(GameState state) {
        this.currentGameState = state;
    }

    public void processAction(Action action) {}

    public void endGame() {
        this.gameOver = true;
    }

    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public Draw getDraw() {
        return draw;
    }

    public void setDraw(Draw draw) {
        this.draw = draw;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getFigureID() {
        return figureID;
    }

    public void setFigureID(int figureID) {
        this.figureID = figureID;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public PrintWriter getWriter() {
        return writer;
    }

    public ObjectInputStream getObjectReader() {
        return objectReader;
    }

    public Game getGame() {
        return game;
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.connectToServer();
    }
}
