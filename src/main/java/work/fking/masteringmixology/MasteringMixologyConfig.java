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
            name = "Highlight potions and stations",
            description = "Toggles alchemical potion/station highlighting on or off",
            position = 2
    )
    default boolean highlightStations() {
        return true;
    }

    @ConfigItem(
            keyName = "retortHighlightColor",
            name = "Retort station color",
            description = "Configures the default retort station highlight color",
            position = 3
    )
    default Color retortHighlightColor() {
        return Color.MAGENTA;
    }

    @ConfigItem(
            keyName = "agitatorHighlightColor",
            name = "Agitator station color",
            description = "Configures the default agitator station highlight color",
            position = 4
    )
    default Color agitatorHighlightColor() {
        return Color.YELLOW;
    }

    @ConfigItem(
            keyName = "alembicHighlightColor",
            name = "Alembic station color",
            description = "Configures the default retort station highlight color",
            position = 5
    )
    default Color alembicHighlightColor() {
        return Color.CYAN;
    }

    @ConfigItem(
            keyName = "stationQuickActionHighlightColor",
            name = "Quick-action color",
            description = "Configures the station quick-action highlight color",
            position = 6
    )
    default Color stationQuickActionHighlightColor() {
        return Color.GREEN;
    }

    @ConfigItem(
            keyName = "notifyDigweed",
            name = "Notify DigWeed",
            description = "Toggles digweed notifications on or off",
            position = 7
    )
    default boolean notifyDigWeed() {
        return true;
    }

    @ConfigItem(
            keyName = "highlightDigweed",
            name = "Highlight DigWeed",
            description = "Toggles digweed highlighting on or off",
            position = 8
    )
    default boolean highlightDigWeed() {
        return true;
    }

    @ConfigItem(
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
