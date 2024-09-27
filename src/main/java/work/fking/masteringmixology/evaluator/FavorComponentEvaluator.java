package work.fking.masteringmixology.evaluator;

import work.fking.masteringmixology.PotionComponent;
import work.fking.masteringmixology.PotionOrder;
import work.fking.masteringmixology.PotionType;

public class FavorComponentEvaluator implements PotionOrderEvaluator {

    private final PotionComponent favoredComponent;

    public FavorComponentEvaluator(PotionComponent favoredComponent) {
        this.favoredComponent = favoredComponent;
    }

    @Override
    public PotionOrder evaluate(EvaluatorContext context) {
        PotionOrder bestOrder = null;
        var bestScore = 0;

        for (var order : context.orders()) {
            var score = computePotionScore(order.potionType());
            if (score > bestScore) {
                bestOrder = order;
                bestScore = score;
            }
        }
        return bestOrder;
    }

    private int computePotionScore(PotionType potionType) {
        var score = 0;

        for (var component : potionType.components()) {
            if (component == favoredComponent) {
                score += 3;
            } else {
                score++;
            }
        }
        return score;
    }
}
