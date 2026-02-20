package com.invadermonky.thaumicapi.api.warpevent;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import thaumcraft.common.config.ModConfig.CONFIG_GRAPHICS;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IWarpEvent {
    /** The unique name identifier of this warp event. */
    @Nonnull
    String getEventName();

    /** The minimum amount of warp required for this event to occur. This value is exclusive. */
    int getMinimumWarp();

    /** The maximum amount of warp allowed for this event to occur. This value is inclusive. */
    int getMaximumWarp();

    /** Play the warp event sound. This should fire clientside only whenever possible. */
    void playClientEventSound(EntityPlayer player, int warp);

    /** The warp event message. */
    @Nullable
    ITextComponent getEventMessage(EntityPlayer player, int warp);


    void performWarpEvent(EntityPlayer player, int warp);

    /** Whether this event will be disabled by the {@link CONFIG_GRAPHICS#nostress} setting. */
    default boolean isStressful() {
        return false;
    }

    /** Whether this warp event is enabled. This should be used for configuration toggles. */
    default boolean isEnabled() {
        return true;
    }
}
