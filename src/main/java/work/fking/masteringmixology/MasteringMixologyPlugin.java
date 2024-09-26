package work.fking.masteringmixology;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.EnumComposition;
import net.runelite.api.Skill;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.DecorativeObjectDespawned;
import net.runelite.api.events.DecorativeObjectSpawned;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import net.runelite.client.ui.overlay.OverlayManager;
import work.fking.masteringmixology.constants.MixologyIDs;
import work.fking.masteringmixology.constants.MixologyVarbits;
import work.fking.masteringmixology.model.MixologyStateMachine;
import work.fking.masteringmixology.model.MixologyStats;
import work.fking.masteringmixology.overlays.MasteringMixologyOverlay2D;
import work.fking.masteringmixology.overlays.MasteringMixologyOverlay3D;

@PluginDescriptor(name = "Mastering Mixology")
public class MasteringMixologyPlugin extends Plugin {

    private static final int ARE_BOOTSTRAP_TICK_COUNTER_START = 4;

    @Inject
    private Client client;

    @Inject
    private MixologyStateMachine state;

    @Inject
    private MixologyStats stats;

    @Inject
    private MasteringMixologyOverlay3D overlay3D;

    @Inject
    private MasteringMixologyOverlay2D overlay2D;

    @Inject
    private UIHelper uiHelper;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private ClientThread clientThread;

    private int areaBootstrapTickCounter = ARE_BOOTSTRAP_TICK_COUNTER_START;
    private boolean inArea = false;

    @Override
    protected void startUp() throws Exception {
        overlayManager.add(overlay3D);
        overlayManager.add(overlay2D);
    }

    @Override
    protected void shutDown() throws Exception {
        overlayManager.remove(overlay3D);
        overlayManager.remove(overlay2D);
    }

    @Subscribe
    public void onDecorativeObjectSpawned(DecorativeObjectSpawned event) {
        var object = event.getDecorativeObject();

        if (object.getId() == MixologyIDs.AGA_LEVER) {
            overlay3D.agaLever = object;
        }
    }

    @Subscribe
    public void onDecorativeObjectDespawned(DecorativeObjectDespawned event) {
        var object = event.getDecorativeObject();

        if (object.getId() == MixologyIDs.AGA_LEVER) {
            overlay3D.agaLever = null;
        }
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event) {
        var object = event.getGameObject();

        switch (object.getId()) {
            case MixologyIDs.CONVEYOR_BELT:
                overlay3D.conveyorBelts.add(object);
                break;
            case MixologyIDs.LYE_LEVER:
                overlay3D.lyeLever = object;
                break;
            case MixologyIDs.MOX_LEVER:
                overlay3D.moxLever = object;
                break;
            case MixologyIDs.ALEMBIC:
                overlay3D.alembic = object;
                break;
            case MixologyIDs.AGITATOR:
                overlay3D.agitator = object;
                break;
            case MixologyIDs.RETORT:
                overlay3D.retort = object;
                break;
            case MixologyIDs.VESSEL:
                overlay3D.vessel = object;
                break;
            case MixologyIDs.HOPPER:
                overlay3D.hopper = object;
                break;
            case MixologyIDs.DIGWEED_NE:
                uiHelper.digweedNE = object;
                break;
            case MixologyIDs.DIGWEED_SE:
                uiHelper.digweedSE = object;
                break;
            case MixologyIDs.DIGWEED_SW:
                uiHelper.digweedSW = object;
                break;
            case MixologyIDs.DIGWEED_NW:
                uiHelper.digweedNW = object;
                break;
        }
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event) {
        var object = event.getGameObject();

        switch (object.getId()) {
            case MixologyIDs.CONVEYOR_BELT:
                overlay3D.conveyorBelts.remove(object);
                break;
            case MixologyIDs.LYE_LEVER:
                overlay3D.lyeLever = null;
                break;
            case MixologyIDs.MOX_LEVER:
                overlay3D.moxLever = null;
                break;
            case MixologyIDs.ALEMBIC:
                overlay3D.alembic = null;
                break;
            case MixologyIDs.AGITATOR:
                overlay3D.agitator = null;
                break;
            case MixologyIDs.RETORT:
                overlay3D.retort = null;
                break;
            case MixologyIDs.VESSEL:
                overlay3D.vessel = null;
                break;
            case MixologyIDs.HOPPER:
                overlay3D.hopper = null;
                break;
            case MixologyIDs.DIGWEED_NE:
                uiHelper.digweedNE = null;
                break;
            case MixologyIDs.DIGWEED_SE:
                uiHelper.digweedSE = null;
                break;
            case MixologyIDs.DIGWEED_SW:
                uiHelper.digweedSW = null;
                break;
            case MixologyIDs.DIGWEED_NW:
                uiHelper.digweedNW = null;
                break;
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        Widget mixologyWidget = client.getWidget(MixologyIDs.MIXOLOGY_WIDGET_ID);
        state.onTickUpdate();

        if (mixologyWidget != null) {
            inArea = true;

            if (areaBootstrapTickCounter >= 0) {
                areaBootstrapTickCounter--;

                if (areaBootstrapTickCounter < 0) {
                    state.start();
                }
            }
        } else {
            if (inArea) {
                inArea = false;
                areaBootstrapTickCounter = ARE_BOOTSTRAP_TICK_COUNTER_START;
                state.stop();
            }
        }
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        if (MixologyVarbits.isRelevantVarbit(event.getVarbitId())) {
            state.onVarbitUpdate();
            stats.updateVarbits();
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        stats.processChatMessage(event);
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged configChanged)
    {
        if (configChanged.getGroup().equals(MasteringMixologyConfig.GROUP)) {
            clientThread.invoke(() -> state.onVarbitUpdate());
        }
    }
    @Provides
    MasteringMixologyConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(MasteringMixologyConfig.class);
    }
}
