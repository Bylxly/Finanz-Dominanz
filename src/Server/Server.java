package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Random;

public class Server {

    private final int PORT = 5555;

    private HashMap<String, Game> gameMap;
    private int tempPlayerCount = 0;


    public Server() {
        this.gameMap = new HashMap<String, Game>();
    }

    public String createGame() {
        String gameCode = generateLobbycode();
        gameMap.put(gameCode, new Game());
        return gameCode;
    }

    // Temp method for quick start
    public String createGame(String lobbyCode) {
        gameMap.put(lobbyCode, new Game());
        return lobbyCode;
    }

    public void joinGame(Socket client, String code, ObjectOutputStream out, BufferedReader in) {
        gameMap.get(code).makePlayer("Player " + ++tempPlayerCount, client, out, in);
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();

                ObjectOutputStream objectOut = new ObjectOutputStream(clientSocket.getOutputStream());
                BufferedReader clientIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));


                objectOut.writeObject((new Message(MsgType.ASK_SERVER, null)));
                String clientMsg = clientIn.readLine();

                if (clientMsg.equals("CREATE")) {
                    String gameCode = createGame();
                    objectOut.writeObject(new Message(MsgType.INFO, "Your game code is: " + gameCode));
                    gameMap.get(gameCode).start();
                    joinGame(clientSocket, gameCode, objectOut, clientIn);
                }
                // Temp also for quick join
                else if (clientMsg.equals("CREATE_CUSTOM")) {
                    String gameCode = createGame("ABCDEF");
                    objectOut.writeObject(new Message(MsgType.INFO, "Your game code is: " + gameCode));
                    gameMap.get(gameCode).start();
                    joinGame(clientSocket, gameCode, objectOut, clientIn);
                }
                else {
                    if (!gameMap.containsKey(clientMsg)) {
                        objectOut.writeObject(new Message(MsgType.INFO, "Game not found"));
                    }
                    else {
                        objectOut.writeObject(new Message(MsgType.INFO, "Successfully connected"));
                        joinGame(clientSocket, clientMsg, objectOut, clientIn);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String generateLobbycode() {
        String characters = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder lobbyCode = new StringBuilder();
        Random random = new Random();

        do {
            for (int i = 0; i < 6; i++) {
                int randomInt = random.nextInt(characters.length());
                lobbyCode.append(characters.charAt(randomInt));
            }
        } while (gameMap.containsKey(lobbyCode.toString()));

        System.out.println(lobbyCode.toString());
        return lobbyCode.toString();
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.startServer();
    }

}
