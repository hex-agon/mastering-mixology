package work.fking.masteringmixology;

import work.fking.masteringmixology.evaluator.BestExperienceEvaluator;
import work.fking.masteringmixology.evaluator.FavorModifierEvaluator;
import work.fking.masteringmixology.evaluator.FavorComponentEvaluator;
import work.fking.masteringmixology.evaluator.PotionOrderEvaluator;

public enum Strategy {
    FAVOR_EXPERIENCE("Favor experience", new BestExperienceEvaluator()),
    FAVOR_ALEMBIC("Favor alembic", new FavorModifierEvaluator(PotionModifier.CRYSTALISED)),
    FAVOR_RETORT("Favor retort", new FavorModifierEvaluator(PotionModifier.CONCENTRATED)),
    FAVOR_MOX("Favor mox", new FavorComponentEvaluator(PotionComponent.MOX)),
    FAVOR_AGA("Favor aga", new FavorComponentEvaluator(PotionComponent.AGA)),
    FAVOR_LYE("Favor lye", new FavorComponentEvaluator(PotionComponent.LYE));

    private final String name;
    private final PotionOrderEvaluator evaluator;

    Strategy(String name, PotionOrderEvaluator evaluator) {
        this.name = name;
        this.evaluator = evaluator;
    }

    public PotionOrderEvaluator evaluator() {
        return evaluator;
    }

    @Override
    public String toString() {
        return name;
    }
}
