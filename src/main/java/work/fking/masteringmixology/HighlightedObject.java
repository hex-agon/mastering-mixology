package work.fking.masteringmixology;

import net.runelite.api.TileObject;

import java.awt.Color;

public class HighlightedObject {

    private final TileObject object;
    private final Color color;

    public HighlightedObject(TileObject object, Color color) {
        this.object = object;
        this.color = color;
    }

    public TileObject object() {
        return object;
    }

    public Color color() {
        return color;
    }
}