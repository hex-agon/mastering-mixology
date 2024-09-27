package work.fking.masteringmixology;

import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

public class MasteringMixologyOverlay extends Overlay {

    private final MasteringMixologyPlugin plugin;
    private final ModelOutlineRenderer modelOutlineRenderer;

    @Inject
    MasteringMixologyOverlay(MasteringMixologyPlugin plugin, ModelOutlineRenderer modelOutlineRenderer) {
        this.plugin = plugin;
        this.modelOutlineRenderer = modelOutlineRenderer;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        for (var tileObject : plugin.highlightedObjects().values()) {
            modelOutlineRenderer.drawOutline(tileObject, 2, Color.RED, 0);
        }
        return null;
    }
}
