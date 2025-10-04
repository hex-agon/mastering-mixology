package work.fking.masteringmixology;

import org.apache.commons.text.WordUtils;

public enum HerbMox
{
    GUAM(10),
    MARRENTILL(13),
    TARROMIN(15),
    HARRALANDER(20),
    ;

    private final int amount;
    HerbMox(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public String toString()
    {
        return WordUtils.capitalizeFully(name().toLowerCase().replace('_', ' '));
    }
}
