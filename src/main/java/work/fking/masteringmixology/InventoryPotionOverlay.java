package work.fking.masteringmixology;

import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;

import javax.annotation.Nullable;
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
        if (!plugin.isInLab() || config.inventoryPotionTagType() == InventoryPotionTagType.NONE) {
            return;
        }

        var potion = PotionType.fromItemId(itemId);

        if (potion == null) {
            return;
        }

        var bounds = widgetItem.getCanvasBounds();
        var x = bounds.x;
        var y = bounds.y + 13;

        drawRecipe(graphics2D, potion, x + 1, y + 1, Color.BLACK); // Drop shadow

        if (config.inventoryPotionTagType() == InventoryPotionTagType.COLORED) {
            drawRecipe(graphics2D, potion, x, y, null);
            return;
        }

        drawRecipe(graphics2D, potion, x, y, Color.WHITE);
    }

    private void drawRecipe(Graphics2D graphics2D, PotionType potion, int x, int y, @Nullable Color color) {
        graphics2D.setFont(FontManager.getRunescapeSmallFont());

        if (color != null) {
            graphics2D.setColor(color);
            graphics2D.drawString(potion.abbreviation(), x, y);
            return;
        }

        for (var component : potion.components()) {
            graphics2D.setColor(component.color());
            graphics2D.drawString(String.valueOf(component.character()), x, y);
            x += graphics2D.getFontMetrics().charWidth(component.character());
        }
    }
}
