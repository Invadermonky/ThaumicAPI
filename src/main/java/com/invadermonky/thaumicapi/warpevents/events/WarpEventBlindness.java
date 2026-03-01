package com.invadermonky.thaumicapi.warpevents.events;

import com.invadermonky.thaumicapi.api.warpevent.IWarpEvent;
import com.invadermonky.thaumicapi.api.warpevent.WarpEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.ITextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@WarpEvent
public class WarpEventBlindness implements IWarpEvent {

    @Override
    public @NotNull String getEventName() {
        return "thaumcraft.blindness";
    }

    @Override
    public int getMinimumWarp() {
        return 68;
    }

    @Override
    public int getEventWeight() {
        return 4;
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
            PotionEffect effect = new PotionEffect(MobEffects.BLINDNESS, Math.min(32000, 5 * warp), 0, true, true);
            player.addPotionEffect(effect);
        }
    }
}
