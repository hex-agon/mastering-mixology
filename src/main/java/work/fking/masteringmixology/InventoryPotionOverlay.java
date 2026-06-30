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
        if (!plugin.isInLab()) {
            return;
        }

        var potion = PotionType.fromItemId(itemId);

        if (potion == null) {
            return;
        }

        var bounds = widgetItem.getCanvasBounds();

        // Recipe tag (bottom-left) — unchanged.
        if (config.inventoryPotionTagType() != InventoryPotionTagType.NONE) {
            var x = bounds.x + 5;
            var y = bounds.y + 30;

            drawRecipe(graphics2D, potion, x + 1, y + 1, Color.BLACK); // Drop shadow

            if (config.inventoryPotionTagType() == InventoryPotionTagType.COLORED) {
                drawRecipe(graphics2D, potion, x, y, null);
            } else {
                drawRecipe(graphics2D, potion, x, y, Color.WHITE);
            }
        }

        // Station tag (top-left) — only on finished potions we've tagged with a station.
        if (config.showStationTags()) {
            var widget = widgetItem.getWidget();
            if (widget != null) {
                var modifier = plugin.stationTag(widget.getIndex());
                if (modifier != null) {
                    drawStationTag(graphics2D, modifier, bounds.x + 1, bounds.y + 10);
                }
            }
        }
    }

    private void drawStationTag(Graphics2D graphics2D, PotionModifier modifier, int x, int y) {
        String text = stationLabel(modifier);
        graphics2D.setFont(FontManager.getRunescapeSmallFont());
        graphics2D.setColor(Color.BLACK);
        graphics2D.drawString(text, x + 1, y + 1); // Drop shadow
        graphics2D.setColor(stationColor(modifier));
        graphics2D.drawString(text, x, y);
    }

    private static String stationLabel(PotionModifier modifier) {
        switch (modifier) {
            case HOMOGENOUS:   return "Agi";
            case CONCENTRATED: return "Ret";
            case CRYSTALISED:  return "Ale";
            default:           return "";
        }
    }

    private static Color stationColor(PotionModifier modifier) {
        switch (modifier) {
            case HOMOGENOUS:   return new Color(120, 230, 120); // green
            case CONCENTRATED: return new Color(255, 170, 90);  // orange
            case CRYSTALISED:  return new Color(120, 200, 255); // cyan
            default:           return Color.WHITE;
        }
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
