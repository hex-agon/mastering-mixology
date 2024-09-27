package work.fking.masteringmixology.evaluator;

import work.fking.masteringmixology.PotionOrder;

public class BestExperienceEvaluator implements PotionOrderEvaluator {

    @Override
    public PotionOrder evaluate(EvaluatorContext context) {
        PotionOrder bestOrder = null;
        var bestExperience = 0;

        for (PotionOrder order : context.orders()) {
            var experience = order.potionType().experience();

            if (experience > bestExperience) {
                bestOrder = order;
                bestExperience = experience;
            }
        }
        return bestOrder;
    }
}
