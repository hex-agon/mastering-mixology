package work.fking.masteringmixology.model;

import java.util.Comparator;
import java.util.stream.Stream;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import org.apache.commons.lang3.tuple.Pair;
import work.fking.masteringmixology.constants.MixologyVarbits;
import work.fking.masteringmixology.enums.Potion;
import work.fking.masteringmixology.enums.PotionSelectionStrategy;
import work.fking.masteringmixology.enums.RefinementType;

public class MixologyOrder
{
	public static final MixologyOrder EMPTY = new MixologyOrder(Potion.NONE, Potion.NONE, Potion.NONE, RefinementType.NONE,
		RefinementType.NONE, RefinementType.NONE, 60, PotionSelectionStrategy.REWARD_SHOP_BALANCE);

	// High base value to ensure priority
	private static final int RETORT_REFINERY_VALUE = 10000;

	public final Potion firstPotion;
	public final Potion secondPotion;
	public final Potion thirdPotion;

	public final RefinementType firstPotionRefinement;
	public final RefinementType secondPotionRefinement;
	public final RefinementType thirdPotionRefinement;

	public final Potion mostValuablePotion;
	public final RefinementType mostValuablePotionRefinement;
	public final boolean isValidOrder;

	private final int playerHerbloreLevel;
	private final PotionSelectionStrategy strategy;


	public MixologyOrder(Potion firstPotion, Potion secondPotion, Potion thirdPotion, RefinementType firstPotionRefinement,
						 RefinementType secondPotionRefinement, RefinementType thirdPotionRefinement, int playerHerbloreLevel,
						 PotionSelectionStrategy strategy)
	{
		this.firstPotion = firstPotion;
		this.secondPotion = secondPotion;
		this.thirdPotion = thirdPotion;
		this.firstPotionRefinement = firstPotionRefinement;
		this.secondPotionRefinement = secondPotionRefinement;
		this.thirdPotionRefinement = thirdPotionRefinement;
		this.playerHerbloreLevel = playerHerbloreLevel;
		this.strategy = strategy;

		var potionStream = Stream.of(Pair.of(firstPotion, firstPotionRefinement), Pair.of(secondPotion, secondPotionRefinement),
			Pair.of(thirdPotion, thirdPotionRefinement));
		var targetPair =
			potionStream.filter(it -> it.getLeft().herbloreLevel <= playerHerbloreLevel).max(Comparator.comparingInt(p -> selectPotionComparisonProperty(p, strategy)));

		if (targetPair.isPresent())
		{
			mostValuablePotion = targetPair.get().getLeft();
			mostValuablePotionRefinement = targetPair.get().getRight();
		}
		else
		{
			mostValuablePotion = Potion.NONE;
			mostValuablePotionRefinement = RefinementType.NONE;
		}

		isValidOrder =
			firstPotion != Potion.NONE && secondPotion != Potion.NONE && thirdPotion != Potion.NONE &&
				firstPotionRefinement != RefinementType.NONE &&
				secondPotionRefinement != RefinementType.NONE &&
				thirdPotionRefinement != RefinementType.NONE &&
				mostValuablePotion != Potion.NONE &&
				mostValuablePotionRefinement != RefinementType.NONE;
	}

	public static MixologyOrder fromVarbits(Client client, PotionSelectionStrategy strategy)
	{
		return new MixologyOrder(Potion.fromVarbitValue(client.getVarbitValue(MixologyVarbits.ORDER_FIRST_POTION)),
			Potion.fromVarbitValue(client.getVarbitValue(MixologyVarbits.ORDER_SECOND_POTION)),
			Potion.fromVarbitValue(client.getVarbitValue(MixologyVarbits.ORDER_THIRD_POTION)),
			RefinementType.fromVarbitValue(client.getVarbitValue(MixologyVarbits.ORDER_FIRST_POTION_REFINEMENT)),
			RefinementType.fromVarbitValue(client.getVarbitValue(MixologyVarbits.ORDER_SECOND_POTION_REFINEMENT)),
			RefinementType.fromVarbitValue(client.getVarbitValue(MixologyVarbits.ORDER_THIRD_POTION_REFINEMENT)),
			client.getBoostedSkillLevel(Skill.HERBLORE),
			strategy
		);
	}

	// TODO: Use lombok to generate equals
	public boolean doesEqual(MixologyOrder other)
	{
		return firstPotion == other.firstPotion &&
			secondPotion == other.secondPotion &&
			thirdPotion == other.thirdPotion &&
			firstPotionRefinement == other.firstPotionRefinement &&
			secondPotionRefinement == other.secondPotionRefinement &&
			thirdPotionRefinement == other.thirdPotionRefinement &&
			mostValuablePotion == other.mostValuablePotion &&
			mostValuablePotionRefinement == other.mostValuablePotionRefinement &&
			playerHerbloreLevel == other.playerHerbloreLevel &&
			strategy == other.strategy;
	}

	private static int selectPotionComparisonProperty(Pair<Potion, RefinementType> pair, PotionSelectionStrategy strategy)
	{
		var potion = pair.getLeft();
		var refinementType = pair.getRight();
		switch (strategy)
		{
			case PREFER_RETORT:
				return refinementType == RefinementType.RETORT ? RETORT_REFINERY_VALUE + potion.totalXP : potion.totalXP;
			case REWARD_SHOP_BALANCE:
				return potion.potionShopValue;
			case AGA:
				return potion.agaValue;
			case LYE:
				return potion.lyeValue;
			case MOX:
				return potion.moxValue;
			case HIGHEST_XP:
			default:
				return potion.totalXP;
		}
	}
}