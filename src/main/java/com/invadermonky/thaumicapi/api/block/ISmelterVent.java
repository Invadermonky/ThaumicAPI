package com.invadermonky.thaumicapi.api.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public interface ISmelterVent {
    /**
     * Determines whether this auxiliary vent can boost the passed smelter. This method should include facing
     * cehcks as well as any other checks necessary.
     *
     * @param world The vent world object
     * @param pos The vent block position
     * @param state The vent block state
     * @param smelter The smelter querying the vent
     * @return True if the vent can boost the passed smelter
     */
    boolean canVentSmelter(World world, BlockPos pos, IBlockState state, @Nullable TileEntity smelter);

    float getFluxFilterChance(World world, BlockPos pos, IBlockState state, @Nullable TileEntity smelter);
}
