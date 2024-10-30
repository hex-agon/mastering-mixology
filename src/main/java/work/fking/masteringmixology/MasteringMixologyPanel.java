package work.fking.masteringmixology;

import net.runelite.api.Client;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ComponentOrientation;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.components.LayoutableRenderableEntity;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.SplitComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MasteringMixologyPanel extends OverlayPanel {

    private final Client client;
    private final MasteringMixologyPlugin plugin;
    private final MasteringMixologyConfig config;
    private final SpriteManager spriteManager;
    private final ItemManager itemManager;
    private final Map<String, BufferedImage> spriteCache = new HashMap<>();

    @Inject
    public MasteringMixologyPanel(Client client, MasteringMixologyPlugin plugin, MasteringMixologyConfig config, SpriteManager spriteManager, ItemManager itemManager) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.spriteManager = spriteManager;
        this.itemManager = itemManager;

        setPosition(OverlayPosition.BOTTOM_LEFT);
        setMovable(true);

        panelComponent.setOrientation(ComponentOrientation.VERTICAL);
        panelComponent.setGap(new Point(0, 5));
        panelComponent.setBackgroundColor(config.panelBackgroundColor());
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        // Hide the overlay if conditions are not met
        if (!plugin.isInLabRegion() || !config.displayPanel() || !config.showPanelInLab() && plugin.isInLab()) {
            return null;
        }

        // Display Potion Orders if enabled
        if (config.displayOrdersInPanel()) {
            displayOrders();
        }

        // Display Resin and Paste amounts
        if (config.displayResinInPanel() || config.displayPasteInPanel()) {
            displayResinAndPaste();
        }

        return super.render(graphics);
    }

    public void updateBackgroundColor() {
        panelComponent.setBackgroundColor(config.panelBackgroundColor());
    }

    private void displayOrders() {
        // Title
        panelComponent.getChildren().add(TitleComponent.builder()
                .text("Potion Orders")
                .color(Color.ORANGE)
                .build());

        for (PotionOrder order : plugin.getPotionOrders()) {
            addPotionOrderComponent(order);
        }
    }

    private void addPotionOrderComponent(PotionOrder order) {
        BufferedImage modifierIcon = getScaledSprite(order.potionModifier().spriteId(), config.orderIconSize());
        String potionName = itemManager.getItemComposition(order.potionType().itemId()).getName();

        // Create text component for the potion order
        // We have to remove </col> from the recipe string since TextComponent doesn't do that
        String recipe = order.potionType().recipe().replaceAll("</col>", "");
        String text = potionName + " (" + recipe + "<col=ffffff>)";
        LineComponent lineComponent = LineComponent.builder()
                .left(text)
                .build();

        // Create ImageComponent for the icon
        ImageComponent iconComponent = new ImageComponent(modifierIcon);

        // Combine icon and text horizontally using SplitComponent
        SplitComponent orderComponent = SplitComponent.builder()
                .first(iconComponent)
                .second(lineComponent)
                .orientation(ComponentOrientation.HORIZONTAL)
                .gap(new Point(5, 0))
                .build();

        // Add to the panel
        panelComponent.getChildren().add(orderComponent);
    }

    private void displayResinAndPaste() {
        // Create components for each resin and paste
        LayoutableRenderableEntity moxComponent = createResinAndPasteComponent(PotionComponent.MOX, MasteringMixologyPlugin.VARBIT_MOX_PASTE, MasteringMixologyPlugin.VARP_MOX_RESIN);
        LayoutableRenderableEntity agaComponent = createResinAndPasteComponent(PotionComponent.AGA, MasteringMixologyPlugin.VARBIT_AGA_PASTE, MasteringMixologyPlugin.VARP_AGA_RESIN);
        LayoutableRenderableEntity lyeComponent = createResinAndPasteComponent(PotionComponent.LYE, MasteringMixologyPlugin.VARBIT_LYE_PASTE, MasteringMixologyPlugin.VARP_LYE_RESIN);

        // Combine them horizontally
        SplitComponent resinSplit = SplitComponent.builder()
                .first(moxComponent)
                .second(agaComponent)
                .orientation(ComponentOrientation.HORIZONTAL)
                .gap(new Point(10, 0))
                .build();

        resinSplit = SplitComponent.builder()
                .first(resinSplit)
                .second(lyeComponent)
                .orientation(ComponentOrientation.HORIZONTAL)
                .gap(new Point(10, 0))
                .build();

        panelComponent.getChildren().add(resinSplit);
    }

    private LayoutableRenderableEntity createResinAndPasteComponent(PotionComponent component, int pasteVarbitId, int resinVarpId) {
        List<LayoutableRenderableEntity> amountComponents = new ArrayList<>();

        Font font = getFont(config.resinFontSize());

        if (config.displayPasteInPanel()) {
            int pasteAmount = client.getVarbitValue(pasteVarbitId);
            LineComponent pasteTextComponent = LineComponent.builder()
                    .left(String.valueOf(pasteAmount))
                    .leftFont(font)
                    .build();
            amountComponents.add(pasteTextComponent);
        }

        if (config.displayResinInPanel()) {
            int resinAmount = client.getVarpValue(resinVarpId);
            LineComponent resinTextComponent = LineComponent.builder()
                    .left(String.valueOf(resinAmount))
                    .leftFont(font)
                    .leftColor(Color.decode("#" + component.color()))
                    .build();
            amountComponents.add(resinTextComponent);
        }

        // Combine amounts vertically
        LayoutableRenderableEntity amountsSplit = amountComponents.get(0);
        for (int i = 1; i < amountComponents.size(); i++) {
            amountsSplit = SplitComponent.builder()
                    .first(amountsSplit)
                    .second(amountComponents.get(i))
                    .orientation(ComponentOrientation.VERTICAL)
                    .gap(new Point(0, 2))
                    .build();
        }

        // Create ImageComponent for the icon
        BufferedImage componentIcon = getScaledSprite(component.spriteId(), config.resinIconSize());
        ImageComponent iconComponent = new ImageComponent(componentIcon);

        // Combine icon and amounts horizontally
        return SplitComponent.builder()
                .first(iconComponent)
                .second(amountsSplit)
                .orientation(ComponentOrientation.HORIZONTAL)
                .gap(new Point(5, 0))
                .build();
    }

    private BufferedImage getScaledSprite(int spriteId, int size) {
        String key = spriteId + "-" + size;
        return spriteCache.computeIfAbsent(key, k -> {
            BufferedImage sprite = spriteManager.getSprite(spriteId, 0);
            if (sprite != null) {
                return ImageUtil.resizeImage(sprite, size, size, true);
            }
            return null;
        });
    }

    private Font getFont(int size) {
        return FontManager.getRunescapeFont().deriveFont((float) size);
    }
}
