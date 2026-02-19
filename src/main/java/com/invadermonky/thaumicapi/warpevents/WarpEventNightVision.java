package com.invadermonky.thaumicapi.warpevents;

import com.invadermonky.thaumicapi.api.warpevent.IWarpEvent;
import com.invadermonky.thaumicapi.api.warpevent.WarpEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@WarpEvent
public class WarpEventNightVision implements IWarpEvent {
    @Override
    public @NotNull String getEventName() {
        return "thaumcraft.night_vision";
    }

    @Override
    public int getMinimumWarp() {
        return 48;
    }

    @Override
    public int getMaximumWarp() {
        return 52;
    }

    @Override
    public void playClientEventSound(EntityPlayer player, int warp) {

    }

    @Override
    public @Nullable ITextComponent getEventMessage(EntityPlayer player, int warp) {
        return new TextComponentTranslation("warp.text.10").setStyle(new Style().setColor(TextFormatting.DARK_PURPLE).setItalic(true));
    }

    @Override
    public void performWarpEvent(EntityPlayer player, int warp) {
        PotionEffect effect = new PotionEffect(MobEffects.NIGHT_VISION, Math.min(40 * warp, 6000), 0, true, true);
        player.addPotionEffect(effect);
    }
}
