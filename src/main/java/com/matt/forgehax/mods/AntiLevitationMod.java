package com.matt.forgehax.mods;

import com.matt.forgehax.events.LocalPlayerUpdateEvent;
import com.matt.forgehax.util.mod.loader.RegisterMod;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.matt.forgehax.Wrapper.*;

/**
 * Created on 11/28/2016 by fr1kin
 */

@RegisterMod
public class AntiLevitationMod extends ToggleMod {
    public AntiLevitationMod() {
        super("AntiLevitation", false, "No levitation");
    }

    @SubscribeEvent
    public void onUpdate(LocalPlayerUpdateEvent event) {
        if(getLocalPlayer().isPotionActive(Potion.getPotionFromResourceLocation("levitation"))) {
            getLocalPlayer().removeActivePotionEffect(Potion.getPotionFromResourceLocation("levitation"));
        }
    }
}
