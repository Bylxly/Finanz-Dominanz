package Client;

import Server.Game;
import Server.Player;
import Server.Field.Field;
import processing.core.PApplet;
import java.util.ArrayList;
import java.util.List;

public class Draw extends PApplet {
    private Client client;
    private Game game;
    private int cellSize = 80;
    private int offsetX = 50;
    private int offsetY = 50;

    private String currentField = "None";
    private String currentPlayer = "Player 1";

    private ArrayList<Field> fields = new ArrayList<>();

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
        noLoop(); // Ensures updates happen only when necessary
    }

    @Override
    public void draw() {
        background(34, 139, 34); // Green background for the game board

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
        redraw(); // Force redraw when the game state is updated
    }

    private void displayMessage(String message) {
        fill(0);
        textAlign(CENTER, CENTER);
        text(message, width / 2.0f, height / 2.0f);
    }

    private void drawFields() {
        for (Field field : fields) {
            field.draw();
        }
    }

    private void drawPlayers() {
        if (game == null) return;

        List<Player> players = game.getPlayers();

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            Server.Field.Field currentField = player.getCurrentField();
            int fieldIndex = client.getFieldIndex(currentField);

            int x = (fieldIndex % 10) * cellSize + offsetX;
            int y = (fieldIndex / 10) * cellSize + offsetY;

            fill(255);
            ellipse(x + cellSize / 2.0f, y + cellSize / 2.0f, 20, 20);

            fill(0);
            textAlign(CENTER, CENTER);
            text(i + 1, x + cellSize / 2.0f, y + cellSize / 2.0f); // Player number
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

    private void drawButtons() {
        fill(200);
        rect(950, 620, 200, 30, 5); // Option 1 button
        rect(950, 660, 200, 30, 5); // Option 2 button
        rect(950, 700, 200, 30, 5); // Option 3 button

        fill(0);
        textAlign(CENTER, CENTER);
        text("Option 1", 1050, 635);
        text("Option 2", 1050, 675);
        text("Option 3", 1050, 715);
    }

    private void initializeFields() {
        float boardSize = height;
        float cellSize = height / 11.0f;

        for (int i = 0; i < 11; i++) {
            fields.add(new Field(i * cellSize, 0, cellSize, cellSize, "Street " + i)); // Top row
            fields.add(new Field(i * cellSize, height - cellSize, cellSize, cellSize, "Street " + (30 - i))); // Bottom row
        }

        for (int i = 1; i < 10; i++) {
            fields.add(new Field(0, i * cellSize, cellSize, cellSize, "Street " + (40 - i))); // Left column
            fields.add(new Field(width - cellSize - (boardSize / 2), i * cellSize, cellSize, cellSize, "Street " + (10 + i))); // Right column
        }
    }

    @Override
    public void mousePressed() {
        for (Field field : fields) {
            if (field.isClicked()) {
                currentField = field.getName();
                println("Clicked on: " + currentField);
                redraw(); // Ensure changes are reflected
            }
        }

        // Handle button clicks
        if (mouseX > 950 && mouseX < 1150) {
            if (mouseY > 620 && mouseY < 650) {
                handleOption1();
            } else if (mouseY > 660 && mouseY < 690) {
                handleOption2();
            } else if (mouseY > 700 && mouseY < 730) {
                handleOption3();
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

    // Inner Field class
    private class Field {
        float x, y, width, height;
        String label;

        Field(float x, float y, float width, float height, String label) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.label = label;
        }

        void draw() {
            fill(isMouseOver() ? color(200, 255, 200) : color(255, 223, 186));
            stroke(0);
            rect(x, y, width, height);
            fill(0);
            textAlign(CENTER, CENTER);
            text(label, x + width / 2, y + height / 2);
        }

        boolean isMouseOver() {
            return mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height;
        }

        boolean isClicked() {
            return isMouseOver() && mousePressed;
        }

        String getName() {
            return label;
        }
    }
}
