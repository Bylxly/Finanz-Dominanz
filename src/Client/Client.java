package Client;

import Server.State.GameState;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.IOException;

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

            reader = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(serverSocket.getOutputStream()), true);

            new Thread(this::readFromServer).start();

        } catch (IOException e) {
            System.out.println("Error connecting to the server: " + e.getMessage());
        }
    }

    private void readFromServer() {
        try {
            String serverMessage;
            while ((serverMessage = reader.readLine()) != null) {
                System.out.println("Server: " + serverMessage);
            }
        } catch (IOException e) {
            System.out.println("Error reading from the server: " + e.getMessage());
        }
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
                serverSocket.close();
                isConnected = false;
                System.out.println("Disconnected from the server.");
            }
        } catch (IOException e) {
            System.out.println("Error disconnecting: " + e.getMessage());
        }
    }

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

    public static void main (String[] args){
        Client client = new Client();
        client.connectToServer();
    }
}
