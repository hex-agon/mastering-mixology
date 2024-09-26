package work.fking.masteringmixology.geels.enums;

import com.geel.masteringmixology.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

@AllArgsConstructor
public enum AlchemyBuilding {
    ALEMBIC_CRYSTALIZER(3, "Alembic (Crystalizer)", 55391, new WorldPoint(1392,9325,0)), // Alembic
    RETORT_CONCENTRATOR(2, "Retort (Concentrator)", 55389, new WorldPoint(1396,9326,0)), // Retort
    AGITATOR_HOMOGENIZER(1, "Agitator (Homogenizer)", 55390, new WorldPoint(1395,9328,0)), // Agitator
    NONE(0,  "", 0, new WorldPoint(0,0,0));

    @Getter
    private int id;

    @Getter
    private String name;

    @Getter
    private int buildingId;

    @Getter
    private WorldPoint interactionLocation;

    public static AlchemyBuilding FromId(int id) {
        switch (id) {
            case 3:
                return ALEMBIC_CRYSTALIZER;
            case 2:
                return RETORT_CONCENTRATOR;
            case 1:
                return AGITATOR_HOMOGENIZER;
            case 0:
                return NONE;
            default:
                return null;
        }
    }

    public static AlchemyBuilding FromBuildingObjectId(int id) {
        for(var potion : AlchemyBuilding.values()) {
            if(potion.getBuildingId() == id) {
                return potion;
            }
        }

        return AlchemyBuilding.NONE;
    }

    public static AlchemyBuilding FromProgressVarbitId(int varbitId)
    {
        if(varbitId == Constants.VB_ALEMBIC_PROGRESS) {
            return AlchemyBuilding.ALEMBIC_CRYSTALIZER;
        }

        if(varbitId == Constants.VB_RETORT_PROGRESS) {
            return AlchemyBuilding.RETORT_CONCENTRATOR;
        }

        if(varbitId == Constants.VB_AGITATOR_PROGRESS) {
            return AlchemyBuilding.AGITATOR_HOMOGENIZER;
        }

        return AlchemyBuilding.NONE;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
