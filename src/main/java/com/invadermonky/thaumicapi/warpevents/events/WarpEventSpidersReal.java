package com.invadermonky.thaumicapi.warpevents.events;

import com.invadermonky.thaumicapi.api.warpevent.WarpEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import thaumcraft.Thaumcraft;

@WarpEvent
public class WarpEventSpidersReal extends WarpEventSpidersFake {
    @Override
    public @NotNull ResourceLocation getEventName() {
        return new ResourceLocation(Thaumcraft.MODID, "spiders_real");
    }

    @Override
    public int getMinimumWarp() {
        return 88;
    }

    @Override
    public void performWarpEvent(EntityPlayer player, int warp) {
        if(!player.world.isRemote) {
            int count = Math.min(50, warp);
            this.spawnSpiders(player, count, true);
        }
    }
}
