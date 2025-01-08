package Client.Elements;

import processing.core.PApplet;

public class GTextBox {
    private float x, y, width, height;
    private String text = "";
    private String savedText = "";
    private int backgroundColor, focusBackgroundColor, textColor, borderColor;
    private boolean isActive;
    private boolean focused = false;

    public GTextBox(String name, float x, float y, float width, float height, int backgroundColor, int focusBackgroundColor, int textColor, int borderColor, boolean isActive) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.backgroundColor = backgroundColor;
        this.focusBackgroundColor = focusBackgroundColor;
        this.textColor = textColor;
        this.borderColor = borderColor;
        this.isActive = isActive;
    }

    public void draw(PApplet applet) {
        if (focused) {
            applet.fill(focusBackgroundColor);
        } else {
            applet.fill(backgroundColor);
        }

        applet.stroke(borderColor);
        applet.rect(x, y, width, height);

        applet.fill(textColor);
        applet.textAlign(PApplet.LEFT, PApplet.CENTER);
        applet.text(text, x + 5, y + height / 2);
    }

    public void keyPressed(char key, int keyCode) {
        if (focused) {
            if (keyCode == PApplet.BACKSPACE && text.length() > 0) {
                text = text.substring(0, text.length() - 1);
            } else if (keyCode == PApplet.ENTER) {
                focused = false;
                saveText();
            } else if (key != PApplet.CODED) {
                text += key;
            }
        }
    }

    public boolean mousePressed(float mouseX, float mouseY) {
        if (isActive && mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height) {
            focused = true;
            return true;
        }
        focused = false;
        return false;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public boolean isFocused() {
        return focused;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    public String getSavedText() {
        return savedText;
    }

    private void saveText() {
        this.savedText = text;
    }
}
