package work.fking.masteringmixology;

import java.util.Arrays;

import static work.fking.masteringmixology.PotionComponent.AGA;
import static work.fking.masteringmixology.PotionComponent.LYE;
import static work.fking.masteringmixology.PotionComponent.MOX;

public enum PotionType {
    MAMMOTH_MIGHT_MIX(60, MOX, MOX, MOX),
    MYSTIC_MANA_AMALGAM(60, MOX, MOX, AGA),
    MARLEYS_MOONLIGHT(60, MOX, MOX, LYE),
    ALCO_AUGMENTATOR(76, AGA, AGA, AGA),
    AZURE_AURA_MIX(68, AGA, AGA, MOX),
    AQUALUX_AMALGAM(72, AGA, LYE, AGA),
    LIPLACK_LIQUOR(86, LYE, LYE, LYE),
    MEGALITE_LIQUID(80, MOX, LYE, LYE),
    ANTI_LEECH_LOTION(84, AGA, LYE, LYE),
    MIXALOT(64, MOX, AGA, LYE);

    private static final PotionType[] TYPES = PotionType.values();

    private final String recipe;
    private final int levelReq;
    private final int experience;
    private final PotionComponent[] components;

    PotionType(int levelReq, PotionComponent... components) {
        this.recipe = colorizeRecipe(components);
        this.levelReq = levelReq;
        this.experience = Arrays.stream(components).mapToInt(PotionComponent::experience).sum();
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

    public static void main(String[] args) {
        for (var type : TYPES) {
            var sum = Arrays.stream(type.components).mapToInt(c -> c.ordinal() + 1).sum();
            System.out.println(type + " - " + sum);
        }
    }
}
