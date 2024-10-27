package work.fking.masteringmixology;

import java.util.Comparator;

public enum PotionOrderSorting {
    VANILLA("Vanilla (random)", null),
    BY_STATION("By station", Comparator.comparing(order -> order.potionModifier().ordinal()));

    private final String name;
    private final Comparator<PotionOrder> comparator;

    PotionOrderSorting(String name, Comparator<PotionOrder> comparator) {
        this.name = name;
        this.comparator = comparator;
    }

    public Comparator<PotionOrder> comparator() {
        return comparator;
    }

    @Override
    public String toString() {
        return name;
    }
}
