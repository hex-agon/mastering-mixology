package work.fking.masteringmixology.evaluator;

import work.fking.masteringmixology.PotionOrder;

public class BestExperienceEvaluator implements PotionOrderEvaluator {

    @Override
    public PotionOrder evaluate(EvaluatorContext context) {
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
        return bestOrder;
    }
}
