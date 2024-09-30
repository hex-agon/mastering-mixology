package work.fking.masteringmixology;

import java.util.Arrays;

import static work.fking.masteringmixology.PotionComponent.AGA;
import static work.fking.masteringmixology.PotionComponent.LYE;
import static work.fking.masteringmixology.PotionComponent.MOX;

public enum PotionType {
    MAMMOTH_MIGHT_MIX(60, 30011, MOX, MOX, MOX),
    MYSTIC_MANA_AMALGAM(60, 30012, MOX, MOX, AGA),
    MARLEYS_MOONLIGHT(60, 30013, MOX, MOX, LYE),
    ALCO_AUGMENTATOR(76, 30014, AGA, AGA, AGA),
    AZURE_AURA_MIX(68, 30016, AGA, AGA, MOX),
    AQUALUX_AMALGAM(72, 30015, AGA, LYE, AGA),
    LIPLACK_LIQUOR(86, 30017, LYE, LYE, LYE),
    MEGALITE_LIQUID(80, 30019, MOX, LYE, LYE),
    ANTI_LEECH_LOTION(84, 30018, AGA, LYE, LYE),
    MIXALOT(64, 30020, MOX, AGA, LYE);

    private static final PotionType[] TYPES = PotionType.values();

    private final String recipe;
    private final int levelReq;
    private final int itemId;
    private final int experience;
    private final PotionComponent[] components;

    PotionType(int levelReq, int itemId, PotionComponent... components) {
        this.recipe = colorizeRecipe(components);
        this.levelReq = levelReq;
        this.experience = Arrays.stream(components).mapToInt(PotionComponent::experience).sum();
        this.components = components;
        this.itemId = itemId;
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

    public int itemId() { return itemId; }

    public PotionComponent[] components() {
        return components;
    }
}
