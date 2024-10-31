package work.fking.masteringmixology;

import net.runelite.api.Client;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
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
import java.text.DecimalFormat;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

class GoalInfoBoxOverlay extends OverlayPanel {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.0");
    private static final int BORDER_SIZE = 3;
    private static final int VERTICAL_GAP = 2;
    private static final int ICON_AND_GOAL_GAP = 5;
    private static final Rectangle TOP_PANEL_BORDER = new Rectangle(2, 0, 4, 4);
    private static final int COMPONENT_SPRITE_SIZE = 16;
    private static final Color PROGRESS_BAR_BACKGROUND_COLOR = new Color(61, 56, 49);

    private final Client client;
    private final MasteringMixologyPlugin plugin;
    private final MasteringMixologyConfig config;
    private final ItemManager itemManager;
    private final SpriteManager spriteManager;

    private final PanelComponent topPanel = new PanelComponent();

    private final EnumMap<PotionComponent, ComponentData> componentDataMap = new EnumMap<>(PotionComponent.class);
    private double overallProgress = 0.0;
    private int itemsAffordable = 0;
    private RewardItem rewardItem;
    private int rewardQuantity = 1;

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

        setPosition(OverlayPosition.TOP_RIGHT);
        setPriority(PRIORITY_MED);
        panelComponent.setPreferredSize(new Dimension(250, 0));
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

        topPanel.getChildren().clear();
        graphics.setFont(FontManager.getRunescapeSmallFont());

        // Build the top display with the affordable amount / goal amount
        String goalAmountText = "";
        if (rewardItem.isRepeatable() && rewardQuantity > 1) {
            goalAmountText = QuantityFormatter.quantityToStackSize(itemsAffordable) + "/" + QuantityFormatter.quantityToStackSize(rewardQuantity);
        }

        final LineComponent topLine = LineComponent.builder()
                .left(rewardItem.itemName())
                .leftFont(FontManager.getRunescapeFont())
                .right(goalAmountText)
                .rightFont(FontManager.getRunescapeBoldFont())
                .build();

        // Build the bottom line with the overall progress percentage
        final LineComponent bottomLine = LineComponent.builder()
                .left("Progress:")
                .leftFont(FontManager.getRunescapeFont())
                .right(DECIMAL_FORMAT.format(overallProgress * 100) + "%")
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

        // Add a progress bar for each component
        if (config.showResinBars()) {
            for (PotionComponent component : PotionComponent.values()) {
                createProgressBar(component);
            }
        }

        return super.render(graphics);
    }

    private void createProgressBar(PotionComponent component) {
        ComponentData data = componentDataMap.get(component);

        final ImageComponent imageComponent = new ImageComponent(getComponentSprite(component));
        final ProgressBarComponent progressBarComponent = new ProgressBarComponent();
        progressBarComponent.setForegroundColor(component.color());
        progressBarComponent.setBackgroundColor(PROGRESS_BAR_BACKGROUND_COLOR);
        progressBarComponent.setValue(data.percentageToGoal * 100);
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
                // Resize and center the sprite
                BufferedImage resizedImage = ImageUtil.resizeImage(sprite, COMPONENT_SPRITE_SIZE, COMPONENT_SPRITE_SIZE, true);
                return ImageUtil.resizeCanvas(resizedImage, COMPONENT_SPRITE_SIZE, COMPONENT_SPRITE_SIZE);
            }
            return null;
        });
    }

    public void markDataAsDirty() {
        this.dataDirty = true;
    }

    private void recalculateData() {
        rewardItem = config.selectedReward();
        rewardQuantity = rewardItem.isRepeatable() ? config.rewardQuantity() : 1;

        // Create the component data for each component
        for (PotionComponent component : PotionComponent.values()) {
            int currentAmount = client.getVarpValue(component.resinVarpId());
            int baseGoalAmount = rewardItem.componentCost(component);
            componentDataMap.put(component, new ComponentData(currentAmount, baseGoalAmount, rewardQuantity));
        }

        // Calculate the amount of items affordable based on the component with the lowest affordable amount
        int minAffordable = componentDataMap.values().stream()
                .mapToInt(data -> data.affordableAmount)
                .min()
                .orElse(0);
        itemsAffordable = Math.min(minAffordable, rewardQuantity);

        // Overall progress is the average of all component progress
        overallProgress = componentDataMap.values().stream()
                .mapToDouble(data -> data.percentageToGoal)
                .average()
                .orElse(0.0);

        dataDirty = false;
    }

    private static class ComponentData {
        final int currentAmount;
        final int goalAmount;
        final double percentageToGoal;
        final int affordableAmount;

        ComponentData(int currentAmount, int baseGoalAmount, int rewardQuantity) {
            this.currentAmount = currentAmount;
            this.goalAmount = baseGoalAmount * rewardQuantity;

            if (goalAmount == 0) {
                this.percentageToGoal = 1.0;
                this.affordableAmount = rewardQuantity;
            } else {
                this.percentageToGoal = Math.min((double) currentAmount / goalAmount, 1.0);
                this.affordableAmount = currentAmount / baseGoalAmount;
            }
        }
    }
}
