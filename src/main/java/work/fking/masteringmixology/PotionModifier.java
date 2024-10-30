package work.fking.masteringmixology;

public enum PotionModifier {
    HOMOGENOUS(AlchemyObject.AGITATOR, 5674),
    CONCENTRATED(AlchemyObject.RETORT, 5672),
    CRYSTALISED(AlchemyObject.ALEMBIC, 5673);

    private static final PotionModifier[] TYPES = PotionModifier.values();

    private final AlchemyObject alchemyObject;
    private final int spriteId;

    PotionModifier(AlchemyObject alchemyObject, int spriteId) {
        this.alchemyObject = alchemyObject;
        this.spriteId = spriteId;
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

    public int spriteId() {
        return spriteId;
    }
}
