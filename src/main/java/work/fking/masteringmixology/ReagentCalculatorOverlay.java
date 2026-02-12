package work.fking.masteringmixology;

import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.MenuEntry;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import net.runelite.client.util.Text;

public class ReagentCalculatorOverlay extends Overlay {
    private final Client client;
    private final MasteringMixologyPlugin plugin;
    private final MasteringMixologyConfig config;
    private final TooltipManager tooltipManager;

    private static final int HOPPER_MAXIMUM = 3000;

    @Inject
    public ReagentCalculatorOverlay(
        Client client,
        MasteringMixologyPlugin plugin,
        MasteringMixologyConfig config,
        TooltipManager tooltipManager
    )
    {
        this.tooltipManager = tooltipManager;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics2D) {
        if (!config.showReagentCalculator()) {
            return null;
        }

        MenuEntry[] menuEntries = client.getMenuEntries();
        int last = menuEntries.length - 1;
        if (last < 0) {
            return null;
        }

        MenuEntry menuEntry = menuEntries[last];
        String target = menuEntry.getTarget();
        target = Text.removeTags(target);
        String option = menuEntry.getOption();

        if (option.equals("Deposit") && target.equals("Hopper"))
        {
            String tooltip = String.format(
                "Herbs to fill hopper:</br>" +
                "Mox: %dx %s</br>" +
                "Aga: %dx %s</br>" +
                "Lye: %dx %s",
                getAmount(PotionComponent.MOX), config.moxHerb(),
                getAmount(PotionComponent.AGA), config.agaHerb(),
                getAmount(PotionComponent.LYE), config.lyeHerb()
            );
            tooltipManager.add(new Tooltip(tooltip));
        }
        return null;
    }

    private int getAmount(PotionComponent type)
    {
        int currentResin;
        int resinPerHerb;
        switch (type) {
            case MOX:
                currentResin = client.getVarbitValue(VarbitID.MM_AVAILABLE_MOX);
                resinPerHerb = config.moxHerb().getAmount();
                break;
            case AGA:
                currentResin = client.getVarbitValue(VarbitID.MM_AVAILABLE_AGA);
                resinPerHerb = config.agaHerb().getAmount();
                break;
            case LYE:
                currentResin = client.getVarbitValue(VarbitID.MM_AVAILABLE_LYE);
                resinPerHerb = config.lyeHerb().getAmount();
                break;
            default:
                currentResin = 0;
                resinPerHerb = 1;
                break;
        }
        return (int) Math.floor((HOPPER_MAXIMUM - currentResin) / (double) resinPerHerb);
    }
}
