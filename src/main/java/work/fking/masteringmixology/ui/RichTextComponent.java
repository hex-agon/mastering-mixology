package work.fking.masteringmixology.ui;

import net.runelite.client.ui.overlay.components.LayoutableRenderableEntity;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

public class RichTextComponent implements LayoutableRenderableEntity {
    private final List<TextElement> textElements;
    private final Font font;
    private final boolean drawShadow;
    private Point preferredLocation = new Point();
    private final Rectangle bounds = new Rectangle();

    public RichTextComponent(List<TextElement> textElements, Font font, boolean drawShadow) {
        this.textElements = textElements;
        this.font = font;
        this.drawShadow = drawShadow;
    }

    public RichTextComponent(String text, Color color, Font font, boolean drawShadow) {
        this.textElements = List.of(new TextElement(text, color));
        this.font = font;
        this.drawShadow = drawShadow;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        Font originalFont = graphics.getFont();
        if (font != null) {
            graphics.setFont(font);
        }

        FontMetrics metrics = graphics.getFontMetrics();

        int x = preferredLocation.x;
        int y = preferredLocation.y + metrics.getAscent();

        for (TextElement element : textElements) {
            // Draw shadow if enabled
            if (drawShadow) {
                graphics.setColor(Color.BLACK);
                graphics.drawString(element.getText(), x + 1, y + 1);
            }

            // Draw text
            graphics.setColor(element.getColor());
            graphics.drawString(element.getText(), x, y);

            x += metrics.stringWidth(element.getText());
        }

        int width = x - preferredLocation.x;
        int height = metrics.getHeight();

        bounds.setLocation(preferredLocation);
        bounds.setSize(width, height);

        // Restore the original font
        if (font != null) {
            graphics.setFont(originalFont);
        }

        return new Dimension(width, height);
    }

    @Override
    public Rectangle getBounds() {
        return bounds;
    }

    @Override
    public void setPreferredSize(Dimension dimension) {
        // Not used
    }

    @Override
    public void setPreferredLocation(Point preferredLocation) {
        this.preferredLocation = preferredLocation;
    }
}
