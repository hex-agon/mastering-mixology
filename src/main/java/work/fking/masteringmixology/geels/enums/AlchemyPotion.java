package work.fking.masteringmixology.geels.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.ItemID;

@AllArgsConstructor
public enum AlchemyPotion {
    NONE("", "", 0, 0, 0, 0, 0, 0, 0),
    MAMMOTH_MIGHT_MIX("Mammoth-might mix", "MMM", 1, 60, ItemID.MAMMOTHMIGHT_MIX, ItemID.MAMMOTHMIGHT_MIX_30021, 3, 0, 0),
    MYSTIC_MANA_AMALGAM("Mystic mana amalgam", "MMA", 2, 60, ItemID.MYSTIC_MANA_AMALGAM, ItemID.MYSTIC_MANA_AMALGAM_30022, 2, 1, 0),
    MARLEYS_MOONLIGHT("Marley's moonlight", "MML", 3, 60,  ItemID.MARLEYS_MOONLIGHT, ItemID.MARLEYS_MOONLIGHT_30023, 2, 0, 1),
    ALCO_AUGMENTATOR("Alco-augmentator", "AAA", 4, 76, ItemID.ALCOAUGMENTATOR, ItemID.ALCOAUGMENTATOR_30024, 0, 3, 0),
    AZURE_AURA_MIX("Azure aura mix", "AAM", 5, 68, ItemID.AZURE_AURA_MIX, ItemID.AZURE_AURA_MIX_30026, 1, 2, 0),
    AQUALUX_AMALGAM("Aqualux amalgam", "AAL", 6, 72, ItemID.AQUALUX_AMALGAM, ItemID.AQUALUX_AMALGAM_30025, 0, 2, 1),
    LIPLACK_LIQUOR("Liplack liquor", "LLL", 7, 86, ItemID.LIPLACK_LIQUOR, ItemID.LIPLACK_LIQUOR_30027, 0, 0, 3),
    MEGALITE_LIQUID("Megalite liquid", "MLL", 8, 80, ItemID.MEGALITE_LIQUID, ItemID.MEGALITE_LIQUID_30029, 1, 0, 2),
    ANTI_LEECH_LOTION("Anti-leech lotion", "ALL",9, 84, ItemID.ANTILEECH_LOTION, ItemID.ANTILEECH_LOTION_30028, 0, 1, 2),
    MIXALOT("Mixalot", "MAL", 10, 64, ItemID.MIXALOT, ItemID.MIXALOT_30030, 1, 1, 1);

    @Getter
    private final String Name;

    @Getter
    private final String RecipeName;

    @Getter
    private final int PotionId;

    @Getter
    private final int HerbloreLevel;

    @Getter
    private final int BaseItemId;

    @Getter
    private final int InfusedItemId;

    @Getter
    private final int MoxRequired;

    @Getter
    private final int AgaRequired;

    @Getter
    private final int LyeRequired;

    public static AlchemyPotion FromId(int potionIndex) {
        // Todo: not a bruteforce search of a sorted, constant, array lmao
        for (var potion : AlchemyPotion.values()) {
            if (potion.PotionId == potionIndex) {
                return potion;
            }
        }

        return AlchemyPotion.NONE;
    }

    public static AlchemyPotion FromName(String name) {
        // come on dude
        for (var potion : AlchemyPotion.values()) {
            if (potion.getName().equalsIgnoreCase(name)) {
                return potion;
            }
        }

        return AlchemyPotion.NONE;
    }
}
