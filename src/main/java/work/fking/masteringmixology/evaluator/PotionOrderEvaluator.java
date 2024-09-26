package work.fking.masteringmixology.evaluator;

import work.fking.masteringmixology.PotionOrder;

import java.util.List;

public interface PotionOrderEvaluator {

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
