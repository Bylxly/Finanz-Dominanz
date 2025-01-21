package Client.Elements;

import Client.HandleAction;
import processing.core.PApplet;

public class GButton {
    float x, y, width, height;
    String label, name;
    int colorDefault, colorHover;
    boolean mouseOverEffect, isActive;
    private Runnable action;

    // Konstruktor für GButton
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

    // Methode zum Zeichnen des Buttons
    public void draw(PApplet app) {
        if (!isActive) return;

        app.fill(isMouseOver(app) && mouseOverEffect ? colorHover : colorDefault);
        app.rect(x, y, width, height, 5);

        app.fill(0);
        app.textAlign(PApplet.CENTER, PApplet.CENTER);
        app.text(label, x + width / 2, y + height / 2);
    }

    // Methode zur Überprüfung, ob die Maus über dem Button ist
    boolean isMouseOver(PApplet app) {
        if (!isActive) return false;
        return app.mouseX > x && app.mouseX < x + width && app.mouseY > y && app.mouseY < y + height;
    }

    // Methode zur Überprüfung, ob der Button geklickt wurde
    public boolean isClicked(PApplet app) {
        return isMouseOver(app) && app.mousePressed;
    }

    // Methode zum Setzen des Aktivitätsstatus des Buttons
    public void setActive(boolean active) {
        this.isActive = active;
    }

    // Methode zum Setzen einer Aktion, die beim Klicken ausgeführt wird
    public GButton setAction(Runnable action) {
        this.action = action;
        return this;
    }

    // Methode zur Ausführung der festgelegten Aktion
    public void performAction() {
        if (action != null) {
            action.run();
        }
    }

    // Methode zum Abrufen des Namens des Buttons
    public String getName() {
        return name;
    }
}