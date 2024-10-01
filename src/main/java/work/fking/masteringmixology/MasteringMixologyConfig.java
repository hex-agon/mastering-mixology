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
            keyName = "highlightInventory",
            name = "Highlight inventory",
            description = "Toggles inventory item highlighting on or off",
            position = 2
    )
    default boolean highlightInventory() { return true; }

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
            keyName = "alembicHighlightColor",
            name = "Alembic Station color",
            description = "Configures the alembic station highlight color",
            position = 4
    )
    default Color alembicHighlightColor() {
        return Color.MAGENTA;
    }

    @ConfigItem(
            keyName = "agitatorHighlightColor",
            name = "Agitator Station color",
            description = "Configures the agitator station highlight color",
            position = 5
    )
    default Color agitatorHighlightColor() {
        return Color.CYAN;
    }

    @ConfigItem(
            keyName = "retortHighlightColor",
            name = "Retort Station color",
            description = "Configures the retort station highlight color",
            position = 6
    )
    default Color retortHighlightColor() {
        return Color.YELLOW;
    }

    @ConfigItem(
            keyName = "stationQuickActionHighlightColor",
            name = "Quick-action color",
            description = "Configures the station quick-action highlight color",
            position = 7
    )
    default Color stationQuickActionHighlightColor() {
        return Color.GREEN;
    }

    @ConfigItem(
            keyName = "notifyDigweed",
            name = "Notify DigWeed",
            description = "Toggles digweed notifications on or off",
            position = 8
    )
    default boolean notifyDigWeed() {
        return true;
    }

    @ConfigItem(
            keyName = "highlightDigweed",
            name = "Highlight DigWeed",
            description = "Toggles digweed highlighting on or off",
            position = 9
    )
    default boolean highlightDigWeed() {
        return true;
    }

    @ConfigItem(
            keyName = "digweedHighlightColor",
            name = "DigWeed color",
            description = "Configures the digweed highlight color",
            position = 10
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
