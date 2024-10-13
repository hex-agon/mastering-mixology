package work.fking.masteringmixology;

import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;

import javax.inject.Inject;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static work.fking.masteringmixology.PotionComponent.AGA;
import static work.fking.masteringmixology.PotionComponent.LYE;
import static work.fking.masteringmixology.PotionComponent.MOX;

public class InventoryPotionOverlay extends WidgetItemOverlay {
    private final MasteringMixologyPlugin plugin;
    private final MasteringMixologyConfig config;
    private final Logger logger = LoggerFactory.getLogger(InventoryPotionOverlay.class);
    private BufferedImage retortSprite;
    private BufferedImage alembicSprite;
    private BufferedImage agitatorSprite;
    private boolean isInitialized;

    @Inject
    private SpriteManager spriteManager;

    @Inject
    private ItemManager itemManager;

    @Inject
    InventoryPotionOverlay(MasteringMixologyPlugin plugin, MasteringMixologyConfig config) {
        this.plugin = plugin;
        this.config = config;
        showOnInventory();
    }

    private void initialize() {
        retortSprite = spriteManager.getSprite(5672, 0);
        alembicSprite = spriteManager.getSprite(5673, 0);
        agitatorSprite = spriteManager.getSprite(5674, 0);
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

    @Override
    public void renderItemOverlay(Graphics2D graphics2D, int itemId, WidgetItem widgetItem) {
        if (!plugin.isInLab() || !config.identifyPotions()) {
            return;
        }

        if (!isInitialized) {
            initialize();
        }

        if (PotionType.fromItemId(30020 < itemId ? itemId - 10: itemId) == null) {
            return;
        }

        var potion = plugin.getNextPotion();

        if (potion == null) {
            logger.warn("Could not get next potion");
            return;
        }

        var bounds = widgetItem.getCanvasBounds();
        var x = bounds.x;
        var y = bounds.y + 10;
        var recipeChars = potion.type.recipe().toCharArray();

        drawRecipe(graphics2D, recipeChars, x + 1, y + 1, true);
        drawRecipe(graphics2D, recipeChars, x, y, false);

        if (!potion.isModified) {
            return;
        }

        if (potion.modifier == null) {
            graphics2D.setFont(FontManager.getRunescapeBoldFont());
            graphics2D.setColor(Color.BLACK);
            graphics2D.drawString("?", x + 4, y + 18);
            graphics2D.setColor(Color.WHITE);
            graphics2D.drawString("?", x + 3, y + 17);
            return;
        }

        switch (potion.modifier.alchemyObject()) {
            case RETORT:
                graphics2D.drawImage(retortSprite, x, y, null);
                break;
            case ALEMBIC:
                graphics2D.drawImage(alembicSprite, x, y, null);
                break;
            case AGITATOR:
                graphics2D.drawImage(agitatorSprite, x, y, null);
                break;
        }
    }
}
