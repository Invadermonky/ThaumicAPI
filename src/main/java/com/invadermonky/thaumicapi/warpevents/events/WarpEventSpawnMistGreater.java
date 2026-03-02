package com.invadermonky.thaumicapi.warpevents.events;

import com.invadermonky.thaumicapi.api.warpevent.WarpEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import thaumcraft.Thaumcraft;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketMiscEvent;

@WarpEvent
public class WarpEventSpawnMistGreater extends WarpEventSpawnMist {
    @Override
    public @NotNull ResourceLocation getEventName() {
        return new ResourceLocation(Thaumcraft.MODID, "spawn_mist_greater");
    }

    @Override
    public int getMinimumWarp() {
        return 64;
    }

    @Override
    public void performWarpEvent(EntityPlayer player, int warp) {
        if(!player.world.isRemote) {
            PacketHandler.INSTANCE.sendTo(new PacketMiscEvent((byte) 1), (EntityPlayerMP) player);
            this.spawnGuardians(player, warp / 30);
        }
    }
}
