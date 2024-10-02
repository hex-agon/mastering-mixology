package work.fking.masteringmixology.evaluator;

import work.fking.masteringmixology.PotionOrder;

import java.util.List;
import java.util.Map;

public interface PotionOrderEvaluator {

    /**
     * Evaluates all the potion orders and returns a map containing strategy scores for each order.
     *
     * @param context The context containing the potion orders and additional player related information.
     * @return Strategy scores for each order.
     */
    Map<PotionOrder, Integer> evaluate(EvaluatorContext context);

    class EvaluatorContext {

        private final List<PotionOrder> orders;

        public EvaluatorContext(List<PotionOrder> orders, int lyeResin, int agaResin, int moxResin) {
            this.orders = orders;
        }

        public List<PotionOrder> orders() {
            return orders;
        }
    }
}
