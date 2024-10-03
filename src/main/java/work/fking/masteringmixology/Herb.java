package work.fking.masteringmixology;

import net.runelite.api.ItemID;

public enum Herb {
    MARRENTILL("Marrentill", PotionComponent.MOX, 13, ItemID.GRIMY_MARRENTILL, ItemID.MARRENTILL, ItemID.MARRENTILL_POTION_UNF),
    TARROMIN("Tarromin", PotionComponent.MOX, 15, ItemID.GRIMY_TARROMIN, ItemID.TARROMIN, ItemID.TARROMIN_POTION_UNF),
    GUAM("Guam leaf", PotionComponent.MOX, 10, ItemID.GRIMY_GUAM_LEAF, ItemID.GUAM_LEAF, ItemID.GUAM_POTION_UNF),
    HARRALANDER("Harralander", PotionComponent.MOX, 20, ItemID.GRIMY_HARRALANDER, ItemID.HARRALANDER, ItemID.HARRALANDER_POTION_UNF),
    LANTADYME("Lantadyme", PotionComponent.AGA, 40, ItemID.GRIMY_LANTADYME, ItemID.LANTADYME, ItemID.LANTADYME_POTION_UNF),
    IRIT("Irit leaf", PotionComponent.AGA, 30, ItemID.GRIMY_IRIT_LEAF, ItemID.IRIT_LEAF, ItemID.IRIT_POTION_UNF),
    DWARF("Dwarf weed", PotionComponent.AGA, 42, ItemID.GRIMY_DWARF_WEED, ItemID.DWARF_WEED, ItemID.DWARF_WEED_POTION_UNF),
    TORSTOL("Torstol", PotionComponent.AGA, 44, ItemID.GRIMY_TORSTOL, ItemID.TORSTOL, ItemID.TORSTOL_POTION_UNF),
    TOADFLAX("Toadflax", PotionComponent.LYE, 32, ItemID.GRIMY_TOADFLAX, ItemID.TOADFLAX, ItemID.TOADFLAX_POTION_UNF),
    CADANTINE("Cadantine", PotionComponent.AGA, 34, ItemID.GRIMY_CADANTINE, ItemID.CADANTINE, ItemID.CADANTINE_POTION_UNF),
    KWUARM("Kwuarm", PotionComponent.LYE, 33, ItemID.GRIMY_KWUARM, ItemID.KWUARM, ItemID.KWUARM_POTION_UNF),
    AVANTOE("Avantoe", PotionComponent.LYE, 30, ItemID.GRIMY_AVANTOE, ItemID.AVANTOE, ItemID.AVANTOE_POTION_UNF),
    SNAPDRAGON("Snapdragon", PotionComponent.LYE, 40, ItemID.GRIMY_SNAPDRAGON, ItemID.SNAPDRAGON, ItemID.SNAPDRAGON_POTION_UNF),
    RANARR("Ranarr weed", PotionComponent.LYE, 26, ItemID.GRIMY_RANARR_WEED, ItemID.RANARR_WEED, ItemID.RANARR_POTION_UNF),
    HUASCA("Huasca", PotionComponent.AGA, 20, ItemID.GRIMY_HUASCA, ItemID.HUASCA, ItemID.HUASCA_POTION_UNF);

    private static final Herb[] TYPES = Herb.values();

    private final String cleanHerbName;
    private final int[] itemIds;
    private final PotionComponent potionComponent;
    private final int pastePerHerb;

    Herb(String cleanHerbName, PotionComponent potionComponent, int pastePerHerb, int... itemIds) {
        this.cleanHerbName = cleanHerbName;
        this.potionComponent = potionComponent;
        this.pastePerHerb = pastePerHerb;
        this.itemIds = itemIds;
    }

    public static Herb getHerbFromItemId(int itemId) {
        for (Herb herb : TYPES) {
            for (int id : herb.itemIds) {
                if (id == itemId) {
                    return herb;
                }
            }
        }
        return null;
    }

    public String cleanHerbName() {
        return cleanHerbName;
    }

    public PotionComponent potionComponent() {
        return potionComponent;
    }

    public int pastePerHerb() {
        return pastePerHerb;
    }
}