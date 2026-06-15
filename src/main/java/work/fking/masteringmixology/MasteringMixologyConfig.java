package work.fking.masteringmixology;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Notification;
import net.runelite.client.config.Range;

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
            keyName = "soundEffectAlembic",
            name = "Fix Alembic quick-action sound effect",
            description = "Fixes the Alembic quick-action sound effect to play at the correct time",
            position = 8
    )
    default boolean soundEffectAlembic() {
        return true;
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
            description = "Tick the rewards you're working toward. The overlay shows progress toward the combined cost; the notification fires when you meet it.",
            position = 13
    )
    String REWARD_TRACKING = "RewardTracking";

    @ConfigItem(
            section = REWARD_TRACKING,
            keyName = "thresholdNotification",
            name = "Threshold notification",
            description = "Fires once when your resin totals meet the combined cost of every selected reward",
            position = 1
    )
    default Notification thresholdNotification() {
        return Notification.OFF;
    }

    @ConfigItem(
            section = REWARD_TRACKING,
            keyName = "showResinBars",
            name = "Show Resin Bars",
            description = "Toggle to display or hide the resin progress bars",
            position = 2
    )
    default boolean showResinBars() {
        return true;
    }

    @ConfigItem(
            section = REWARD_TRACKING,
            keyName = "trackPrescriptionGoggles",
            name = "Prescription Goggles",
            description = "Include Prescription Goggles in the tracked totals",
            position = 3
    )
    default boolean trackPrescriptionGoggles() {
        return false;
    }

    @ConfigItem(
            section = REWARD_TRACKING,
            keyName = "trackAlchemistLabcoat",
            name = "Alchemist Labcoat",
            description = "Include Alchemist Labcoat in the tracked totals",
            position = 4
    )
    default boolean trackAlchemistLabcoat() {
        return false;
    }

    @ConfigItem(
            section = REWARD_TRACKING,
            keyName = "trackAlchemistPants",
            name = "Alchemist Pants",
            description = "Include Alchemist Pants in the tracked totals",
            position = 5
    )
    default boolean trackAlchemistPants() {
        return false;
    }

    @ConfigItem(
            section = REWARD_TRACKING,
            keyName = "trackAlchemistGloves",
            name = "Alchemist Gloves",
            description = "Include Alchemist Gloves in the tracked totals",
            position = 6
    )
    default boolean trackAlchemistGloves() {
        return false;
    }

    @ConfigItem(
            section = REWARD_TRACKING,
            keyName = "trackReagentPouch",
            name = "Reagent Pouch",
            description = "Include Reagent Pouch in the tracked totals",
            position = 7
    )
    default boolean trackReagentPouch() {
        return false;
    }

    @ConfigItem(
            section = REWARD_TRACKING,
            keyName = "trackPotionStorage",
            name = "Potion Storage",
            description = "Include Potion Storage in the tracked totals",
            position = 8
    )
    default boolean trackPotionStorage() {
        return false;
    }

    @ConfigItem(
            section = REWARD_TRACKING,
            keyName = "trackAlchemistsAmulet",
            name = "Alchemist's Amulet",
            description = "Include Alchemist's Amulet in the tracked totals",
            position = 9
    )
    default boolean trackAlchemistsAmulet() {
        return false;
    }

    @ConfigItem(
            section = REWARD_TRACKING,
            keyName = "trackChuggingBarrel",
            name = "Chugging Barrel",
            description = "Include Chugging Barrel(s) in the tracked totals",
            position = 10
    )
    default boolean trackChuggingBarrel() {
        return false;
    }

    @ConfigItem(
            section = REWARD_TRACKING,
            keyName = "chuggingBarrelQuantity",
            name = "  Chugging Barrel qty",
            description = "How many Chugging Barrels to include when ticked",
            position = 11
    )
    @Range(min = 1, max = 100000)
    default int chuggingBarrelQuantity() {
        return 1;
    }

    @ConfigItem(
            section = REWARD_TRACKING,
            keyName = "trackApprenticePack",
            name = "Apprentice Potion Pack",
            description = "Include Apprentice Potion Pack(s) in the tracked totals",
            position = 12
    )
    default boolean trackApprenticePack() {
        return false;
    }

    @ConfigItem(
            section = REWARD_TRACKING,
            keyName = "apprenticePackQuantity",
            name = "  Apprentice Pack qty",
            description = "How many Apprentice Potion Packs to include when ticked",
            position = 13
    )
    @Range(min = 1, max = 100000)
    default int apprenticePackQuantity() {
        return 1;
    }

    @ConfigItem(
            section = REWARD_TRACKING,
            keyName = "trackAdeptPack",
            name = "Adept Potion Pack",
            description = "Include Adept Potion Pack(s) in the tracked totals",
            position = 14
    )
    default boolean trackAdeptPack() {
        return false;
    }

    @ConfigItem(
            section = REWARD_TRACKING,
            keyName = "adeptPackQuantity",
            name = "  Adept Pack qty",
            description = "How many Adept Potion Packs to include when ticked",
            position = 15
    )
    @Range(min = 1, max = 100000)
    default int adeptPackQuantity() {
        return 1;
    }

    @ConfigItem(
            section = REWARD_TRACKING,
            keyName = "trackExpertPack",
            name = "Expert Potion Pack",
            description = "Include Expert Potion Pack(s) in the tracked totals",
            position = 16
    )
    default boolean trackExpertPack() {
        return false;
    }

    @ConfigItem(
            section = REWARD_TRACKING,
            keyName = "expertPackQuantity",
            name = "  Expert Pack qty",
            description = "How many Expert Potion Packs to include when ticked",
            position = 17
    )
    @Range(min = 1, max = 100000)
    default int expertPackQuantity() {
        return 1;
    }

    @ConfigItem(
            section = REWARD_TRACKING,
            keyName = "trackAldarium",
            name = "Aldarium",
            description = "Include Aldarium in the tracked totals",
            position = 18
    )
    default boolean trackAldarium() {
        return false;
    }

    @ConfigItem(
            section = REWARD_TRACKING,
            keyName = "aldariumQuantity",
            name = "  Aldarium qty",
            description = "How many Aldarium to include when ticked",
            position = 19
    )
    @Range(min = 1, max = 100000)
    default int aldariumQuantity() {
        return 1;
    }

    @ConfigSection(
            name = "Recommended-Potion Highlight",
            description = "Colour each order green (brew it) or red (skip it) based on the adaptive meta-strategy",
            position = 15
    )
    String RECOMMENDED_HIGHLIGHT = "RecommendedHighlight";

    @ConfigItem(
            section = RECOMMENDED_HIGHLIGHT,
            keyName = "highlightRecommendedPotions",
            name = "Highlight recommended potions",
            description = "Colours each order's name green if the adaptive meta-strategy recommends brewing it, red otherwise. Requires at least one tracked reward in Reward Tracking.",
            position = 1
    )
    default boolean highlightRecommendedPotions() {
        return false;
    }

    @ConfigItem(
            section = RECOMMENDED_HIGHLIGHT,
            keyName = "recommendedPotionColor",
            name = "Recommended colour",
            description = "Text colour for orders the meta-strategy says you should brew",
            position = 2
    )
    default Color recommendedPotionColor() {
        return new Color(0, 200, 0);
    }

    @ConfigItem(
            section = RECOMMENDED_HIGHLIGHT,
            keyName = "notRecommendedPotionColor",
            name = "Skip colour",
            description = "Text colour for orders the meta-strategy says you should skip (reroll)",
            position = 3
    )
    default Color notRecommendedPotionColor() {
        return new Color(220, 50, 50);
    }

    @ConfigSection(
            name = "Accessibility",
            description = "Dyslexia / colour-blind friendly alternatives: replace M/A/L letters with coloured blocks and swap the recommend/skip text + resin colours for higher-contrast options",
            position = 20
    )
    String ACCESSIBILITY = "Accessibility";

    @ConfigItem(
            section = ACCESSIBILITY,
            keyName = "dyslexicMixology",
            name = "Dyslexic/Colorblind Mode",
            description = "Replace M/A/L letters with coloured blocks in the order list and inventory tags; apply the accessibility colours below to recommend/skip text, resin blocks, levers, and progress bars",
            position = 1
    )
    default boolean accessibilityMode() {
        return false;
    }

    @ConfigItem(
            section = ACCESSIBILITY,
            keyName = "accessibilityRecommendColor",
            name = "Recommend colour",
            description = "Text colour for orders the meta-strategy says you should brew (applied when accessibility mode is on)",
            position = 2
    )
    default Color accessibilityRecommendColor() {
        return new Color(0xFFC107);
    }

    @ConfigItem(
            section = ACCESSIBILITY,
            keyName = "accessibilitySkipColor",
            name = "Skip colour",
            description = "Text colour for orders the meta-strategy says you should skip (applied when accessibility mode is on)",
            position = 3
    )
    default Color accessibilitySkipColor() {
        return new Color(0x4A4A4A);
    }

    @ConfigItem(
            section = ACCESSIBILITY,
            keyName = "accessibilityMoxColor",
            name = "MOX colour",
            description = "Colour for MOX resin (blocks, lever, progress bar) when accessibility mode is on",
            position = 4
    )
    default Color accessibilityMoxColor() {
        return new Color(0x9D4EDD);
    }

    @ConfigItem(
            section = ACCESSIBILITY,
            keyName = "accessibilityAgaColor",
            name = "AGA colour",
            description = "Colour for AGA resin (blocks, lever, progress bar) when accessibility mode is on",
            position = 5
    )
    default Color accessibilityAgaColor() {
        return new Color(0x00FFB3);
    }

    @ConfigItem(
            section = ACCESSIBILITY,
            keyName = "accessibilityLyeColor",
            name = "LYE colour",
            description = "Colour for LYE resin (blocks, lever, progress bar) when accessibility mode is on",
            position = 6
    )
    default Color accessibilityLyeColor() {
        return new Color(0xFF6D00);
    }
}
