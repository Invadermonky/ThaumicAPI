package com.invadermonky.thaumicapi.warpevents.events;

import com.invadermonky.thaumicapi.api.warpevent.IWarpEvent;
import com.invadermonky.thaumicapi.api.warpevent.WarpEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.Thaumcraft;

@WarpEvent
public class WarpEventExplosionSound implements IWarpEvent {
    @Override
    public @NotNull ResourceLocation getEventName() {
        return new ResourceLocation(Thaumcraft.MODID, "explosion_sound");
    }

    @Override
    public int getMinimumWarp() {
        return 10;
    }

    @Override
    public int getEventWeight() {
        return 10;
    }

    @Override
    public void playClientEventSound(EntityPlayer player, int warp) {
        player.world.playSound(player, player.posX + (double)((player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 10.0F), player.posY + (double)((player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 10.0F), player.posZ + (double)((player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 10.0F), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.AMBIENT, 4.0F, (1.0F + (player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.2F) * 0.7F);
    }

    @Override
    public @Nullable ITextComponent getEventMessage(EntityPlayer player, int warp) {
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
