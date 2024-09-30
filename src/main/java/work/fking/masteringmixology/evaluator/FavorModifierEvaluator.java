package work.fking.masteringmixology.evaluator;

import work.fking.masteringmixology.PotionModifier;
import work.fking.masteringmixology.PotionOrder;

import java.util.ArrayList;
import java.util.List;

public class FavorModifierEvaluator implements PotionOrderEvaluator {

    private final PotionModifier modifier;

    public FavorModifierEvaluator(PotionModifier modifier) {
        this.modifier = modifier;
    }

    @Override
    public List<PotionOrder> evaluate(EvaluatorContext context) {
        List<PotionOrder> bestPotionOrders = new ArrayList<>();
        for (var order : context.orders()) {
            if (order.potionModifier() == modifier) {
                bestPotionOrders.add(order);
            }
        }
        return bestPotionOrders;
    }
}
