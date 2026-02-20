package com.invadermonky.thaumicapi.mixins.events;

import com.invadermonky.thaumicapi.warpevents.WarpEventHandler;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.common.lib.events.WarpEvents;

@Mixin(value = WarpEvents.class, remap = false)
public class WarpEventsMixin {
    @Inject(method = "checkWarpEvent", at = @At("HEAD"), cancellable = true)
    private static void tapiOverhaulWarpEvents(EntityPlayer player, CallbackInfo ci) {
        WarpEventHandler.checkWarpEvent(player);
        ci.cancel();
    }
}
