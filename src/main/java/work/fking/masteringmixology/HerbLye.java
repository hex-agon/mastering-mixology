package work.fking.masteringmixology;

import org.apache.commons.text.WordUtils;

public enum HerbLye
{
    RANARR_WEED(26),
    TOADFLAX(32),
    AVANTOE(30),
    KWUARM(33),
    SNAPDRAGON(40),
    ;

    private final int amount;
    HerbLye(int amount) {
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
