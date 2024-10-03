package work.fking.masteringmixology;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import javax.inject.Singleton;
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

@Singleton
public class LeverHighlighter extends Overlay
{
	public final static int MIXER_LEFT = 11324;
	public final static int MIXER_MIDDLE = 11325;
	public final static int MIXER_RIGHT = 11326;

	private final Client client;
	private final MasteringMixologyConfig config;
	private final ModelOutlineRenderer modelOutlineRenderer;

	private PotionOrder order = null;

	@Inject
	LeverHighlighter(Client client, MasteringMixologyConfig config, ModelOutlineRenderer modelOutlineRenderer)
	{
		this.client = client;
		this.config = config;
		this.modelOutlineRenderer = modelOutlineRenderer;
	}

	GameObject lyeLever;
	DecorativeObject agaLever;
	GameObject moxLever;

	private void outlineTargetLever(TileObject lever, Color color)
	{
		outlineObject(lever, color);
	}

	private void outlineObject(TileObject object, Color color)
	{
		modelOutlineRenderer.drawOutline(object, config.highlightBorderWidth(), color, config.highlightFeather());
	}

	private void updateLeversToPull(Graphics2D graphics)
	{
		if (order == null)
		{
			return;
		}

		PotionComponent left = PotionComponent.fromVarbitValue(client.getVarbitValue(MIXER_LEFT));
		PotionComponent middle = PotionComponent.fromVarbitValue(client.getVarbitValue(MIXER_MIDDLE));
		PotionComponent right = PotionComponent.fromVarbitValue(client.getVarbitValue(MIXER_RIGHT));

		int lyeCount = 0;
		int moxCount = 0;
		int agaCount = 0;

		for (var component : order.potionType().components())
		{
			if (component == PotionComponent.MOX)
			{
				moxCount++;
			}
			else if (component == PotionComponent.AGA)
			{
				agaCount++;
			}
			else if (component == PotionComponent.LYE)
			{
				lyeCount++;
			}
		}

		if (left != PotionComponent.NONE)
		{
			if (left == PotionComponent.MOX)
			{
				moxCount--;
			}
			else if (left == PotionComponent.AGA)
			{
				agaCount--;
			}
			else if (left == PotionComponent.LYE)
			{
				lyeCount--;
			}

			if (middle != PotionComponent.NONE)
			{
				if (middle == PotionComponent.MOX)
				{
					moxCount--;
				}
				else if (middle == PotionComponent.AGA)
				{
					agaCount--;
				}
				else if (middle == PotionComponent.LYE)
				{
					lyeCount--;
				}

				if (right != PotionComponent.NONE)
				{
					if (right == PotionComponent.MOX)
					{
						moxCount--;
					}
					else if (right == PotionComponent.AGA)
					{
						agaCount--;
					}
					else if (right == PotionComponent.LYE)
					{
						lyeCount--;
					}
				}
			}
		}

		for (var component : order.potionType().components())
		{
			if (component == PotionComponent.MOX)
			{
				if (moxCount > 0)
				{
					outlineTargetLever(moxLever, config.moxLeverOutline());
					drawLeverPullCount(moxLever, moxCount, config.moxLeverOutline(), graphics);
				}
			}
			else if (component == PotionComponent.AGA)
			{
				if (agaCount > 0)
				{
					outlineTargetLever(agaLever, config.agaLeverOutline());
					drawLeverPullCount(agaLever, agaCount, config.agaLeverOutline(), graphics);
				}
			}
			else if (component == PotionComponent.LYE)
			{
				if (lyeCount > 0)
				{
					outlineTargetLever(lyeLever, config.lyeLeverOutline());
					drawLeverPullCount(lyeLever, lyeCount, config.lyeLeverOutline(), graphics);
				}
			}
		}
	}

	private void drawLeverPullCount(TileObject targetLever, Integer pullCount, Color color, Graphics2D graphics)
	{
		String text = String.format("%dx", pullCount);
		LocalPoint crucibleLoc = targetLever.getLocalLocation();
		crucibleLoc = new LocalPoint(crucibleLoc.getX(), crucibleLoc.getY(), client.getTopLevelWorldView());
		Point pos = Perspective.getCanvasTextLocation(client, graphics, crucibleLoc, text, 250);
		OverlayUtil.renderTextLocation(graphics, pos, text, color);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		updateLeversToPull(graphics);
		return null;
	}

	public void setOrder(PotionOrder order)
	{
		this.order = order;
	}
}
