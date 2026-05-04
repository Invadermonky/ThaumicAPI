package com.invadermonky.thaumicapi.mixins.mmce;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import kport.modularmagic.common.integration.JeiPlugin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = JeiPlugin.class, remap = false)
public class JeiPluginMixinMMCE {
    /**
     * @author Invadermonky
     * @reason Cancelling duplicate IIngredient registering to prevent JEI crash with MMCE.
     */
    @ModifyExpressionValue(
            method = "registerIngredients",
            at = @At(
                    value = "INVOKE",
                    target = "Lhellfirepvp/modularmachinery/common/base/Mods;isPresent()Z",
                    ordinal = 2
            )
    )
    private boolean cancelIIngredientRegisterMixin(boolean original) {
        return true;
    }
}
