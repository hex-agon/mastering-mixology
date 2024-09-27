package work.fking.masteringmixology;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Skill;
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
import work.fking.masteringmixology.evaluator.PotionOrderEvaluator.EvaluatorContext;

import javax.inject.Inject;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@PluginDescriptor(name = "Mastering Mixology")
public class MasteringMixologyPlugin extends Plugin {

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
    private static final int EXTRA_WIDTH = 30;

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
    private MasteringMixologyTooltips tooltips;

    private final Map<AlchemyObject, HighlightedObject> highlightedObjects = new LinkedHashMap<>();
    private List<PotionOrder> potionOrders = Collections.emptyList();
    private PotionOrder bestPotionOrder;

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
        overlayManager.add(tooltips);
    }

    @Override
    protected void shutDown() {
        overlayManager.remove(overlay);
        overlayManager.remove(tooltips);
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
        var widget = client.getWidget(COMPONENT_POTION_ORDERS);

        if (widget != null) {
            widget.setOriginalWidth(widget.getOriginalWidth() + EXTRA_WIDTH);
        }
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
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        var varbitId = event.getVarbitId();
        var value = event.getValue();

        // Whenever a potion is delivered, all the potion order related varbits are reset to 0 first then
        // set to the new values. We can use this to clear all the stations.
        if (varbitId == VARBIT_POTION_ORDER_1 && value == 0) {
            unHighlightAllStations();
        } else if (varbitId == VARBIT_MIXING_VESSEL_POTION) {
            if (!config.highlightStations() || value == 0) {
                return;
            }
            var mixingVesselPotionType = PotionType.from(value - 1);
            var anyMatch = false;

            for (var potionOrder : potionOrders) {
                if (potionOrder.potionType() == mixingVesselPotionType) {
                    anyMatch = true;
                    unHighlightAllStations();
                    highlightObject(potionOrder.potionModifier().alchemyObject(), config.stationHighlightColor());
                }
            }
            if (!anyMatch) {
                unHighlightAllStations();
            }
        } else if (varbitId == VARBIT_ALEMBIC_POTION && value == 0) {
            // Finished crystalising
            unHighlightObject(AlchemyObject.ALEMBIC);
        } else if (varbitId == VARBIT_AGITATOR_POTION && value == 0) {
            // Finished homogenising
            unHighlightObject(AlchemyObject.AGITATOR);
        } else if (varbitId == VARBIT_RETORT_POTION && value == 0) {
            // Finished crystalising
            unHighlightObject(AlchemyObject.RETORT);
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

        if (spotAnimId == SPOT_ANIM_ALEMBIC && highlightedObjects.containsKey(AlchemyObject.ALEMBIC)) {
            highlightObject(AlchemyObject.ALEMBIC, config.stationQuickActionHighlightColor());
        }

        if (spotAnimId == SPOT_ANIM_AGITATOR && highlightedObjects.containsKey(AlchemyObject.AGITATOR)) {
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
        updatePotionOrders();

        var bestPotionOrderIdx = bestPotionOrder != null ? bestPotionOrder.idx() : -1;

        for (int orderIdx = 1; orderIdx <= 3; orderIdx++) {
            // The first text widget is always the interface title 'Potion Orders'
            appendPotionRecipe(textComponents.get(orderIdx), orderIdx, bestPotionOrderIdx == orderIdx);
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

    private void updatePotionOrders() {
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

    private void appendPotionRecipe(Widget component, int orderIdx, boolean highlight) {
        var potionType = getPotionType(orderIdx);

        if (potionType == null) {
            return;
        }
        if (highlight) {
            component.setText("<col=00ff00>" + component.getText() + "</col> (" + potionType.recipe() + ")");
        } else {
            component.setText(component.getText() + " (" + potionType.recipe() + ")");
        }
        component.setOriginalWidth(component.getOriginalWidth() + EXTRA_WIDTH);
    }

    private List<PotionOrder> getPotionOrders() {
        var potionOrders = new ArrayList<PotionOrder>(3);

        for (int orderIdx = 1; orderIdx <= 3; orderIdx++) {
            var potionType = getPotionType(orderIdx);
            var potionModifier = getPotionModifier(orderIdx);

            if (potionType == null || potionModifier == null) {
                continue;
            }
            // Player cannot make the potion so we don't even consider it as an option
            if (client.getRealSkillLevel(Skill.HERBLORE) < potionType.levelReq()) {
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
