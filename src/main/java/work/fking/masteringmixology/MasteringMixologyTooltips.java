package work.fking.masteringmixology;

import net.runelite.api.*;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.InterfaceID;
import net.runelite.api.widgets.WidgetUtil;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.QuantityFormatter;

import javax.inject.Inject;
import java.awt.*;

public class MasteringMixologyTooltips extends Overlay {

    private final Client client;
    private final MasteringMixologyConfig config;
    private final TooltipManager tooltipManager;
    @Inject
    private ItemManager itemManager;
    private final StringBuilder itemStringBuilder = new StringBuilder();

    @Inject
    MasteringMixologyTooltips(Client client, MasteringMixologyConfig config, TooltipManager tooltipManager) {
        this.client = client;
        this.config = config;
        this.tooltipManager = tooltipManager;

        setPosition(OverlayPosition.DYNAMIC);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (client.isMenuOpen() || !config.showTooltip()) {
            return null;
        }

        // Find the last menu entry
        final MenuEntry[] menuEntries = client.getMenuEntries();
        final int last = menuEntries.length - 1;
        if (last < 0) {
            return null;
        }

        final MenuEntry menuEntry = menuEntries[last];
        final MenuAction action = menuEntry.getType();
        final int widgetId = menuEntry.getParam1();
        final int groupId = WidgetUtil.componentToInterface(widgetId);

        // Tooltip for inventory item
        if (action == MenuAction.WIDGET_TARGET && menuEntry.getWidget().getId() == ComponentID.INVENTORY_CONTAINER) {
            makeTooltip(menuEntry);
        }

        // Tooltip for bank items (inventory & bank)
        if (action == MenuAction.CC_OP  && (groupId == InterfaceID.INVENTORY || groupId == InterfaceID.BANK || groupId == InterfaceID.BANK_INVENTORY)) {
            makeTooltip(menuEntry);
        }

        return null;
    }

    private void makeTooltip(MenuEntry menuEntry) {
        // Get the item container
        ItemContainer itemContainer = getContainer(menuEntry);
        if (itemContainer == null) {
            return;
        }

        // Find the item in the container to get stack size
        final int index = menuEntry.getParam0();
        final Item item = itemContainer.getItem(index);
        if (item == null) {
            return;
        }

        // Get the herb
        Herb herb = Herb.getHerbFromItemId(item.getId());
        if (herb == null) {
            return;
        }

        // Get the GE Price of the item if enabled
        int gePrice = 0;
        if (config.showPricePerPaste()) {
            int id = itemManager.canonicalize(item.getId());
            gePrice = itemManager.getItemPrice(id);
        }

        // Create the text and add it to the tooltip manager
        String text = stackValueText(herb, item.getQuantity(), gePrice);
        tooltipManager.add(new Tooltip(ColorUtil.prependColorTag(text, new Color(238, 238, 238))));
    }

    private ItemContainer getContainer(MenuEntry menuEntry) {
        final int widgetId = menuEntry.getParam1();

        // Inventory item
        if (widgetId == ComponentID.INVENTORY_CONTAINER || widgetId == ComponentID.BANK_INVENTORY_ITEM_CONTAINER) {
            return client.getItemContainer(InventoryID.INVENTORY);
        }

        // Bank item
        else if (widgetId == ComponentID.BANK_ITEM_CONTAINER) {
            return client.getItemContainer(InventoryID.BANK);
        }

        return null;
    }

    private String stackValueText(Herb herb, int qty, int gePrice) {
        int amount = herb.pastePerHerb();

        // Append the ingredient name and result amount
        itemStringBuilder.append(herb.potionComponent().getName())
                .append(": ")
                .append(QuantityFormatter.quantityToStackSize((long) amount * qty));

        // Append the value of each item
        if (qty > 1 && config.showTooltipEach()) {
            itemStringBuilder.append(" (")
                    .append(QuantityFormatter.quantityToStackSize(amount))
                    .append(" ea)");
        }

        // Append the price per paste on a new line
        if (gePrice > 0) {
            itemStringBuilder.append("</br>")
                    .append(QuantityFormatter.formatNumber(gePrice / amount))
                    .append(" gp/paste");
        }

        // Build string and reset builder
        final String text = itemStringBuilder.toString();
        itemStringBuilder.setLength(0);
        return text;
    }
}
