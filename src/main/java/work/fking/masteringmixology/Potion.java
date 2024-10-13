package work.fking.masteringmixology;

public class Potion {
    public PotionType type;
    public PotionModifier modifier = null;
    public boolean isModified;
    public boolean hasDigweed = false;

    public Potion(PotionType type, boolean isModified) {
        this.type = type;
        this.isModified = isModified;
    }

    public void setModifier(PotionModifier modifier) {
        this.modifier = modifier;
        isModified = true;
    }

    public String toString() {
        var s = type.abbreviation();

        if (isModified) {
            if (modifier == null) {
                s += "???";
            } else if (modifier == PotionModifier.HOMOGENOUS) {
                s += "HOM";
            } else if (modifier == PotionModifier.CONCENTRATED) {
                s += "CON";
            } else if (modifier == PotionModifier.CRYSTALISED) {
                s += "CRY";
            }
        } else {
            s += "___";
        }

        if (hasDigweed) {
            s += "D";
        } else {
            s += "_";
        }

        return s;
    }
}
