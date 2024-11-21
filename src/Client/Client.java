package Client;

import Server.Field.Field;
import Server.Field.Property.Property;
import Server.Game;
import Server.Message;
import Server.Player;
import Server.State.GameState;

import java.io.*;
import java.net.Socket;

public class Client {
    private Socket serverSocket;
    private int playerID;
    private boolean isConnected;
    private GameState currentGameState;
    private boolean isTurn;
    private Draw draw;
    private UI ui;
    private int money;
    private int figureID;
    private boolean gameOver;
    private BufferedReader reader;
    private PrintWriter writer;
    private ObjectInputStream objectReader;
    private static Game game;

    public Client() {
        this.isConnected = false;
        this.isTurn = false;
        this.gameOver = false;
        this.money = 0;
    }

    public void connectToServer() {
        try {
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter server IP address: ");
            String ipAddress = consoleReader.readLine();
            System.out.print("Enter server port: ");
            int port = Integer.parseInt(consoleReader.readLine());

            serverSocket = new Socket(ipAddress, port);
            this.isConnected = true;
            System.out.println("Connected to the server at " + ipAddress + ":" + port);

            writer = new PrintWriter(new OutputStreamWriter(serverSocket.getOutputStream()), true);

            objectReader = new ObjectInputStream(serverSocket.getInputStream());

            new Thread(this::readGameUpdates).start();

        } catch (IOException e) {
            System.out.println("Error connecting to the server: " + e.getMessage());
        }
    }


    private void readGameUpdates() {
        try {
            while (isConnected && !serverSocket.isClosed()) {
                Object obj = objectReader.readObject();
                if (obj instanceof Game) {
                    updateGame((Game) obj);
                } else if (obj instanceof Message) {
                    String message = ((Message) obj).getMessage();
                    System.out.println(message);
                    try {
                        Action.ServerMessage serverMessage = Action.ServerMessage.valueOf(message);
                        serverMessage.execute(this);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Unknown server message: " + message);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error reading game updates: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }

}

    private synchronized void updateGame(Game updatedGame) {
        this.game = updatedGame;
        printBoard();
    }


    public void sendAction(Action action) {
        if (isConnected) {
            writer.println(action.toString());
        }
    }

    public void disconnect() {
        try {
            if (isConnected) {
                reader.close();
                writer.close();
                objectReader.close();
                serverSocket.close();
                isConnected = false;
                System.out.println("Disconnected from the server.");
            }
        } catch (IOException e) {
            System.out.println("Error disconnecting: " + e.getMessage());
        }
    }
    // For testing purpose
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

    public static void main(String[] args) {
        Client client = new Client();
        client.connectToServer();
    }
}
