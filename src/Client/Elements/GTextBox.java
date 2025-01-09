package Client.Elements;

import processing.core.PApplet;

import static processing.core.PConstants.*;

public class GTextBox {
    private float x, y, width, height;
    private String text = "";
    private String savedText = "";
    private int backgroundColor, focusBackgroundColor, hoverBackgroundColor, textColor, borderColor;
    private boolean isActive;
    private boolean focused = false;

    public GTextBox(String name, float x, float y, float width, float height, int backgroundColor, int focusBackgroundColor, int hoverBackgroundColor, int textColor, int borderColor, boolean isActive) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.backgroundColor = backgroundColor;
        this.focusBackgroundColor = focusBackgroundColor;
        this.hoverBackgroundColor = hoverBackgroundColor; // New hover color
        this.textColor = textColor;
        this.borderColor = borderColor;
        this.isActive = isActive;
    }

    public void draw(PApplet applet) {
        // Check if mouse is over the text box
        boolean isMouseOver = isMouseOver(applet);

        // Set background color based on focus and hover state
        if (focused) {
            applet.fill(focusBackgroundColor);
        } else if (isMouseOver) {
            applet.fill(hoverBackgroundColor); // Use hover color
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
            if (keyCode == BACKSPACE && text.length() > 0) {
                text = text.substring(0, text.length() - 1);
                System.out.println("Text after BACKSPACE: " + text); // Debug
            } else if (key != CODED && keyCode != ENTER) {
                text += key;
                System.out.println("Text after key press: " + text); // Debug
            } else if (keyCode == ENTER) {
                focused = false;
                saveText();
                System.out.println("Textbox unfocused, saved text: " + savedText); // Debug
            }
        }
    }



    // New method to check if the mouse is over the text box
    private boolean isMouseOver(PApplet applet) {
        return isActive && applet.mouseX > x && applet.mouseX < x + width && applet.mouseY > y && applet.mouseY < y + height;
    }

    public boolean mousePressed(float mouseX, float mouseY, PApplet applet) {
        boolean inBounds = isMouseOver(applet);
        focused = inBounds;
        return inBounds;
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