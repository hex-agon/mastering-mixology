package work.fking.masteringmixology;

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

    private final MasteringMixologyPlugin plugin;
    private final MasteringMixologyConfig config;
    private final ItemManager itemManager;
    private final SpriteManager spriteManager;

    private final PanelComponent topPanel = new PanelComponent();

    private final EnumMap<PotionComponent, BufferedImage> componentSpriteCache = new EnumMap<>(PotionComponent.class);

    @Inject
    GoalInfoBoxOverlay(MasteringMixologyPlugin plugin, MasteringMixologyConfig config,
                       ItemManager itemManager, SpriteManager spriteManager) {
        super(plugin);
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
        Goal goal = plugin.getGoal();
        RewardItem rewardItem = goal.getRewardItem();
        if (rewardItem == RewardItem.NONE || !plugin.isInLabRegion()) {
            return null;
        }

        topPanel.getChildren().clear();
        graphics.setFont(FontManager.getRunescapeSmallFont());

        // Build the top display with the affordable amount / goal amount
        String goalAmountText = "";
        if (rewardItem.isRepeatable() && goal.getRewardQuantity() > 1) {
            goalAmountText = QuantityFormatter.quantityToStackSize(goal.getItemsAffordable()) + "/" + QuantityFormatter.quantityToStackSize(goal.getRewardQuantity());
        }

        var topLine = LineComponent.builder()
                .left(rewardItem.itemName())
                .leftFont(FontManager.getRunescapeFont())
                .right(goalAmountText)
                .rightFont(FontManager.getRunescapeBoldFont())
                .build();

        // Build the bottom line with the overall progress percentage
        var bottomLine = LineComponent.builder()
                .left("Progress:")
                .leftFont(FontManager.getRunescapeFont())
                .right(DECIMAL_FORMAT.format(goal.getOverallProgress() * 100) + "%")
                .rightFont(FontManager.getRunescapeFont())
                .rightColor(goal.getOverallProgress() >= 1 ? Color.GREEN : Color.WHITE)
                .build();

        var textSplit = SplitComponent.builder()
                .first(topLine)
                .second(bottomLine)
                .orientation(ComponentOrientation.VERTICAL)
                .build();

        ImageComponent rewardImageComponent = new ImageComponent(itemManager.getImage(rewardItem.itemId()));
        var topInfoSplit = SplitComponent.builder()
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
                createProgressBar(goal, component);
            }
        }

        return super.render(graphics);
    }

    private void createProgressBar(Goal goal, PotionComponent component) {
        Goal.ComponentData data = goal.getComponentData(component);

        ImageComponent imageComponent = new ImageComponent(getComponentSprite(component));
        ProgressBarComponent progressBarComponent = new ProgressBarComponent();
        progressBarComponent.setForegroundColor(component.color());
        progressBarComponent.setBackgroundColor(PROGRESS_BAR_BACKGROUND_COLOR);
        progressBarComponent.setValue(data.percentageToGoal * 100);
        progressBarComponent.setLeftLabel(QuantityFormatter.quantityToStackSize(data.currentAmount));
        progressBarComponent.setRightLabel(QuantityFormatter.quantityToStackSize(data.goalAmount));

        var progressBarSplit = SplitComponent.builder()
                .first(imageComponent)
                .second(progressBarComponent)
                .orientation(ComponentOrientation.HORIZONTAL)
                .gap(new Point(ICON_AND_GOAL_GAP, 0))
                .build();

        panelComponent.getChildren().add(progressBarSplit);
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
}
