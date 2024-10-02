package work.fking.masteringmixology.evaluator;

import work.fking.masteringmixology.PotionComponent;
import work.fking.masteringmixology.PotionOrder;
import work.fking.masteringmixology.PotionType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FavorComponentEvaluator implements PotionOrderEvaluator {

    private final PotionComponent favoredComponent;

    public FavorComponentEvaluator(PotionComponent favoredComponent) {
        this.favoredComponent = favoredComponent;
    }

    @Override
    public Map<PotionOrder, Integer> evaluate(EvaluatorContext context) {
        Map<PotionOrder, Integer> scores = new HashMap<>();
        for (var order : context.orders()) {
            scores.put(order, computePotionScore(order.potionType()));
        }
        return scores;
    }

    private int computePotionScore(PotionType potionType) {
        int score = 0;
        score += 2 * potionType.getReward(favoredComponent);
        score += Arrays.stream(PotionComponent.values()).mapToInt(potionType::getReward).sum();
        return score;
    }
}
