package Client;

import Server.State.GameState;
import processing.core.PGraphics;
import processing.core.PImage;
import java.util.List;

public class Draw {
    /*private PGraphics canvas;
    private GameState gameState;
    private List<PImage> assets;

    public Draw(PGraphics canvas) {
        this.canvas = canvas;
        this.assets = new java.util.ArrayList<>();
    }

    public void processing() {
        canvas.beginDraw();
        canvas.background(255);
        drawFigures(gameState.getFigures());
        canvas.endDraw();
    }

    public void loadGameState(GameState state) {
        this.gameState = state;
    }

    public void drawFigures(List<Figure> figures) {
        for (Figure figure : figures) {
            PImage figureImage = assets.get(figure.getFigureID());
            canvas.image(figureImage, figure.getX(), figure.getY());
        }
    }

    public void clearCanvas() {
        canvas.beginDraw();
        canvas.clear();
        canvas.endDraw();
    }

    public void addAsset(PImage asset) {
        assets.add(asset);
    }*/
}
