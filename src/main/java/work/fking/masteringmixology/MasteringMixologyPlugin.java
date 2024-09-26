package work.fking.masteringmixology;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import work.fking.masteringmixology.evaluator.BestExperienceEvaluator;
import work.fking.masteringmixology.evaluator.PotionOrderEvaluator;
import work.fking.masteringmixology.evaluator.PotionOrderEvaluator.EvaluatorContext;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@PluginDescriptor(name = "Mastering Mixology")
public class MasteringMixologyPlugin extends Plugin {

    private static final int PROC_MASTERING_MIXOLOGY_BUILD_POTION_ORDER = 7063;

    private static final int VARBIT_POTION_ORDER_1 = 11315;
    private static final int VARBIT_POTION_MODIFIER_1 = 11316;
    private static final int VARBIT_POTION_ORDER_2 = 11317;
    private static final int VARBIT_POTION_MODIFIER_2 = 11318;
    private static final int VARBIT_POTION_ORDER_3 = 11319;
    private static final int VARBIT_POTION_MODIFIER_3 = 11320;

    private static final int VARBIT_LYE_RESIN = 4414;
    private static final int VARBIT_AGA_RESIN = 4415;
    private static final int VARBIT_MOX_RESIN = 4416;

    private static final int COMPONENT_POTION_ORDERS_GROUP_ID = 882;
    private static final int COMPONENT_POTION_ORDERS = COMPONENT_POTION_ORDERS_GROUP_ID << 16 | 2;
    private static final int EXTRA_WIDTH = 30;

    @Inject
    private Client client;

    @Inject
    private MasteringMixologyConfig config;

    private PotionOrderEvaluator potionOrderStrategy = new BestExperienceEvaluator();

    @Provides
    MasteringMixologyConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(MasteringMixologyConfig.class);
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
        var evaluatorContext = createEvaluatorContext();

        if (evaluatorContext == null) {
            return;
        }
        var bestPotionOrder = potionOrderStrategy.evaluate(evaluatorContext);

        for (int orderIdx = 1; orderIdx <= 3; orderIdx++) {
            // The first text widget is always the interface title 'Potion Orders'
            appendPotionRecipe(textComponents.get(orderIdx), orderIdx, bestPotionOrder.idx() == orderIdx);
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

    private EvaluatorContext createEvaluatorContext() {
        var potionOrders = getPotionOrders();

        if (potionOrders.isEmpty()) {
            return null;
        }
        return new EvaluatorContext(
                potionOrders,
                client.getVarbitValue(VARBIT_LYE_RESIN),
                client.getVarbitValue(VARBIT_AGA_RESIN),
                client.getVarbitValue(VARBIT_MOX_RESIN)
        );
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
}
