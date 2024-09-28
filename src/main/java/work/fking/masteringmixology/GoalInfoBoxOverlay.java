package work.fking.masteringmixology;

import net.runelite.api.Client;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.components.ComponentOrientation;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.ProgressBarComponent;
import net.runelite.client.ui.overlay.components.SplitComponent;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.QuantityFormatter;

import javax.inject.Inject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

class GoalInfoBoxOverlay extends OverlayPanel {
    private static final int BORDER_SIZE = 3;
    private static final int VERTICAL_GAP = 2;
    private static final int ICON_AND_GOAL_GAP = 5;
    private static final Rectangle TOP_PANEL_BORDER = new Rectangle(2, 0, 4, 4);
    private static final int COMPONENT_SPRITE_SIZE = 16;

    private final Client client;
    private final MasteringMixologyPlugin plugin;
    private final MasteringMixologyConfig config;
    private final ItemManager itemManager;
    private final SpriteManager spriteManager;

    private final PanelComponent topPanel = new PanelComponent();

    private final EnumMap<PotionComponent, ComponentData> componentDataMap = new EnumMap<>(PotionComponent.class);
    private double overallProgress = 0.0;
    private RewardItem rewardItem;

    private final Map<Integer, BufferedImage> rewardIconCache = new HashMap<>();
    private final EnumMap<PotionComponent, BufferedImage> componentSpriteCache = new EnumMap<>(PotionComponent.class);

    private boolean dataDirty = true;

    @Inject
    GoalInfoBoxOverlay(Client client, MasteringMixologyPlugin plugin, MasteringMixologyConfig config,
                       ItemManager itemManager, SpriteManager spriteManager) {
        super(plugin);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.itemManager = itemManager;
        this.spriteManager = spriteManager;

        panelComponent.setBorder(new Rectangle(BORDER_SIZE, BORDER_SIZE, BORDER_SIZE, BORDER_SIZE));
        panelComponent.setGap(new Point(0, VERTICAL_GAP));
        topPanel.setBorder(TOP_PANEL_BORDER);
        topPanel.setBackgroundColor(null);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        // Recalculate the data before checking if the overlay should be displayed
        if (dataDirty) {
            recalculateData();
        }

        if (rewardItem == RewardItem.NONE || !plugin.isInLabRegion()) {
            return null;
        }

        // Clear the panel components
        topPanel.getChildren().clear();

        // Set the font for the panel
        graphics.setFont(FontManager.getRunescapeSmallFont());

        // Build the top display with the reward item and overall progress
        final LineComponent topLine = LineComponent.builder()
                .left(rewardItem.itemName())
                .leftFont(FontManager.getRunescapeFont())
                .build();

        final LineComponent bottomLine = LineComponent.builder()
                .left("Progress:")
                .leftFont(FontManager.getRunescapeFont())
                .right((int) (overallProgress * 100) + "%")
                .rightFont(FontManager.getRunescapeFont())
                .rightColor(overallProgress >= 1 ? Color.GREEN : Color.WHITE)
                .build();

        final SplitComponent textSplit = SplitComponent.builder()
                .first(topLine)
                .second(bottomLine)
                .orientation(ComponentOrientation.VERTICAL)
                .build();

        final ImageComponent rewardImageComponent = new ImageComponent(getRewardImage());
        final SplitComponent topInfoSplit = SplitComponent.builder()
                .first(rewardImageComponent)
                .second(textSplit)
                .orientation(ComponentOrientation.HORIZONTAL)
                .gap(new Point(ICON_AND_GOAL_GAP, 0))
                .build();

        topPanel.getChildren().add(topInfoSplit);
        panelComponent.getChildren().add(topPanel);

        if (config.showResinProgressBars()) {
            // Add the progress bars in order: MOX, AGA, LYE, PotionComponent order is not the same
            createProgressBar(PotionComponent.MOX);
            createProgressBar(PotionComponent.AGA);
            createProgressBar(PotionComponent.LYE);
        }

        return super.render(graphics);
    }

    public void markDataAsDirty() {
        this.dataDirty = true;
    }

    private void recalculateData() {
        rewardItem = config.selectedReward();

        double totalPercentage = 0.0;
        totalPercentage += getAndCalculateComponentData(PotionComponent.MOX, MasteringMixologyPlugin.VARP_MOX_RESIN);
        totalPercentage += getAndCalculateComponentData(PotionComponent.AGA, MasteringMixologyPlugin.VARP_AGA_RESIN);
        totalPercentage += getAndCalculateComponentData(PotionComponent.LYE, MasteringMixologyPlugin.VARP_LYE_RESIN);
        overallProgress = totalPercentage / PotionComponent.values().length;

        dataDirty = false;
    }

    private double getAndCalculateComponentData(PotionComponent component, int varp) {
        int currentAmount = client.getVarpValue(varp);
        int goalAmount = rewardItem.componentCost(component);
        double percentage = goalAmount == 0 ? 1.0 : Math.min((double) currentAmount / goalAmount, 1.0);

        ComponentData data = new ComponentData(currentAmount, goalAmount, percentage);
        componentDataMap.put(component, data);

        return percentage;
    }

    private void createProgressBar(PotionComponent component) {
        ComponentData data = componentDataMap.get(component);

        final ImageComponent imageComponent = new ImageComponent(getComponentSprite(component));
        final ProgressBarComponent progressBarComponent = new ProgressBarComponent();
        progressBarComponent.setForegroundColor(Color.decode("#" + component.color()));
        progressBarComponent.setBackgroundColor(new Color(61, 56, 49));
        progressBarComponent.setValue(data.percentage * 100d);
        progressBarComponent.setLeftLabel(QuantityFormatter.quantityToStackSize(data.currentAmount));
        progressBarComponent.setRightLabel(QuantityFormatter.quantityToStackSize(data.goalAmount));

        final SplitComponent progressBarSplit = SplitComponent.builder()
                .first(imageComponent)
                .second(progressBarComponent)
                .orientation(ComponentOrientation.HORIZONTAL)
                .gap(new Point(ICON_AND_GOAL_GAP, 0))
                .build();

        panelComponent.getChildren().add(progressBarSplit);
    }

    private BufferedImage getRewardImage() {
        return rewardIconCache.computeIfAbsent(rewardItem.itemId(), itemManager::getImage);
    }

    private BufferedImage getComponentSprite(PotionComponent component) {
        return componentSpriteCache.computeIfAbsent(component, comp -> {
            BufferedImage sprite = spriteManager.getSprite(comp.spriteId(), 0);
            if (sprite != null) {
                BufferedImage resizedImage = ImageUtil.resizeImage(sprite, COMPONENT_SPRITE_SIZE, COMPONENT_SPRITE_SIZE, true);
                return ImageUtil.resizeCanvas(resizedImage, COMPONENT_SPRITE_SIZE, COMPONENT_SPRITE_SIZE);
            }
            return null;
        });
    }

    private static class ComponentData {
        final int currentAmount;
        final int goalAmount;
        final double percentage;

        ComponentData(int currentAmount, int goalAmount, double percentage) {
            this.currentAmount = currentAmount;
            this.goalAmount = goalAmount;
            this.percentage = percentage;
        }
    }
}
