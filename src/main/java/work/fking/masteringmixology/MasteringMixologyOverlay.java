package work.fking.masteringmixology;

import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
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
        plugin.resetPotionIndex(); // this goes here because fuck you.

        for (var highlightedObject : plugin.highlightedObjects().values()) {
            modelOutlineRenderer.drawOutline(highlightedObject.object(), highlightedObject.outlineWidth(), highlightedObject.color(), highlightedObject.feather());
        }
        return null;
    }
}
