package com.invadermonky.thaumicapi.warpevents.events;

import com.invadermonky.thaumicapi.api.warpevent.IWarpEvent;
import com.invadermonky.thaumicapi.api.warpevent.WarpEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.common.entities.monster.EntityMindSpider;

@WarpEvent
public class WarpEventSpidersFake implements IWarpEvent {
    @Override
    public @NotNull String getEventName() {
        return "thaumcraft.spiders_fake";
    }

    @Override
    public int getMinimumWarp() {
        return 56;
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
        return new TextComponentTranslation("warp.text.7").setStyle(new Style().setColor(TextFormatting.DARK_PURPLE).setItalic(true));
    }

    @Override
    public void performWarpEvent(EntityPlayer player, int warp) {
        if(!player.world.isRemote) {
            int count = Math.min(50, warp);
            this.spawnSpiders(player, count, false);
        }
    }

    protected void spawnSpiders(EntityPlayer player, int count, boolean real) {
        for(int i = 0; i < count; ++i) {
            EntityMindSpider spider = new EntityMindSpider(player.world);
            int playerX = MathHelper.floor(player.posX);
            int playerY = MathHelper.floor(player.posY);
            int playerZ = MathHelper.floor(player.posZ);
            boolean success = false;

            for (int l = 0; l < 50; ++l) {
                int posX = playerX + MathHelper.getInt(player.world.rand, 7, 24) * MathHelper.getInt(player.world.rand, -1, 1);
                int posY = playerY + MathHelper.getInt(player.world.rand, 7, 24) * MathHelper.getInt(player.world.rand, -1, 1);
                int posZ = playerZ + MathHelper.getInt(player.world.rand, 7, 24) * MathHelper.getInt(player.world.rand, -1, 1);
                if (player.world.getBlockState(new BlockPos(posX, posY - 1, posZ)).isFullCube()) {
                    spider.setPosition(posX, posY, posZ);
                    if (player.world.checkNoEntityCollision(spider.getEntityBoundingBox()) && player.world.getCollisionBoxes(spider, spider.getEntityBoundingBox()).isEmpty() && !player.world.containsAnyLiquid(spider.getEntityBoundingBox())) {
                        success = true;
                        break;
                    }
                }
            }
            if (success) {
                spider.setAttackTarget(player);
                if (!real) {
                    spider.setViewer(player.getName());
                    spider.setHarmless(true);
                }
                player.world.spawnEntity(spider);
            }
        }
    }
}
