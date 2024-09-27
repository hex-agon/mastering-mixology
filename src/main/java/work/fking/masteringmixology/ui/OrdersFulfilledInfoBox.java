package work.fking.masteringmixology.ui;

import net.runelite.client.ui.overlay.infobox.Counter;
import net.runelite.client.util.QuantityFormatter;
import work.fking.masteringmixology.MasteringMixologyPlugin;

import java.awt.image.BufferedImage;

public class OrdersFulfilledInfoBox extends Counter {
    private final String name;

    public OrdersFulfilledInfoBox(MasteringMixologyPlugin plugin, BufferedImage image, String name, int count) {
        super(image, plugin, count);
        this.name = name;
    }

    @Override
    public String getText() {
        return QuantityFormatter.quantityToRSDecimalStack(getCount());
    }

    @Override
    public String getTooltip() {
        return name;
    }
}