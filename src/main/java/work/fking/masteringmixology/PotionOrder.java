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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PotionOrder)) {
            return false;
        }
        PotionOrder other = (PotionOrder) o;
        return idx == other.idx && potionType == other.potionType && potionModifier == other.potionModifier;
    }

    @Override
    public int hashCode() {
        return idx + 31 * potionType.hashCode() + 31 * 31 * potionModifier.hashCode();
    }

    @Override
    public String toString() {
        return "PotionOrder{" +
                "idx=" + idx +
                ", potionType=" + potionType +
                ", potionModifier=" + potionModifier +
                '}';
    }
}
