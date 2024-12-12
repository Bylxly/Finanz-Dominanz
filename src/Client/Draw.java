package Client;

import Server.Game;
import Server.Player;
import Server.Field.Field;
import processing.core.PApplet;
import java.util.ArrayList;
import java.util.List;
import Client.Elements.*;

public class Draw extends PApplet {
    private Client client;
    private Game game;
    private int cellSize = 80;

    private String currentField = "None";
    private String currentPlayer = "";

    private ArrayList<GField> fields = new ArrayList<>();
    private ArrayList<GButton> buttons = new ArrayList<>();

    public Draw(Client client) {
        this.client = client;
        HandleAction.initialize(client, currentField, currentPlayer);
    }

    @Override
    public void settings() {
        size(1200, 800);
    }

    @Override
    public void setup() {
        surface.setTitle("Monopoly Game");
        textSize(14);
        initializeFields();
        createButtons();
        noLoop();
    }

    @Override
    public void draw() {
        background(34, 139, 34);

        if (game == null) {
            displayMessage("Waiting for game state...");
        } else {
            drawFields();      // Draw the game board fields
            drawPlayers();     // Draw player positions
            drawInfoPanel();   // Draw the info panel
            drawButtons();     // Draw action buttons
        }
    }

    public void updateGameState(Game updatedGame) {
        this.game = updatedGame;

        Field[] board = game.getBoard();

        if (fields.size() != board.length) {
            System.out.println("Mismatch between field count and board size.");
            System.out.println(board.length);
            return;
        }

        for (int i = 0; i < fields.size(); i++) {
            String fieldName = board[i].getUIName();
            fields.get(i).setName(fieldName);
        }

        redraw();
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

    private void drawInfoPanel() {
        fill(255);
        rect(950, 500, 220, 100, 10);

        fill(0);
        textAlign(LEFT, TOP);
        text("Current Turn: " +  client.getGame().getActivePlayer(), 960, 510);
        text("Field: " + currentField, 960, 540);
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

    private void initializeFields() {
        float boardSize = height;
        float cellSize = height / 11.0f;

        Field[] board = game.getBoard();

        // Top row (0 to 10)
        for (int i = 0; i < 11; i++) {
            fields.add(new GField(i * cellSize, 0, cellSize, cellSize, board[i].getUIName()));
        }

        // Right column (11 to 20)
        for (int i = 1; i < 10; i++) {
            fields.add(new GField(width - cellSize - (boardSize / 2), i * cellSize, cellSize, cellSize, board[10 + i].getUIName()));
        }

        // Bottom row (21 to 30)
        for (int i = 10; i >= 0; i--) {
            fields.add(new GField(i * cellSize, height - cellSize, cellSize, cellSize, board[20 + (10 - i)].getUIName()));
        }

        // Left column (31 to 39)
        for (int i = 9; i > 0; i--) {
            fields.add(new GField(0, i * cellSize, cellSize, cellSize, board[40 - i].getUIName()));
        }
    }

    private void drawPlayers() {
        if (game == null) return;

        List<Player> players = game.getPlayers();

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            Field currentField = player.getCurrentField();
            int fieldIndex = client.getFieldIndex(currentField);


            GField field = fields.get(fieldIndex);
            float x = field.getX();
            float y = field.getY();

            fill(255);
            ellipse(x + cellSize / 2.0f, y + cellSize / 2.0f, 20, 20);

            fill(0);
            textAlign(CENTER, CENTER);
            text(i + 1, x + cellSize / 2.0f, y + cellSize / 2.0f);
        }
    }
    @Override
    public void mousePressed() {
        for (GButton button : buttons) {
            if (button.isClicked(this)) {
                button.performAction();
            }
        }
    }
}
