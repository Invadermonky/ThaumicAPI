package com.invadermonky.thaumicapi.mixins.multiblocked;

import com.cleanroommc.multiblocked.jei.JeiPlugin;
import com.cleanroommc.multiblocked.jei.ingredient.AspectListIngredient;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = JeiPlugin.class, remap = false)
public class JeiPluginMixinMB {
    @Redirect(
            method = "registerIngredients",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/cleanroommc/multiblocked/jei/ingredient/AspectListIngredient;registerIngredients(Lmezz/jei/api/ingredients/IModIngredientRegistration;)V"
            )
    )
    private void cancelAspectIngredientRegister(AspectListIngredient instance, IModIngredientRegistration registry) {

    }
}
