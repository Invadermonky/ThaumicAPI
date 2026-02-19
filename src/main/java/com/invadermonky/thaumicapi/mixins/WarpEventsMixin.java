package com.invadermonky.thaumicapi.mixins;

import com.invadermonky.thaumicapi.api.warpevent.IWarpEvent;
import com.invadermonky.thaumicapi.events.PlayerWarpEvent;
import com.invadermonky.thaumicapi.network.messages.MessageWarpEvent;
import com.invadermonky.thaumicapi.registry.WarpEventRegistry;
import com.invadermonky.thaumicapi.utils.PlayerHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.*;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.common.items.armor.ItemFortressArmor;
import thaumcraft.common.lib.events.WarpEvents;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketMiscEvent;

@Mixin(value = WarpEvents.class, remap = false)
public class WarpEventsMixin {
    @Inject(method = "checkWarpEvent", at = @At("HEAD"), cancellable = true)
    private static void tapiOverhaulWarpEvents(EntityPlayer player, CallbackInfo ci) {

        //TODO: Maybe rewrite all of this?
        IPlayerWarp warpCap = ThaumcraftCapabilities.getWarp(player);
        int temp = warpCap.get(IPlayerWarp.EnumWarpType.TEMPORARY);
        int norm = warpCap.get(IPlayerWarp.EnumWarpType.NORMAL);
        int perm = warpCap.get(IPlayerWarp.EnumWarpType.PERMANENT);
        if (temp > 0) {
            ThaumcraftApi.internalMethods.addWarpToPlayer(player, -1, IPlayerWarp.EnumWarpType.TEMPORARY);
        }
        if (!player.isCreative()) {
            int playerWarp = temp + norm + perm;
            int actualWarp = norm + perm;
            int gearWarp = PlayerHelper.getWarpFromGear(player);
            int totalWarp = playerWarp + gearWarp;

            int warpCounter = warpCap.getCounter();






            if (warpCounter > 0 && totalWarp + gearWarp > 0 && (double) player.world.rand.nextInt(100) <= Math.sqrt(warpCounter)) {
                warpCounter = (int) (warpCounter - Math.max(5.0, Math.sqrt(warpCounter) * 2.0 - (gearWarp * 2)));
                warpCap.setCounter(warpCounter);


                int effectiveWarp = player.world.rand.nextInt(totalWarp) + gearWarp;
                ItemStack helm = player.inventory.armorInventory.get(3);
                if (helm.getItem() instanceof ItemFortressArmor && helm.getTagCompound() != null && helm.getTagCompound().hasKey("mask") && helm.getTagCompound().getInteger("mask") == 0) {
                    effectiveWarp -= 2 + player.world.rand.nextInt(4);
                }

                PacketHandler.INSTANCE.sendTo(new PacketMiscEvent((byte) 0), (EntityPlayerMP) player);








                IWarpEvent warpEvent = WarpEventRegistry.getWarpEvent(player.world, effectiveWarp);
                if (warpEvent != null) {
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
        ci.cancel();
    }
}
