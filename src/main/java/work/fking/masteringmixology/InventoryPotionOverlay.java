package work.fking.masteringmixology;

import net.runelite.api.Client;
import net.runelite.api.SpritePixels;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;

import javax.inject.Inject;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import static work.fking.masteringmixology.PotionComponent.AGA;
import static work.fking.masteringmixology.PotionComponent.LYE;
import static work.fking.masteringmixology.PotionComponent.MOX;

public class InventoryPotionOverlay extends WidgetItemOverlay {
    private static final int RETORT_SPRITE_ID = 5672;
    private static final int ALEMBIC_SPRITE_ID = 5673;
    private static final int AGITATOR_SPRITE_ID = 5674;
    private static final int DIGWEED_ITEM_ID = 30031;

    private final MasteringMixologyPlugin plugin;
    private final MasteringMixologyConfig config;
    private final HashMap<Potion, BufferedImage> potionOverlays = new HashMap<>();

    @Inject
    private SpriteManager spriteManager;

    @Inject
    private ItemManager itemManager;

    @Inject
    private Client client;

    @Inject
    InventoryPotionOverlay(MasteringMixologyPlugin plugin, MasteringMixologyConfig config) {
        this.plugin = plugin;
        this.config = config;
        showOnInventory();
    }

    private void drawRecipe(Graphics2D g, char[] recipeChars, int x, int y, boolean isBlack) {
        g.setFont(FontManager.getRunescapeSmallFont());
        g.setColor(Color.BLACK);

        for (char recipeChar : recipeChars) {
            if (recipeChar == 'M') {
                if (!isBlack) {
                    g.setColor(Color.decode("#" + MOX.color()));
                }

                g.drawString("M", x, y);
                x += 8;
            } else if (recipeChar == 'A') {
                if (!isBlack) {
                    g.setColor(Color.decode("#" + AGA.color()));
                }

                g.drawString("A", x, y);
                x += 7;
            } else if (recipeChar == 'L') {
                if (!isBlack) {
                    g.setColor(Color.decode("#" + LYE.color()));
                }

                g.drawString("L", x, y);
                x += 5;
            }
        }
    }

    private BufferedImage drawPotionOverlay(Potion potion) {
        var image = new BufferedImage(36, 32, BufferedImage.TYPE_INT_ARGB);
        var graphics2D = image.createGraphics();
        var x = 0;
        var y = 10;
        var recipeChars = potion.type.recipe().toCharArray();

        drawRecipe(graphics2D, recipeChars, x + 1, y + 1, true); // Drop shadow
        drawRecipe(graphics2D, recipeChars, x, y, false);

        if (potion.isModified) {
            if (potion.modifier == null) {
                graphics2D.setFont(FontManager.getRunescapeBoldFont());
                graphics2D.setColor(Color.BLACK);
                graphics2D.drawString("?", x + 4, y + 18); // Drop shadow
                graphics2D.setColor(Color.WHITE);
                graphics2D.drawString("?", x + 3, y + 17);
            } else if (potion.modifier == PotionModifier.CONCENTRATED) {
                graphics2D.drawImage(spriteManager.getSprite(RETORT_SPRITE_ID, 0), x, y, null);
            } else if (potion.modifier == PotionModifier.HOMOGENOUS) {
                graphics2D.drawImage(spriteManager.getSprite(ALEMBIC_SPRITE_ID, 0), x, y, null);
            } else if (potion.modifier == PotionModifier.CRYSTALISED) {
                graphics2D.drawImage(spriteManager.getSprite(AGITATOR_SPRITE_ID, 0), x, y, null);
            }
        }

        if (!potion.hasDigweed) {
            return image;
        }

        var pixels = client.createItemSprite(DIGWEED_ITEM_ID, 1, 1, SpritePixels.DEFAULT_SHADOW_COLOR, 0, false, 384);

        if (pixels == null) {
            return image;
        }

        var sprite = pixels.toBufferedImage();
        graphics2D.drawImage(sprite, x + 5, y - 5, null);
        return image;
    }

    @Override
    public void renderItemOverlay(Graphics2D graphics2D, int itemId, WidgetItem widgetItem) {
        var potionType = PotionType.fromItemId(30020 < itemId ? itemId - 10: itemId);
        if (!plugin.isInLab() || !config.identifyPotions() || potionType == null) {
            return;
        }

        var potion = plugin.getNextPotion();

        if (potion == null || potionType != potion.type) {
            plugin.syncInventoryPotionsAtEndOfFrame();
            return;
        }

        if (!potionOverlays.containsKey(potion)) {
            potionOverlays.put(potion, drawPotionOverlay(potion));
        }

        var image = potionOverlays.get(potion);
        var bounds = widgetItem.getCanvasBounds();

        graphics2D.drawImage(image, bounds.x, bounds.y, null);
    }
}
