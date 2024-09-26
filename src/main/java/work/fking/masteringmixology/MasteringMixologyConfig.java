package work.fking.masteringmixology;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import work.fking.masteringmixology.geels.enums.AlchemyBuilding;

@ConfigGroup("masteringmixology")
public interface MasteringMixologyConfig extends Config {

    @ConfigItem(
            keyName = "highlightbestxp",
            name = "Highlight Best Experience",
            description = "Highlights the best experience potion"
    )
    default boolean highlightBestExperience() {
        return true;
    }

    AlchemyBuilding prioritisedBuilding();
}
