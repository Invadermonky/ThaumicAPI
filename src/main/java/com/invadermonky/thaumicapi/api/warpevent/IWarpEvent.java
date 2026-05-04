package com.invadermonky.thaumicapi.api.warpevent;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import thaumcraft.common.config.ModConfig.CONFIG_GRAPHICS;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * An interface used to create new Thaumcraft 6 warp events.
 */
public interface IWarpEvent {
    /**
     * The unique resource location identifier of this warp event.
     */
    @Nonnull
    ResourceLocation getEventName();

    /**
     * The minimum amount of player warp required for this event to occur. This value is exclusive.
     */
    int getMinimumWarp();

    /**
     * The maximum amount of player warp allowed for this event to occur. This value is inclusive.
     */
    default int getMaximumWarp() {
        return Integer.MAX_VALUE;
    }

    /**
     * The weight of this warp event. Higher weight will increase the chance of this event
     * occurring. Base Thaumcraft events have a default weight of 10.
     */
    int getEventWeight();

    /**
     * Play the warp event sound. This method is only fired client-side.
     */
    void playClientEventSound(EntityPlayer player, int warp);

    /**
     * Returns the warp event message sent to the player when this event fires.
     */
    @Nullable
    ITextComponent getEventMessage(EntityPlayer player, int warp);

    /**
     * Runs the warp event. This is where all warp event processing should occur.
     * This method will fire server-side first, then client-side.
     */
    void performWarpEvent(EntityPlayer player, int warp);

    /**
     * Checks if this event should fire. This can be used to create event conditionals
     * such as research requirements or location checks. This method fires server-side.
     */
    default boolean shouldEventProcess(EntityPlayer player) {
        return true;
    }

    /**
     * Whether this event will be disabled by the {@link CONFIG_GRAPHICS#nostress} setting.
     * This setting is primarily used to disable events that may cause anxiety for users
     * such as the heartbeat sound effect or creeper fuse igniting.
     */
    default boolean isStressful() {
        return false;
    }

    /**
     * Whether this warp event is enabled. Returning false with this method will fully
     * disable the event, such as with configuration toggles.
     * <p>
     * For event conditionals, use {@link IWarpEvent#shouldEventProcess(EntityPlayer)}.
     */
    default boolean isEnabled() {
        return true;
    }
}
