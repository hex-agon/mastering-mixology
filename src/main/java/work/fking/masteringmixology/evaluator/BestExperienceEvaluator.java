package work.fking.masteringmixology.evaluator;

import work.fking.masteringmixology.PotionOrder;

import java.util.HashMap;
import java.util.Map;

public class BestExperienceEvaluator implements PotionOrderEvaluator {

    @Override
    public Map<PotionOrder, Integer> evaluate(EvaluatorContext context) {

        Map<PotionOrder, Integer> scores = new HashMap<>();
        for (PotionOrder order : context.orders()) {
            var potionExperience = order.potionType().experience();
            var modifierExperience = order.potionModifier().quickActionExperience();
            var totalExperience = potionExperience + modifierExperience;

            scores.put(order, totalExperience);
        }
        return scores;
    }
}
