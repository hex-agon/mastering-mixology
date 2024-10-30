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

    // General Configurations
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
            keyName = "displayResin",
            name = "Display resin amount",
            description = "Display total resin amounts",
            position = 2
    )
    default boolean displayResin() {
        return false;
    }

    @ConfigItem(
            keyName = "identifyPotions",
            name = "Identify potions",
            description = "Identify potions in your inventory",
            position = 3
    )
    default boolean identifyPotions() {
        return true;
    }


    // Highlights Section
    @ConfigSection(
            name = "Highlights",
            description = "Highlighting related configuration",
            position = 10
    )
    String HIGHLIGHTS = "Highlights";

    @ConfigItem(
            section = HIGHLIGHTS,
            keyName = "highlightLevers",
            name = "Highlight levers",
            description = "Highlight levers",
            position = 1
    )
    default boolean highlightLevers() {
        return true;
    }

    @ConfigItem(
            section = HIGHLIGHTS,
            keyName = "highlightStations",
            name = "Highlight stations",
            description = "Toggles alchemical station highlighting on or off",
            position = 2
    )
    default boolean highlightStations() {
        return true;
    }

    @ConfigItem(
            section = HIGHLIGHTS,
            keyName = "highlightQuickActionEvents",
            name = "Highlight quick-action events",
            description = "Toggles station quick-action events highlighting on or off",
            position = 3
    )
    default boolean highlightQuickActionEvents() {
        return true;
    }

    @ConfigItem(
            section = HIGHLIGHTS,
            keyName = "highlightDigweed",
            name = "Highlight DigWeed",
            description = "Toggles digweed highlighting on or off",
            position = 4
    )
    default boolean highlightDigWeed() {
        return true;
    }

    @ConfigItem(
            section = HIGHLIGHTS,
            keyName = "notifyDigweed",
            name = "Notify DigWeed",
            description = "Toggles digweed notifications on or off",
            position = 5
    )
    default Notification notifyDigWeed() {
        return Notification.ON;
    }

    @ConfigItem(
            section = HIGHLIGHTS,
            keyName = "stationHighlightColor",
            name = "Station color",
            description = "Configures the default station highlight color",
            position = 6
    )
    default Color stationHighlightColor() {
        return Color.MAGENTA;
    }

    @ConfigItem(
            section = HIGHLIGHTS,
            keyName = "stationQuickActionHighlightColor",
            name = "Quick-action color",
            description = "Configures the station quick-action highlight color",
            position = 7
    )
    default Color stationQuickActionHighlightColor() {
        return Color.GREEN;
    }

    @ConfigItem(
            section = HIGHLIGHTS,
            keyName = "digweedHighlightColor",
            name = "DigWeed color",
            description = "Configures the digweed highlight color",
            position = 8
    )
    default Color digweedHighlightColor() {
        return Color.GREEN;
    }

    @ConfigItem(
            section = HIGHLIGHTS,
            keyName = "highlightStyle",
            name = "Highlight Style",
            description = "Change the style of the highlight",
            position = 9
    )
    default HighlightedObject.HighlightStyle highlightStyle() {
        return HighlightedObject.HighlightStyle.OUTLINE;
    }

    @ConfigItem(
            section = HIGHLIGHTS,
            keyName = "stationQuickActionHighlightStyle",
            name = "Quick-action Style",
            description = "Configures the station quick-action highlight style",
            position = 10
    )
    default HighlightedObject.HighlightStyle stationQuickActionHighlightStyle() {
        return HighlightedObject.HighlightStyle.CLICK_BOX;
    }

    @ConfigItem(
            section = HIGHLIGHTS,
            keyName = "digweedHighlightStyle",
            name = "DigWeed Style",
            description = "Configures the DigWeed highlight style",
            position = 11
    )
    default HighlightedObject.HighlightStyle digweedHighlightStyle() {
        return HighlightedObject.HighlightStyle.OUTLINE;
    }

    @ConfigItem(
            section = HIGHLIGHTS,
            keyName = "leverHighlightStyle",
            name = "Lever Style",
            description = "Configures the lever highlight style",
            position = 12
    )
    default HighlightedObject.HighlightStyle leverHighlightStyle() {
        return HighlightedObject.HighlightStyle.OUTLINE;
    }

    @ConfigItem(
            section = HIGHLIGHTS,
            keyName = "highlightBorderWidth",
            name = "Border width",
            description = "Configures the border width of the object highlights",
            position = 13
    )
    default int highlightBorderWidth() {
        return 2;
    }

    @ConfigItem(
            section = HIGHLIGHTS,
            keyName = "highlightFeather",
            name = "Feather",
            description = "Configures the amount of 'feathering' to be applied to the object highlights",
            position = 14
    )
    default int highlightFeather() {
        return 1;
    }
}
