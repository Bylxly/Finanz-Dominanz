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
    private String currentPlayer = "Player 1";

    private ArrayList<GField> fields = new ArrayList<>();

    private GButton btnOption1, btnOption2, btnOption3, btnRoll;

    private boolean fieldsInitialized = false;

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
            drawFields();
            drawPlayers();
            drawInfoPanel();
            drawButtons();
        }
    }

    public void updateGameState(Game updatedGame) {
        this.game = updatedGame;

        if (!fieldsInitialized) {
            initializeFields();
        }

        Field[] board = game.getBoard();
        if (fields.size() != board.length) {
            System.out.println("Mismatch between field count and board size.");
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

    private void drawPlayers() {
        if (game == null) return;

        List<Player> players = game.getPlayers();

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            Field currentField = player.getCurrentField();
            int fieldIndex = client.getFieldIndex(currentField);

            int x = 0, y = 0;
            if (fieldIndex <= 10) {
                // Top row
                x = fieldIndex * cellSize;
                y = 0;
            } else if (fieldIndex <= 20) {
                // Right column
                x = 10 * cellSize;
                y = (fieldIndex - 10) * cellSize;
            } else if (fieldIndex <= 30) {
                // Bottom row
                x = (fieldIndex - 21) * cellSize;
                y = 10 * cellSize;
            } else if (fieldIndex <= 39) {
                // Left column
                x = 0;
                y = (fieldIndex - 30) * cellSize;
            }

            fill(255);
            ellipse(x + cellSize / 2.0f, y + cellSize / 2.0f, 20, 20);

            fill(0);
            textAlign(CENTER, CENTER);
            text(i + 1, x + cellSize / 2.0f, y + cellSize / 2.0f);
        }
    }

    private void drawInfoPanel() {
        fill(255);
        rect(950, 500, 220, 100, 10);

        fill(0);
        textAlign(LEFT, TOP);
        text("Current Turn: " + currentPlayer, 960, 510);
        text("Field: " + currentField, 960, 540);
    }

    private void createButtons() {
        btnOption1 = new GButton(950, 620, 200, 30, "Option 1", color(200), color(255), true, true);
        btnOption2 = new GButton(950, 660, 200, 30, "Option 2", color(200), color(255), true, true);
        btnOption3 = new GButton(950, 700, 200, 30, "Option 3", color(200), color(255), true, true);
        btnRoll = new GButton(950, 760, 200, 30, "Roll Dice", color(200), color(255), true, true);
    }

    private void drawButtons() {
        btnOption1.draw(this);
        btnOption2.draw(this);
        btnOption3.draw(this);
        btnRoll.draw(this);
        loop();
    }

    private void initializeFields() {
        if (fieldsInitialized) return;

        float cellSize = height / 11.0f;

        if (game == null || game.getBoard() == null) {
            System.out.println("Placeholder names.");
            for (int i = 0; i < 11; i++) {
                fields.add(new GField(i * cellSize, 0, cellSize, cellSize, "Street " + i));
                fields.add(new GField(i * cellSize, height - cellSize, cellSize, cellSize, "Street " + (30 - i)));
            }
            for (int i = 1; i < 10; i++) {
                fields.add(new GField(0, i * cellSize, cellSize, cellSize, "Street " + (40 - i)));
                fields.add(new GField(width - cellSize, i * cellSize, cellSize, cellSize, "Street " + (10 + i)));
            }
        } else {
            Field[] board = game.getBoard();
            for (int i = 0; i <= 10; i++) {
                fields.add(new GField(i * cellSize, 0, cellSize, cellSize, board[i].getUIName()));
            }
            for (int i = 11; i <= 20; i++) {
                fields.add(new GField(10 * cellSize, (i - 10) * cellSize, cellSize, cellSize, board[i].getUIName()));
            }
            for (int i = 30; i >= 21; i--) {
                fields.add(new GField((i - 21) * cellSize, 10 * cellSize, cellSize, cellSize, board[i].getUIName()));
            }
            for (int i = 39; i >= 31; i--) {
                fields.add(new GField(0, (i - 30) * cellSize, cellSize, cellSize, board[i].getUIName()));
            }
        }
        fieldsInitialized = true;
    }

    @Override
    public void mousePressed() {
        for (GField field : fields) {
            if (field.isClicked(this)) {
                currentField = field.getName();
                println("Clicked on: " + currentField);
                redraw();
            }
        }

        if (btnOption1.isClicked (this)) {
            handleOption1();
        } else if (btnOption2.isClicked(this)) {
            handleOption2();
        } else if (btnOption3.isClicked(this)) {
            handleOption3();
        } else if (btnRoll.isClicked(this)) {
            handleRoll();
        }
    }

    private void handleOption1() {
        println("Option 1 selected for field: " + currentField);
    }

    private void handleOption2() {
        println("Option 2 selected for field: " + currentField);
    }

    private void handleOption3() {
        println("Option 3 selected for field: " + currentField);
    }

    private void handleRoll() {
        println("Rolling dice for player: " + currentPlayer);
        Action.ServerMessage.doRoll(client);
    }
}