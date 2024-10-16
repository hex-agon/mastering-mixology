package work.fking.masteringmixology;

import net.runelite.api.widgets.WidgetItem;
import net.runelite.api.ItemID;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import static work.fking.masteringmixology.PotionComponent.AGA;
import static work.fking.masteringmixology.PotionComponent.LYE;
import static work.fking.masteringmixology.PotionComponent.MOX;

public class InventoryPotionOverlay extends WidgetItemOverlay {
    private static final int MODIFIED_POTION_ID_DIFF = ItemID.MIXALOT_30030 - ItemID.MIXALOT;
    private final HashMap<PotionType, BufferedImage> imageCache = new HashMap<>();
    private final MasteringMixologyPlugin plugin;
    private final MasteringMixologyConfig config;

    @Inject
    InventoryPotionOverlay(MasteringMixologyPlugin plugin, MasteringMixologyConfig config) {
        this.plugin = plugin;
        this.config = config;
        showOnInventory();
    }

    public void clearCache() {
        imageCache.clear();
    }

    @Override
    public void renderItemOverlay(Graphics2D graphics2D, int itemId, WidgetItem widgetItem) {
        if (!plugin.isInLab() || config.inventoryPotionTagType() == InventoryPotionTagType.NONE) {
            return;
        }

        var potion = PotionType.fromItemId(itemId <= ItemID.MIXALOT ? itemId : itemId - MODIFIED_POTION_ID_DIFF);

        if (potion == null) {
            return;
        }

        if (!imageCache.containsKey(potion)) {
            imageCache.put(potion, createRecipeImage(potion));
        }

        var bounds = widgetItem.getCanvasBounds();
        graphics2D.drawImage(imageCache.get(potion), bounds.x, bounds.y, null);
    }

    private BufferedImage createRecipeImage(PotionType potion) {
        // Font measurements come from: graphics2D.getFontMetrics(FontManager.getRunescapeSmallFont())
        var image = new BufferedImage(25, 13, BufferedImage.TYPE_INT_ARGB);
        var graphics2D = image.createGraphics();
        drawRecipe(graphics2D, potion, 1, 13, Color.BLACK); // Drop shadow

        if (config.inventoryPotionTagType() == InventoryPotionTagType.COLORED) {
            drawRecipe(graphics2D, potion, 0, 12, null);
            return image;
        }

        drawRecipe(graphics2D, potion, 0, 12, Color.WHITE);
        return image;
    }

    private void drawRecipe(Graphics2D graphics2D, PotionType potion, int x, int y, @Nullable Color color) {
        graphics2D.setFont(FontManager.getRunescapeSmallFont());

        if (color != null) {
            graphics2D.setColor(color);
            graphics2D.drawString(potion.abbreviation(), x, y);
            return;
        }

        for (var component : potion.components()) {
            if (component == MOX) {
                graphics2D.setColor(Color.decode("#" + MOX.color()));
                graphics2D.drawString("M", x, y);
                x += 8;
            } else if (component == AGA) {
                graphics2D.setColor(Color.decode("#" + AGA.color()));
                graphics2D.drawString("A", x, y);
                x += 7;
            } else if (component == LYE) {
                graphics2D.setColor(Color.decode("#" + LYE.color()));
                graphics2D.drawString("L", x, y);
                x += 5;
            }
        }
    }
}
