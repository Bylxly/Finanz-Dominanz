package Client;

import Server.Game;
import Server.Player;
import Server.Field.Field;
import processing.core.PApplet;

import java.util.List;

public class Draw extends PApplet {
    private Client client;
    private Game game;
    private int cellSize = 80;
    private int offsetX = 50;
    private int offsetY = 50;

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
        noLoop();
    }

    @Override
    public void draw() {
        background(255);

        if (game == null) {
            displayMessage("Waiting for game state...");
        } else {
            drawBoard();
            drawPlayers();
            drawInfoPanel();
        }
    }

    public void updateGameState(Game updatedGame) {
        this.game = updatedGame;
        redraw();
    }

    private void displayMessage(String message) {
        fill(0);
        textAlign(CENTER, CENTER);
        text(message, width / 2.0f, height / 2.0f);
    }

    private void drawBoard() {
        Field[] board = game.getBoard();
        int boardSize = board.length;

        for (int i = 0; i < boardSize; i++) {
            int x = (i % 10) * cellSize + offsetX;
            int y = (i / 10) * cellSize + offsetY;

            fill(200, 200, 255);
            stroke(0);
            rect(x, y, cellSize, cellSize);

            fill(0);
            textAlign(CENTER, CENTER);
            text(board[i].getName(), x + cellSize / 2.0f, y + cellSize / 2.0f);
        }
    }

    private void drawPlayers() {
        List<Player> players = game.getPlayers();

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            Field currentField = player.getCurrentField();
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
        int panelX = 10 * cellSize + offsetX + 20;
        int panelWidth = 300;

        fill(230);
        rect(panelX, offsetY, panelWidth, height - 2 * offsetY);

        fill(0);
        textAlign(LEFT, TOP);
        int textX = panelX + 10;
        int textY = offsetY + 10;

        text("Game Info", textX, textY);
        textY += 20;
        text("Next Player: " + game.getActivePlayer().getName(), textX, textY);
        textY += 20;
        text("Last Roll: " + game.getRoll().getNumber1() + " + " + game.getRoll().getNumber2() + " = " + game.getRoll().getTotal(), textX, textY);
        textY += 40;

        text("Player Stats", textX, textY);
        textY += 20;

        for (Player player : game.getPlayers()) {
            text(player.getName() + ": $" + player.getMoney(), textX, textY);
            textY += 20;
        }
    }

    @Override
    public void mousePressed() {
        if (game != null) {
            println("Mouse clicked at: " + mouseX + ", " + mouseY);
        }
    }
}
