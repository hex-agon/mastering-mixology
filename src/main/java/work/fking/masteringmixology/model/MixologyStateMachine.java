package work.fking.masteringmixology.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.Getter;
import net.runelite.api.Client;
import work.fking.masteringmixology.MasteringMixologyConfig;
import work.fking.masteringmixology.enums.MixologyState;
import work.fking.masteringmixology.enums.Potion;
import work.fking.masteringmixology.enums.PotionComponent;
import work.fking.masteringmixology.enums.RefinementType;

@Singleton
public class MixologyStateMachine
{
	@Inject
	private Client client;

	@Inject
	private MasteringMixologyConfig config;

	private MixologyOrder order = MixologyOrder.EMPTY;

	@Getter
	private MixologyState state = MixologyState.WAITING_TO_START;

	@Getter
	private MixologyVariablesSnapshot variablesSnapshot = MixologyVariablesSnapshot.EMPTY;

	@Getter
	private boolean isStarted = false;

	@Getter
	private final Map<PotionComponent, Integer> leversToPullMap = new HashMap<>();

	public Potion getTargetPotion()
	{
		return order.mostValuablePotion;
	}

	public RefinementType getTargetRefinementType()
	{
		return order.mostValuablePotionRefinement;
	}

	public void stop()
	{
		isStarted = false;
		order = MixologyOrder.EMPTY;
		state = MixologyState.WAITING_TO_START;
		variablesSnapshot = MixologyVariablesSnapshot.EMPTY;
	}

	public void start()
	{
		isStarted = true;
		order = MixologyOrder.fromVarbits(client, config.potionSelectionStrategy());
		state = MixologyState.MIXING;
		variablesSnapshot = MixologyVariablesSnapshot.fromVarbits(client);

		processMixingState(variablesSnapshot);
	}

	public void onTickUpdate()
	{
		if (!isStarted)
		{
			return;
		}

		if (state == MixologyState.MIXING)
		{
			updateLeversToPull();
		}
	}

	public void onVarbitUpdate()
	{
		var orderFromVarbits = MixologyOrder.fromVarbits(client, config.potionSelectionStrategy());
		var variablesFromVarbits = MixologyVariablesSnapshot.fromVarbits(client);

		if (!order.doesEqual(orderFromVarbits) || !order.isValidOrder)
		{
			// RESET
			order = orderFromVarbits;
			state = MixologyState.MIXING;
			variablesSnapshot = variablesFromVarbits;
		}


		switch (state)
		{
			case MIXING:
				processMixingState(variablesFromVarbits);
				break;
			case MIX_READY:
				processPotionReadyState(variablesFromVarbits);
				break;
			case READY_TO_REFINE:
				processReadyToRefineState(variablesFromVarbits);
				break;
			case REFINING:
				processRefiningState(variablesFromVarbits);
				break;
		}

		variablesSnapshot = variablesFromVarbits;

	}

	private void updateLeversToPull()
	{
		var targetPotion = getTargetPotion();

		var leversToPull = new ArrayList<>(Arrays.asList(targetPotion.firstComponent, targetPotion.secondComponent,
			targetPotion.thirdComponent));

		for (var component : PotionComponent.values())
		{
			leversToPullMap.put(component, 0);
		}

		if (leversToPull.remove(variablesSnapshot.componentInFirstMixer))
		{
			leversToPull.remove(variablesSnapshot.componentInSecondMixer);
		}

		for (var component : leversToPull)
		{
			leversToPullMap.put(component, leversToPullMap.get(component) + 1);
		}
	}

	private void processMixingState(MixologyVariablesSnapshot nextSnapshot)
	{
		updateLeversToPull();
		if (nextSnapshot.potionInVessel == order.mostValuablePotion)
		{
			state = MixologyState.MIX_READY;
		}
	}

	private void processPotionReadyState(MixologyVariablesSnapshot nextSnapshot)
	{
		if (nextSnapshot.potionInVessel == Potion.NONE)
		{
			state = MixologyState.READY_TO_REFINE;
		}
		else if (nextSnapshot.potionInVessel != variablesSnapshot.potionInVessel)
		{
			state = MixologyState.MIXING;
		}
	}

	private void processReadyToRefineState(MixologyVariablesSnapshot nextSnapshot)
	{
		switch (order.mostValuablePotionRefinement)
		{
			case AGITATOR:
				if (variablesSnapshot.agitatorLevel < nextSnapshot.agitatorLevel)
				{
					state = MixologyState.REFINING;
				}
			case ALEMBIC:
				if (variablesSnapshot.alembicLevel < nextSnapshot.alembicLevel)
				{
					state = MixologyState.REFINING;
				}
			case RETORT:
				if (variablesSnapshot.retortLevel < nextSnapshot.retortLevel)
				{
					state = MixologyState.REFINING;
				}
		}
	}


	private void processRefiningState(MixologyVariablesSnapshot nextSnapshot)
	{
		switch (order.mostValuablePotionRefinement)
		{
			case AGITATOR:
				if (variablesSnapshot.agitatorLevel > 9 && nextSnapshot.agitatorLevel == 0)
				{
					state = MixologyState.READY_TO_DEPOSIT;
				}
				break;
			case ALEMBIC:
				if (variablesSnapshot.alembicLevel > 9 && nextSnapshot.alembicLevel == 0)
				{
					state = MixologyState.READY_TO_DEPOSIT;
				}
				break;
			case RETORT:
				if (variablesSnapshot.retortLevel > 9 && nextSnapshot.retortLevel == 0)
				{
					state = MixologyState.READY_TO_DEPOSIT;
				}
				break;
		}
	}
}
