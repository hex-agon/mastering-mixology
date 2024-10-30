package work.fking.masteringmixology;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;

public class MasteringMixologyOverlay extends Overlay {

    private final Client client;
    private final MasteringMixologyPlugin plugin;
    private final ModelOutlineRenderer modelOutlineRenderer;

    @Inject
    MasteringMixologyOverlay(Client client, MasteringMixologyPlugin plugin, ModelOutlineRenderer modelOutlineRenderer) {
        this.client = client;
        this.plugin = plugin;
        this.modelOutlineRenderer = modelOutlineRenderer;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        for (var highlightedObject : plugin.highlightedObjects().values()) {
            modelOutlineRenderer.drawOutline(highlightedObject.object(), highlightedObject.outlineWidth(), highlightedObject.color(), highlightedObject.feather());
            drawText(graphics, highlightedObject);
        }
        return null;
    }

    private void drawText(Graphics2D graphics, MasteringMixologyPlugin.HighlightedObject highlightedObject) {
        String text = highlightedObject.text();
        if (text == null || text.isEmpty()) {
            return;
        }

        // Increase the font size
        Font font = FontManager.getRunescapeFont().deriveFont(20f);
        Font originalFont = graphics.getFont();
        graphics.setFont(font);

        // Draw the text
        LocalPoint textLocation = highlightedObject.object().getLocalLocation();
        // Parse </br> in the text and split it into multiple lines
        int zOffset = 100;
        for (String line : text.split("</br>")) {
            Point canvasLocation = Perspective.getCanvasTextLocation(client, graphics, textLocation, line, zOffset);
            OverlayUtil.renderTextLocation(graphics, canvasLocation, line, highlightedObject.color());

            // Decrease the zOffset with font height + 5 to draw the next line below the previous one
            zOffset -= graphics.getFontMetrics().getHeight() + 5;
        }

        // Set the font back to the original
        graphics.setFont(originalFont);
    }
}
