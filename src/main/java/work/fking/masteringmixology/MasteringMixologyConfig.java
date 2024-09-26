package work.fking.masteringmixology;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;
import work.fking.masteringmixology.enums.PotionSelectionStrategy;

@ConfigGroup(MasteringMixologyConfig.GROUP)
public interface MasteringMixologyConfig extends Config
{
	String GROUP = "masteringmixology";

	@ConfigSection(
		name = "Options",
		description = "Configuration for plugin behavior",
		position = 0
	)
	String optionsSection = "optionsSection";

	@ConfigSection(
		name = "Highlight colors",
		description = "Colors for highlighting various mixology objects",
		position = 1
	)
	String highlightColorSection = "highlightColorSection";


	@ConfigSection(
		name = "Highlight color style",
		description = "Styles for highlighting colors of mixology objects",
		position = 2
	)
	String highlightStyleSection = "highlightStyleSection";

	@ConfigItem(
		position = 0,
		keyName = "potionSelectionStrategy",
		name = "Potion strategy",
		description = "Which strategy should the plugin use to select the target potion to make. For Prefer Retort, XP is used as a " +
			"fallback value to select the best retort option, or the next best option if no retort is available",
		section = optionsSection
	)
	default PotionSelectionStrategy potionSelectionStrategy()
	{
		return PotionSelectionStrategy.HIGHEST_XP;
	}

	@ConfigItem(
		position = 1,
		keyName = "isOverlayEnabled",
		name = "Infobox enabled",
		description = "Toggles to display the minigame infobox or not",
		section = optionsSection
	)
	default boolean isOverlayEnabled()
	{
		return true;
	}

	@ConfigItem(
		position = 2,
		keyName = "isLeverHighlightEnabled",
		name = "Lever highlight",
		description = "Toggles to highlight the lever or not",
		section = optionsSection
	)
	default boolean isLeverHighlightEnabled()
	{
		return true;
	}

	@ConfigItem(
		position = 3,
		keyName = "isVesselHighlightEnabled",
		name = "Vessel highlight",
		description = "Toggles to highlight the vessel or not",
		section = optionsSection
	)
	default boolean isVesselHighlightEnabled()
	{
		return true;
	}

	@ConfigItem(
		position = 4,
		keyName = "isStationHighlightEnabled",
		name = "Refinery highlight",
		description = "Toggles to highlight the refinery stations or not. Speedup highlight has a different toggle!",
		section = optionsSection
	)
	default boolean isStationHighlightEnabled()
	{
		return true;
	}

	@ConfigItem(
		position = 5,
		keyName = "isStationSpeedupHighlightEnabled",
		name = "Refinery speedup highlight",
		description = "Toggles to highlight the refinery stations or not when speedup is possible",
		section = optionsSection
	)
	default boolean isStationSpeedupHighlightEnabled()
	{
		return true;
	}

	@ConfigItem(
		position = 6,
		keyName = "isStationSpeedupInfoboxHighlightEnabled",
		name = "Refinery speedup infobox highlight",
		description = "Toggles to highlight the infobox or not when refinery speedup is possible",
		section = optionsSection
	)
	default boolean isStationSpeedupInfoboxHighlightEnabled()
	{
		return true;
	}

	@ConfigItem(
		position = 7,
		keyName = "isConveyorBeltHighlightEnabled",
		name = "Conveyor belt highlight",
		description = "Toggles to highlight the conveyor belts or not",
		section = optionsSection
	)
	default boolean isConveyorBeltHighlightEnabled()
	{
		return true;
	}

	@ConfigItem(
		position = 8,
		keyName = "isEmptyHopperHighlightEnabled",
		name = "Low hopper highlight",
		description = "Toggles to highlight the low level hopper or not",
		section = optionsSection
	)
	default boolean isEmptyHopperHighlightEnabled()
	{
		return true;
	}

	@ConfigItem(
		position = 9,
		keyName = "isLeverPullCountTextEnabled",
		name = "Lever pull text",
		description = "Toggles to display the amount of pulls required on a lever or not",
		section = optionsSection
	)
	default boolean isLeverPullCountTextEnabled()
	{
		return true;
	}

	@ConfigItem(
		position = 10,
		keyName = "isDigweedHighlightEnabled",
		name = "Digweed highlight",
		description = "Toggles to highlight the mature digweed herb",
		section = optionsSection
	)
	default boolean isDigweedHighlightEnabled()
	{
		return true;
	}

	@ConfigItem(
		position = 11,
		keyName = "isDigweedInfoboxHighlightEnabled",
		name = "Digweed infobox highlight",
		description = "Toggles to highlight the infobox when mature digweed herb spawns",
		section = optionsSection
	)
	default boolean isDigweedInfoboxHighlightEnabled()
	{
		return true;
	}


	@Alpha
	@ConfigItem(
		position = 0,
		keyName = "moxLeverOutline",
		name = "Mox lever outline",
		description = "Color to use to outline the Mox lever",
		section = highlightColorSection
	)
	default Color moxLeverOutline()
	{
		return Color.BLUE;
	}

	@Alpha
	@ConfigItem(
		position = 1,
		keyName = "agaLeverOutline",
		name = "Aga lever outline",
		description = "Color to use to outline the Aga lever",
		section = highlightColorSection
	)
	default Color agaLeverOutline()
	{
		return Color.GREEN;
	}

	@Alpha
	@ConfigItem(
		position = 2,
		keyName = "lyeLeverOutline",
		name = "Lye lever outline",
		description = "Color to use to outline the Lye lever",
		section = highlightColorSection
	)
	default Color lyeLeverOutline()
	{
		return Color.RED;
	}

	@Alpha
	@ConfigItem(
		position = 3,
		keyName = "vesselOutline",
		name = "Vessel outline",
		description = "Color to use to outline the vessel",
		section = highlightColorSection
	)
	default Color vesselOutline()
	{
		return Color.YELLOW;
	}

	@Alpha
	@ConfigItem(
		position = 4,
		keyName = "refineryOutline",
		name = "Refinery outline",
		description = "Color to use to outline the refinery station",
		section = highlightColorSection
	)
	default Color refineryOutline()
	{
		return Color.YELLOW;
	}

	@Alpha
	@ConfigItem(
		position = 5,
		keyName = "refineryPreOutline",
		name = "Refinery pre outline",
		description = "Color to use to outline the refinery station when picking up the potion to pre-indicate which station to go to",
		section = highlightColorSection
	)
	default Color refineryPreOutline()
	{
		return new Color(255, 255, 0, 120);
	}

	@Alpha
	@ConfigItem(
		position = 6,
		keyName = "conveyorBeltOutline",
		name = "Conveyor belt outline",
		description = "Color to use to outline the conveyor belts",
		section = highlightColorSection
	)
	default Color conveyorBeltOutline()
	{
		return Color.YELLOW;
	}

	@Alpha
	@ConfigItem(
		position = 7,
		keyName = "hopperDepositNeededOutline",
		name = "Hopper deposit outline",
		description = "Color to use to outline the hopper when it's contents are insufficient for the mixed potion",
		section = highlightColorSection
	)
	default Color hopperDepositNeededOutline()
	{
		return Color.RED;
	}

	@Alpha
	@ConfigItem(
		position = 8,
		keyName = "refinerySpeedupOutline",
		name = "Refinery speedup outline",
		description = "Color to use to outline the agitator or alembic station when speedup action is possible",
		section = highlightColorSection
	)
	default Color refinerySpeedupOutline()
	{
		return Color.CYAN;
	}

	@Alpha
	@ConfigItem(
		position = 9,
		keyName = "refinerySpeedupInfoboxHighlight",
		name = "Refinery speedup infobox highlight",
		description = "Color to use to highlight the infobox when speedup action is possible",
		section = highlightColorSection
	)
	default Color refinerySpeedupInfoboxHighlight()
	{
		return new Color(20, 131, 137, 108);
	}

	@Alpha
	@ConfigItem(
		position = 10,
		keyName = "digweedOutline",
		name = "Digweed outline",
		description = "Color to use to outline the the mature digweed",
		section = highlightColorSection
	)
	default Color digweedOutline()
	{
		return new Color(50, 205, 50);
	}

	@Alpha
	@ConfigItem(
		position = 11,
		keyName = "digweedInfoboxHighlight",
		name = "Digweed infobox highlight",
		description = "Color to use to highlight the infobox when mature digweed spawns",
		section = highlightColorSection
	)
	default Color digweedInfoboxHighlight()
	{
		return new Color(32, 125, 32, 108);
	}

	@ConfigItem(
		position = 0,
		keyName = "outlineFeather",
		name = "Outline feather",
		description = "Specify between 0-4 how much of the model outline should be faded",
		section = highlightStyleSection
	)
	@Range(
		min = 0,
		max = 4
	)
	default int outlineFeather()
	{
		return 0;
	}

	@ConfigItem(
		position = 1,
		keyName = "borderWidth",
		name = "Border width",
		description = "Width of the object outline border",
		section = highlightStyleSection
	)
	@Range()
	default int borderWidth()
	{
		return 2;
	}
}
