package com.invadermonky.thaumicapi.warpevents.events;

import com.invadermonky.thaumicapi.api.warpevent.IWarpEvent;
import com.invadermonky.thaumicapi.api.warpevent.WarpEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.Thaumcraft;
import thaumcraft.common.lib.potions.PotionSunScorned;

@WarpEvent
public class WarpEventSunScorned implements IWarpEvent {
    @Override
    public @NotNull ResourceLocation getEventName() {
        return new ResourceLocation(Thaumcraft.MODID, "sun_scorned");
    }

    @Override
    public int getMinimumWarp() {
        return 36;
    }

    @Override
    public int getEventWeight() {
        return 10;
    }

    @Override
    public void playClientEventSound(EntityPlayer player, int warp) {

    }

    @Override
    public @Nullable ITextComponent getEventMessage(EntityPlayer player, int warp) {
        return new TextComponentTranslation("warp.text.5").setStyle(new Style().setColor(TextFormatting.DARK_PURPLE).setItalic(true));
    }

    @Override
    public void performWarpEvent(EntityPlayer player, int warp) {
        if(!player.world.isRemote) {
            PotionEffect effect = new PotionEffect(PotionSunScorned.instance, 5000, Math.min(3, warp / 15), true, true);
            effect.getCurativeItems().clear();
            player.addPotionEffect(effect);
        }
    }
}
