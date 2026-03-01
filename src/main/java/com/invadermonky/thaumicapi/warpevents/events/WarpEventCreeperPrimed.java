package com.invadermonky.thaumicapi.warpevents.events;

import com.invadermonky.thaumicapi.api.warpevent.IWarpEvent;
import com.invadermonky.thaumicapi.api.warpevent.WarpEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@WarpEvent
public class WarpEventCreeperPrimed implements IWarpEvent {
    @Override
    public @NotNull String getEventName() {
        return "thaumcraft.creeper_primed";
    }

    @Override
    public int getMinimumWarp() {
        return 0;
    }

    @Override
    public int getEventWeight() {
        return 4;
    }

    @Override
    public void playClientEventSound(EntityPlayer player, int warp) {
        player.world.playSound(player, player.getPosition(), SoundEvents.ENTITY_CREEPER_PRIMED, SoundCategory.AMBIENT, 1.0F, 0.5F);
    }

    @Nullable
    @Override
    public ITextComponent getEventMessage(EntityPlayer player, int warp) {
        return null;
    }

    @Override
    public void performWarpEvent(EntityPlayer player, int warp) {

    }

    @Override
    public boolean isStressful() {
        return true;
    }
}
