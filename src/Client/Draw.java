package Client;

import Server.Field.Property.Property;
import Server.Game;
import Server.Player;
import Server.Field.Field;
import processing.core.PApplet;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Client.Elements.*;

public class Draw extends PApplet {
    private Client client;
    private Game game;
    private MainMenu mainmenu;
    private int cellSize = 80;

    private String currentField = "None";
    private String currentPlayer = "";

    private ArrayList<GField> fields = new ArrayList<>();
    public static ArrayList<GButton> buttons = new ArrayList<>();
    public static ArrayList<GTextBox> textboxes = new ArrayList<>();
    private GPanel infoPanel;
    private boolean isConnected = false;
    private Field f;
    private boolean firstTrenderC = false;
    private boolean init = false;

    public Draw(Client client) {
        this.client = client;
    }

    @Override
    public void settings() {
        size(1200, 800);
    }

    @Override
    public void setup() {
        surface.setTitle("Monopoly Game");
        mainmenu = new MainMenu(this);
    }

    @Override
    public void draw() {
        if (!isConnected) {
            background(200);
            mainmenu.draw();
            if (mainmenu.isConnectionComplete()) {
                isConnected = true;
                client = mainmenu.getClient();
                initializeGameComponents();
            }
        } else {
            renderGame();
            firstTrenderC = true;
        }
    }



    private void renderGame() {
        background(34, 139, 34);

        if (game == null) {
            displayMessage("Waiting for game state...");
            initializeGameComponents();
        } else {
            drawFields();
            infoPanel.display();
            drawButtons();
            drawTextBoxes();
            drawPlayers();
        }
    }
    private void createInfoPanel() {
        infoPanel = new GPanel(this, 900, 20, 220, 500, color(255));
        updateInfoPanel();
    }

    public void updateGameState(Game updatedGame) {
        System.out.println("Updating game state...");
        this.game = updatedGame;

        Field[] board = game.getBoard();

        for (int i = 0; i < fields.size(); i++) {
            String fieldName = board[i].getUIName();
            fields.get(i).setName(fieldName);
        }
        printBoard();
        updateInfoPanel();
        redraw();
        if (firstTrenderC) renderGame();
    }

    private void initializeGameComponents() {

        System.out.println("Initializing game components...");
        game = client.getGame();

        if (game == null) {
            System.out.println("Game object is null. Initialization aborted.");
            return;
        }
        if (!init) {
            init = true;
            initializeFields();
            createButtons();
            createInfoPanel();
            createTextboxes();
            System.out.println("Game components initialized.");
            HandleAction.initialize(client, currentField, currentPlayer);
            redraw();

        }

    }


    private void updateInfoPanel() {
        if (infoPanel == null) initializeGameComponents();
        if (game != null) {
            currentPlayer = game.getActivePlayer().getName();
            currentField = game.getActivePlayer().getCurrentField().getName();
            Player activePlayer = game.getActivePlayer();

            StringBuilder ownedFields = new StringBuilder("Owned Fields:\n");
            if (activePlayer.getProperties().isEmpty()) {
                ownedFields.append("None");
            } else {
                for (Property property : activePlayer.getProperties()) {
                    ownedFields.append(property.getName()).append("\n");
                }
            }

            infoPanel.clearText();
            infoPanel.addText("Status:", color(0), true, false);
            infoPanel.addText("Number of Players: " + game.getPlayers().size(), color(0), false, false);
            infoPanel.addText("Number of Fields: " + game.getBoard().length, color(0), false, false);
            infoPanel.addText("Next Player: " + currentPlayer, color(0), false, false);
            infoPanel.addText("Last Roll: " + game.getRoll().getNumber1() + " + " + game.getRoll().getNumber2() + " = " + game.getRoll().getTotal(), color(0), false, false);
            infoPanel.addText("", color(0), false, false);
            infoPanel.addText("Player Status:", color(0), true, false);

            for (Player player : game.getPlayers()) {
                infoPanel.addText("Status of Player " + player.getName(), color(0), true, false);
                infoPanel.addText("Money: " + player.getMoney(), color(0), false, false);
                infoPanel.addText("Owned Fields: " + (player.getProperties().isEmpty() ? "None" : String.join(", ", player.getProperties().stream().map(Property::getUIName).toArray(String[]::new))), color(0), false, false);
                infoPanel.addText("Current Field: " + player.getCurrentField().getUIName(), color(0), false, false);
                infoPanel.addText("", color(0), false, false);
            }
            infoPanel.addText("", color(0), false, false);
        }
    }

    private void displayMessage(String message) {
        fill(0);
        textAlign(CENTER, CENTER);
        text(message, width / 2.0f, height / 2.0f);
    }

    private void drawFields() {
        for (GField field : fields) {
            field.draw(this);
        }
    }

    private void createButtons() {
        buttons.add(new GButton("btnOption1", 950, 700, 200, 30, "Option 1", color(200), color(255), true, false)
                .setAction(HandleAction.ActionType.OPTION1::action));

        buttons.add(new GButton("btnOption2", 950, 740, 200, 30, "Option 2", color(200), color(255), true, false)
                .setAction(HandleAction.ActionType.OPTION2::action));

        buttons.add(new GButton("btnOption3", 950, 780, 200, 30, "Option 3", color(200), color(255), true, false)
                .setAction(HandleAction.ActionType.OPTION3::action));

        buttons.add(new GButton("btnRoll", 950, 670, 200, 90, "Roll Dice", color(200), color(255), true, false)
                .setAction(HandleAction.ActionType.ROLL::action));

        buttons.add(new GButton("btnBuyY", 950, 620, 200, 30, "Buy Yes", color(200), color(255), true, false)
                .setAction(HandleAction.ActionType.BUY_Y::action));

        buttons.add(new GButton("btnBuyN", 950, 660, 200, 30, "Buy No", color(200), color(255), true, false)
                .setAction(HandleAction.ActionType.BUY_N::action));

        buttons.add(new GButton("btnNextEND", 950, 620, 200, 30, "End Turn", color(200), color(255), true, false)
                .setAction(HandleAction.ActionType.END::action));

        buttons.add(new GButton("btnNextBUILD", 950, 660, 200, 30, "Build", color(200), color(255), true, false)
                .setAction(HandleAction.ActionType.BUILD::action));

        buttons.add(new GButton("btnNextBANKRUPT", 950, 700, 200, 30, "Bankrupt", color(200), color(255), true, false)
                .setAction(HandleAction.ActionType.BANKRUPT::action));

        System.out.println("Buttons created: " + buttons.stream().map(GButton::getName).toList());
    }

    private void createTextboxes(){
        textboxes.add(new GTextBox("tbAuction", 950, 700, 200, 30, color(240), color(200), color(255), color(0), color(50), false));
    }

    private void drawButtons() {
        for (GButton button : buttons) {
            button.draw(this);
        }
        loop();
    }

    public void setButtonActive(String name, boolean active) {
        for (GButton button : buttons) {
            if (button.getName().equals(name)) {
                button.setActive(active);
                return;
            }
        }
        System.out.println("Button not found: " + name);
    }
    private void drawTextBoxes() {
        for (GTextBox tb : textboxes) {
            tb.draw(this);
        }
        loop();
    }

    public void setTextboxesActive(String name, boolean active) {
        for (GTextBox tb : textboxes) {
            if (tb.getName().equals(name)) {
                tb.setActive(active);
                return;
            }
        }
        System.out.println("Button not found: " + name);
    }



    private void initializeFields() {
        float boardSize = height;
        float cellSize = height / 11.0f;

        Field[] board = game.getBoard();

        for (int i = 0; i < 11; i++) {
            fields.add(new GField(i * cellSize, 0, cellSize, cellSize, board[i].getUIName()));
        }

        for (int i = 1; i < 10; i++) {
            fields.add(new GField(width - cellSize - (boardSize / 2), i * cellSize, cellSize, cellSize, board[10 + i].getUIName()));
        }

        for (int i = 10; i >= 0; i--) {
            fields.add(new GField(i * cellSize, height - cellSize, cellSize, cellSize, board[20 + (10 - i)].getUIName()));
        }

        for (int i = 9; i > 0; i--) {
            fields.add(new GField(0, i * cellSize, cellSize, cellSize, board[40 - i].getUIName()));
        }
    }

    private void drawPlayers() {
        if (game == null || fields.isEmpty()) return;

        List<Player> players = game.getPlayers();

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            Field currentField = player.getCurrentField();

            if (currentField == null) {
                System.out.println("Player " + player.getName() + " is on a null field.");
                continue;
            }

            int fieldIndex = client.getFieldIndex(currentField);
            if (fieldIndex < 0 || fieldIndex >= fields.size()) {
                System.out.println("Invalid field index for player " + player.getName() +
                        " at field " + currentField.getUIName());
                continue;
            }

            GField field = fields.get(fieldIndex);
            float x = field.getX();
            float y = field.getY();

            float offset = 10 * (i % 4);
            fill(255);
            ellipse(x + cellSize / 2.0f + offset, y + cellSize / 2.0f + offset, 20, 20);

            fill(0);
            textAlign(CENTER, CENTER);
            text(i + 1, x + cellSize / 2.0f + offset, y + cellSize / 2.0f + offset);
        }
    }


    @Override
    public void mousePressed() {
        for (GButton button : buttons) {
            if (button.isClicked(this)) {
                button.performAction();
            }
        }
        mainmenu.ipTextBox.setFocused(false);
        mainmenu.portTextBox.setFocused(false);
        if (mainmenu.lobbyCodeTextBox != null) {
            mainmenu.lobbyCodeTextBox.setFocused(false);
        }

        if (mainmenu.ipTextBox.mousePressed(this)) {
            System.out.println("IP Textbox clicked.");
        } else if (mainmenu.portTextBox.mousePressed(this)) {
            System.out.println("Port Textbox clicked.");
        } else if (mainmenu.lobbyCodeTextBox != null && mainmenu.lobbyCodeTextBox.mousePressed(this)) {
            System.out.println("Lobby Code Textbox clicked.");
        }

        if (mainmenu.connectButton.isClicked(this)) {
            mainmenu.connectButton.performAction();
        }

        if (mainmenu.createGameButton.isClicked(this)) {
            mainmenu.createGameButton.performAction();
        }

        if (mainmenu.joinGameButton.isClicked(this)) {
            mainmenu.joinGameButton.performAction();
        }
    }

    @Override
    public void keyPressed() {
        if (mainmenu.ipTextBox.isFocused()) {
            mainmenu.ipTextBox.keyPressed(key, keyCode);
        } else if (mainmenu.portTextBox.isFocused()) {
            mainmenu.portTextBox.keyPressed(key, keyCode);
        } else if (mainmenu.lobbyCodeTextBox != null && mainmenu.lobbyCodeTextBox.isFocused()) {
            mainmenu.lobbyCodeTextBox.keyPressed(key, keyCode);
        }
    }
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
}