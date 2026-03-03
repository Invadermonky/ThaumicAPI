package com.invadermonky.thaumicapi.handlers;

import com.invadermonky.thaumicapi.ThaumicAPIMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = ThaumicAPIMod.MOD_ID)
public class CommonEventHandler {
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            if (PlayerMovementAbilityHandler.isValidSideForMovement(player)) {
                PlayerMovementAbilityHandler.tick(player);
            }
        }
    }

    @SubscribeEvent
    public static void onJump(LivingEvent.LivingJumpEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer && PlayerMovementAbilityHandler.isValidSideForMovement((EntityPlayer) event.getEntityLiving())) {
            PlayerMovementAbilityHandler.onJump((EntityPlayer) event.getEntityLiving());
        }
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityPlayer && PlayerMovementAbilityHandler.isValidSideForMovement((EntityPlayer) event.getEntity())) {
            PlayerMovementAbilityHandler.onPlayerRecreation((EntityPlayer) event.getEntity());
        }
    }
}
