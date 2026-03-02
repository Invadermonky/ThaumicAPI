package com.invadermonky.thaumicapi.warpevents.events;

import com.invadermonky.thaumicapi.api.warpevent.IWarpEvent;
import com.invadermonky.thaumicapi.api.warpevent.WarpEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.Thaumcraft;
import thaumcraft.common.entities.monster.cult.EntityCultistPortalLesser;

@WarpEvent
public class WarpEventCrimsonPortal implements IWarpEvent {
    @Override
    public @NotNull ResourceLocation getEventName() {
        return new ResourceLocation(Thaumcraft.MODID, "crimson_portal");
    }

    @Override
    public int getMinimumWarp() {
        return 84;
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
        return new TextComponentTranslation("warp.text.16").setStyle(new Style().setColor(TextFormatting.DARK_PURPLE).setItalic(true));
    }

    @Override
    public void performWarpEvent(EntityPlayer player, int warp) {
        if(!player.world.isRemote) {
            this.spawnPortal(player);
        }
    }

    protected void spawnPortal(EntityPlayer player) {
        EntityCultistPortalLesser eg = new EntityCultistPortalLesser(player.world);
        int playerX = MathHelper.floor(player.posX);
        int playerY = MathHelper.floor(player.posY);
        int playerZ = MathHelper.floor(player.posZ);

        for(int l = 0; l < 50; ++l) {
            int posX = playerX + MathHelper.getInt(player.world.rand, 7, 24) * MathHelper.getInt(player.world.rand, -1, 1);
            int posY = playerY + MathHelper.getInt(player.world.rand, 7, 24) * MathHelper.getInt(player.world.rand, -1, 1);
            int posZ = playerZ + MathHelper.getInt(player.world.rand, 7, 24) * MathHelper.getInt(player.world.rand, -1, 1);
            eg.setPosition((double)posX + (double)0.5F, (double)posY + (double)1.0F, (double)posZ + (double)0.5F);
            if (player.world.getBlockState(new BlockPos(posX, posY - 1, posZ)).isOpaqueCube() && player.world.checkNoEntityCollision(eg.getEntityBoundingBox()) && player.world.getCollisionBoxes(eg, eg.getEntityBoundingBox()).isEmpty() && !player.world.containsAnyLiquid(eg.getEntityBoundingBox())) {
                eg.onInitialSpawn(player.world.getDifficultyForLocation(new BlockPos(eg)), null);
                player.world.spawnEntity(eg);
                break;
            }
        }
    }
}
