package work.fking.masteringmixology.overlays;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ComponentConstants;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import work.fking.masteringmixology.MasteringMixologyConfig;
import work.fking.masteringmixology.UIHelper;
import work.fking.masteringmixology.enums.MixologyState;
import work.fking.masteringmixology.enums.PotionComponent;
import work.fking.masteringmixology.model.MixologyStateMachine;
import work.fking.masteringmixology.model.MixologyStats;

public class MasteringMixologyOverlay2D extends OverlayPanel
{
	private static final int PREFERRED_WIDTH = 375;
	private static final String MOX_COLOR = "0000FF";
	private static final String AGA_COLOR = "00FF00";
	private static final String LYE_COLOR = "FF0000";

	@Inject
	private MixologyStateMachine state;

	@Inject
	private MixologyStats stats;

	@Inject
	private MasteringMixologyConfig config;

	@Inject
	private UIHelper uiHelper;

	public MasteringMixologyOverlay2D()
	{
		super();
		setPosition(OverlayPosition.TOP_CENTER);
		setPreferredSize(new Dimension(PREFERRED_WIDTH, 0));
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!state.isStarted() || !config.isOverlayEnabled())
		{
			return super.render(graphics);
		}

		panelComponent.setBackgroundColor(ComponentConstants.STANDARD_BACKGROUND_COLOR);

		var targetPotion = state.getTargetPotion();

		panelComponent.getChildren().add(TitleComponent.builder()
			.text("Easy Mixology")
			.color(Color.GREEN)
			.build());

		panelComponent.getChildren().add(LineComponent.builder()
			.left("Player points")
			.right(colorCodeString(stats.getPlayerMoxCount() == -1 ? "?" : String.valueOf(stats.getPlayerMoxCount()), MOX_COLOR) + " / " +
				colorCodeString(stats.getPlayerAgaCount() == -1 ? "?" : String.valueOf(stats.getPlayerAgaCount()), AGA_COLOR) + " / " +
				colorCodeString(stats.getPlayerLyeCount() == -1 ? "?" : String.valueOf(stats.getPlayerLyeCount()), LYE_COLOR))
			.build());

		panelComponent.getChildren().add(LineComponent.builder()
			.left("Strategy")
			.right(config.potionSelectionStrategy().toString())
			.build());


		panelComponent.getChildren().add(LineComponent.builder()
			.left("Stage")
			.right(state.getState().toString())
			.build());

		panelComponent.getChildren().add(LineComponent.builder()
			.left("Refinery")
			.right(state.getTargetRefinementType().toString())
			.build());

		panelComponent.getChildren().add(LineComponent.builder()
			.left("Potion")
			.right(targetPotion.toString())
			.build());

		panelComponent.getChildren().add(LineComponent.builder()
			.left("Components")
			.right(colorCodePotionComponent(targetPotion.firstComponent) + " / " + colorCodePotionComponent(targetPotion.secondComponent) + " / " + colorCodePotionComponent(targetPotion.thirdComponent))
			.build());

		if (config.isStationSpeedupInfoboxHighlightEnabled() && state.getState() == MixologyState.REFINING && (uiHelper.isAgitatorSpeedupObjectPresent()) || uiHelper.isAlembicSpeedupObjectPresent())
		{
			panelComponent.setBackgroundColor(config.refinerySpeedupInfoboxHighlight());
		}

		if (config.isDigweedInfoboxHighlightEnabled() && uiHelper.isMatureDigweedPresent())
		{
			panelComponent.setBackgroundColor(config.digweedInfoboxHighlight());
		}

		return super.render(graphics);
	}

	private static String colorCodePotionComponent(PotionComponent component)
	{
		String color = component == PotionComponent.AGA ? AGA_COLOR : component == PotionComponent.LYE ? LYE_COLOR : MOX_COLOR;

		return colorCodeString(component.toString(), color);
	}

	private static String colorCodeString(String text, String colorCode)
	{
		return "<col=" + colorCode + ">" + text + "<col=FFFFFF>";
	}
}
