package work.fking.masteringmixology;

import java.util.Arrays;

import static work.fking.masteringmixology.PotionComponent.*;

public enum PotionType {
    MAMMOTH_MIGHT_MIX(60, 1350, MOX, MOX, MOX),
    MYSTIC_MANA_AMALGAM(60, 1750, MOX, MOX, AGA),
    MARLEYS_MOONLIGHT(60, 2150, MOX, MOX, LYE),
    ALCO_AUGMENTATOR(76, 2550, AGA, AGA, AGA),
    AZURE_AURA_MIX(68, 2150, AGA, AGA, MOX),
    AQUALUX_AMALGAM(72, 2950, AGA, LYE, AGA),
    LIPLACK_LIQUOR(86, 3750, LYE, LYE, LYE),
    MEGALITE_LIQUID(80, 2950, MOX, LYE, LYE),
    ANTI_LEECH_LOTION(84, 3350, AGA, LYE, LYE),
    MIXALOT(64, 2550, MOX, AGA, LYE);

    private static final PotionType[] TYPES = PotionType.values();

    private final String recipe;
    private final int levelReq;
    private final int experience;
    private final PotionComponent[] components;

    PotionType(int levelReq, int experience, PotionComponent... components) {
        this.recipe = colorizeRecipe(components);
        this.levelReq = levelReq;
        this.experience = experience;
        this.components = components;
    }

    public static PotionType from(int potionTypeId) {
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

    public String recipe() {
        return recipe;
    }

    public int levelReq() {
        return levelReq;
    }

    public int experience() {
        return experience;
    }

    public PotionComponent[] components() {
        return components;
    }
}
