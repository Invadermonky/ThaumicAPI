package com.invadermonky.thaumicapi.warpevents.events;

import com.invadermonky.thaumicapi.api.warpevent.WarpEvent;
import net.minecraft.entity.player.EntityPlayer;
import org.jetbrains.annotations.NotNull;

@WarpEvent
public class WarpEventSpidersReal extends WarpEventSpidersFake {
    @Override
    public @NotNull String getEventName() {
        return "thaumcraft.spiders_real";
    }

    @Override
    public int getMinimumWarp() {
        return 88;
    }

    @Override
    public int getMaximumWarp() {
        return 92;
    }

    @Override
    public void performWarpEvent(EntityPlayer player, int warp) {
        if(!player.world.isRemote) {
            int count = Math.min(50, warp);
            this.spawnSpiders(player, count, true);
        }
    }
}
