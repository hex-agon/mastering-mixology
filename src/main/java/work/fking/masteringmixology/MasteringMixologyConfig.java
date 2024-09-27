package work.fking.masteringmixology;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("masteringmixology")
public interface MasteringMixologyConfig extends Config {

    @ConfigSection(
            name = "Highlights",
            description = "Highlighting related configuration",
            position = 0
    )
    String highlights = "Highlights";

    @ConfigItem(
            keyName = "strategy",
            name = "Strategy",
            description = "Selects the potion order highlighting strategy"
    )
    default Strategy strategy() {
        return Strategy.FAVOR_EXPERIENCE;
    }

    @ConfigItem(
            keyName = "highlightStations",
            name = "Highlight stations",
            description = "Toggles alchemical station highlighting on or off"
    )
    default boolean highlightStations() {
        return true;
    }

    @ConfigItem(
            keyName = "notifyDigweed",
            name = "Notify DigWeed",
            description = "Toggles digweed notifications on or off"
    )
    default boolean notifyDigWeed() {
        return true;
    }

    @ConfigItem(
            keyName = "highlightDigweed",
            name = "Highlight DigWeed",
            description = "Toggles digweed highlighting on or off"
    )
    default boolean highlightDigWeed() {
        return true;
    }

    @ConfigItem(
            section = highlights,
            keyName = "highlightBorderWidth",
            name = "Border width",
            description = "Configures the border width of the object highlights"
    )
    default int highlightBorderWidth() {
        return 2;
    }

    @ConfigItem(
            section = highlights,
            keyName = "highlightFeather",
            name = "Feather",
            description = "Configures the amount of 'feathering' to be applied to the object highlights"
    )
    default int highlightFeather() {
        return 1;
    }
}
