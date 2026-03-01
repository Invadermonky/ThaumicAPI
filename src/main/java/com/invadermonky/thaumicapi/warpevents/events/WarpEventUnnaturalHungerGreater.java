package com.invadermonky.thaumicapi.warpevents.events;

import com.invadermonky.thaumicapi.api.warpevent.WarpEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.lib.potions.PotionUnnaturalHunger;

@WarpEvent
public class WarpEventUnnaturalHungerGreater extends WarpEventUnnaturalHunger {
    @Override
    public @NotNull String getEventName() {
        return "thaumcraft.unnatural_hunger_greater";
    }

    @Override
    public int getMinimumWarp() {
        return 76;
    }

    @Override
    public void performWarpEvent(EntityPlayer player, int warp) {
        if(!player.world.isRemote) {
            PotionEffect effect = new PotionEffect(PotionUnnaturalHunger.instance, 6000, Math.min(3, warp / 15), true, true);
            effect.getCurativeItems().clear();
            effect.addCurativeItem(new ItemStack(Items.ROTTEN_FLESH));
            effect.addCurativeItem(new ItemStack(ItemsTC.brain));
            player.addPotionEffect(effect);
        }
    }
}
