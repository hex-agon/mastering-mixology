package work.fking.masteringmixology;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GraphicsObject;
import net.runelite.api.ObjectComposition;
import work.fking.masteringmixology.constants.MixologyIDs;

@Singleton
public class UIHelper
{
	@Inject
	private Client client;

	public GameObject digweedNE;
	public GameObject digweedSE;
	public GameObject digweedSW;
	public GameObject digweedNW;

	public boolean isAgitatorSpeedupObjectPresent() {
		return isGraphicsObjectPresent(MixologyIDs.AGITATOR_SPEEDUP_OBJECT_ID);
	}

	public boolean isAlembicSpeedupObjectPresent() {
		return isGraphicsObjectPresent(MixologyIDs.ALEMBIC_SPEEDUP_OBJECT_ID);
	}

	public boolean isMatureDigweedPresent() {
		return getMatureDigweedObjectOrNull() != null;
	}

	public GameObject getMatureDigweedObjectOrNull() {
		var compositionNE = getObjectComposition(digweedNE.getId());
		var compositionSE = getObjectComposition(digweedSE.getId());
		var compositionSW = getObjectComposition(digweedSW.getId());
		var compositionNW = getObjectComposition(digweedNW.getId());

		if (compositionNE != null && compositionNE.getId() == MixologyIDs.MATURE_DIGWEED_COMPOSITION_ID) {
			return digweedNE;
		}
		if (compositionSE != null && compositionSE.getId() == MixologyIDs.MATURE_DIGWEED_COMPOSITION_ID) {
			return digweedSE;
		}
		if (compositionSW != null && compositionSW.getId() == MixologyIDs.MATURE_DIGWEED_COMPOSITION_ID) {
			return digweedSW;
		}
		if (compositionNW != null && compositionNW.getId() == MixologyIDs.MATURE_DIGWEED_COMPOSITION_ID) {
			return digweedNW;
		}

		return null;
	}

	private boolean isGraphicsObjectPresent(int graphicsObjectId) {
		for (GraphicsObject graphicsObject : client.getGraphicsObjects()) {
			if (graphicsObject.getId() == graphicsObjectId) {
				return true;
			}
		}
		return false;
	}

	@Nullable
	private ObjectComposition getObjectComposition(int id) {
		ObjectComposition objectComposition = client.getObjectDefinition(id);
		return objectComposition.getImpostorIds() == null ? objectComposition : objectComposition.getImpostor();
	}
}
