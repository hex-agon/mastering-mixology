package work.fking.masteringmixology;

import java.util.Comparator;

public class PotionComparators {
    public static Comparator<PotionOrder> byStation() {
        return Comparator.comparing(order -> order.potionModifier().ordinal());
    }

    public static Comparator<PotionOrder> shortestPath() {
        return Comparator.comparing(order -> {
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
        });
    }
}
