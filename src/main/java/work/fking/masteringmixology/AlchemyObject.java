package work.fking.masteringmixology;

import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

public enum AlchemyObject {
    MOX_LEVER(ObjectID.MOX_LEVER, new WorldPoint(1395, 9324, 0)),
    AGA_LEVER(ObjectID.AGA_LEVER, new WorldPoint(1394, 9324, 0)),
    LYE_LEVER(ObjectID.LYE_LEVER, new WorldPoint(1393, 9324, 0)),
    MIXING_VESSEL(NullObjectID.NULL_55395, new WorldPoint(1394, 9326, 0)),
    ALEMBIC(NullObjectID.NULL_55391, new WorldPoint(1391, 9325, 0)),
    AGITATOR(NullObjectID.NULL_55390, new WorldPoint(1393, 9329, 0)),
    RETORT(NullObjectID.NULL_55389, new WorldPoint(1397, 9325, 0)),
    CONVEYOR_BELT(ObjectID.CONVEYOR_BELT_54917, new WorldPoint(1394, 9331, 0)),
    HOPPER(ObjectID.HOPPER_54903, new WorldPoint(1394, 9322, 0)),
    DIGWEED_NORTH_EAST(NullObjectID.NULL_55396, new WorldPoint(1399, 9331, 0)),
    DIGWEED_SOUTH_EAST(NullObjectID.NULL_55397, new WorldPoint(1399, 9322, 0)),
    DIGWEED_SOUTH_WEST(NullObjectID.NULL_55398, new WorldPoint(1389, 9322, 0)),
    DIGWEED_NORTH_WEST(NullObjectID.NULL_55399, new WorldPoint(1389, 9331, 0));

    private final int objectId;
    private final WorldPoint coordinate;

    AlchemyObject(int objectId, WorldPoint coordinate) {
        this.objectId = objectId;
        this.coordinate = coordinate;
    }

    public boolean isStation() {
        return this == RETORT || this == AGITATOR || this == ALEMBIC || this == MIXING_VESSEL;
    }

    public boolean isDigweed() {
        return this == DIGWEED_NORTH_EAST || this == DIGWEED_SOUTH_EAST || this == DIGWEED_SOUTH_WEST || this == DIGWEED_NORTH_WEST;
    }

    public int objectId() {
        return objectId;
    }

    public WorldPoint coordinate() {
        return coordinate;
    }
}
