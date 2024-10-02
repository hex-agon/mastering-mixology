package work.fking.masteringmixology;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static work.fking.masteringmixology.PotionComponent.AGA;
import static work.fking.masteringmixology.PotionComponent.LYE;
import static work.fking.masteringmixology.PotionComponent.MOX;

public enum PotionType {
    // Multiply exp by 10 to account for the fact that exp has 1 hidden decimal point of precision
    MAMMOTH_MIGHT_MIX(60, 30011, 1900, MOX, MOX, MOX),
    MYSTIC_MANA_AMALGAM(63, 30012, 2150, MOX, MOX, AGA),
    MARLEYS_MOONLIGHT(66, 30013, 2400, MOX, MOX, LYE),
    ALCO_AUGMENTATOR(60, 30014, 1900, AGA, AGA, AGA),
    AZURE_AURA_MIX(69, 30016, 2650, AGA, AGA, MOX),
    AQUALUX_AMALGAM(72, 30015, 2900, AGA, LYE, AGA),
    LIPLACK_LIQUOR(60, 30017, 1900, LYE, LYE, LYE),
    MEGALITE_LIQUID(75, 30019, 3150, MOX, LYE, LYE),
    ANTI_LEECH_LOTION(78, 30018, 3400, AGA, LYE, LYE),
    MIXALOT(81, 30020, 3650, MOX, AGA, LYE);

    private static final PotionType[] TYPES = PotionType.values();
    public static final Set<Integer> ALL_POTION_IDS = new HashSet<>();

    static {
        for (PotionType type : TYPES) {
            ALL_POTION_IDS.add(type.itemId());
        }
    }

    private final String recipe;
    private final int levelReq;
    private final int itemId;
    private final int experience;
    private final Map<PotionComponent, Integer> pointRewards = new EnumMap<>(PotionComponent.class);
    private final PotionComponent[] components;

    PotionType(int levelReq, int itemId, int experience, PotionComponent... components) {
        this.recipe = colorizeRecipe(components);
        this.levelReq = levelReq;
        this.experience = experience;
        this.components = components;
        this.itemId = itemId;
        switch (this) {
            case MAMMOTH_MIGHT_MIX:
                pointRewards.put(MOX, 2);
                pointRewards.put(AGA, 0);
                pointRewards.put(LYE, 0);
                break;
            case MYSTIC_MANA_AMALGAM:
                pointRewards.put(MOX, 2);
                pointRewards.put(AGA, 1);
                pointRewards.put(LYE, 0);
                break;
            case MARLEYS_MOONLIGHT:
                pointRewards.put(MOX, 2);
                pointRewards.put(AGA, 0);
                pointRewards.put(LYE, 1);
                break;
            case ALCO_AUGMENTATOR:
                pointRewards.put(MOX, 0);
                pointRewards.put(AGA, 2);
                pointRewards.put(LYE, 0);
                break;
            case AZURE_AURA_MIX:
                pointRewards.put(MOX, 1);
                pointRewards.put(AGA, 2);
                pointRewards.put(LYE, 0);
                break;
            case AQUALUX_AMALGAM:
                pointRewards.put(MOX, 0);
                pointRewards.put(AGA, 2);
                pointRewards.put(LYE, 1);
                break;
            case LIPLACK_LIQUOR:
                pointRewards.put(MOX, 0);
                pointRewards.put(AGA, 0);
                pointRewards.put(LYE, 2);
                break;
            case MEGALITE_LIQUID:
                pointRewards.put(MOX, 1);
                pointRewards.put(AGA, 0);
                pointRewards.put(LYE, 2);
                break;
            case ANTI_LEECH_LOTION:
                pointRewards.put(MOX, 0);
                pointRewards.put(AGA, 1);
                pointRewards.put(LYE, 2);
                break;
            case MIXALOT:
                pointRewards.put(MOX, 2);
                pointRewards.put(AGA, 2);
                pointRewards.put(LYE, 2);
                break;
        }
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

    /** How much of a particular component is rewarded when an order with this type is fulfilled */
    public int getReward(PotionComponent component) {
        return pointRewards.getOrDefault(component, 0);
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

    public int itemId() {
        return itemId;
    }
}
