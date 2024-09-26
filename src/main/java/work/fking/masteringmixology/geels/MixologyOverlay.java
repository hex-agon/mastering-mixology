package work.fking.masteringmixology.geels;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemContainer;
import net.runelite.api.Point;
import net.runelite.api.TileObject;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import work.fking.masteringmixology.MasteringMixologyPlugin;
import work.fking.masteringmixology.geels.enums.AlchemyBuilding;
import work.fking.masteringmixology.geels.enums.AlchemyContract;
import work.fking.masteringmixology.geels.enums.AlchemyPaste;

import javax.inject.Inject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;

@Slf4j
public class MixologyOverlay extends Overlay {
    private final Client client;
    private final MixologyGameState gameState;

    @Inject
    private MixologyOverlay(Client client, MixologyGameState gameState, MasteringMixologyPlugin plugin) {
        super(plugin);
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.client = client;
        this.gameState = gameState;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!gameState.isInArea())
            return null;

        final ItemContainer itemContainer = client.getItemContainer(InventoryID.INVENTORY);
        if (itemContainer == null) {
            return null;
        }

        var contracts = gameState.getContracts();
        var bestContract = gameState.getBestContract(contracts);

        if (bestContract == null) {
            return null;
        }

        /**
         * Logic (in this order):
         *
         * If interacting with building, do BUILDING_ACTIVE_HIGHLIGHT
         * If no mix in inventory, do LEVER_OR_MIXER_HIGHLIGHT
         * If special mix in inventory, do CONVEYOR_HIGHLIGHT
         * If bare mix in inventory, do BUILDING_PASSIVE_HIGHLIGHT
         */

        var interactingWithBuilding = gameState.getCurrentlyUsingBuilding() != AlchemyBuilding.NONE;
        var bareMixInInventory = itemContainer.contains(bestContract.getPotion().getBaseItemId());
        var specialMixInInventory = itemContainer.contains(bestContract.getPotion().getInfusedItemId());
        var mixInInventory = bareMixInInventory || specialMixInInventory;

        if (interactingWithBuilding) {
            buildingActiveHighlight(graphics);
            return null;
        }

        if (!mixInInventory) {
            leverOrMixerHighlight(graphics, bestContract);
            return null;
        }

        // TODO: Better state tracking than "any infused of the right type in inventory"
        if (specialMixInInventory) {
            conveyorHighlight(graphics);
            return null;
        }

        if (bareMixInInventory) {
            buildingPassiveHighlight(graphics, bestContract);
            return null;
        }

        return null;
    }

    private void buildingActiveHighlight(Graphics2D graphics) {
        var building = gameState.getCurrentlyUsingBuilding();
        var buildingObject = gameState.getBuildingObjects().get(building);

        if (buildingObject == null) {
            return;
        }

        Color highlightColor = Color.GREEN;
        if (building == AlchemyBuilding.ALEMBIC_CRYSTALIZER && gameState.getCrystalizerProgress() == 4) {
            highlightColor = Color.YELLOW;
        }

        highlightObject(graphics, buildingObject, highlightColor);
    }

    private void buildingPassiveHighlight(Graphics2D graphics, AlchemyContract contract) {
        var building = contract.getType();
        var buildingObject = gameState.getBuildingObjects().get(building);

        if(buildingObject == null) {
            return;
        }

        highlightObject(graphics, buildingObject, Color.YELLOW);
    }

    private void leverOrMixerHighlight(Graphics2D graphics, AlchemyContract contract) {
        var moxLever = gameState.getLeverObjects().get(AlchemyPaste.MOX);
        var agaLever = gameState.getLeverObjects().get(AlchemyPaste.AGA);
        var lyeLever = gameState.getLeverObjects().get(AlchemyPaste.LYE);

        if(moxLever == null || agaLever == null || lyeLever == null) {
            return;
        }

        var currentMix = gameState.getCurrentMix();
        var currentMox = currentMix.getOrDefault(AlchemyPaste.MOX, 0);
        var currentAga = currentMix.getOrDefault(AlchemyPaste.AGA, 0);
        var currentLye = currentMix.getOrDefault(AlchemyPaste.LYE, 0);
        var currentFull = (currentMox + currentAga + currentLye) == 3;
        var requiredMox = contract.getPotion().getMoxRequired();
        var requiredAga = contract.getPotion().getAgaRequired();
        var requiredLye = contract.getPotion().getLyeRequired();

        if(currentMox < requiredMox) {
            highlightObject(graphics, moxLever, Color.YELLOW);
        } else if(currentAga < requiredAga) {
            highlightObject(graphics, agaLever, Color.YELLOW);
        } else if(currentLye < requiredLye) {
            highlightObject(graphics, lyeLever, Color.YELLOW);
        } else {
            highlightObject(graphics, gameState.getMixerObject(), Color.GREEN);
        }
    }

    private void conveyorHighlight(Graphics2D graphics) {
        for(var conveyor : gameState.getConveyorObjects()) {
            highlightObject(graphics, conveyor, Color.GREEN);
        }
    }


    private void highlightObject(Graphics2D graphics, TileObject object, Color color) {
        Point mousePosition = client.getMouseCanvasPosition();

        Shape objectClickbox = object.getClickbox();
        if (objectClickbox != null) {
            if (objectClickbox.contains(mousePosition.getX(), mousePosition.getY())) {
                graphics.setColor(color.darker());
            } else {
                graphics.setColor(color);
            }

            graphics.draw(objectClickbox);
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
            graphics.fill(objectClickbox);
        }
    }
}
