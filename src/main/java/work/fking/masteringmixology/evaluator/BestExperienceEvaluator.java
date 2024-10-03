package work.fking.masteringmixology.evaluator;

import work.fking.masteringmixology.PotionOrder;

import java.util.ArrayList;
import java.util.List;

public class BestExperienceEvaluator implements PotionOrderEvaluator {

    @Override
    public List<PotionOrder> evaluate(EvaluatorContext context) {
        List<PotionOrder> bestPotionOrders = new ArrayList<>();
        PotionOrder bestOrder = null;
        var bestExperience = 0;

        for (PotionOrder order : context.orders()) {
            var potionExperience = order.potionType().experience();
            var modifierExperience = order.potionModifier().quickActionExperience();
            var totalExperience = potionExperience + modifierExperience;

            if (totalExperience > bestExperience) {
                bestOrder = order;
                bestExperience = totalExperience;
            }
        }
        if (bestOrder != null) {
            bestPotionOrders.add(bestOrder);
        }
        return bestPotionOrders;
    }
}
