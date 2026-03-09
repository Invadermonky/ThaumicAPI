package com.invadermonky.thaumicapi.mixins.block;

import com.invadermonky.thaumicapi.api.block.ISmelterVent;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import thaumcraft.common.blocks.essentia.BlockSmelterVent;
import thaumcraft.common.lib.utils.BlockStateUtils;

@Mixin(value = BlockSmelterVent.class, remap = false)
public abstract class BlockSmelterVentMixin extends Block implements ISmelterVent {
    public BlockSmelterVentMixin(Material blockMaterialIn, MapColor blockMapColorIn) {
        super(blockMaterialIn, blockMapColorIn);
    }

    @ModifyReturnValue(method = "canPlaceBlockOnSide", at = @At("RETURN"), remap = true)
    public boolean fixAuxPlacementMixin(boolean original, @Local(argsOnly = true, ordinal = 0) World world, @Local(argsOnly = true, ordinal = 0) BlockPos pos, @Local(argsOnly = true, ordinal = 0) EnumFacing side) {
        if(!original) {
            BlockPos placePos = pos.offset(side.getOpposite());
            IBlockState placeOn = world.getBlockState(placePos);
            return super.canPlaceBlockOnSide(world, pos, side) && side.getAxis().isHorizontal() && placeOn.isSideSolid(world, placePos, side);
        }
        return original;
    }

    @Override
    public boolean canVentSmelter(World world, BlockPos pos, IBlockState state, @Nullable TileEntity smelter) {
        EnumFacing facing = BlockStateUtils.getFacing(state);
        return smelter != null && world.getTileEntity(pos.offset(facing)) == smelter;
    }

    @Override
    public float getFluxFilterChance(World world, BlockPos pos, IBlockState state, @Nullable TileEntity smelter) {
        return 0.333f;
    }
}
