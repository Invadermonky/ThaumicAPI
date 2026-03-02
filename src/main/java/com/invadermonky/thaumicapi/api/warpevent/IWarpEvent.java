package com.invadermonky.thaumicapi.api.warpevent;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import thaumcraft.common.config.ModConfig.CONFIG_GRAPHICS;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IWarpEvent {
    /** The unique resource location identifier of this warp event. */
    @Nonnull
    ResourceLocation getEventName();

    /** The minimum amount of warp required for this event to occur. This value is exclusive. */
    int getMinimumWarp();

    /** The maximum amount of warp allowed for this event to occur. This value is inclusive. */
    default int getMaximumWarp() {
        return Integer.MAX_VALUE;
    }

    int getEventWeight();

    /** Play the warp event sound. This method is only fired client-side. */
    void playClientEventSound(EntityPlayer player, int warp);

    /** The warp event message. */
    @Nullable
    ITextComponent getEventMessage(EntityPlayer player, int warp);

    /** Runs the warp event. This method will fire server-side first, then client-side. */
    void performWarpEvent(EntityPlayer player, int warp);

    /** Checks if this event should fire. This method only fires server-side */
    default boolean shouldEventProcess(EntityPlayer player) {
        return true;
    }

    /** Whether this event will be disabled by the {@link CONFIG_GRAPHICS#nostress} setting. */
    default boolean isStressful() {
        return false;
    }

    /** Whether this warp event is enabled. This should be used for configuration toggles. */
    default boolean isEnabled() {
        return true;
    }
}
