package work.fking.masteringmixology;

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Notification;
import net.runelite.client.ui.overlay.components.ComponentConstants;

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
            keyName = "identifyPotions",
            name = "Identify potions",
            description = "Identify potions in your inventory",
            position = 4
    )
    default boolean identifyPotions() {
        return true;
    }

    @ConfigItem(
            section = HIGHLIGHTS,
            keyName = "stationHighlightColor",
            name = "Station color",
            description = "Configures the default station highlight color",
            position = 5
    )
    default Color stationHighlightColor() {
        return Color.MAGENTA;
    }

    @ConfigItem(
            section = HIGHLIGHTS,
            keyName = "stationQuickActionHighlightColor",
            name = "Quick-action color",
            description = "Configures the station quick-action highlight color",
            position = 6
    )
    default Color stationQuickActionHighlightColor() {
        return Color.GREEN;
    }

    @ConfigItem(
            section = HIGHLIGHTS,
            keyName = "notifyDigweed",
            name = "Notify DigWeed",
            description = "Toggles digweed notifications on or off",
            position = 7
    )
    default Notification notifyDigWeed() {
        return Notification.ON;
    }

    @ConfigItem(
            section = HIGHLIGHTS,
            keyName = "highlightDigweed",
            name = "Highlight DigWeed",
            description = "Toggles digweed highlighting on or off",
            position = 8
    )
    default boolean highlightDigWeed() {
        return true;
    }

    @ConfigItem(
            section = HIGHLIGHTS,
            keyName = "digweedHighlightColor",
            name = "DigWeed color",
            description = "Configures the digweed highlight color",
            position = 9
    )
    default Color digweedHighlightColor() {
        return Color.GREEN;
    }

    @ConfigItem(
            section = HIGHLIGHTS,
            keyName = "highlightBorderWidth",
            name = "Border width",
            description = "Configures the border width of the object highlights",
            position = 10
    )
    default int highlightBorderWidth() {
        return 2;
    }

    @ConfigItem(
            section = HIGHLIGHTS,
            keyName = "highlightFeather",
            name = "Feather",
            description = "Configures the amount of 'feathering' to be applied to the object highlights",
            position = 11
    )
    default int highlightFeather() {
        return 1;
    }


    // Panel Section
    @ConfigSection(
            name = "Panel",
            description = "Panel related configuration",
            position = 20
    )
    String PANEL_SECTION = "Panel";

    @ConfigItem(
            section = PANEL_SECTION,
            keyName = "displayPanel",
            name = "Display Overlay Panel",
            description = "Toggles the overlay panel visibility",
            position = 1
    )
    default boolean displayPanel() {
        return false;
    }

    @ConfigItem(
            section = PANEL_SECTION,
            keyName = "displayOrdersInPanel",
            name = "Display Potion Orders",
            description = "Show potion orders in the panel",
            position = 2
    )
    default boolean displayOrdersInPanel() {
        return true;
    }

    @ConfigItem(
            section = PANEL_SECTION,
            keyName = "displayPasteInPanel",
            name = "Display Paste Amounts",
            description = "Show paste amounts in the panel",
            position = 3
    )
    default boolean displayPasteInPanel() {
        return true;
    }

    @ConfigItem(
            section = PANEL_SECTION,
            keyName = "displayResinInPanel",
            name = "Display Resin Amounts",
            description = "Show resin amounts in the panel",
            position = 4
    )
    default boolean displayResinInPanel() {
        return true;
    }

    @ConfigItem(
            section = PANEL_SECTION,
            keyName = "showPanelInLab",
            name = "Show Panel in Lab",
            description = "Show the panel when inside the lab UI",
            position = 5
    )
    default boolean showPanelInLab() {
        return false;
    }

    @ConfigItem(
            section = PANEL_SECTION,
            keyName = "orderIconSize",
            name = "Order Icon Size",
            description = "Set the icon size for potion orders",
            position = 6
    )
    default int orderIconSize() {
        return 20;
    }

    @ConfigItem(
            section = PANEL_SECTION,
            keyName = "orderFontSize",
            name = "Order Font Size",
            description = "Set the font size for potion orders",
            position = 7
    )
    default int orderFontSize() {
        return 16;
    }

    @ConfigItem(
            section = PANEL_SECTION,
            keyName = "resinIconSize",
            name = "Resin Icon Size",
            description = "Set the icon size for resin amounts",
            position = 8
    )
    default int resinIconSize() {
        return 20;
    }

    @ConfigItem(
            section = PANEL_SECTION,
            keyName = "resinFontSize",
            name = "Resin Font Size",
            description = "Set the font size for resin amounts",
            position = 9
    )
    default int resinFontSize() {
        return 16;
    }

    @Alpha
    @ConfigItem(
            section = PANEL_SECTION,
            keyName = "panelBackgroundColor",
            name = "Panel Background Color",
            description = "Set the background color for the panel",
            position = 10
    )
    default Color panelBackgroundColor() {
        return ComponentConstants.STANDARD_BACKGROUND_COLOR;
    }
}
