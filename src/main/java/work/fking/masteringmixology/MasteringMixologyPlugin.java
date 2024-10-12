package work.fking.masteringmixology;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.FontID;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.TileObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GraphicsObjectCreated;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetPositionMode;
import net.runelite.api.widgets.WidgetTextAlignment;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ColorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static work.fking.masteringmixology.AlchemyObject.AGA_LEVER;
import static work.fking.masteringmixology.AlchemyObject.LYE_LEVER;
import static work.fking.masteringmixology.AlchemyObject.MOX_LEVER;
import static work.fking.masteringmixology.PotionComponent.AGA;
import static work.fking.masteringmixology.PotionComponent.LYE;
import static work.fking.masteringmixology.PotionComponent.MOX;

@PluginDescriptor(name = "Mastering Mixology")
public class MasteringMixologyPlugin extends Plugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(MasteringMixologyPlugin.class);

    private static final int PROC_MASTERING_MIXOLOGY_BUILD_POTION_ORDER = 7063;
    private static final int PROC_MASTERING_MIXOLOGY_BUILD_REAGENTS = 7064;

    private static final int VARBIT_POTION_ORDER_1 = 11315;
    private static final int VARBIT_POTION_MODIFIER_1 = 11316;
    private static final int VARBIT_POTION_ORDER_2 = 11317;
    private static final int VARBIT_POTION_MODIFIER_2 = 11318;
    private static final int VARBIT_POTION_ORDER_3 = 11319;
    private static final int VARBIT_POTION_MODIFIER_3 = 11320;

    private static final int VARP_LYE_RESIN = 4414;
    private static final int VARP_AGA_RESIN = 4415;
    private static final int VARP_MOX_RESIN = 4416;

    private static final int VARBIT_ALEMBIC_PROGRESS = 11328;
    private static final int VARBIT_AGITATOR_PROGRESS = 11329;

    private static final int VARBIT_AGITATOR_QUICKACTION = 11337;
    private static final int VARBIT_ALEMBIC_QUICKACTION = 11338;

    private static final int VARBIT_MIXING_VESSEL_POTION = 11339;
    private static final int VARBIT_AGITATOR_POTION = 11340;
    private static final int VARBIT_RETORT_POTION = 11341;
    private static final int VARBIT_ALEMBIC_POTION = 11342;

    private static final int VARBIT_DIGWEED_NORTH_EAST = 11330;
    private static final int VARBIT_DIGWEED_SOUTH_EAST = 11331;
    private static final int VARBIT_DIGWEED_SOUTH_WEST = 11332;
    private static final int VARBIT_DIGWEED_NORTH_WEST = 11333;

    private static final int SPOT_ANIM_AGITATOR = 2954;
    private static final int SPOT_ANIM_ALEMBIC = 2955;

    private static final int COMPONENT_POTION_ORDERS_GROUP_ID = 882;
    private static final int COMPONENT_POTION_ORDERS = COMPONENT_POTION_ORDERS_GROUP_ID << 16 | 2;

    @Inject
    private Client client;

    @Inject
    private MasteringMixologyConfig config;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private Notifier notifier;

    @Inject
    private ClientThread clientThread;

    @Inject
    private MasteringMixologyOverlay overlay;

    @Inject
    private InventoryPotionOverlay potionOverlay;

    private final Map<AlchemyObject, HighlightedObject> highlightedObjects = new LinkedHashMap<>();
    private List<PotionOrder> potionOrders = Collections.emptyList();
    private boolean inLab = false;

    private PotionType alembicPotionType;
    private PotionType agitatorPotionType;
    private PotionType retortPotionType;

    private int previousAgitatorProgess;
    private int previousAlembicProgress;

    private int agitatorQuickActionTicks = 0;
    private int alembicQuickActionTicks = 0;

    public Map<AlchemyObject, HighlightedObject> highlightedObjects() {
        return highlightedObjects;
    }

    public boolean isInLab() {
        return inLab;
    }

    @Provides
    MasteringMixologyConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(MasteringMixologyConfig.class);
    }

    @Override
    protected void startUp() {
        overlayManager.add(overlay);
        overlayManager.add(potionOverlay);

        if (client.getGameState() == GameState.LOGGED_IN) {
            clientThread.invokeLater(this::initialize);
        }
    }

    @Override
    protected void shutDown() {
        overlayManager.remove(overlay);
        overlayManager.remove(potionOverlay);
        inLab = false;
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOGIN_SCREEN || event.getGameState() == GameState.HOPPING) {
            highlightedObjects.clear();
        }
    }

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded event) {
        if (event.getGroupId() != COMPONENT_POTION_ORDERS_GROUP_ID) {
            return;
        }

        initialize();
    }

    @Subscribe
    public void onWidgetClosed(WidgetClosed event) {
        if (event.getGroupId() != COMPONENT_POTION_ORDERS_GROUP_ID) {
            return;
        }

        highlightedObjects.clear();
        inLab = false;
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals(MasteringMixologyConfig.CONFIG_GROUP)) {
            return;
        }

        if (!config.highlightStations()) {
            unHighlightAllStations();
        }

        if (!config.highlightDigWeed()) {
            unHighlightObject(AlchemyObject.DIGWEED_NORTH_EAST);
            unHighlightObject(AlchemyObject.DIGWEED_SOUTH_EAST);
            unHighlightObject(AlchemyObject.DIGWEED_SOUTH_WEST);
            unHighlightObject(AlchemyObject.DIGWEED_NORTH_WEST);
        }

        if (config.highlightLevers()) {
            highlightLevers();
        } else {
            unHighlightLevers();
        }
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event) {
        if (!inLab || !config.highlightStations() || event.getContainerId() != InventoryID.INVENTORY.getId()) {
            return;
        }
        // Do not update the highlight if there's a potion in a station
        if (alembicPotionType != null || agitatorPotionType != null || retortPotionType != null) {
            return;
        }
        var inventory = event.getItemContainer();

        // Find the first potion item and highlight its station
        for (var item : inventory.getItems()) {
            var potionType = PotionType.fromItemId(item.getId());

            if (potionType == null) {
                continue;
            }
            for (var order : potionOrders) {
                if (order.potionType() == potionType && !order.fulfilled()) {
                    unHighlightAllStations();
                    highlightObject(order.potionModifier().alchemyObject(), config.stationHighlightColor());
                    return;
                }
            }
        }
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        var varbitId = event.getVarbitId();
        var value = event.getValue();

        // Whenever a potion is delivered, all the potion order related varbits are reset to 0 first then
        // set to the new values. We can use this to clear all the stations.
        if (varbitId == VARBIT_POTION_ORDER_1) {
            if (value == 0) {
                unHighlightAllStations();
            } else {
                clientThread.invokeAtTickEnd(this::updatePotionOrders);
            }
        } else if (varbitId == VARBIT_ALEMBIC_POTION) {
            if (value == 0) {
                // Finished crystalising
                unHighlightObject(AlchemyObject.ALEMBIC);
                tryFulfillOrder(alembicPotionType, PotionModifier.CRYSTALISED);
                tryHighlightNextStation();
                LOGGER.debug("Finished crystalising {}", alembicPotionType);
                alembicPotionType = null;
            } else {
                alembicPotionType = PotionType.fromIdx(value - 1);
                LOGGER.debug("Alembic potion type: {}", alembicPotionType);
            }
        } else if (varbitId == VARBIT_AGITATOR_POTION) {
            if (value == 0) {
                // Finished homogenising
                unHighlightObject(AlchemyObject.AGITATOR);
                tryFulfillOrder(agitatorPotionType, PotionModifier.HOMOGENOUS);
                tryHighlightNextStation();
                LOGGER.debug("Finished homogenising {}", agitatorPotionType);
                agitatorPotionType = null;
            } else {
                agitatorPotionType = PotionType.fromIdx(value - 1);
                LOGGER.debug("Agitator potion type: {}", agitatorPotionType);
            }
        } else if (varbitId == VARBIT_RETORT_POTION) {
            if (value == 0) {
                // Finished concentrating
                unHighlightObject(AlchemyObject.RETORT);
                tryFulfillOrder(retortPotionType, PotionModifier.CONCENTRATED);
                tryHighlightNextStation();
                LOGGER.debug("Finished concentrating {}", retortPotionType);
                retortPotionType = null;
            } else {
                retortPotionType = PotionType.fromIdx(value - 1);
                LOGGER.debug("Retort potion type: {}", retortPotionType);
            }
        } else if (varbitId == VARBIT_DIGWEED_NORTH_EAST) {
            if (value == 1) {
                if (config.highlightDigWeed()) {
                    highlightObject(AlchemyObject.DIGWEED_NORTH_EAST, config.digweedHighlightColor());
                }
                notifier.notify(config.notifyDigWeed(), "A digweed has spawned north east.");
            } else {
                unHighlightObject(AlchemyObject.DIGWEED_NORTH_EAST);
            }
        } else if (varbitId == VARBIT_DIGWEED_SOUTH_EAST) {
            if (value == 1) {
                if (config.highlightDigWeed()) {
                    highlightObject(AlchemyObject.DIGWEED_SOUTH_EAST, config.digweedHighlightColor());
                }
                notifier.notify(config.notifyDigWeed(), "A digweed has spawned south east.");
            } else {
                unHighlightObject(AlchemyObject.DIGWEED_SOUTH_EAST);
            }
        } else if (varbitId == VARBIT_DIGWEED_SOUTH_WEST) {
            if (value == 1) {
                if (config.highlightDigWeed()) {
                    highlightObject(AlchemyObject.DIGWEED_SOUTH_WEST, config.digweedHighlightColor());
                }
                notifier.notify(config.notifyDigWeed(), "A digweed has spawned south west.");
            } else {
                unHighlightObject(AlchemyObject.DIGWEED_SOUTH_WEST);
            }
        } else if (varbitId == VARBIT_DIGWEED_NORTH_WEST) {
            if (value == 1) {
                if (config.highlightDigWeed()) {
                    highlightObject(AlchemyObject.DIGWEED_NORTH_WEST, config.digweedHighlightColor());
                }
                notifier.notify(config.notifyDigWeed(), "A digweed has spawned north west.");
            } else {
                unHighlightObject(AlchemyObject.DIGWEED_NORTH_WEST);
            }
        } else if (varbitId == VARBIT_AGITATOR_PROGRESS) {
            if (agitatorQuickActionTicks == 2) {
                // quick action was triggered two ticks ago, so it's now too late
                resetDefaultHighlight(AlchemyObject.AGITATOR);
                agitatorQuickActionTicks = 0;
            }
            if (agitatorQuickActionTicks == 1) {
                agitatorQuickActionTicks = 2;
            }
            if (value < previousAgitatorProgess) {
                // progress was set back due to a quick action failure
                resetDefaultHighlight(AlchemyObject.AGITATOR);
            }
            previousAgitatorProgess = value;
        } else if (varbitId == VARBIT_ALEMBIC_PROGRESS) {
            if (alembicQuickActionTicks == 1) {
                // quick action was triggered last tick, so it's now too late
                resetDefaultHighlight(AlchemyObject.ALEMBIC);
                alembicQuickActionTicks = 0;
            }
            if (value < previousAlembicProgress) {
                // progress was set back due to a quick action failure
                resetDefaultHighlight(AlchemyObject.ALEMBIC);
            }
            previousAlembicProgress = value;
        } else if (varbitId == VARBIT_AGITATOR_QUICKACTION) {
            // agitator quick action was just successfully popped
            resetDefaultHighlight(AlchemyObject.AGITATOR);
        } else if (varbitId == VARBIT_ALEMBIC_QUICKACTION) {
            // alembic quick action was just successfully popped
            resetDefaultHighlight(AlchemyObject.ALEMBIC);
        }
    }

    @Subscribe
    public void onGraphicsObjectCreated(GraphicsObjectCreated event) {
        var spotAnimId = event.getGraphicsObject().getId();

        if (!config.highlightQuickActionEvents()) {
            return;
        }
        if (spotAnimId == SPOT_ANIM_ALEMBIC && alembicPotionType != null) {
            highlightObject(AlchemyObject.ALEMBIC, config.stationQuickActionHighlightColor());
            // start counting ticks for alembic so we know to un-highlight on the next alembic varbit update
            // note this quick action has a 1 tick window, so we use an int that goes 0 -> 1 -> unhighlight
            alembicQuickActionTicks = 1;
        }

        if (spotAnimId == SPOT_ANIM_AGITATOR && agitatorPotionType != null) {
            highlightObject(AlchemyObject.AGITATOR, config.stationQuickActionHighlightColor());
            // start counting ticks for agitator so we know to un-highlight on the next agitator varbit update
            // note this quick action has a 2-tick window, so we use an int that goes 0 -> 1 -> 2 -> unhighlight
            agitatorQuickActionTicks = 1;
        }
    }

    @Subscribe
    public void onScriptPostFired(ScriptPostFired event) {
        if (event.getScriptId() != PROC_MASTERING_MIXOLOGY_BUILD_REAGENTS) {
            return;
        }
        var baseWidget = client.getWidget(COMPONENT_POTION_ORDERS);

        if (baseWidget == null) {
            return;
        }
        var textComponents = findTextComponents(baseWidget);

        if (textComponents.size() < 4) {
            return;
        }

        for (var order : potionOrders) {
            // The first text widget is always the interface title 'Potion Orders'
            appendPotionRecipe(textComponents.get(order.idx()), order.idx(), order.fulfilled());
        }

        if (config.displayResin()) {
            var parentWidth = baseWidget.getWidth();
            var dx = parentWidth / 3;
            int x = dx / 2;

            addResinText(baseWidget.createChild(-1, WidgetType.TEXT), x, VARP_MOX_RESIN, MOX);
            addResinText(baseWidget.createChild(-1, WidgetType.TEXT), x + dx, VARP_AGA_RESIN, AGA);
            addResinText(baseWidget.createChild(-1, WidgetType.TEXT), x + dx * 2, VARP_LYE_RESIN, LYE);
        }
    }

    private void initialize() {
        var ordersLayer = client.getWidget(COMPONENT_POTION_ORDERS_GROUP_ID, 0);
        if (ordersLayer == null || ordersLayer.isSelfHidden()) {
            return;
        }

        LOGGER.debug("initialize plugin");
        inLab = true;
        updatePotionOrders();
        highlightLevers();
        tryHighlightNextStation();
    }

    public void highlightObject(AlchemyObject alchemyObject, Color color) {
        var worldView = client.getTopLevelWorldView();

        if (worldView == null) {
            return;
        }
        var localPoint = LocalPoint.fromWorld(worldView, alchemyObject.coordinate());

        if (localPoint == null) {
            return;
        }
        var tiles = worldView.getScene().getTiles();
        var tile = tiles[worldView.getPlane()][localPoint.getSceneX()][localPoint.getSceneY()];

        for (var gameObject : tile.getGameObjects()) {
            if (gameObject == null) {
                continue;
            }

            if (gameObject.getId() == alchemyObject.objectId()) {
                highlightedObjects.put(alchemyObject, new HighlightedObject(gameObject, color, config.highlightBorderWidth(), config.highlightFeather()));
                return;
            }
        }
        // The aga lever is actually a wall decoration, not a scenery object
        var decorativeObject = tile.getDecorativeObject();

        if (decorativeObject != null && decorativeObject.getId() == alchemyObject.objectId()) {
            highlightedObjects.put(alchemyObject, new HighlightedObject(decorativeObject, color, config.highlightBorderWidth(), config.highlightFeather()));
        }
    }

    public void resetDefaultHighlight(AlchemyObject alchemyObject) {
        if (config.highlightStations()) {
            highlightObject(alchemyObject, config.stationHighlightColor());
        }
    }

    public void unHighlightObject(AlchemyObject alchemyObject) {
        highlightedObjects.remove(alchemyObject);
    }

    private void unHighlightAllStations() {
        unHighlightObject(AlchemyObject.RETORT);
        unHighlightObject(AlchemyObject.ALEMBIC);
        unHighlightObject(AlchemyObject.AGITATOR);
    }

    private void highlightLevers() {
        if (!config.highlightLevers()) {
            return;
        }

        highlightObject(LYE_LEVER, Color.decode("#" + LYE.color()));
        highlightObject(AGA_LEVER, Color.decode("#" + AGA.color()));
        highlightObject(MOX_LEVER, Color.decode("#" + MOX.color()));
    }

    private void unHighlightLevers() {
        unHighlightObject(LYE_LEVER);
        unHighlightObject(AGA_LEVER);
        unHighlightObject(MOX_LEVER);
    }

    private void updatePotionOrders() {
        LOGGER.debug("Updating potion orders");
        potionOrders = getPotionOrders();

        // Trigger a fake varbit update to force run the clientscript proc
        var varbitType = client.getVarbit(VARBIT_POTION_ORDER_1);

        if (varbitType != null) {
            client.queueChangedVarp(varbitType.getIndex());
        }
    }

    private List<Widget> findTextComponents(Widget parent) {
        var children = parent.getDynamicChildren();
        var textComponents = new ArrayList<Widget>();

        for (var child : children) {
            if (child.getType() != WidgetType.TEXT) {
                continue;
            }
            textComponents.add(child);
        }
        return textComponents;
    }

    private void appendPotionRecipe(Widget component, int orderIdx, boolean fulfilled) {
        var potionType = getPotionType(orderIdx);

        if (potionType == null) {
            return;
        }
        var builder = new StringBuilder(component.getText());

        if (fulfilled) {
            builder.append(" (<col=00ff00>done!</col>)");
        } else {
            builder.append(" (").append(potionType.recipe()).append(")");
        }
        component.setText(builder.toString());
    }

    private void addResinText(Widget widget, int x, int varp, PotionComponent component) {
        var amount = client.getVarpValue(varp);
        var color = ColorUtil.fromHex(component.color()).getRGB();

        widget.setText(amount + "")
                .setTextColor(color)
                .setOriginalWidth(20)
                .setOriginalHeight(15)
                .setFontId(FontID.QUILL_8)
                .setOriginalY(0)
                .setOriginalX(x)
                .setYPositionMode(WidgetPositionMode.ABSOLUTE_BOTTOM)
                .setXTextAlignment(WidgetTextAlignment.CENTER)
                .setYTextAlignment(WidgetTextAlignment.CENTER);

        widget.revalidate();
        LOGGER.debug("adding resin text {} at {} with color {}", amount, x, color);
    }

    private void tryFulfillOrder(PotionType potionType, PotionModifier modifier) {
        for (var order : potionOrders) {
            if (order.potionType() == potionType && order.potionModifier() == modifier && !order.fulfilled()) {
                LOGGER.debug("Order {} has been fulfilled", order);
                order.setFulfilled(true);
                break;
            }
        }
    }

    private void tryHighlightNextStation() {
        var inventory = client.getItemContainer(InventoryID.INVENTORY);

        if (inventory == null) {
            return;
        }

        for (var order : potionOrders) {
            if (order.fulfilled()) {
                continue;
            }
            if (inventory.contains(order.potionType().itemId())) {
                LOGGER.debug("Highlighting station for order {}", order);
                highlightObject(order.potionModifier().alchemyObject(), config.stationHighlightColor());
                break;
            }
        }
    }

    private List<PotionOrder> getPotionOrders() {
        var potionOrders = new ArrayList<PotionOrder>(3);

        for (int orderIdx = 1; orderIdx <= 3; orderIdx++) {
            var potionType = getPotionType(orderIdx);
            var potionModifier = getPotionModifier(orderIdx);

            if (potionType == null || potionModifier == null) {
                continue;
            }
            potionOrders.add(new PotionOrder(orderIdx, potionType, potionModifier));
        }
        return potionOrders;
    }

    private PotionType getPotionType(int orderIdx) {
        if (orderIdx == 1) {
            return PotionType.fromIdx(client.getVarbitValue(VARBIT_POTION_ORDER_1) - 1);
        } else if (orderIdx == 2) {
            return PotionType.fromIdx(client.getVarbitValue(VARBIT_POTION_ORDER_2) - 1);
        } else if (orderIdx == 3) {
            return PotionType.fromIdx(client.getVarbitValue(VARBIT_POTION_ORDER_3) - 1);
        } else {
            return null;
        }
    }

    private PotionModifier getPotionModifier(int orderIdx) {
        if (orderIdx == 1) {
            return PotionModifier.from(client.getVarbitValue(VARBIT_POTION_MODIFIER_1) - 1);
        } else if (orderIdx == 2) {
            return PotionModifier.from(client.getVarbitValue(VARBIT_POTION_MODIFIER_2) - 1);
        } else if (orderIdx == 3) {
            return PotionModifier.from(client.getVarbitValue(VARBIT_POTION_MODIFIER_3) - 1);
        } else {
            return null;
        }
    }

    public static class HighlightedObject {

        private final TileObject object;
        private final Color color;
        private final int outlineWidth;
        private final int feather;

        private HighlightedObject(TileObject object, Color color, int outlineWidth, int feather) {
            this.object = object;
            this.color = color;
            this.outlineWidth = outlineWidth;
            this.feather = feather;
        }

        public TileObject object() {
            return object;
        }

        public Color color() {
            return color;
        }

        public int outlineWidth() {
            return outlineWidth;
        }

        public int feather() {
            return feather;
        }
    }
}
