package work.fking.masteringmixology;

public enum PotionModifier {
    HOMOGENOUS(AlchemyObject.AGITATOR),
    CONCENTRATED(AlchemyObject.RETORT),
    CRYSTALISED(AlchemyObject.ALEMBIC);

    private static final PotionModifier[] TYPES = PotionModifier.values();

    private final AlchemyObject alchemyObject;

    PotionModifier(AlchemyObject alchemyObject) {
        this.alchemyObject = alchemyObject;
    }

    public static PotionModifier from(int potionModifierId) {
        if (potionModifierId < 0 || potionModifierId >= TYPES.length) {
            return null;
        }
        return TYPES[potionModifierId];
    }

    public AlchemyObject alchemyObject() {
        return alchemyObject;
    }
}
