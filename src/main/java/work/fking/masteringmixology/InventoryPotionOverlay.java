package work.fking.masteringmixology;

import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;

import javax.inject.Inject;
import java.awt.Color;
import java.awt.Graphics2D;

public class InventoryPotionOverlay extends WidgetItemOverlay {

    private final MasteringMixologyPlugin plugin;
    private final MasteringMixologyConfig config;

    @Inject
    InventoryPotionOverlay(MasteringMixologyPlugin plugin, MasteringMixologyConfig config) {
        this.plugin = plugin;
        this.config = config;
        showOnInventory();
    }

    @Override
    public void renderItemOverlay(Graphics2D graphics2D, int itemId, WidgetItem widgetItem) {
        if (!plugin.isInLab() || !config.identifyPotions()) {
            return;
        }

        var potion = PotionType.fromId(itemId);
        if (potion == null) {
            return;
        }

        var bounds = widgetItem.getCanvasBounds();
        graphics2D.setFont(FontManager.getRunescapeSmallFont());

        graphics2D.setColor(Color.WHITE);
        graphics2D.drawString(potion.abbreviation(), bounds.x - 1, bounds.y + 15);
    }
}
