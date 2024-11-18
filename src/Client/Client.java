package Client;

import Server.State.GameState;

import java.net.Socket;

public class Client {
    private Socket socket;
    private int playerID;
    private boolean connected;
    private GameState currentgameState;
    private boolean isTurn;
    private Draw draw;
    private UI ui;
    private int money;
    private int figureID;
    private boolean gameOver;
    public Client() {}
}
