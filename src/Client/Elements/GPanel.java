package Client.Elements;

import processing.core.PApplet;
import java.util.ArrayList;
import java.util.List;

public class GPanel {
    private List<TextItem> textItems;
    private float x, y; // Position of the panel
    private float width, height; // Size of the panel
    private PApplet parent; // Reference to the PApplet
    private int backgroundColor; // Background color of the panel

    public GPanel(PApplet parent, float x, float y, float width, float height, int backgroundColor) {
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.backgroundColor = backgroundColor;
        this.textItems = new ArrayList<>();
    }

    public void addText(String text, int color, boolean bold, boolean italic) {
        String[] lines = text.split("/n");
        for (String line : lines) {
            TextItem textItem = new TextItem(line.trim(), color, bold, italic);
            textItems.add(textItem);
        }
    }

    public void removeText(int index) {
        if (index >= 0 && index < textItems.size()) {
            textItems.remove(index);
        }
    }

    public void clearText() {
        textItems.clear();
    }

    public void updateText(int index, String newText) {
        if (index >= 0 && index < textItems.size()) {
            textItems.get(index).setText(newText);
        }
    }

    public void display() {
        parent.fill(backgroundColor);
        parent.rect(x, y, width, height);

        float textY = y + 20; // Starting Y position for text
        for (TextItem item : textItems) {
            item.display(x + (width/2), textY);
            textY += item.getHeight() + 5; // Move down for the next item
        }
    }

    private class TextItem {
        private String text;
        private int color;
        private boolean bold;
        private boolean italic;

        public TextItem(String text, int color, boolean bold, boolean italic) {
            this.text = text;
            this.color = color;
            this.bold = bold;
            this.italic = italic;
        }

        public void setText(String newText) {
            this.text = newText;
        }

        public float getHeight() {
            return (bold ? 16 : 12) + (italic ? 2 : 0); // Adjust height based on style
        }

        public void display(float x, float y) {
            parent.fill(color);
            parent.textSize(bold ? 16 : 12);
            if (italic) {
                parent.textFont(parent.createFont("Arial", bold ? 16 : 12, true)); // Italic font
            } else {
                parent.textFont(parent.createFont("Arial", bold ? 16 : 12, false)); // Regular font
            }
            parent.text(text, x, y);
        }
    }
}