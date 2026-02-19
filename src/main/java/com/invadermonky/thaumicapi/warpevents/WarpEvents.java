package com.invadermonky.thaumicapi.warpevents;

import baubles.api.BaublesApi;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.common.items.armor.ItemFortressArmor;
import thaumcraft.common.lib.events.PlayerEvents;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketMiscEvent;

public class WarpEvents {
    //TODO: These are the remaining methos that need to be transferred to the new event handler.
    public static void checkWarpEvent(EntityPlayer player) {
        IPlayerWarp wc = ThaumcraftCapabilities.getWarp(player);
        ThaumcraftApi.internalMethods.addWarpToPlayer(player, -1, IPlayerWarp.EnumWarpType.TEMPORARY);
        int tw = wc.get(IPlayerWarp.EnumWarpType.TEMPORARY);
        int nw = wc.get(IPlayerWarp.EnumWarpType.NORMAL);
        int pw = wc.get(IPlayerWarp.EnumWarpType.PERMANENT);
        int warp = tw + nw + pw;
        int actualwarp = pw + nw;
        int gearWarp = getWarpFromGear(player);
        warp += gearWarp;
        int warpCounter = wc.getCounter();
        int r = player.world.rand.nextInt(100);
        if (warpCounter > 0 && warp > 0 && (double)r <= Math.sqrt((double)warpCounter)) {
            warp = Math.min(100, (warp + warp + warpCounter) / 3);
            warpCounter = (int)((double)warpCounter - Math.max((double)5.0F, Math.sqrt((double)warpCounter) * (double)2.0F - (double)(gearWarp * 2)));
            wc.setCounter(warpCounter);
            int eff = player.world.rand.nextInt(warp) + gearWarp;
            ItemStack helm = (ItemStack)player.inventory.armorInventory.get(3);
            if (helm.getItem() instanceof ItemFortressArmor && helm.hasTagCompound() && helm.getTagCompound().hasKey("mask") && helm.getTagCompound().getInteger("mask") == 0) {
                eff -= 2 + player.world.rand.nextInt(4);
            }

            PacketHandler.INSTANCE.sendTo(new PacketMiscEvent((byte)0), (EntityPlayerMP)player);
            if (eff > 0) {
            }

            if (actualwarp > 10 && !ThaumcraftCapabilities.knowsResearch(player, new String[]{"BATHSALTS"}) && !ThaumcraftCapabilities.knowsResearch(player, new String[]{"!BATHSALTS"})) {
                player.sendStatusMessage(new TextComponentString("§5§o" + I18n.translateToLocal("warp.text.8")), true);
                ThaumcraftApi.internalMethods.completeResearch(player, "!BATHSALTS");
            }

            if (actualwarp > 25 && !ThaumcraftCapabilities.knowsResearch(player, new String[]{"ELDRITCHMINOR"})) {
                ThaumcraftApi.internalMethods.completeResearch(player, "ELDRITCHMINOR");
            }

            if (actualwarp > 50 && !ThaumcraftCapabilities.knowsResearch(player, new String[]{"ELDRITCHMAJOR"})) {
                ThaumcraftApi.internalMethods.completeResearch(player, "ELDRITCHMAJOR");
            }
        }

    }

    private static int getWarpFromGear(EntityPlayer player) {
        int w = PlayerEvents.getFinalWarp(player.getHeldItemMainhand(), player);

        for(int a = 0; a < 4; ++a) {
            w += PlayerEvents.getFinalWarp((ItemStack)player.inventory.armorInventory.get(a), player);
        }

        IInventory baubles = BaublesApi.getBaubles(player);

        for(int a = 0; a < baubles.getSizeInventory(); ++a) {
            w += PlayerEvents.getFinalWarp(baubles.getStackInSlot(a), player);
        }

        return w;
    }


}
