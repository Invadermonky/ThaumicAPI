package com.invadermonky.thaumicapi.warpevents;

import com.invadermonky.thaumicapi.api.warpevent.IWarpEvent;
import com.invadermonky.thaumicapi.events.PlayerWarpEvent;
import com.invadermonky.thaumicapi.network.messages.MessageWarpEvent;
import com.invadermonky.thaumicapi.utils.helpers.PlayerHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.common.lib.network.PacketHandler;

public class WarpEventHandler {

    public static void checkWarpEvent(EntityPlayer player) {
        IPlayerWarp warpCapability = ThaumcraftCapabilities.getWarp(player);
        int temp = warpCapability.get(IPlayerWarp.EnumWarpType.TEMPORARY);
        int norm = warpCapability.get(IPlayerWarp.EnumWarpType.NORMAL);
        int perm = warpCapability.get(IPlayerWarp.EnumWarpType.PERMANENT);
        if (temp > 0) {
            ThaumcraftApi.internalMethods.addWarpToPlayer(player, -1, IPlayerWarp.EnumWarpType.TEMPORARY);
        }

        //TODO: Creative check might be better somewhere else.
        if(player.isCreative())
            return;

        int playerWarp = temp + norm + perm;
        int gearWarp = PlayerHelper.getWarpFromGear(player);
        int totalWarp = playerWarp + gearWarp;
        int actualWarp = norm + perm;

        int warpCounter = warpCapability.getCounter();
        if(warpCounter > 0 && totalWarp > 0 && (double) player.world.rand.nextInt(100) <= Math.sqrt(warpCounter)) {
            warpCounter = (int) (warpCounter - Math.max(5.0, Math.sqrt(warpCounter) * 2.0 - (gearWarp * 2)));
            warpCapability.setCounter(warpCounter);
            int eventWarp = player.world.rand.nextInt(playerWarp) + gearWarp - PlayerHelper.getWarpProtectionFromGear(player);

            IWarpEvent warpEvent = WarpEventRegistry.getWarpEvent(player, eventWarp);
            if(warpEvent != null) {
                performWarpEvent(player, totalWarp, warpEvent);
            }
            checkWarpResearch(player, actualWarp);
        }
    }

    public static void performWarpEvent(EntityPlayer player, int totalWarp, IWarpEvent warpEvent) {
        if(player.world.isRemote)
            return;

        PlayerWarpEvent.Pre event = new PlayerWarpEvent.Pre(player, totalWarp, warpEvent);
        if (!MinecraftForge.EVENT_BUS.post(event)) {
            //Perform the event server-side
            event.getWarpEvent().performWarpEvent(player, totalWarp);
            //Send warp event message
            ITextComponent message = event.getWarpEvent().getEventMessage(player, totalWarp);
            if (message != null) {
                player.sendStatusMessage(message, true);
            }
            //Perform the event client-side
            if (player instanceof EntityPlayerMP) {
                PacketHandler.INSTANCE.sendTo(new MessageWarpEvent(event.getWarpEvent(), totalWarp), (EntityPlayerMP) player);
            }
            //Post the post-event event
            MinecraftForge.EVENT_BUS.post(new PlayerWarpEvent.Post(player, totalWarp, event.getWarpEvent()));
        }
    }

    public static void checkWarpResearch(EntityPlayer player, int actualWarp) {
        if (actualWarp > 10 && !ThaumcraftCapabilities.knowsResearch(player, "BATHSALTS") && !ThaumcraftCapabilities.knowsResearch(player, "!BATHSALTS")) {
            player.sendStatusMessage(new TextComponentTranslation("warp.text.8").setStyle(new Style().setColor(TextFormatting.DARK_PURPLE).setItalic(true)), true);
            ThaumcraftApi.internalMethods.completeResearch(player, "!BATHSALTS");
        }

        if (actualWarp > 25 && !ThaumcraftCapabilities.knowsResearch(player, "ELDRITCHMINOR")) {
            ThaumcraftApi.internalMethods.completeResearch(player, "ELDRITCHMINOR");
        }

        if (actualWarp > 50 && !ThaumcraftCapabilities.knowsResearch(player, "ELDRITCHMAJOR")) {
            ThaumcraftApi.internalMethods.completeResearch(player, "ELDRITCHMAJOR");
        }
    }
}
