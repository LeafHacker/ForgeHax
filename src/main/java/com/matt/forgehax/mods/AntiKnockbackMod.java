package com.matt.forgehax.mods;

import com.matt.forgehax.asm.events.ApplyCollisionMotionEvent;
import com.matt.forgehax.asm.events.WaterMovementEvent;
import com.matt.forgehax.asm.events.PacketEvent;
import com.matt.forgehax.asm.reflection.FastReflection;
import com.matt.forgehax.util.mod.loader.RegisterMod;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@RegisterMod
public class AntiKnockbackMod extends ToggleMod {
    private Property multiplierX;
    private Property multiplierY;
    private Property multiplierZ;

    public AntiKnockbackMod() {
        super("AntiKnockback", false, "Removes knockback movement");
    }

    @Override
    public void loadConfig(Configuration configuration) {
        addSettings(
                multiplierX = configuration.get(getModName(),
                        "multiplierX",
                        0.D,
                        "X motion multiplier"
                ),
                multiplierY = configuration.get(getModName(),
                        "multiplierY",
                        0.D,
                        "Y motion multiplier"
                ),
                multiplierZ = configuration.get(getModName(),
                        "multiplierZ",
                        0.D,
                        "Z motion multiplier"
                )
        );
    }

    /**
     * Stops TNT and knockback velocity
     */
    @SubscribeEvent
    public void onPacketRecieved(PacketEvent.Incoming.Pre event) {
        if(event.getPacket() instanceof SPacketExplosion) {
            // for tnt knockback
            SPacketExplosion packet = (SPacketExplosion)event.getPacket();
            FastReflection.Fields.SPacketExplosion_motionX.set(packet, FastReflection.Fields.SPacketExplosion_motionX.get(packet) * (float)multiplierX.getDouble());
            FastReflection.Fields.SPacketExplosion_motionY.set(packet, FastReflection.Fields.SPacketExplosion_motionY.get(packet) * (float)multiplierY.getDouble());
            FastReflection.Fields.SPacketExplosion_motionZ.set(packet, FastReflection.Fields.SPacketExplosion_motionZ.get(packet) * (float)multiplierZ.getDouble());
        }
        if(event.getPacket() instanceof SPacketEntityVelocity) {
            // for player knockback
            if(((SPacketEntityVelocity) event.getPacket()).getEntityID() == MC.player.getEntityId()) {
                double multiX = multiplierX.getDouble();
                double multiY = multiplierY.getDouble();
                double multiZ = multiplierZ.getDouble();
                if(multiX == 0 && multiY == 0 && multiZ == 0) {
                    event.setCanceled(true);
                } else {
                    SPacketEntityVelocity packet = (SPacketEntityVelocity)event.getPacket();
                    FastReflection.Fields.SPacketEntityVelocity_motionX.set(packet, (int)(FastReflection.Fields.SPacketEntityVelocity_motionX.get(packet) * multiX));
                    FastReflection.Fields.SPacketEntityVelocity_motionY.set(packet, (int)(FastReflection.Fields.SPacketEntityVelocity_motionY.get(packet) * multiY));
                    FastReflection.Fields.SPacketEntityVelocity_motionZ.set(packet, (int)(FastReflection.Fields.SPacketEntityVelocity_motionZ.get(packet) * multiZ));
                }
            }
        }
    }

    /**
     * Stops velocity from water
     */
    @SubscribeEvent
    public void onWaterMovementEvent(WaterMovementEvent event) {
        if(event.getEntity().equals(MC.player)) {
            Vec3d moveDir = event.getMoveDir().normalize();
            event.getEntity().motionX += (moveDir.xCoord * 0.014D) * multiplierX.getDouble();
            event.getEntity().motionY += (moveDir.yCoord * 0.014D) * multiplierY.getDouble();
            event.getEntity().motionZ += (moveDir.zCoord * 0.014D) * multiplierZ.getDouble();
            event.setCanceled(true);
        }
    }

    /**
     * Stops velocity from collision
     */
    @SubscribeEvent
    public void onApplyCollisionMotion(ApplyCollisionMotionEvent event) {
        if(event.getEntity().equals(MC.player)) {
            event.getEntity().addVelocity(
                    event.getMotionX() * multiplierX.getDouble(),
                    event.getMotionY() * multiplierY.getDouble(),
                    event.getMotionZ() * multiplierZ.getDouble()
            );
            event.setCanceled(true);
        }
    }
}
