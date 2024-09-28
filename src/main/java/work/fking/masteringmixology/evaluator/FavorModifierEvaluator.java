package work.fking.masteringmixology.evaluator;

import work.fking.masteringmixology.PotionModifier;
import work.fking.masteringmixology.PotionOrder;

public class FavorModifierEvaluator implements PotionOrderEvaluator {

    private final PotionModifier modifier;

    public FavorModifierEvaluator(PotionModifier modifier) {
        this.modifier = modifier;
    }

    @Override
    public PotionOrder evaluate(EvaluatorContext context) {
        for (var order : context.orders()) {
            if (order.potionModifier() == modifier) {
                return order;
            }
        }
        return null;
    }
}
