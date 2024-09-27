package work.fking.masteringmixology.evaluator;

import work.fking.masteringmixology.PotionOrder;

import java.util.List;

public interface PotionOrderEvaluator {

    /**
     * Evaluates all the potion orders and determines which one should be highlighted.
     * Must always return one of the potion orders.
     *
     * @param context The context containing the potion orders and additional player related information.
     * @return The potion order to be highlighted.
     */
    PotionOrder evaluate(EvaluatorContext context);

    class EvaluatorContext {

        private final List<PotionOrder> orders;
        private final int lyeResin;
        private final int agaResin;
        private final int moxResin;

        public EvaluatorContext(List<PotionOrder> orders, int lyeResin, int agaResin, int moxResin) {
            this.orders = orders;
            this.lyeResin = lyeResin;
            this.agaResin = agaResin;
            this.moxResin = moxResin;
        }

        public List<PotionOrder> orders() {
            return orders;
        }

        public int lyeResin() {
            return lyeResin;
        }

        public int agaResin() {
            return agaResin;
        }

        public int moxResin() {
            return moxResin;
        }
    }
}
