package com.invadermonky.thaumicapi.warpevents.events;

import com.invadermonky.thaumicapi.api.warpevent.IWarpEvent;
import com.invadermonky.thaumicapi.api.warpevent.WarpEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.common.entities.monster.EntityEldritchGuardian;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketMiscEvent;

@WarpEvent
public class WarpEventSpawnMist implements IWarpEvent {
    @Override
    public @NotNull String getEventName() {
        return "thaumcraft.spawn_mist";
    }

    @Override
    public int getMinimumWarp() {
        return 28;
    }

    @Override
    public int getMaximumWarp() {
        return 32;
    }

    @Override
    public void playClientEventSound(EntityPlayer player, int warp) {

    }

    @Override
    public @Nullable ITextComponent getEventMessage(EntityPlayer player, int warp) {
        return new TextComponentTranslation("warp.text.6").setStyle(new Style().setColor(TextFormatting.DARK_PURPLE).setItalic(true));
    }

    @Override
    public void performWarpEvent(EntityPlayer player, int warp) {
        if(!player.world.isRemote) {
            PacketHandler.INSTANCE.sendTo(new PacketMiscEvent((byte) 1), (EntityPlayerMP) player);
            this.spawnGuardians(player, 1);
        }
    }

    protected void spawnGuardians(EntityPlayer player, int count) {
        for(int i = 0; i < count; i++) {
            EntityEldritchGuardian guardian = new EntityEldritchGuardian(player.world);
            int playerX = MathHelper.floor(player.posX);
            int playerY = MathHelper.floor(player.posY);
            int playerZ = MathHelper.floor(player.posZ);

            for(int l = 0; l < 50; ++l) {
                int posX = playerX + MathHelper.getInt(player.world.rand, 7, 24) * MathHelper.getInt(player.world.rand, -1, 1);
                int posY = playerY + MathHelper.getInt(player.world.rand, 7, 24) * MathHelper.getInt(player.world.rand, -1, 1);
                int posZ = playerZ + MathHelper.getInt(player.world.rand, 7, 24) * MathHelper.getInt(player.world.rand, -1, 1);
                if (player.world.getBlockState(new BlockPos(posX, posY - 1, posZ)).isFullCube()) {
                    guardian.setPosition(posX, posY, posZ);
                    if (player.world.checkNoEntityCollision(guardian.getEntityBoundingBox()) && player.world.getCollisionBoxes(guardian, guardian.getEntityBoundingBox()).isEmpty() && !player.world.containsAnyLiquid(guardian.getEntityBoundingBox())) {
                        guardian.setAttackTarget(player);
                        player.world.spawnEntity(guardian);
                        break;
                    }
                }
            }
        }
    }
}
