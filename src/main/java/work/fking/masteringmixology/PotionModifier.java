package work.fking.masteringmixology;

public enum PotionModifier {
    // Clicking the quick-time event on the Agitator gives 14 experience
    HOMOGENOUS(AlchemyObject.AGITATOR, 14),
    // Each click on the Retort gives 2 experience for a max of 7 clicks
    CONCENTRATED(AlchemyObject.RETORT, 14),
    // Clicking the quick-time event on the Alembic gives 14 experience
    CRYSTALISED(AlchemyObject.ALEMBIC, 14);

    private static final PotionModifier[] TYPES = PotionModifier.values();

    private final AlchemyObject alchemyObject;
    private final int quickActionExperience;

    PotionModifier(AlchemyObject alchemyObject, int quickActionExperience) {
        this.alchemyObject = alchemyObject;
        this.quickActionExperience = quickActionExperience;
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

    public int quickActionExperience() {
        return quickActionExperience;
    }
}
