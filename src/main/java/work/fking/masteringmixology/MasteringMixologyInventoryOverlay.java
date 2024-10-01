package work.fking.masteringmixology;

import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

class MasteringMixologyInventoryOverlay extends WidgetItemOverlay
{
    private final ItemManager itemManager;
    private final MasteringMixologyPlugin plugin;

    @Inject
    private MasteringMixologyInventoryOverlay(ItemManager itemManager, MasteringMixologyPlugin plugin, MasteringMixologyConfig config)
    {
        this.itemManager = itemManager;
        this.plugin = plugin;
        showOnInventory();
    }

    @Override
    public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem widgetItem)
    {
        final Color color = plugin.highlightedItems().get(itemId);
        if (color == null)
        {
            return;
        }

        Rectangle bounds = widgetItem.getCanvasBounds();
        final BufferedImage outline = itemManager.getItemOutline(itemId, widgetItem.getQuantity(), color);
        graphics.drawImage(outline, (int) bounds.getX(), (int) bounds.getY(), null);
    }
}
