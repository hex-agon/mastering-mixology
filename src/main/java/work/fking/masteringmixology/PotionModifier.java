package work.fking.masteringmixology;

public enum PotionModifier {
    HOMOGENOUS,
    CONCENTRATED,
    CRYSTALISED;

    private static final PotionModifier[] TYPES = PotionModifier.values();

    public static PotionModifier from(int potionModifierId) {
        if (potionModifierId < 0 || potionModifierId >= TYPES.length) {
            return null;
        }
        return TYPES[potionModifierId];
    }
}
