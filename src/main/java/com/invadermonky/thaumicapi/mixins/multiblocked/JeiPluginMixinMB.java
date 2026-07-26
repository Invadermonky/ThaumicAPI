package com.invadermonky.thaumicapi.mixins.multiblocked;

import com.cleanroommc.multiblocked.jei.JeiPlugin;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = JeiPlugin.class, remap = false)
public class JeiPluginMixinMB {
    @ModifyExpressionValue(
            method = "registerIngredients",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/fml/common/Loader;isModLoaded(Ljava/lang/String;)Z",
                    ordinal = 1
            )
    )
    private boolean cancelAspectListRegisterTAPI(boolean original) {
        return true;
    }
}
