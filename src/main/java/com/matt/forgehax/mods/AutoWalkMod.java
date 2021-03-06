package com.matt.forgehax.mods;

import com.matt.forgehax.events.LocalPlayerUpdateEvent;
import com.matt.forgehax.util.key.Bindings;
import com.matt.forgehax.util.mod.loader.RegisterMod;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import static com.matt.forgehax.Wrapper.*;

@RegisterMod
public class AutoWalkMod extends ToggleMod {
    public Property stopAtUnloadedChunks;

    private boolean isBound = false;

    public AutoWalkMod() {
        super("AutoWalk", false, "Automatically walks forward");
    }

    @Override
    public void loadConfig(Configuration configuration) {
        addSettings(
                stopAtUnloadedChunks = configuration.get(getModName(),
                        "stop_at_unloaded_chunks",
                        true,
                        "Stop moving at unloaded chunks")
        );
    }

    @Override
    public void onDisabled() {
        super.onDisabled();
        if(isBound) {
            Bindings.forward.setPressed(false);
            Bindings.forward.unbind();
            isBound = false;
        }
    }

    @SubscribeEvent
    public void onUpdate(LocalPlayerUpdateEvent event) {
        if(!isBound) {
            Bindings.forward.bind();
            isBound = true;
        }
        if(!Bindings.forward.getBinding().isKeyDown())
            Bindings.forward.setPressed(true);

        if(stopAtUnloadedChunks.getBoolean()) {
            if(!getWorld().getChunkFromBlockCoords(getLocalPlayer().getPosition()).isLoaded())
                Bindings.forward.setPressed(false);
        }
    }
}
