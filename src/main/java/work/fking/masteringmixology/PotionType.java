package work.fking.masteringmixology;

import java.util.Arrays;

import static work.fking.masteringmixology.PotionComponent.AGA;
import static work.fking.masteringmixology.PotionComponent.LYE;
import static work.fking.masteringmixology.PotionComponent.MOX;

public enum PotionType {
    MAMMOTH_MIGHT_MIX(1900, MOX, MOX, MOX),
    MYSTIC_MANA_AMALGAM(2150, MOX, MOX, AGA),
    MARLEYS_MOONLIGHT(2400, MOX, MOX, LYE),
    ALCO_AUGMENTATOR(1900, AGA, AGA, AGA),
    AZURE_AURA_MIX(2650, AGA, AGA, MOX),
    AQUALUX_AMALGAM(2900, AGA, LYE, AGA),
    LIPLACK_LIQUOR(1900, LYE, LYE, LYE),
    MEGALITE_LIQUID(3150, MOX, LYE, LYE),
    ANTI_LEECH_LOTION(3400, AGA, LYE, LYE),
    MIXALOT(3650, MOX, AGA, LYE);

    private static final PotionType[] TYPES = PotionType.values();

    private final String recipe;
    private final int experience;
    private final PotionComponent[] components;

    PotionType(int experience, PotionComponent... components) {
        this.recipe = colorizeRecipe(components);
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

    public int experience() {
        return experience;
    }

    public PotionComponent[] components() {
        return components;
    }
}
