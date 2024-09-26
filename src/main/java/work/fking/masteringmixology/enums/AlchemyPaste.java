package work.fking.masteringmixology.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.ItemID;

@AllArgsConstructor
public enum AlchemyPaste {
    NONE(0, 0, ""),
    MOX(1, ItemID.MOX_PASTE, "Mox"),
    AGA(2, ItemID.AGA_PASTE, "Aga"),
    LYE(3, ItemID.LYE_PASTE, "Lye");

    @Getter
    private final int PasteId;

    @Getter
    private final int ItemId;

    @Getter
    private final String Name;

    public static AlchemyPaste FromId(int id) {
        switch(id) {
            case 1:
                return MOX;
            case 2:
                return AGA;
            case 3:
                return LYE;
            default:
                return NONE;
        }
    }
}
