package work.fking.masteringmixology;
import java.util.Objects;

public class Potion {
    public final PotionType type;
    public PotionModifier modifier = null;
    public boolean isModified;
    public boolean hasDigweed = false;

    public Potion(PotionType type, boolean isModified) {
        this.type = type;
        this.isModified = isModified;
    }

    public void modify(PotionModifier modifier) {
        this.modifier = modifier;
        isModified = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o instanceof Potion) {
            Potion potion = (Potion) o;
            return isModified == potion.isModified &&
                    hasDigweed == potion.hasDigweed &&
                    type == potion.type &&
                    Objects.equals(modifier, potion.modifier);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, modifier, isModified, hasDigweed);
    }
}
