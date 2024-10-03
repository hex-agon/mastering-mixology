package work.fking.masteringmixology;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

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
            keyName = "strategy",
            name = "Strategy",
            description = "Selects the potion order highlighting strategy",
            position = 1
    )
    default Strategy strategy() {
        return Strategy.FAVOR_EXPERIENCE;
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
    default boolean notifyDigWeed() {
        return true;
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
            position = 11
    )
    String tooltipSection = "tooltipSection";

    @ConfigItem(
            keyName = "showTooltip",
            name = "Show Paste Tooltips",
            description = "Whether items in the bank should show a tooltip",
            position = 12,
            section = tooltipSection
    )
    default boolean showTooltip() {
        return false;
    }

    @ConfigItem(
            keyName = "showTooltipEach",
            name = "Show how much paste each herb gives",
            description = "Whether the tooltip should include the paster per single item",
            position = 13,
            section = tooltipSection
    )
    default boolean showTooltipEach() {
        return true;
    }

    @ConfigItem(
            keyName = "showPricePerPaste",
            name = "Show GE price per paste",
            description = "Whether the tooltip should show price per paste",
            position = 14,
            section = tooltipSection
    )
    default boolean showPricePerPaste() {
        return false;
    }
}
