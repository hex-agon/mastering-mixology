package work.fking.masteringmixology.overlays;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.DecorativeObject;
import net.runelite.api.GameObject;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.TileObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;
import work.fking.masteringmixology.MasteringMixologyConfig;
import work.fking.masteringmixology.UIHelper;
import work.fking.masteringmixology.enums.PotionComponent;
import work.fking.masteringmixology.enums.RefinementType;
import work.fking.masteringmixology.model.MixologyStateMachine;
import work.fking.masteringmixology.model.MixologyStats;

public class MasteringMixologyOverlay3D extends Overlay
{
	@Inject
	private ModelOutlineRenderer modelOutlineRenderer;

	@Inject
	private MixologyStateMachine state;

	@Inject
	private MixologyStats stats;

	@Inject
	private MasteringMixologyConfig config;

	@Inject
	private UIHelper uiHelper;

	@Inject
	private Client client;

	public Set<GameObject> conveyorBelts = new HashSet<>();

	public GameObject lyeLever;
	public DecorativeObject agaLever;
	public GameObject moxLever;

	public GameObject alembic;
	public GameObject agitator;
	public GameObject retort;

	public GameObject vessel;
	public GameObject hopper;

	@Override
	public Dimension render(Graphics2D graphics) {
		if (!state.isStarted()) {
			return null;
		}

		switch (state.getState()) {
			case MIXING:
				outlineLevers(graphics);
				outlineHopper();
				break;
			case MIX_READY:
				outlineVessel();
				outlineRefinery(true);
				outlineHopper();
				break;
			case READY_TO_REFINE:
			case REFINING:
				outlineRefinery(false);
				break;
			case READY_TO_DEPOSIT:
				drawConveyorBelt();
				break;
		}

		outlineDigweed();

		return null;
	}

	private void outlineDigweed() {
		if (!config.isDigweedHighlightEnabled() || !uiHelper.isMatureDigweedPresent()) {
			return;
		}

		outlineObject(uiHelper.getMatureDigweedObjectOrNull(), config.digweedOutline());
	}

	private void outlineRefinery(boolean preDraw) {
		if (agitator == null || alembic == null || retort == null) {
			return;
		}

		var targetRefinery = state.getTargetRefinementType() == RefinementType.AGITATOR ? agitator :
			state.getTargetRefinementType() == RefinementType.ALEMBIC ? alembic : retort;

		if (targetRefinery == alembic && config.isStationSpeedupHighlightEnabled() && uiHelper.isAlembicSpeedupObjectPresent()) {
			outlineObject(targetRefinery, config.refinerySpeedupOutline());
			return;
		}
		else if (targetRefinery == agitator && config.isStationSpeedupHighlightEnabled() && uiHelper.isAgitatorSpeedupObjectPresent()) {
			outlineObject(targetRefinery, config.refinerySpeedupOutline());
			return;
		}

		if (!config.isStationHighlightEnabled()) {
			return;
		}

		outlineObject(targetRefinery, preDraw ? config.refineryPreOutline() : config.refineryOutline());
	}

	private void outlineVessel() {
		if (vessel == null || !config.isVesselHighlightEnabled()) {
			return;
		}

		outlineObject(vessel, config.vesselOutline());
	}

	private void outlineLevers(Graphics2D graphics) {
		var pullCountMap = state.getLeversToPullMap();
		var componentsToAdd = pullCountMap.keySet();

		for (var component : componentsToAdd) {
			if (component == PotionComponent.LYE && lyeLever != null && pullCountMap.get(component) != 0) {
				outlineTargetLever(lyeLever, config.lyeLeverOutline());
				drawLeverPullCount(lyeLever, pullCountMap.get(component), config.lyeLeverOutline(), graphics);
			} else if (component == PotionComponent.AGA && agaLever != null && pullCountMap.get(component) != 0) {
				outlineTargetLever(agaLever, config.agaLeverOutline());
				drawLeverPullCount(agaLever, pullCountMap.get(component), config.agaLeverOutline(), graphics);
			} else if (component == PotionComponent.MOX && moxLever != null && pullCountMap.get(component) != 0) {
				outlineTargetLever(moxLever, config.moxLeverOutline());
				drawLeverPullCount(moxLever, pullCountMap.get(component), config.moxLeverOutline(), graphics);
			}
		}
	}

	private void outlineTargetLever(TileObject lever, Color color) {
		if (!config.isLeverHighlightEnabled()) {
			return;
		}

		outlineObject(lever, color);
	}

	private void drawLeverPullCount(TileObject targetLever, Integer pullCount, Color color, Graphics2D graphics)
	{
		if (!config.isLeverPullCountTextEnabled() || pullCount == null)
		{
			return;
		}

		String text = String.format("%dx", pullCount);
		LocalPoint crucibleLoc = targetLever.getLocalLocation();
		crucibleLoc = new LocalPoint(crucibleLoc.getX(), crucibleLoc.getY());
		Point pos = Perspective.getCanvasTextLocation(client, graphics, crucibleLoc, text, 250);
		OverlayUtil.renderTextLocation(graphics, pos, text, color);
	}

	private void drawConveyorBelt() {
		if (!config.isConveyorBeltHighlightEnabled()) {
			return;
		}

		for (var conveyorBelt : conveyorBelts) {
			outlineObject(conveyorBelt, config.conveyorBeltOutline());
		}
	}

	private void outlineHopper() {
		if (hopper == null || !isHopperOutlineNeeded()) {
			return;
		}

		outlineObject(hopper, config.hopperDepositNeededOutline());
	}

	private void outlineObject(TileObject object, Color color) {
		modelOutlineRenderer.drawOutline(object, config.borderWidth(), color, config.outlineFeather());
	}

	private boolean isHopperOutlineNeeded() {
		var potion = state.getTargetPotion();
		return potion.moxValue > stats.getHopperMoxCount() || potion.agaValue > stats.getHopperAgaCount() || potion.lyeValue > stats.getHopperLyeCount();
	}

}
