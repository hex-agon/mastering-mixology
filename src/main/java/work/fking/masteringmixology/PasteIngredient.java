package work.fking.masteringmixology;

import net.runelite.api.ItemID;

import java.util.HashMap;
import java.util.Map;

public enum PasteIngredient {
    MARRENTILL(PotionComponent.MOX, 13, ItemID.GRIMY_MARRENTILL, ItemID.MARRENTILL, ItemID.MARRENTILL_POTION_UNF),
    TARROMIN(PotionComponent.MOX, 15, ItemID.GRIMY_TARROMIN, ItemID.TARROMIN, ItemID.TARROMIN_POTION_UNF),
    GUAM(PotionComponent.MOX, 10, ItemID.GRIMY_GUAM_LEAF, ItemID.GUAM_LEAF, ItemID.GUAM_POTION_UNF),
    HARRALANDER(PotionComponent.MOX, 20, ItemID.GRIMY_HARRALANDER, ItemID.HARRALANDER, ItemID.HARRALANDER_POTION_UNF),
    LANTADYME(PotionComponent.AGA, 40, ItemID.GRIMY_LANTADYME, ItemID.LANTADYME, ItemID.LANTADYME_POTION_UNF),
    IRIT(PotionComponent.AGA, 30, ItemID.GRIMY_IRIT_LEAF, ItemID.IRIT_LEAF, ItemID.IRIT_POTION_UNF),
    DWARF(PotionComponent.AGA, 42, ItemID.GRIMY_DWARF_WEED, ItemID.DWARF_WEED, ItemID.DWARF_WEED_POTION_UNF),
    TORSTOL(PotionComponent.AGA, 44, ItemID.GRIMY_TORSTOL, ItemID.TORSTOL, ItemID.TORSTOL_POTION_UNF),
    TOADFLAX(PotionComponent.LYE, 32, ItemID.GRIMY_TOADFLAX, ItemID.TOADFLAX, ItemID.TOADFLAX_POTION_UNF),
    CADANTINE(PotionComponent.AGA, 34, ItemID.GRIMY_CADANTINE, ItemID.CADANTINE, ItemID.CADANTINE_POTION_UNF),
    KWUARM(PotionComponent.LYE, 33, ItemID.GRIMY_KWUARM, ItemID.KWUARM, ItemID.KWUARM_POTION_UNF),
    AVANTOE(PotionComponent.LYE, 30, ItemID.GRIMY_AVANTOE, ItemID.AVANTOE, ItemID.AVANTOE_POTION_UNF),
    SNAPDRAGON(PotionComponent.LYE, 40, ItemID.GRIMY_SNAPDRAGON, ItemID.SNAPDRAGON, ItemID.SNAPDRAGON_POTION_UNF),
    RANARR(PotionComponent.LYE, 26, ItemID.GRIMY_RANARR_WEED, ItemID.RANARR_WEED, ItemID.RANARR_POTION_UNF),
    HUASCA(PotionComponent.AGA, 20, ItemID.GRIMY_HUASCA, ItemID.HUASCA, ItemID.HUASCA_POTION_UNF);

    private static final Map<Integer, PasteIngredient> ITEM_ID_MAP = new HashMap<>();

    static {
        for (PasteIngredient pasteIngredient : values()) {
            ITEM_ID_MAP.put(pasteIngredient.grimyId, pasteIngredient);
            ITEM_ID_MAP.put(pasteIngredient.cleanId, pasteIngredient);
            ITEM_ID_MAP.put(pasteIngredient.unfPotionId, pasteIngredient);
        }
    }

    private final PotionComponent potionComponent;
    private final int pastePerItem;
    private final int grimyId;
    private final int cleanId;
    private final int unfPotionId;

    PasteIngredient(PotionComponent potionComponent, int pastePerItem, int grimyId, int cleanId, int unfPotionId) {
        this.potionComponent = potionComponent;
        this.pastePerItem = pastePerItem;
        this.grimyId = grimyId;
        this.cleanId = cleanId;
        this.unfPotionId = unfPotionId;
    }

    public static PasteIngredient getFromItemId(int itemId) {
        return ITEM_ID_MAP.get(itemId);
    }

    public PotionComponent potionComponent() {
        return potionComponent;
    }

    public int pastePerItem() {
        return pastePerItem;
    }
}