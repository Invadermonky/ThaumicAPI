package com.invadermonky.thaumicapi.mixins.items;

import com.invadermonky.thaumicapi.utils.helpers.ItemHelper;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thaumcraft.api.items.IVisDiscountGear;
import thaumcraft.common.items.casters.CasterManager;

@Mixin(value = CasterManager.class, remap = false)
public class CasterManagerMixin {
    @Inject(
            method = "getTotalVisDiscount",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/inventory/IInventory;getStackInSlot(I)Lnet/minecraft/item/ItemStack;",
                    ordinal = 0
            )
    )
    private static void getBaubleNbtVisDiscount(EntityPlayer player, CallbackInfoReturnable<Float> cir, @Local(ordinal = 0) IInventory baublesInv,
                                                @Local(ordinal = 1) int slot, @Local(ordinal = 0) LocalIntRef totalRef) {
        ItemStack baubleStack = baublesInv.getStackInSlot(slot);
        int discount = ItemHelper.getNbtVisDiscount(baubleStack);
        if(discount != 0) {
            totalRef.set(totalRef.get() + discount);
        }
    }

    @Inject(
            method = "getTotalVisDiscount",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/EntityPlayer;isPotionActive(Lnet/minecraft/potion/Potion;)Z",
                    ordinal = 0
            )
    )
    private static void getArmorNbtVisDiscount(EntityPlayer player, CallbackInfoReturnable<Float> cir, @Local(ordinal = 0) LocalIntRef totalRef) {
        int discount = 0;
        for(EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            ItemStack stack = player.getItemStackFromSlot(slot);
            if(slot == EntityEquipmentSlot.MAINHAND || slot == EntityEquipmentSlot.OFFHAND) {
                continue;
            }
            discount += ItemHelper.getNbtVisDiscount(stack);
        }

        if(discount != 0) {
            totalRef.set(totalRef.get() + discount);
        }
    }
}
