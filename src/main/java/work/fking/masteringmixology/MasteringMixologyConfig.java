package work.fking.masteringmixology;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Notification;

import java.awt.Color;

import static work.fking.masteringmixology.MasteringMixologyConfig.CONFIG_GROUP;

@ConfigGroup(CONFIG_GROUP)
public interface MasteringMixologyConfig extends Config {

    String CONFIG_GROUP = "masteringmixology";

    @ConfigSection(
            name = "Highlights",
            description = "Highlighting related configuration",
            position = 10
    )
    String HIGHLIGHTS = "Highlights";

    @ConfigItem(
            keyName = "inventoryPotionTags",
            name = "Inventory Potion Tags",
            description = "How potions should be tagged in the inventory",
            position = 1
    )
    default InventoryPotionTagType inventoryPotionTagType() {
        return InventoryPotionTagType.WHITE;
    }

    @ConfigItem(
            keyName = "potionOrderSorting",
            name = "Order sorting",
            description = "Determines how potion orders are sorted in the interface",
            position = 1
    )
    default PotionOrderSorting potionOrderSorting() {
        return PotionOrderSorting.VANILLA;
    }

    @ConfigItem(
            keyName = "highlightLevers",
            name = "Highlight levers",
            description = "Highlight levers",
            position = 2
    )
    default boolean highlightLevers() {
        return true;
    }

    @ConfigItem(
            keyName = "highlightStations",
            name = "Highlight stations",
            description = "Toggles alchemical station highlighting on or off",
            position = 2
    )
    default boolean highlightStations() {
        return true;
    }

    @ConfigItem(
            keyName = "highlightQuickActionEvents",
            name = "Highlight quick-action events",
            description = "Toggles station quick-action events highlighting on or off",
            position = 2
    )
    default boolean highlightQuickActionEvents() {
        return true;
    }

    @ConfigItem(
            keyName = "displayResin",
            name = "Display resin amount",
            description = "Display total resin amounts",
            position = 2
    )
    default boolean displayResin() {
        return false;
    }

    @ConfigItem(
            keyName = "stationHighlightColor",
            name = "Station color",
            description = "Configures the default station highlight color",
            position = 3
    )
    default Color stationHighlightColor() {
        return Color.MAGENTA;
    }

    @ConfigItem(
            keyName = "stationQuickActionHighlightColor",
            name = "Quick-action color",
            description = "Configures the station quick-action highlight color",
            position = 4
    )
    default Color stationQuickActionHighlightColor() {
        return Color.GREEN;
    }

    @ConfigItem(
            keyName = "notifyDigweed",
            name = "Notify DigWeed",
            description = "Toggles digweed notifications on or off",
            position = 5
    )
    default Notification notifyDigWeed() {
        return Notification.ON;
    }

    @ConfigItem(
            keyName = "highlightDigweed",
            name = "Highlight DigWeed",
            description = "Toggles digweed highlighting on or off",
            position = 6
    )
    default boolean highlightDigWeed() {
        return true;
    }

    @ConfigItem(
            keyName = "digweedHighlightColor",
            name = "DigWeed color",
            description = "Configures the digweed highlight color",
            position = 7
    )
    default Color digweedHighlightColor() {
        return Color.GREEN;
    }

    @ConfigItem(
            section = HIGHLIGHTS,
            keyName = "highlightBorderWidth",
            name = "Border width",
            description = "Configures the border width of the object highlights"
    )
    default int highlightBorderWidth() {
        return 2;
    }

    @ConfigItem(
            section = HIGHLIGHTS,
            keyName = "highlightFeather",
            name = "Feather",
            description = "Configures the amount of 'feathering' to be applied to the object highlights"
    )
    default int highlightFeather() {
        return 1;
    }

    @ConfigSection(
            name = "Reward Tracking",
            description = "Track your progress towards rewards",
            position = 13
    )
    String REWARD_TRACKING = "RewardTracking";

    @ConfigItem(
            section = REWARD_TRACKING,
            keyName = "selectedReward",
            name = "Selected Reward",
            description = "Select a reward to track resin for",
            position = 1
    )
    default RewardItem selectedReward() {
        return RewardItem.NONE;
    }

    @ConfigItem(
            section = REWARD_TRACKING,
            keyName = "showResinProgressBars",
            name = "Show Resin Progress Bars",
            description = "Toggle to display or hide the resin progress bars in the goal overlay",
            position = 2
    )
    default boolean showResinProgressBars() {
        return true;
    }
}
