package work.fking.masteringmixology;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.TileObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GraphicsObjectCreated;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.fking.masteringmixology.evaluator.PotionOrderEvaluator.EvaluatorContext;

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

    private static final int VARBIT_POTION_ORDER_1 = 11315;
    private static final int VARBIT_POTION_MODIFIER_1 = 11316;
    private static final int VARBIT_POTION_ORDER_2 = 11317;
    private static final int VARBIT_POTION_MODIFIER_2 = 11318;
    private static final int VARBIT_POTION_ORDER_3 = 11319;
    private static final int VARBIT_POTION_MODIFIER_3 = 11320;

    private static final int VARP_LYE_RESIN = 4414;
    private static final int VARP_AGA_RESIN = 4415;
    private static final int VARP_MOX_RESIN = 4416;

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

    private final Map<AlchemyObject, HighlightedObject> highlightedObjects = new LinkedHashMap<>();
    private List<PotionOrder> potionOrders = Collections.emptyList();
    private PotionOrder bestPotionOrder;

    private PotionType alembicPotionType;
    private PotionType agitatorPotionType;
    private PotionType retortPotionType;

    public Map<AlchemyObject, HighlightedObject> highlightedObjects() {
        return highlightedObjects;
    }

    @Provides
    MasteringMixologyConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(MasteringMixologyConfig.class);
    }

    @Override
    protected void startUp() {
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown() {
        overlayManager.remove(overlay);
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

        highlightLevers();
    }

    @Subscribe
    public void onWidgetClosed(WidgetClosed event) {
        if (event.getGroupId() != COMPONENT_POTION_ORDERS_GROUP_ID) {
            return;
        }

        highlightedObjects.clear();
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
        } else if (varbitId == VARBIT_MIXING_VESSEL_POTION) {
            if (!config.highlightStations() || value == 0) {
                return;
            }
            var mixingVesselPotionType = PotionType.from(value - 1);
            var anyMatch = false;

            for (var order : potionOrders) {
                if (order.potionType() == mixingVesselPotionType && !order.fulfilled()) {
                    anyMatch = true;
                    unHighlightAllStations();
                    highlightObject(order.potionModifier().alchemyObject(), config.stationHighlightColor());
                    break;
                }
            }
            if (!anyMatch) {
                unHighlightAllStations();
            }
        } else if (varbitId == VARBIT_ALEMBIC_POTION) {
            if (value == 0) {
                // Finished crystalising
                unHighlightObject(AlchemyObject.ALEMBIC);
                tryFulfillOrder(alembicPotionType, PotionModifier.CRYSTALISED);
                LOGGER.debug("Finished crystalising {}", alembicPotionType);
                alembicPotionType = null;
            } else {
                alembicPotionType = PotionType.from(value - 1);
                LOGGER.debug("Alembic potion type: {}", alembicPotionType);
            }
        } else if (varbitId == VARBIT_AGITATOR_POTION) {
            if (value == 0) {
                // Finished homogenising
                unHighlightObject(AlchemyObject.AGITATOR);
                tryFulfillOrder(agitatorPotionType, PotionModifier.HOMOGENOUS);
                LOGGER.debug("Finished homogenising {}", agitatorPotionType);
                agitatorPotionType = null;
            } else {
                agitatorPotionType = PotionType.from(value - 1);
                LOGGER.debug("Agitator potion type: {}", agitatorPotionType);
            }
        } else if (varbitId == VARBIT_RETORT_POTION) {
            if (value == 0) {
                // Finished concentrating
                unHighlightObject(AlchemyObject.RETORT);
                tryFulfillOrder(retortPotionType, PotionModifier.CONCENTRATED);
                LOGGER.debug("Finished concentrating {}", retortPotionType);
                retortPotionType = null;
            } else {
                retortPotionType = PotionType.from(value - 1);
                LOGGER.debug("Retort potion type: {}", retortPotionType);
            }
        } else if (varbitId == VARBIT_DIGWEED_NORTH_EAST) {
            if (value == 1) {
                if (config.highlightDigWeed()) {
                    highlightObject(AlchemyObject.DIGWEED_NORTH_EAST, config.digweedHighlightColor());
                }
                if (config.notifyDigWeed()) {
                    notifier.notify("A digweed has spawned north east.");
                }
            } else {
                unHighlightObject(AlchemyObject.DIGWEED_NORTH_EAST);
            }
        } else if (varbitId == VARBIT_DIGWEED_SOUTH_EAST) {
            if (value == 1) {
                if (config.highlightDigWeed()) {
                    highlightObject(AlchemyObject.DIGWEED_SOUTH_EAST, config.digweedHighlightColor());
                }
                if (config.notifyDigWeed()) {
                    notifier.notify("A digweed has spawned south east.");
                }
            } else {
                unHighlightObject(AlchemyObject.DIGWEED_SOUTH_EAST);
            }
        } else if (varbitId == VARBIT_DIGWEED_SOUTH_WEST) {
            if (value == 1) {
                if (config.highlightDigWeed()) {
                    highlightObject(AlchemyObject.DIGWEED_SOUTH_WEST, config.digweedHighlightColor());
                }
                if (config.notifyDigWeed()) {
                    notifier.notify("A digweed has spawned south west.");
                }
            } else {
                unHighlightObject(AlchemyObject.DIGWEED_SOUTH_WEST);
            }
        } else if (varbitId == VARBIT_DIGWEED_NORTH_WEST) {
            if (value == 1) {
                if (config.highlightDigWeed()) {
                    highlightObject(AlchemyObject.DIGWEED_NORTH_WEST, config.digweedHighlightColor());
                }
                if (config.notifyDigWeed()) {
                    notifier.notify("A digweed has spawned north west.");
                }
            } else {
                unHighlightObject(AlchemyObject.DIGWEED_NORTH_WEST);
            }
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
        }

        if (spotAnimId == SPOT_ANIM_AGITATOR && agitatorPotionType != null) {
            highlightObject(AlchemyObject.AGITATOR, config.stationQuickActionHighlightColor());
        }
    }

    @Subscribe
    public void onScriptPostFired(ScriptPostFired event) {
        if (event.getScriptId() != PROC_MASTERING_MIXOLOGY_BUILD_POTION_ORDER) {
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
        var bestPotionOrderIdx = bestPotionOrder != null ? bestPotionOrder.idx() : -1;

        for (var order : potionOrders) {
            // The first text widget is always the interface title 'Potion Orders'
            appendPotionRecipe(textComponents.get(order.idx()), order.idx(), bestPotionOrderIdx == order.idx(), order.fulfilled());
        }
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
        var strategy = config.strategy();

        if (strategy == Strategy.NONE) {
            return;
        }
        var evaluatorContext = new EvaluatorContext(
                potionOrders,
                client.getVarpValue(VARP_LYE_RESIN),
                client.getVarpValue(VARP_AGA_RESIN),
                client.getVarpValue(VARP_MOX_RESIN)
        );
        bestPotionOrder = strategy.evaluator().evaluate(evaluatorContext);
        LOGGER.debug("Best potion order: {}", bestPotionOrder);

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

    private void appendPotionRecipe(Widget component, int orderIdx, boolean highlight, boolean fulfilled) {
        var potionType = getPotionType(orderIdx);

        if (potionType == null) {
            return;
        }
        var builder = new StringBuilder();

        if (highlight) {
            builder.append("<col=00ff00>").append(component.getText()).append("</col>");
        } else {
            builder.append(component.getText());
        }

        if (fulfilled) {
            builder.append(" (<col=00ff00>ready!</col>)");
        } else {
            builder.append(" (").append(potionType.recipe()).append(")");
        }
        component.setText(builder.toString());
    }

    private void tryFulfillOrder(PotionType potionType, PotionModifier modifier) {
        for (var order : potionOrders) {
            if (order.potionType() == potionType && order.potionModifier() == modifier) {
                LOGGER.debug("Order {} has been fulfilled", order);
                order.setFulfilled(true);
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
            return PotionType.from(client.getVarbitValue(VARBIT_POTION_ORDER_1) - 1);
        } else if (orderIdx == 2) {
            return PotionType.from(client.getVarbitValue(VARBIT_POTION_ORDER_2) - 1);
        } else if (orderIdx == 3) {
            return PotionType.from(client.getVarbitValue(VARBIT_POTION_ORDER_3) - 1);
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
