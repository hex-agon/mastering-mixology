package work.fking.masteringmixology.ui;

import java.awt.Color;

public class TextElement {
    private final String text;
    private final Color color;

    public TextElement(String text, Color color) {
        this.text = text;
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public Color getColor() {
        return color;
    }
}
