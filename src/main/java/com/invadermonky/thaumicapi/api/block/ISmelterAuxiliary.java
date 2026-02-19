package com.invadermonky.thaumicapi.api.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public interface ISmelterAuxiliary {
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
    boolean canBoostSmelter(World world, BlockPos pos, IBlockState state, @Nullable TileEntity smelter);

    /**
     * Returns the number of bonus operations granted by this auxiliary vent.
     *
     * @param world The vent world object
     * @param pos The vent block position
     * @param state The vent block state
     * @param smelter The smelter querying the vent
     * @return The number of bonus operations granted by this auxiliary vent.
     */
    int getBonusOperations(World world, BlockPos pos, IBlockState state, @Nullable TileEntity smelter);
}
