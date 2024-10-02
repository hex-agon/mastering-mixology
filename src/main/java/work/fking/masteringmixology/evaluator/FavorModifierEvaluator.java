package work.fking.masteringmixology.evaluator;

import work.fking.masteringmixology.PotionModifier;
import work.fking.masteringmixology.PotionOrder;

import java.util.HashMap;
import java.util.Map;

public class FavorModifierEvaluator implements PotionOrderEvaluator {

    private final PotionModifier modifier;

    public FavorModifierEvaluator(PotionModifier modifier) {
        this.modifier = modifier;
    }

    @Override
    public Map<PotionOrder, Integer> evaluate(EvaluatorContext context) {
        Map<PotionOrder, Integer> scores = new HashMap<>();
        for (var order : context.orders()) {
            if (order.potionModifier() == modifier) {
                scores.put(order, 1);
            } else {
                scores.put(order, 0);
            }
        }
        return scores;
    }
}
