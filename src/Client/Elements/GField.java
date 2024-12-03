package Client.Elements;

import processing.core.PApplet;

public class GField {
    private float x, y, width, height;
    private String label;

    public GField(float x, float y, float width, float height, String label) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.label = label;
    }

    public void draw(PApplet applet) {
        applet.fill(isMouseOver(applet) ? applet.color(200, 255, 200) : applet.color(255, 223, 186));
        applet.stroke(0);
        applet.rect(x, y, width, height);
        applet.fill(0);
        applet.textAlign(PApplet.CENTER, PApplet.CENTER);
        applet.text(label, x + width / 2, y + height / 2);
    }

    public boolean isMouseOver(PApplet applet) {
        return applet.mouseX > x && applet.mouseX < x + width && applet.mouseY > y && applet.mouseY < y + height;
    }

    public boolean isClicked(PApplet applet) {
        return isMouseOver(applet) && applet.mousePressed;
    }

    // Getter for the label
    public String getName() {
        return label;
    }

    // Setter for the label (new method)
    public void setName(String newName) {
        this.label = newName;
    }
}
