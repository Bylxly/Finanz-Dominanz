package Client;

import Client.Elements.*;
import processing.core.PApplet;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

public class MainMenu extends PApplet {

    private PApplet parent;
    private GTextBox ipTextBox;
    private GTextBox portTextBox;
    private GTextBox lobbyCodeTextBox;
    private GButton connectButton;
    private GButton createGameButton;
    private GButton joinGameButton;
    private String serverMessage = "";
    private boolean isConnected = false;
    private Client client;


    public MainMenu(PApplet parent) {
        this.parent = parent;
        this.client = new Client();

        ipTextBox = new GTextBox("tbIP", 400, 200, 300, 30, parent.color(255), parent.color(0), parent.color(0), parent.color(200), true);
        portTextBox = new GTextBox("tbPort", 400, 250, 300, 30, parent.color(255), parent.color(0), parent.color(0), parent.color(200), true);
        lobbyCodeTextBox = new GTextBox("tbLCode", 400, 250, 300, 30, parent.color(255), parent.color(0), parent.color(0), parent.color(200), false);

        connectButton = new GButton("btnConnect", 400, 350, 300, 40, "Connect", parent.color(200), parent.color(255), true, false);
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
        parent.text("Enter Server IP and Port", parent.width / 2.0f, 150);

        ipTextBox.draw(parent);
        portTextBox.draw(parent);
        if (lobbyCodeTextBox != null) {
            lobbyCodeTextBox.draw(parent);
        }
        connectButton.draw(parent);

        if (isConnected) {
            createGameButton.draw(parent);
            joinGameButton.draw(parent);

            parent.fill(255);
            parent.text("Server Message: " + serverMessage, parent.width / 2.0f, 600);
        }
    }



    @Override
    public void keyPressed() {
        if (ipTextBox.isFocused()) {
            ipTextBox.keyPressed(key, keyCode);
        } else if (portTextBox.isFocused()) {
            portTextBox.keyPressed(key, keyCode);
        } else if (lobbyCodeTextBox != null && lobbyCodeTextBox.isFocused()) {
            lobbyCodeTextBox.keyPressed(key, keyCode);
        }
    }

    @Override
    public void mousePressed() {
        ipTextBox.mousePressed(mouseX, mouseY);
        portTextBox.mousePressed(mouseX, mouseY);
        if (lobbyCodeTextBox != null) {
            lobbyCodeTextBox.mousePressed(mouseX, mouseY);
        }

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

            toggleConnectionUI(false);
        } catch (NumberFormatException e) {
            serverMessage = "Invalid port. Please enter a valid number.";
        } catch (Exception e) {
            serverMessage = "Connection failed: " + e.getMessage();
        }
    }

    private void createGame() {
        if (client != null) {
            client.getWriter().println("CREATE");
            String lobbyCode = "ABCDEF";
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
                isConnected = true;
            } else {
                serverMessage = "Please enter a lobby code.";
            }
        }
    }

    private void toggleConnectionUI(boolean enable) {
        ipTextBox.setActive(enable);
        portTextBox.setActive(enable);
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
