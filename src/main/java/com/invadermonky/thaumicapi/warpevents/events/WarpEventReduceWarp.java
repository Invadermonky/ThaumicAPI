package com.invadermonky.thaumicapi.warpevents.events;

import com.invadermonky.thaumicapi.api.warpevent.IWarpEvent;
import com.invadermonky.thaumicapi.api.warpevent.WarpEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.Thaumcraft;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;

@WarpEvent
public class WarpEventReduceWarp implements IWarpEvent {
    @Override
    public @NotNull ResourceLocation getEventName() {
        return new ResourceLocation(Thaumcraft.MODID, "reduce_warp");
    }

    @Override
    public int getMinimumWarp() {
        return 72;
    }

    @Override
    public int getEventWeight() {
        return 10;
    }

    @Override
    public void playClientEventSound(EntityPlayer player, int warp) {

    }

    @Override
    public @Nullable ITextComponent getEventMessage(EntityPlayer player, int warp) {
        return new TextComponentTranslation("warp.text.14").setStyle(new Style().setColor(TextFormatting.DARK_PURPLE).setItalic(true));
    }

    @Override
    public void performWarpEvent(EntityPlayer player, int warp) {
        if(!player.world.isRemote) {
            IPlayerWarp playerWarp = ThaumcraftCapabilities.getWarp(player);
            if (playerWarp.get(IPlayerWarp.EnumWarpType.NORMAL) > 0) {
                ThaumcraftApi.internalMethods.addWarpToPlayer(player, -1, IPlayerWarp.EnumWarpType.NORMAL);
            }
        }
    }
}
