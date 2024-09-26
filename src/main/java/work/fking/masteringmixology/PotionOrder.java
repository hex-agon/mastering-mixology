package work.fking.masteringmixology;

public class PotionOrder {

    private final int idx;
    private final PotionType potionType;
    private final PotionModifier potionModifier;

    public PotionOrder(int idx, PotionType potionType, PotionModifier potionModifier) {
        this.idx = idx;
        this.potionType = potionType;
        this.potionModifier = potionModifier;
    }

    public int idx() {
        return idx;
    }

    public PotionType potionType() {
        return potionType;
    }

    public PotionModifier potionModifier() {
        return potionModifier;
    }
}
