package com.invadermonky.thaumicapi.warpevents.events;

import com.invadermonky.thaumicapi.api.warpevent.IWarpEvent;
import com.invadermonky.thaumicapi.api.warpevent.WarpEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.lib.potions.PotionUnnaturalHunger;

@WarpEvent
public class WarpEventUnnaturalHunger implements IWarpEvent {
    @Override
    public @NotNull String getEventName() {
        return "thaumcraft.unnatural_hunger";
    }

    @Override
    public int getMinimumWarp() {
        return 20;
    }

    @Override
    public int getEventWeight() {
        return 4;
    }

    @Override
    public void playClientEventSound(EntityPlayer player, int warp) {

    }

    @Override
    public @Nullable ITextComponent getEventMessage(EntityPlayer player, int warp) {
        return new TextComponentTranslation("warp.text.2").setStyle(new Style().setColor(TextFormatting.DARK_PURPLE).setItalic(true));
    }

    @Override
    public void performWarpEvent(EntityPlayer player, int warp) {
        if(!player.world.isRemote) {
            PotionEffect pe = new PotionEffect(PotionUnnaturalHunger.instance, 5000, Math.min(3, warp / 15), true, true);
            pe.getCurativeItems().clear();
            pe.addCurativeItem(new ItemStack(Items.ROTTEN_FLESH));
            pe.addCurativeItem(new ItemStack(ItemsTC.brain));
            player.addPotionEffect(pe);
        }
    }
}
