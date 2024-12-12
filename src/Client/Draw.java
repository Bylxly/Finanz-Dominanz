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
    private ArrayList<GButton> buttons = new ArrayList<>();

    private GButton btnOption1, btnOption2, btnOption3, btnRoll, btnBuyY, btnBuyN;
    boolean rollButtonClicked = false;

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
                x = (30 - fieldIndex) * cellSize;
                y = 10 * cellSize;
            } else if (fieldIndex <= 39) {
                // Left column
                x = 0;
                y = (40 - fieldIndex) * cellSize;
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
        btnOption1 = new GButton("btnOption1",950, 700, 200, 30, "Option 1", color(200), color(255), true, false);
        btnOption1.setAction(this::handleOption1);

        btnOption2 = new GButton("btnOption2",950, 740, 200, 30, "Option 2", color(200), color(255), true, false);
        btnOption2.setAction(this::handleOption2);

        btnOption3 = new GButton("btnOption3",950, 780, 200, 30, "Option 3", color(200), color(255), true, false);
        btnOption3.setAction(this::handleOption3);

        btnBuyY = new GButton("btnBuyY",950, 620, 200, 30, "Buy Yes", color(200), color(255), true, false);
        btnBuyY.setAction(this::handleBuyY);

        btnBuyN = new GButton("btnBuyN", 950, 660, 200, 30, "Buy No", color(200), color(255), true, false);
        btnBuyN.setAction(this::handleBuyN);

        btnRoll = new GButton("btnRoll", 950, 670, 200, 90, "Roll Dice", color(200), color(255), true, false);
        btnRoll.setAction(this::handleRoll);
        // Add buttons to the list
        buttons.add(btnOption1);
        buttons.add(btnOption2);
        buttons.add(btnOption3);
        buttons.add(btnBuyY);
        buttons.add(btnBuyN);
        buttons.add(btnRoll);
    }

    private void drawButtons() {
        btnOption1.draw(this);
        btnOption2.draw(this);
        btnOption3.draw(this);
        btnBuyY.draw(this);
        btnBuyN.draw(this);
        btnRoll.draw(this);
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

    @Override
    public void mousePressed() {
        for (GButton button : buttons) {
            if (button.isClicked(this)) {
                button.performAction();
            }
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
        Action.ServerMessage.doRollGUI(client);
        rollButtonClicked = true;
        setButtonActive("btnRoll",false);
    }

    private void handleBuyY() {
        Action.ServerMessage.doBuyGUI(client, true);
        setButtonActive("btnBuyY",false);
        setButtonActive("btnBuyN",false);
    }
    private void handleBuyN() {
        Action.ServerMessage.doBuyGUI(client, false);
        setButtonActive("btnBuyY",false);
        setButtonActive("btnBuyN",false);
    }

}
