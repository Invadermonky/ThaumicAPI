package com.invadermonky.thaumicapi.mixins.thaumcraft.events.events;

import com.invadermonky.thaumicapi.utils.helpers.ItemHelper;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import thaumcraft.common.lib.events.PlayerEvents;

@Mixin(value = PlayerEvents.class, remap = false)
public class PlayerEventsMixin {
    @ModifyReturnValue(method = "getFinalDiscount", at = @At("RETURN"))
    private static int getNbtVisDiscount(int original, @Local(argsOnly = true)ItemStack stack) {
        return original + ItemHelper.getNbtVisDiscount(stack);
    }
}
