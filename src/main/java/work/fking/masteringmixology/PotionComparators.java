package work.fking.masteringmixology;

import java.util.Comparator;

public class PotionComparators {

    // Sort by modifier, in the order CRYSTALISED > HOMOGENOUS > CONCENTRATED
    // And then by PotionType name alphabetically
    public static Comparator<PotionOrder> byStation() {
        return Comparator.comparingInt((PotionOrder order) -> {
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
                .thenComparing(order -> order.potionType().name());
    }
}
