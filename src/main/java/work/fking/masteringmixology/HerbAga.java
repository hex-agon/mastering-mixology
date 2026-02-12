package work.fking.masteringmixology;

import org.apache.commons.text.WordUtils;

public enum HerbAga
{
    IRIT(30),
    HUASCA(20),
    CADANTINE(34),
    LANTADYME(40),
    DWARF_WEED(42),
    TORSTOL(44),
    ;

    private final int amount;
    HerbAga(int amount) {
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
