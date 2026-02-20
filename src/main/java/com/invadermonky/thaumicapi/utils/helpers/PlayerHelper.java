package com.invadermonky.thaumicapi.utils.helpers;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import thaumcraft.common.items.armor.ItemFortressArmor;
import thaumcraft.common.lib.events.PlayerEvents;

public class PlayerHelper {
    public static int getWarpFromGear(EntityPlayer player) {
        int warp = 0;
        for(EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            warp += PlayerEvents.getFinalWarp(player.getItemStackFromSlot(slot), player);
        }

        IBaublesItemHandler baublesHandler = BaublesApi.getBaublesHandler(player);
        for(int slot = 0; slot < baublesHandler.getSlots(); slot++) {
            ItemStack baubleStack = baublesHandler.getStackInSlot(slot);
            warp += PlayerEvents.getFinalWarp(baubleStack, player);
        }
        return warp;
    }

    public static int getWarpProtectionFromGear(EntityPlayer player) {
        //TODO: Change this into a better handler so more items can be used to negate warp effects.
        int warpReduction = 0;
        ItemStack helm = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        if (helm.getItem() instanceof ItemFortressArmor && helm.getTagCompound() != null && helm.getTagCompound().hasKey("mask") && helm.getTagCompound().getInteger("mask") == 0) {
            warpReduction += 2 + player.world.rand.nextInt(4);
        }
        return warpReduction;
    }

    public static RayTraceResult rayTrace(EntityPlayer player, float partialTicks) {
        return rayTrace(player, player.getAttributeMap().getAttributeInstance(EntityPlayer.REACH_DISTANCE).getAttributeValue(), partialTicks);
    }

    public static RayTraceResult rayTrace(EntityLivingBase entityLiving, double blockReachDistance, float partialTicks) {
        Vec3d height = entityLiving.getPositionEyes(partialTicks);
        Vec3d look = entityLiving.getLook(partialTicks);
        Vec3d reach = height.add(look.x * blockReachDistance, look.y * blockReachDistance, look.z * blockReachDistance);
        return entityLiving.world.rayTraceBlocks(height, reach, false, false, true);
    }
}
