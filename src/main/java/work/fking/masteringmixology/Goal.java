package work.fking.masteringmixology;

import net.runelite.api.Client;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class Goal {
    private RewardItem rewardItem;
    private String displayName;
    private boolean multiMode = false;
    private double overallProgress = 0.0;
    private int itemsAffordable = 0;
    private int rewardQuantity = 1;

    /*
     * Whenever we recalculate the goal, we will create a new ComponentData object for each component for caching
     */
    private final Map<PotionComponent, ComponentData> componentDataMap = new EnumMap<>(PotionComponent.class);

    public Goal(RewardItem rewardItem) {
        this.rewardItem = rewardItem;
        this.displayName = rewardItem.itemName();
    }

    public RewardItem getRewardItem() {
        return rewardItem;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isMultiMode() {
        return multiMode;
    }

    public double getOverallProgress() {
        return overallProgress;
    }

    public int getItemsAffordable() {
        return itemsAffordable;
    }

    public int getRewardQuantity() {
        return rewardQuantity;
    }

    public ComponentData getComponentData(PotionComponent component) {
        return componentDataMap.get(component);
    }

    public void recalculate(MasteringMixologyConfig config, Client client) {
        List<Selection> selected = collectSelections(config);

        if (!selected.isEmpty()) {
            multiMode = true;
            rewardItem = selected.get(0).item; // representative item for the icon
            displayName = selected.size() == 1
                    ? formatSingleDisplayName(selected.get(0))
                    : "Selected rewards (" + selected.size() + ")";
            rewardQuantity = 1;

            for (var component : PotionComponent.ENTRIES) {
                int currentAmount = client.getVarpValue(component.resinVarpId());
                int summedCost = 0;
                for (Selection sel : selected) {
                    summedCost += sel.item.componentCost(component) * sel.quantity;
                }
                componentDataMap.put(component, new ComponentData(currentAmount, summedCost, 1));
            }
        } else {
            multiMode = false;
            rewardItem = RewardItem.NONE;
            displayName = "";
            rewardQuantity = 1;

            for (var component : PotionComponent.ENTRIES) {
                int currentAmount = client.getVarpValue(component.resinVarpId());
                componentDataMap.put(component, new ComponentData(currentAmount, 0, 1));
            }
        }

        // Calculate the amount of items affordable based on the component with the lowest affordable amount
        int minAffordable = componentDataMap.values().stream()
                .mapToInt(data -> data.affordableAmount)
                .min()
                .orElse(0);
        itemsAffordable = Math.min(minAffordable, rewardQuantity);

        // Overall progress is the average of all component progress
        overallProgress = componentDataMap.values().stream()
                .mapToDouble(data -> data.percentageToGoal)
                .average()
                .orElse(0.0);
    }

    private static String formatSingleDisplayName(Selection sel) {
        if (sel.quantity > 1) {
            return sel.item.itemName() + " x" + sel.quantity;
        }
        return sel.item.itemName();
    }

    static List<Selection> collectSelections(MasteringMixologyConfig config) {
        List<Selection> list = new ArrayList<>();
        if (config.trackPrescriptionGoggles()) {
            list.add(new Selection(RewardItem.PRESCRIPTION_GOGGLES, 1));
        }
        if (config.trackAlchemistLabcoat()) {
            list.add(new Selection(RewardItem.ALCHEMIST_LABCOAT, 1));
        }
        if (config.trackAlchemistPants()) {
            list.add(new Selection(RewardItem.ALCHEMIST_PANTS, 1));
        }
        if (config.trackAlchemistGloves()) {
            list.add(new Selection(RewardItem.ALCHEMIST_GLOVES, 1));
        }
        if (config.trackReagentPouch()) {
            list.add(new Selection(RewardItem.REAGENT_POUCH, 1));
        }
        if (config.trackPotionStorage()) {
            list.add(new Selection(RewardItem.POTION_STORAGE, 1));
        }
        if (config.trackChuggingBarrel()) {
            list.add(new Selection(RewardItem.CHUGGING_BARREL, 1));
        }
        if (config.trackAlchemistsAmulet()) {
            list.add(new Selection(RewardItem.ALCHEMISTS_AMULET, 1));
        }
        if (config.trackApprenticePack()) {
            list.add(new Selection(RewardItem.APPRENTICE_POTION_PACK, config.apprenticePackQuantity()));
        }
        if (config.trackAdeptPack()) {
            list.add(new Selection(RewardItem.ADEPT_POTION_PACK, config.adeptPackQuantity()));
        }
        if (config.trackExpertPack()) {
            list.add(new Selection(RewardItem.EXPERT_POTION_PACK, config.expertPackQuantity()));
        }
        if (config.trackAldarium()) {
            list.add(new Selection(RewardItem.ALDARIUM, config.aldariumQuantity()));
        }
        return list;
    }

    static final class Selection {
        final RewardItem item;
        final int quantity;

        Selection(RewardItem item, int quantity) {
            this.item = item;
            this.quantity = quantity;
        }
    }

    public static class ComponentData {
        final int currentAmount;
        final int goalAmount;
        final double percentageToGoal;
        final int affordableAmount;

        ComponentData(int currentAmount, int baseGoalAmount, int rewardQuantity) {
            this.currentAmount = currentAmount;
            this.goalAmount = baseGoalAmount * rewardQuantity;

            if (goalAmount == 0) {
                this.percentageToGoal = 1.0;
                this.affordableAmount = rewardQuantity;
            } else {
                this.percentageToGoal = Math.min((double) currentAmount / goalAmount, 1.0);
                this.affordableAmount = currentAmount / baseGoalAmount;
            }
        }
    }
}
