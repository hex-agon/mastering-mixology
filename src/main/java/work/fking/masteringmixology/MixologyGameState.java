package work.fking.masteringmixology;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.DecorativeObjectDespawned;
import net.runelite.api.events.DecorativeObjectSpawned;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.eventbus.Subscribe;
import work.fking.masteringmixology.enums.AlchemyBuilding;
import work.fking.masteringmixology.enums.AlchemyContract;
import work.fking.masteringmixology.enums.AlchemyPaste;
import work.fking.masteringmixology.enums.AlchemyPotion;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Singleton
public class MixologyGameState {

    @Inject
    private Client client;

    @Inject
    private MasteringMixologyConfig config;

    @Getter
    private int hopperMox = -1;

    @Getter
    private int hopperAga = -1;

    @Getter
    private int hopperLye = -1;

    @Getter
    private int pointsMox = -1;

    @Getter
    private int pointsAga = -1;

    @Getter
    private int pointsLye = -1;

    @Getter
    private int crystalizerProgress = -1;

    @Getter
    private int homogenizerProgress = -1;

    @Getter
    private int concentratorProgress = -1;

    @Getter
    private Map<AlchemyPaste, TileObject> leverObjects;

    @Getter
    private Map<AlchemyBuilding, TileObject> buildingObjects;

    @Getter
    private Set<TileObject> conveyorObjects = new HashSet<>();

    @Getter
    private TileObject mixerObject;

    private AlchemyBuilding lastUsedBuilding = AlchemyBuilding.NONE;
    private int lastUsedBuildingTick = 0;

    public void start() {
        leverObjects = new HashMap<>();
        buildingObjects = new HashMap<>();
        conveyorObjects = new HashSet<>();
        mixerObject = null;
    }

    public void stop() {
        leverObjects.clear();
        buildingObjects.clear();
        conveyorObjects.clear();
        mixerObject = null;
    }

    public Map<AlchemyPaste, Integer> getCurrentMix() {
        var ret = new HashMap<AlchemyPaste, Integer>();

        var westVat = client.getVarbitValue(Constants.VB_VAT_WEST);
        var midVat = client.getVarbitValue(Constants.VB_VAT_MID);
        var eastVat = client.getVarbitValue(Constants.VB_VAT_EAST);

        var vats = new int[] {westVat, midVat, eastVat};

        for (var vat : vats) {
            if (vat == 0) {
                continue;
            }

            AlchemyPaste paste = AlchemyPaste.FromId(vat);
            if (!ret.containsKey(paste)) {
                ret.put(paste, 0);
            }

            ret.put(paste, ret.get(paste) + 1);
        }

        return ret;
    }

    public AlchemyContract[] getContracts() {
        var firstContract = getContractFromVarbitIDs(Constants.VB_CONTRACT_1_POTION, Constants.VB_CONTRACT_1_TYPE);
        var secondContract = getContractFromVarbitIDs(Constants.VB_CONTRACT_2_POTION, Constants.VB_CONTRACT_2_TYPE);
        var thirdContract = getContractFromVarbitIDs(Constants.VB_CONTRACT_3_POTION, Constants.VB_CONTRACT_3_TYPE);

        if (firstContract == null || secondContract == null || thirdContract == null) {
            return new AlchemyContract[0];
        }

        // My allocations!! My heap!! Look what you've _done_ to it!
        return new AlchemyContract[] {
                firstContract,
                secondContract,
                thirdContract
        };
    }

    public AlchemyContract getBestContract(AlchemyContract[] contracts) {
        var bestIndex = getBestContractIndex(contracts);
        if (bestIndex == -1) {
            return null;
        }

        return contracts[bestIndex];
    }

    public int getBestContractIndex(AlchemyContract[] contracts) {
        if (contracts == null || contracts.length == 0) {
            return -1;
        }

        int runnerUp = -1;
        for (var i = 0; i < contracts.length; i++) {
            var contract = contracts[i];
            if (!canMakePotion(contract.getPotion())) {
                continue;
            }

            if (contract.getType() == config.prioritisedBuilding()) {
                return i;
            }

            runnerUp = i;
        }

        return runnerUp;
    }

    public AlchemyBuilding getCurrentlyUsingBuilding() {
        if (lastUsedBuilding == AlchemyBuilding.NONE || lastUsedBuildingTick <= 0) {
            return AlchemyBuilding.NONE;
        }

        if (lastUsedBuildingTick > client.getTickCount() || (client.getTickCount() - lastUsedBuildingTick) >= 2) {
            return AlchemyBuilding.NONE;
        }

        if (!client.getLocalPlayer().getWorldLocation().equals(lastUsedBuilding.getInteractionLocation())) {
            return AlchemyBuilding.NONE;
        }

        return lastUsedBuilding;
    }

    /**
     * @return True if the player is currently located within the minigame area
     */
    public boolean isInArea() {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return false;
        }

        Player local = client.getLocalPlayer();
        if (local == null) {
            return false;
        }

        WorldPoint location = local.getWorldLocation();
        if (location.getPlane() != 0) {
            return false;
        }

        int x = location.getX();
        int y = location.getY();

        return x >= 1384 && x <= 1404 && y >= 9319 && y <= 9334;
    }

    public boolean canMakePotion(AlchemyPotion potion) {
        if (client.getBoostedSkillLevel(Skill.HERBLORE) < potion.getHerbloreLevel()) {
            return false;
        }

        if (potion.getMoxRequired() > 0 && hopperMox < (potion.getMoxRequired() * 10)) {
            return false;
        }
        if (potion.getAgaRequired() > 0 && hopperAga < (potion.getAgaRequired() * 10)) {
            return false;
        }
        if (potion.getLyeRequired() > 0 && hopperLye < (potion.getLyeRequired() * 10)) {
            return false;
        }

        return true;
    }

    @Subscribe
    public void onDecorativeObjectSpawned(DecorativeObjectSpawned event) {
        TileObject newObject = event.getDecorativeObject();

        if (newObject.getId() == Constants.OBJECT_LEVER_AGA) {
            leverObjects.put(AlchemyPaste.AGA, newObject);
        }
    }

    @Subscribe
    public void onDecorativeObjectDespawned(DecorativeObjectDespawned event) {
        TileObject newObject = event.getDecorativeObject();

        if (newObject.getId() == Constants.OBJECT_LEVER_AGA) {
            leverObjects.remove(AlchemyPaste.AGA);
        }
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event) {
        TileObject newObject = event.getGameObject();
        if (newObject.getId() == Constants.OBJECT_MIXER) {
            mixerObject = newObject;
        }
        if (newObject.getId() == Constants.OBJECT_LEVER_MOX) {
            leverObjects.put(AlchemyPaste.MOX, newObject);
        }
        if (newObject.getId() == Constants.OBJECT_LEVER_LYE) {
            leverObjects.put(AlchemyPaste.LYE, newObject);
        }

        var building = AlchemyBuilding.FromBuildingObjectId(newObject.getId());
        if (building != AlchemyBuilding.NONE) {
            buildingObjects.put(building, newObject);
        }

        if (newObject.getId() == Constants.OBJECT_CONVEYOR_BELT) {
            conveyorObjects.add(newObject);
        }
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event) {
        TileObject oldObject = event.getGameObject();
        if (oldObject == mixerObject) {
            mixerObject = null;
        }
        if (oldObject.getId() == Constants.OBJECT_LEVER_MOX) {
            leverObjects.remove(AlchemyPaste.MOX);
        }
        if (oldObject.getId() == Constants.OBJECT_LEVER_AGA) {
            leverObjects.remove(AlchemyPaste.AGA);
        }
        if (oldObject.getId() == Constants.OBJECT_LEVER_LYE) {
            leverObjects.remove(AlchemyPaste.LYE);
        }

        var building = AlchemyBuilding.FromBuildingObjectId(oldObject.getId());
        if (building != AlchemyBuilding.NONE) {
            buildingObjects.remove(building);
        }

        if (oldObject.getId() == Constants.OBJECT_CONVEYOR_BELT) {
            conveyorObjects.remove(oldObject);
        }
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        var varbitId = event.getVarbitId();
        var varbitValue = event.getValue();

        // Java isn't letting me do a switch statement on these constants????
        // This fucking language man
        if (varbitId == Constants.VB_PASTE_COUNT_MOX) {
            hopperMox = varbitValue;
            return;
        }
        if (varbitId == Constants.VB_PASTE_COUNT_AGA) {
            hopperAga = varbitValue;
            return;
        }
        if (varbitId == Constants.VB_PASTE_COUNT_LYE) {
            hopperLye = varbitValue;
            return;
        }

        if (varbitId == Constants.VB_POINTS_COUNT_MOX) {
            pointsMox = varbitValue;
            return;
        }
        if (varbitId == Constants.VB_POINTS_COUNT_AGA) {
            pointsAga = varbitValue;
            return;
        }
        if (varbitId == Constants.VB_POINTS_COUNT_LYE) {
            pointsLye = varbitValue;
            return;
        }

        var buildingBeingUsed = AlchemyBuilding.FromProgressVarbitId(varbitId);
        if (buildingBeingUsed != AlchemyBuilding.NONE) {
            handleBuildingUsed(buildingBeingUsed, varbitValue);
        }
    }

    private AlchemyContract getContractFromVarbitIDs(int potionVarbitId, int cookTypeVarbitId) {
        var potion = client.getVarbitValue(potionVarbitId);
        var potionType = client.getVarbitValue(cookTypeVarbitId);

        if (potion <= 0 || potionType <= 0) {
            return null;
        }

        return new AlchemyContract(AlchemyPotion.FromId(potion), AlchemyBuilding.FromId(potionType));
    }

    private void handleBuildingUsed(AlchemyBuilding building, int progress) {
        var player = client.getLocalPlayer();
        var interacting = player.getInteracting();

        if (building == AlchemyBuilding.AGITATOR_HOMOGENIZER) {
            homogenizerProgress = progress;
        }
        if (building == AlchemyBuilding.RETORT_CONCENTRATOR) {
            concentratorProgress = progress;
        }
        if (building == AlchemyBuilding.ALEMBIC_CRYSTALIZER) {
            crystalizerProgress = progress;
        }

        if (progress != 0) {
            lastUsedBuilding = building;
            lastUsedBuildingTick = client.getTickCount();
        } else if (progress == 0) {
            lastUsedBuilding = AlchemyBuilding.NONE;
        }
    }
}
