package com.invadermonky.thaumicapi.events;

import com.invadermonky.thaumicapi.api.warpevent.IWarpEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * Fired when a player warp event occurs. See subevents.
 */
public class PlayerWarpEvent extends PlayerEvent {
    protected IWarpEvent warpEvent;
    protected final int playerWarp;

    public PlayerWarpEvent(EntityPlayer player, int playerWarp, IWarpEvent warpEvent) {
        super(player);
        this.warpEvent = warpEvent;
        this.playerWarp = playerWarp;
    }

    public String getWarpEventName() {
        return this.warpEvent.getEventName();
    }

    public int getPlayerWarp() {
        return this.playerWarp;
    }

    public IWarpEvent getWarpEvent() {
        return this.warpEvent;
    }

    public void setWarpEvent(IWarpEvent event) {
        this.warpEvent = event;
    }

    /**
     * Fired prior to a warp event occurring. This event fires server-side.
     * <p>
     * This event is {@link Cancelable}.<br>
     * This event does not have a {@link Result}.
     */
    @Cancelable
    public static class Pre extends PlayerWarpEvent {
        public Pre(EntityPlayer player, int warp, IWarpEvent event) {
            super(player, warp, event);
        }

        @Override
        public boolean isCancelable() {
            return true;
        }
    }

    /**
     * Fired after a warp event occurs. This event fires server-side.
     * <p>
     * This event is not {@link Cancelable}.<br>
     * This event does not have a {@link Result}.
     */
    public static class Post extends PlayerWarpEvent {
        public Post(EntityPlayer player, int warp, IWarpEvent event) {
            super(player, warp, event);
        }
    }
}
