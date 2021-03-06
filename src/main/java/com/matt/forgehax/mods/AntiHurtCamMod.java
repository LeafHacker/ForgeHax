package com.matt.forgehax.mods;

import com.matt.forgehax.asm.events.HurtCamEffectEvent;
import com.matt.forgehax.util.mod.loader.RegisterMod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@RegisterMod
public class AntiHurtCamMod extends ToggleMod {
    public AntiHurtCamMod() {
        super("AntiHurtcam", false, "Removes hurt camera effect");
    }

    @SubscribeEvent
    public void onHurtCamEffect(HurtCamEffectEvent event) {
        event.setCanceled(true);
    }
}
