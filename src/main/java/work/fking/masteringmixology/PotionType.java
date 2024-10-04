package work.fking.masteringmixology;

import com.google.common.collect.ImmutableMap;
import net.runelite.api.ItemID;

import java.util.Arrays;
import java.util.Map;

import static work.fking.masteringmixology.PotionComponent.AGA;
import static work.fking.masteringmixology.PotionComponent.LYE;
import static work.fking.masteringmixology.PotionComponent.MOX;

public enum PotionType {
    MAMMOTH_MIGHT_MIX(ItemID.MAMMOTHMIGHT_MIX, 1900, MOX, MOX, MOX),
    MYSTIC_MANA_AMALGAM(ItemID.MYSTIC_MANA_AMALGAM, 2150, MOX, MOX, AGA),
    MARLEYS_MOONLIGHT(ItemID.MARLEYS_MOONLIGHT, 2400, MOX, MOX, LYE),
    ALCO_AUGMENTATOR(ItemID.ALCOAUGMENTATOR, 1900, AGA, AGA, AGA),
    AZURE_AURA_MIX(ItemID.AZURE_AURA_MIX, 2650, AGA, AGA, MOX),
    AQUALUX_AMALGAM(ItemID.AQUALUX_AMALGAM, 2900, AGA, LYE, AGA),
    LIPLACK_LIQUOR(ItemID.LIPLACK_LIQUOR, 1900, LYE, LYE, LYE),
    MEGALITE_LIQUID(ItemID.MEGALITE_LIQUID, 3150, MOX, LYE, LYE),
    ANTI_LEECH_LOTION(ItemID.ANTILEECH_LOTION, 3400, AGA, LYE, LYE),
    MIXALOT(ItemID.MIXALOT, 3650, MOX, AGA, LYE);

    private static final PotionType[] TYPES = PotionType.values();
    private static final Map<Integer, PotionType> MAP;

    static {
        var builder = new ImmutableMap.Builder<Integer, PotionType>();
        for (var p : PotionType.values()) {
            builder.put(p.id(), p);
        }
        MAP = builder.build();
    }

    private final int itemId;
    private final String recipe;
    private final String abbreviation;
    private final int experience;
    private final PotionComponent[] components;


    PotionType(int itemId, int experience, PotionComponent... components) {
        this.itemId = itemId;
        this.recipe = colorizeRecipe(components);
        this.experience = experience;
        this.components = components;
        this.abbreviation = "" + components[0].character() + components[1].character() + components[2].character();
    }

    public static PotionType fromId(int id) {
        return MAP.get(id);
    }

    public static PotionType fromIdx(int potionTypeId) {
        if (potionTypeId < 0 || potionTypeId >= TYPES.length) {
            return null;
        }
        return TYPES[potionTypeId];
    }

    private static String colorizeRecipe(PotionComponent[] components) {
        if (components.length != 3) {
            throw new IllegalArgumentException("Invalid potion components: " + Arrays.toString(components));
        }
        return colorizeRecipeComponent(components[0])
                + colorizeRecipeComponent(components[1])
                + colorizeRecipeComponent(components[2]);
    }

    private static String colorizeRecipeComponent(PotionComponent component) {
        return "<col=" + component.color() + ">" + component.character() + "</col>";
    }

    public int id() {
        return itemId;
    }

    public String recipe() {
        return recipe;
    }

    public int experience() {
        return experience;
    }

    public PotionComponent[] components() {
        return components;
    }

    public String abbreviation() {
        return abbreviation;
    }
}
