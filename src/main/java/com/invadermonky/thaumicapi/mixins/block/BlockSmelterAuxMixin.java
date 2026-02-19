package com.invadermonky.thaumicapi.mixins.block;

import com.invadermonky.thaumicapi.api.block.ISmelterAuxiliary;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import thaumcraft.common.blocks.essentia.BlockSmelterAux;
import thaumcraft.common.lib.utils.BlockStateUtils;

@Mixin(value = BlockSmelterAux.class, remap = false)
public class BlockSmelterAuxMixin implements ISmelterAuxiliary {
    @Override
    public boolean canBoostSmelter(World world, BlockPos pos, IBlockState state, @Nullable TileEntity smelter) {
        EnumFacing facing = BlockStateUtils.getFacing(state);
        return smelter != null && world.getTileEntity(pos.offset(facing)) == smelter;
    }

    @Override
    public int getBonusOperations(World world, BlockPos pos, IBlockState state, @Nullable TileEntity smelter) {
        return 1;
    }
}
