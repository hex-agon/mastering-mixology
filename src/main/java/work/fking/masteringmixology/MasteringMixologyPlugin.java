package work.fking.masteringmixology;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.EnumComposition;
import net.runelite.api.Skill;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

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

    private static final String COLOR_RED = "e91e63";
    private static final String COLOR_GREEN = "00e676";
    private static final String COLOR_BLUE = "03a9f4";

    private static final int COMPONENT_POTION_ORDERS_GROUP_ID = 882;
    private static final int COMPONENT_POTION_ORDERS = COMPONENT_POTION_ORDERS_GROUP_ID << 16 | 2;
    private static final int EXTRA_WIDTH = 30;

    @Inject
    private Client client;

    @Inject
    private MasteringMixologyConfig config;

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
        var bestExperienceOrderIdx = findBestExperienceOrderIdx();

        for (int orderIdx = 1; orderIdx <= 3; orderIdx++) {
            // The first text widget is always the interface title 'Potion Orders'
            appendPotionRecipe(textComponents.get(orderIdx), orderIdx, bestExperienceOrderIdx == orderIdx);
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

    private int findBestExperienceOrderIdx() {
        if (!config.highlightBestExperience()) {
            return -1;
        }
        var bestOrderIdx = -1;
        var bestExperience = 0;

        for (int orderIdx = 1; orderIdx <= 3; orderIdx++) {
            var potionType = getPotionType(orderIdx);

            if (potionType == null || client.getRealSkillLevel(Skill.HERBLORE) < potionType.levelReq()) {
                continue;
            }
            if (potionType.experience() > bestExperience) {
                bestOrderIdx = orderIdx;
                bestExperience = potionType.experience();
            }
        }
        return bestOrderIdx;
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

    private enum PotionType {
        MAMMOTH_MIGHT_MIX("MMM", 60, 1350),
        MYSTIC_MANA_AMALGAM("MMA", 60, 1750),
        MARLEYS_MOONLIGHT("MML", 60, 2150),
        ALCO_AUGMENTATOR("AAA", 76, 2550),
        AZURE_AURA_MIX("AAM", 68, 2150),
        AQUALUX_AMALGAM("ALA", 72, 2950),
        LIPLACK_LIQUOR("LLL", 86, 3750),
        MEGALITE_LIQUID("MLL", 80, 2950),
        ANTI_LEECH_LOTION("ALL", 84, 3350),
        MIXALOT("MAL", 64, 2550);

        private static final PotionType[] TYPES = PotionType.values();

        private final String recipe;
        private final int levelReq;
        private final int experience;

        PotionType(String recipe, int levelReq, int experience) {
            this.recipe = colorizeRecipe(recipe);
            this.levelReq = levelReq;
            this.experience = experience;
        }

        public static PotionType from(int potionTypeId) {
            if (potionTypeId < 0 || potionTypeId >= TYPES.length) {
                return null;
            }
            return TYPES[potionTypeId];
        }

        private static String colorizeRecipe(String recipe) {
            if (recipe.length() != 3) {
                throw new IllegalArgumentException("Invalid recipe string: " + recipe);
            }
            return colorizeRecipeComponent(recipe.charAt(0))
                    + colorizeRecipeComponent(recipe.charAt(1))
                    + colorizeRecipeComponent(recipe.charAt(2));
        }

        private static String colorizeRecipeComponent(char character) {
            if (character == 'A') {
                return "<col=" + COLOR_GREEN + ">A</col>";
            } else if (character == 'L') {
                return "<col=" + COLOR_RED + ">L</col>";
            } else if (character == 'M') {
                return "<col=" + COLOR_BLUE + ">M</col>";
            } else {
                return String.valueOf(character);
            }
        }

        public String recipe() {
            return recipe;
        }

        public int levelReq() {
            return levelReq;
        }

        public int experience() {
            return experience;
        }
    }
}
