package Client.Elements;

import processing.core.PApplet;

import static processing.core.PConstants.*;

public class GTextBox {
    private String name = "";
    private float x, y, width, height;
    private String text = "";
    private String savedText = "";
    private int backgroundColor, focusBackgroundColor, hoverBackgroundColor, textColor, borderColor;
    private boolean isActive;
    private boolean focused = false;

    // Constructor to initialize the GTextBox with given parameters
    public GTextBox(String name, float x, float y, float width, float height, int backgroundColor, int focusBackgroundColor, int hoverBackgroundColor, int textColor, int borderColor, boolean isActive) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.backgroundColor = backgroundColor;
        this.focusBackgroundColor = focusBackgroundColor;
        this.hoverBackgroundColor = hoverBackgroundColor;
        this.textColor = textColor;
        this.borderColor = borderColor;
        this.isActive = isActive;
    }

    // Method to draw the text box on the screen
    public void draw(PApplet applet) {
        if (!isActive) return;
        boolean isMouseOver = isMouseOver(applet);
        if (focused) {
            applet.fill(focusBackgroundColor);
        } else if (isMouseOver) {
            applet.fill(hoverBackgroundColor);
        } else {
            applet.fill(backgroundColor);
        }

        applet.stroke(borderColor);
        applet.rect(x, y, width, height);

        applet.fill(textColor);
        applet.textAlign(PApplet.LEFT, PApplet.CENTER);
        applet.text(text, x + 5, y + height / 2);
    }

    // Method to handle key presses
    public void keyPressed(char key, int keyCode) {
        if (focused) {
            if (keyCode == BACKSPACE && text.length() > 0) {
                text = text.substring(0, text.length() - 1);
            } else if (key != CODED && keyCode != ENTER) {
                text += key;
            } else if (keyCode == ENTER) {
                focused = false;
                saveText();
            }
        }
    }

    // Method to check if the mouse is over the text box
    private boolean isMouseOver(PApplet applet) {
        return isActive && applet.mouseX > x && applet.mouseX < x + width && applet.mouseY > y && applet.mouseY < y + height;
    }

    // Method to handle mouse press events
    public boolean mousePressed(PApplet applet) {
        if (isMouseOver(applet)) {
            setFocused(true);
            return true;
        } else {
            setFocused(false);
            return false;
        }
    }

    // Method to get the current text in the text box
    public String getText() {
        return text;
    }

    // Method to set the text in the text box
    public void setText(String text) {
        this.text = text;
    }

    // Method to set the active state of the text box
    public void setActive(boolean active) {
        this.isActive = active;
    }

    // Method to check if the text box is focused
    public boolean isFocused() {
        return focused;
    }

    // Method to set the focused state of the text box
    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    // Method to get the saved text
    public String getSavedText() {
        return savedText;
    }

    // Method to save the current text
    private void saveText() {
        this.savedText = text;
    }

    // Method to get the name of the text box
    public Object getName() { return this.name; }
}