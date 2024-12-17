package Client;

import Client.Elements.*;
import processing.core.PApplet;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

public class MainMenu extends PApplet {

    private GTextBox ipTextBox;
    private GTextBox portTextBox;
    private GTextBox lobbyCodeTextBox;
    private GButton connectButton;
    private GButton createGameButton;
    private GButton joinGameButton;
    private String serverMessage = "";
    private boolean isConnected = false;
    private Client client; // Reference to the existing Client instance

    public MainMenu() {
        this.client = client; // Initialize the client reference
    }

    @Override
    public void settings() {
        size(1200, 800);
    }

    @Override
    public void setup() {
        surface.setTitle("Monopoly Game - Main Menu");
        textSize(14);

        ipTextBox = new GTextBox(450, 200, 300, 30, color(255), color(0), color(0), true);
        portTextBox = new GTextBox(450, 250, 300, 30, color(255), color(0), color(0), true);

        connectButton = new GButton("connect", 450, 300, 300, 40, "Connect", color(200), color(0), true, false);
        connectButton.setAction(this::connectToServer);

        createGameButton = new GButton("create", 450, 400, 300, 40, "Create Game", color(200), color(0), true, false);
        createGameButton.setAction(this::createGame);
        createGameButton.setActive(false);

        joinGameButton = new GButton("join", 450, 450, 300, 40, "Join Game", color(200), color(0), true, false);
        joinGameButton.setAction(this::joinGame);
        joinGameButton.setActive(false);

        lobbyCodeTextBox = new GTextBox(450, 500, 300, 30, color(255), color(0), color(0), true);
        lobbyCodeTextBox.setActive(false);
    }

    @Override
    public void draw() {
        background(34, 139, 34);

        fill(255);
        textAlign(CENTER);
        text("Enter Server IP and Port", width / 2.0f, 150);

        ipTextBox.draw(this);
        portTextBox.draw(this);
        connectButton.draw(this);

        if (isConnected) {
            createGameButton.draw(this);
            joinGameButton.draw(this);
            lobbyCodeTextBox.draw(this);

            text("Server Message: " + serverMessage, width / 2.0f, 600);
        }
    }

    @Override
    public void keyPressed() {
        if (!isConnected) {
            ipTextBox.keyPressed(key, keyCode);
            portTextBox.keyPressed(key, keyCode);
        } else {
            lobbyCodeTextBox.keyPressed(key, keyCode);
        }
    }

    @Override
    public void mousePressed() {
        ipTextBox.mousePressed(mouseX, mouseY);
        portTextBox.mousePressed(mouseX, mouseY);
        lobbyCodeTextBox.mousePressed(mouseX, mouseY);

        if (connectButton.isClicked(this)) {
            connectButton.performAction();
        }
        if (createGameButton.isClicked(this) && isConnected) {
            createGameButton.performAction();
        }
        if (joinGameButton.isClicked(this) && isConnected) {
            joinGameButton.performAction();
        }
    }

    private void connectToServer() {
        String ipAddress = ipTextBox.getSavedText().isEmpty() ? "localhost" : ipTextBox.getSavedText();
        String portInput = portTextBox.getSavedText().isEmpty() ? "5555" : portTextBox.getSavedText();

        try {
            int port = Integer.parseInt(portInput);
            client.connectToServer(ipAddress, port);

            isConnected = true;
            serverMessage = "Connected to server at " + ipAddress + ":" + port;

            createGameButton.setActive(true);
            joinGameButton.setActive(true);
            lobbyCodeTextBox.setActive(true);
        } catch (NumberFormatException e) {
            serverMessage = "Connection failed: " + e.getMessage();
        }
    }

    private void createGame() {
        if (client != null) {
            client.getWriter().println("CREATE");
            String lobbyCode = "ABCDEF"; // Replace with actual code from server
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(lobbyCode), null);
            serverMessage = "Game created. Lobby code copied to clipboard: " + lobbyCode;
        }
    }

    private void joinGame() {
        if (client != null) {
            String lobbyCode = lobbyCodeTextBox.getSavedText();
            if (!lobbyCode.isEmpty()) {
                client.getWriter().println(lobbyCode);
                serverMessage = "Attempting to join game with code: " + lobbyCode;
            } else {
                serverMessage = "Please enter a lobby code.";
            }
        }
    }
}