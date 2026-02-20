package com.invadermonky.thaumicapi.warpevents.events;

import com.invadermonky.thaumicapi.api.warpevent.IWarpEvent;
import com.invadermonky.thaumicapi.api.warpevent.WarpEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.ITextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.common.lib.potions.PotionBlurredVision;

@WarpEvent
public class WarpEventBlurredVision implements IWarpEvent {
    @Override
    public @NotNull String getEventName() {
        return "thaumcraft.blurred_vision";
    }

    @Override
    public int getMinimumWarp() {
        return 32;
    }

    @Override
    public int getMaximumWarp() {
        return 36;
    }

    @Override
    public void playClientEventSound(EntityPlayer player, int warp) {

    }

    @Override
    public @Nullable ITextComponent getEventMessage(EntityPlayer player, int warp) {
        return null;
    }

    @Override
    public void performWarpEvent(EntityPlayer player, int warp) {
        if(!player.world.isRemote) {
            PotionEffect effect = new PotionEffect(PotionBlurredVision.instance, Math.min(32000, 10 * warp), 0, true, true);
            player.addPotionEffect(effect);
        }
    }
}
