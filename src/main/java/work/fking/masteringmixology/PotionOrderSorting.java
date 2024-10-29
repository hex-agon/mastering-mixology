package work.fking.masteringmixology;

import java.util.Comparator;

public enum PotionOrderSorting {
    VANILLA("Vanilla (random)", null),
    // Sort by modifier, in the order CRYSTALISED > HOMOGENOUS > CONCENTRATED
    BY_STATION("By station", Comparator
            .comparingInt((PotionOrder order) -> {
                switch (order.potionModifier()) {
                    case CRYSTALISED:
                        return 0;
                    case HOMOGENOUS:
                        return 1;
                    case CONCENTRATED:
                        return 2;
                    default:
                        return Integer.MAX_VALUE;
                }
            })
            .thenComparing(order -> order.potionType().name()) // Secondary sorting by PotionType name alphabetically
    );

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
