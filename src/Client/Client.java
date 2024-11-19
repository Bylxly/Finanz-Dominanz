package Client;

import Server.State.GameState;
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

    public Client() {
        this.isConnected = false;
        this.isTurn = false;
        this.gameOver = false;
        this.money = 0;
    }

    public void connectToServer() {}

    public void sendAction(Action action) {}

    public void disconnect() {}

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
}

