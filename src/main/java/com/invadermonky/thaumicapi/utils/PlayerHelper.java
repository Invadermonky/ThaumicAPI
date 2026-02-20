package com.invadermonky.thaumicapi.utils;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
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

}
