package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private final int PORT = 5555;

    private List<Game> games; //TODO: Map
    private int tempPlayerCount = 0;


    public Server() {
        games = new ArrayList<>();
    }

    public void createGame() {
        games.add(new Game());
    }

    public void joinGame(Socket client, int index) {
        games.get(index).makePlayer("Player " + ++tempPlayerCount, client);
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                if (games.isEmpty()) {
                    createGame();
                }
                joinGame(clientSocket, 0);
                if (tempPlayerCount >= 2) {
                    games.get(0).start();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.createGame();
        server.startServer();
    }

}
