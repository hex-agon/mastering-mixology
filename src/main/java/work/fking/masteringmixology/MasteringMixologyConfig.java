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
            name = "Tooltip",
            description = "Tooltip related configuration",
            position = 20
    )
    String TOOLTIP = "tooltipSection";

    @ConfigItem(
            section = TOOLTIP,
            keyName = "showTooltip",
            name = "Enable Tooltips",
            description = "Show tooltips for items in the bank.",
            position = 1
    )
    default boolean showTooltip() {
        return false;
    }

    @ConfigItem(
            section = TOOLTIP,
            keyName = "showTooltipEach",
            name = "Show Per-Item Amount",
            description = "Include the paste amount per single item in the tooltip.",
            position = 2
    )
    default boolean showTooltipEach() {
        return true;
    }

    @ConfigItem(
            section = TOOLTIP,
            keyName = "showPricePerPaste",
            name = "Include Price Per Paste",
            description = "Show GE price per paste in the tooltip.",
            position = 3
    )
    default boolean showPricePerPaste() {
        return false;
    }
}
