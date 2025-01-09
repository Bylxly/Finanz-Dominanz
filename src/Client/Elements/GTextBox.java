package Client.Elements;

import processing.core.PApplet;

import static processing.core.PConstants.*;

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


    public boolean mousePressed(float mouseX, float mouseY) {
        boolean inBounds = isActive && mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height;
        if (inBounds) {
            focused = true;
            System.out.println("Textbox focused: " + text); // Debug
        } else {
            focused = false;
        }
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
