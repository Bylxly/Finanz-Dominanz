package Client.Elements;

import processing.core.PApplet;

public class GButton {
    float x, y, width, height;
    String label;
    int colorDefault, colorHover;
    boolean mouseOverEffect;

    public GButton(float x, float y, float width, float height, String label, int colorDefault, int colorHover, boolean mouseOverEffect) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.label = label;
        this.colorDefault = colorDefault;
        this.colorHover = colorHover;
        this.mouseOverEffect = mouseOverEffect;
    }

    public void draw(PApplet app) {
        app.fill(isMouseOver(app) && mouseOverEffect ? colorHover : colorDefault);
        app.rect(x, y, width, height, 5);

        app.fill(0);
        app.textAlign(PApplet.CENTER, PApplet.CENTER);
        app.text(label, x + width / 2, y + height / 2);
    }

    boolean isMouseOver(PApplet app) {
        return app.mouseX > x && app.mouseX < x + width && app.mouseY > y && app.mouseY < y + height;
    }

    public boolean isClicked(PApplet app) {
        return isMouseOver(app) && app.mousePressed;
    }
}

