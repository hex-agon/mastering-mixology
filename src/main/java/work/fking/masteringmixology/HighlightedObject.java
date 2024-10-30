package work.fking.masteringmixology;

import net.runelite.api.TileObject;

import java.awt.Color;

public class HighlightedObject {

    private final TileObject object;
    private final Color color;
    private final HighlightReason reason;

    public HighlightedObject(TileObject object, Color color, HighlightReason reason) {
        this.object = object;
        this.color = color;
        this.reason = reason;
    }

    public TileObject object() {
        return object;
    }

    public Color color() {
        return color;
    }

    public HighlightReason reason() {
        return reason;
    }

    public enum HighlightReason {
        STATION,
        QUICK_ACTION,
        LEVER,
        DIGWEED
    }

    public enum HighlightStyle {
        OUTLINE,
        CLICK_BOX,
        HULL,
    }
}