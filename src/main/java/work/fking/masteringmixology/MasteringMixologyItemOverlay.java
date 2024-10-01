package work.fking.masteringmixology;

import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.ui.overlay.WidgetItemOverlay;

import javax.inject.Inject;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Collections;

public class MasteringMixologyItemOverlay extends WidgetItemOverlay {

    private final MasteringMixologyPlugin plugin;

    @Inject
    protected MasteringMixologyItemOverlay(MasteringMixologyPlugin plugin) {
        this.plugin = plugin;
        showOnInventory();
    }

    @Override
    public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem widgetItem) {

        var requiredModifiers = plugin.getRequiredModifiers(itemId);
        if (requiredModifiers.isEmpty()) { return; }

        Collections.sort(requiredModifiers);

        Rectangle bounds = widgetItem.getCanvasBounds();
        float arcLength = 360f / requiredModifiers.size();

        for (int i = 0; i < requiredModifiers.size(); i++) {
            Color c = plugin.getHighlightColor(requiredModifiers.get(i));
            graphics.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()/3));
            graphics.fillArc((int) bounds.getX(), (int) bounds.getY(), (int) bounds.getWidth(), (int) bounds.getHeight(), (int) (i*arcLength), (int) arcLength);
        }
    }
}
