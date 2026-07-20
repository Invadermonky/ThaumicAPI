package com.invadermonky.thaumicapi.mixins.multiblocked;

import com.cleanroommc.multiblocked.jei.ingredient.AspectListIngredient;
import com.invadermonky.thaumicapi.api.ThaumicAPIJEIPlugin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AspectListIngredient.class, remap = false)
public class AspectListIngredientMixinMB {
    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void redirectAspectIngredient(CallbackInfo ci) {
        AspectListIngredient.INSTANCE = ThaumicAPIJEIPlugin.ASPECT_INGREDIENT;
    }
}
