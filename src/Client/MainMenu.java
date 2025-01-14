package Client;

import Client.Elements.*;
import processing.core.PApplet;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

public class MainMenu extends PApplet {

    private PApplet parent;
    GTextBox portTextBox;
    GTextBox ipTextBox;
    GTextBox lobbyCodeTextBox;
    GButton connectButton;
    GButton createGameButton;
    GButton joinGameButton;
    private String serverMessage = "";
    private boolean isConnected = false;
    private Client client;
    private boolean connectedS = false;


    public MainMenu(PApplet parent) {
        this.parent = parent;
        this.client = new Client();

        portTextBox = new GTextBox("tbPort", 400, 250, 300, 30, parent.color(255), parent.color(120), parent.color(220), parent.color(0), parent.color(50), true);
        ipTextBox = new GTextBox("tbIP", 400, 200, 300, 30, parent.color(255), parent.color(120), parent.color(220), parent.color(0), parent.color(50), true);
        lobbyCodeTextBox = new GTextBox("tbLCode", 400, 250, 300, 30, parent.color(255), parent.color(220), parent.color(220), parent.color(0), parent.color(50), false);

        connectButton = new GButton("btnConnect", 400, 350, 300, 40, "Connect", parent.color(200), parent.color(255), true, true);
        connectButton.setAction(this::connectToServer);

        createGameButton = new GButton("btnCreate", 400, 400, 300, 40, "Create Game", parent.color(200), parent.color(255), true, false);
        createGameButton.setAction(this::createGame);

        joinGameButton = new GButton("btnJoin", 400, 450, 300, 40, "Join Game", parent.color(200), parent.color(255), true, false);
        joinGameButton.setAction(this::joinGame);

        toggleConnectionUI(true);
    }


    public void draw() {
        parent.background(34, 139, 34);

        parent.fill(255);
        parent.textAlign(PApplet.CENTER);
        parent.text("Enter Server IP and Port", parent.width / 1.5f, 150);

        ipTextBox.draw(parent);
        portTextBox.draw(parent);
        lobbyCodeTextBox.draw(parent);



        connectButton.draw(parent);
        if (connectedS) {
            createGameButton.draw(parent);
            joinGameButton.draw(parent);

            parent.fill(255);
            parent.text("Server Message: " + serverMessage, parent.width / 2.0f, 600);
        }
        //redraw();
    }


    private void connectToServer() {
        String portInput = portTextBox.getSavedText().isEmpty() ? "5555" : portTextBox.getSavedText();
        String ipAddress = ipTextBox.getSavedText().isEmpty() ? "localhost" : ipTextBox.getSavedText();

//        String portInput = portTextBox.getSavedText();
//        String ipAddress = ipTextBox.getSavedText();


        try {
            int port = Integer.parseInt(portInput);
            client.connectToServer(ipAddress, port);

            serverMessage = "Connected to server at " + ipAddress + ":" + port;
            connectedS = true;
            toggleConnectionUI(false);
        } catch (NumberFormatException e) {
            serverMessage = "Invalid port. Please enter a valid number.";
        } catch (Exception e) {
            serverMessage = "Connection failed: " + e.getMessage();
        }
    }

    private void createGame() {
        if (client != null) {
            try {
                client.getWriter().println("CREATE");
                String lobbyCode = "ABCDEF";
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(lobbyCode), null);
                serverMessage = "Game created. Lobby code copied to clipboard: " + lobbyCode;
                createGameButton.setActive(false);
                joinGameButton.setActive(false);
                lobbyCodeTextBox.setActive(false);
                isConnected = true;
            }catch (Exception e) {
                serverMessage = "Game creation failed: " + e.getMessage();
            }
        }
    }

    private void joinGame() {
        if (client != null) {
            String lobbyCode = lobbyCodeTextBox.getSavedText();
            if (!lobbyCode.isEmpty()) {
                client.getWriter().println(lobbyCode);
                serverMessage = "Attempting to join game with code: " + lobbyCode;
                isConnected = true;
            } else {
                serverMessage = "Please enter a lobby code.";
            }
        }
    }

    private void toggleConnectionUI(boolean enable) {
        System.out.println("UI" + enable);
        portTextBox.setActive(enable);
        ipTextBox.setActive(enable);
        connectButton.setActive(enable);

        createGameButton.setActive(!enable);
        joinGameButton.setActive(!enable);
        lobbyCodeTextBox.setActive(!enable);

    }

    public boolean isConnectionComplete() {
        return isConnected;
    }

    public Client getClient() {
        return client;
    }
}
