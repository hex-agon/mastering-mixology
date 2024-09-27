package work.fking.masteringmixology.evaluator;

import work.fking.masteringmixology.PotionModifier;
import work.fking.masteringmixology.PotionOrder;

public class FavorAlembicEvaluator implements PotionOrderEvaluator {

    @Override
    public PotionOrder evaluate(EvaluatorContext context) {
        for (var order : context.orders()) {
            if (order.potionModifier() == PotionModifier.CRYSTALISED) {
                return order;
            }
        }
        return null;
    }
}
