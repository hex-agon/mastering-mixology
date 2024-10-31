package work.fking.masteringmixology;

import net.runelite.api.TileObject;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;
import net.runelite.client.util.ColorUtil;

import javax.inject.Inject;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;

public class MasteringMixologyOverlay extends Overlay {
    private final MasteringMixologyPlugin plugin;
    private final MasteringMixologyConfig config;
    private final ModelOutlineRenderer modelOutlineRenderer;

    @Inject
    MasteringMixologyOverlay(MasteringMixologyPlugin plugin, MasteringMixologyConfig config, ModelOutlineRenderer modelOutlineRenderer) {
        this.plugin = plugin;
        this.config = config;
        this.modelOutlineRenderer = modelOutlineRenderer;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        for (var highlightedObject : plugin.highlightedObjects().values()) {
            TileObject object = highlightedObject.object();
            Color color = highlightedObject.color();

            switch (config.highlightStyle()) {
                case OUTLINE:
                    modelOutlineRenderer.drawOutline(object, config.highlightBorderWidth(), color, config.highlightFeather());
                    break;
                case CLICK_BOX:
                    drawShape(graphics, object.getClickbox(), color);
                    break;
            }
        }
        return null;
    }

    private void drawShape(Graphics2D graphics, Shape shape, Color color) {
        if (shape != null) {
            Stroke stroke = new BasicStroke((float) config.highlightBorderWidth());
            Color fillColor = ColorUtil.colorWithAlpha(color, 20);
            OverlayUtil.renderPolygon(graphics, shape, color.darker(), fillColor, stroke);
        }
    }
}
