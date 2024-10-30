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
            position = 3
    )
    default boolean highlightStations() {
        return true;
    }

    @ConfigItem(
            keyName = "highlightQuickActionEvents",
            name = "Highlight quick-action events",
            description = "Toggles station quick-action events highlighting on or off",
            position = 4
    )
    default boolean highlightQuickActionEvents() {
        return true;
    }

    @ConfigItem(
            keyName = "highlightMixingVessel",
            name = "Highlight mixing vessel",
            description = "Highlight the mixing vessel",
            position = 5
    )
    default boolean highlightMixingVessel() {
        return true;
    }

    @ConfigItem(
            keyName = "highlightMixingVesselInvalid",
            name = "Highlight invalid mixing vessel",
            description = "Highlight the mixing vessel red when you have the wrong ingredients",
            position = 6
    )
    default boolean highlightMixingVesselInvalid() {
        return true;
    }

    @ConfigItem(
            keyName = "displayResin",
            name = "Display resin amount",
            description = "Display total resin amounts",
            position = 8
    )
    default boolean displayResin() {
        return false;
    }

    @ConfigItem(
            keyName = "stationHighlightColor",
            name = "Station color",
            description = "Configures the default station highlight color",
            position = 9
    )
    default Color stationHighlightColor() {
        return Color.MAGENTA;
    }

    @ConfigItem(
            keyName = "stationQuickActionHighlightColor",
            name = "Quick-action color",
            description = "Configures the station quick-action highlight color",
            position = 10
    )
    default Color stationQuickActionHighlightColor() {
        return Color.GREEN;
    }

    @ConfigItem(
            keyName = "notifyDigweed",
            name = "Notify DigWeed",
            description = "Toggles digweed notifications on or off",
            position = 11
    )
    default Notification notifyDigWeed() {
        return Notification.ON;
    }

    @ConfigItem(
            keyName = "highlightDigweed",
            name = "Highlight DigWeed",
            description = "Toggles digweed highlighting on or off",
            position = 12
    )
    default boolean highlightDigWeed() {
        return true;
    }

    @ConfigItem(
            keyName = "digweedHighlightColor",
            name = "DigWeed color",
            description = "Configures the digweed highlight color",
            position = 13
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
}
