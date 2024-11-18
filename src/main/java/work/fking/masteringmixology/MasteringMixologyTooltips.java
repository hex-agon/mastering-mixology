package work.fking.masteringmixology;

import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.InterfaceID;
import net.runelite.api.widgets.WidgetUtil;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import net.runelite.client.util.QuantityFormatter;

import javax.inject.Inject;
import java.awt.Dimension;
import java.awt.Graphics2D;

public class MasteringMixologyTooltips extends Overlay {

    private final Client client;
    private final MasteringMixologyConfig config;
    private final TooltipManager tooltipManager;
    private final ItemManager itemManager;

    @Inject
    MasteringMixologyTooltips(Client client, MasteringMixologyConfig config, TooltipManager tooltipManager, ItemManager itemManager) {
        this.client = client;
        this.config = config;
        this.tooltipManager = tooltipManager;
        this.itemManager = itemManager;

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

        ItemContainer itemContainer = getItemContainer(action, menuEntry, groupId);
        if (itemContainer == null) {
            return null;
        }

        // Find the item in the container to get stack size
        final int index = menuEntry.getParam0();
        final Item item = itemContainer.getItem(index);
        if (item == null) {
            return null;
        }

        PasteIngredient pasteIngredient = PasteIngredient.getFromItemId(item.getId());
        if (pasteIngredient == null) {
            return null;
        }

        // Get the GE Price of the item if enabled
        int gePrice = 0;
        if (config.showPricePerPaste()) {
            int id = itemManager.canonicalize(item.getId());
            gePrice = itemManager.getItemPrice(id);
        }

        // Create the text and add it to the tooltip manager
        String text = stackValueText(pasteIngredient, item.getQuantity(), gePrice);
        tooltipManager.add(new Tooltip(text));

        return null;
    }

    private ItemContainer getItemContainer(MenuAction action, MenuEntry menuEntry, int groupId) {
        if (action == MenuAction.WIDGET_TARGET && menuEntry.getWidget() != null && menuEntry.getWidget().getId() == ComponentID.INVENTORY_CONTAINER) {
            return client.getItemContainer(InventoryID.INVENTORY);
        } else if (action == MenuAction.CC_OP) {
            if (groupId == InterfaceID.BANK) {
                return client.getItemContainer(InventoryID.BANK);
            } else if (groupId == InterfaceID.INVENTORY || groupId == InterfaceID.BANK_INVENTORY) {
                return client.getItemContainer(InventoryID.INVENTORY);
            }
        }
        return null;
    }

    private String stackValueText(PasteIngredient pasteIngredient, int qty, int gePrice) {
        int amount = pasteIngredient.pastePerItem();

        // Append the ingredient name and result amount
        String text = pasteIngredient.potionComponent().formattedName() + ": "
                + QuantityFormatter.quantityToStackSize((long) amount * qty);

        // Append the value of each item
        if (qty > 1 && config.showTooltipEach()) {
            text += " (" + QuantityFormatter.quantityToStackSize(amount) + " ea)";
        }

        // Append the price per paste on a new line
        if (gePrice > 0) {
            text += "</br>" + QuantityFormatter.formatNumber(gePrice / amount) + " gp/paste";
        }

        return text;
    }
}
