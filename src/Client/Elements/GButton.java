package Client.Elements;

import Client.HandleAction;
import processing.core.PApplet;

public class GButton {
    float x, y, width, height;
    String label, name;
    int colorDefault, colorHover;
    boolean mouseOverEffect, isActive;
    private Runnable action;

    public GButton(String name, float x, float y, float width, float height, String label, int colorDefault, int colorHover, boolean mouseOverEffect, boolean isActive) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.label = label;
        this.colorDefault = colorDefault;
        this.colorHover = colorHover;
        this.mouseOverEffect = mouseOverEffect;
        this.isActive = isActive;
    }

    // draw Button
    public void draw(PApplet app) {
        if (!isActive) return;

        app.fill(isMouseOver(app) && mouseOverEffect ? colorHover : colorDefault);
        app.rect(x, y, width, height, 5);

        app.fill(0);
        app.textAlign(PApplet.CENTER, PApplet.CENTER);
        app.text(label, x + width / 2, y + height / 2);
    }

    // check if the mouse is over the Button
    boolean isMouseOver(PApplet app) {
        if (!isActive) return false;
        return app.mouseX > x && app.mouseX < x + width && app.mouseY > y && app.mouseY < y + height;
    }

    // check if the Button was Clicked
    public boolean isClicked(PApplet app) {
        return isMouseOver(app) && app.mousePressed;
    }

    // en-/disable Button
    public void setActive(boolean active) {
        this.isActive = active;
    }

    // set Action
    public GButton setAction(Runnable action) {
        this.action = action;
        return this;
    }

    // run Button Action
    public void performAction() {
        if (action != null) {
            action.run();
        }
    }

    public String getName() {
        return name;
    }
}