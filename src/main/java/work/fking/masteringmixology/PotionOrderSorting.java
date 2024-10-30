package work.fking.masteringmixology;

import java.util.Comparator;

public enum PotionOrderSorting {
    VANILLA("Vanilla (random)", null),
    BY_STATION("By station", Comparator.comparing(order -> order.potionModifier().ordinal())),
    SHORTEST_PATH("Shortest Path", Comparator.comparing(order -> {
        switch (order.potionModifier()) {
            case CRYSTALISED:
                return 1;
            case CONCENTRATED:
                return 2;
            case HOMOGENOUS:
                return 3;
            default:
                throw new IllegalStateException("Unexpected value: " + order.potionModifier().toString());
        }
    }));
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
