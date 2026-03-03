package com.invadermonky.thaumicapi.utils.helpers;

import com.invadermonky.thaumicapi.api.block.ISmelterAuxiliary;
import com.invadermonky.thaumicapi.api.block.ISmelterVent;
import com.invadermonky.thaumicapi.mixins.tile.TileAlembicAccessor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.tiles.essentia.TileAlembic;

public class SmelterHelper {
    /**
     * Processes the connected alembics by calling {@link TileAlembic#processAlembics(World, BlockPos, Aspect)} via accessor.
     *
     * @param world The world object.
     * @param alembicPos The position of the first alembic.
     * @param aspect The aspect to transfer.
     * @return If the transfer successfully transferred one aspect of that type to the connectted alembics.
     */
    public static boolean processAlembics(World world, BlockPos alembicPos, Aspect aspect) {
        return TileAlembicAccessor.invokeProcessAlembics(world, alembicPos, aspect);
    }

    /**
     * Returns
     * @param world
     * @param pos The position of the
     * @param state
     * @return
     */
    @Nullable
    public static ISmelterAuxiliary getSmelterAuxiliary(World world, BlockPos pos, IBlockState state) {
        if(state.getBlock() instanceof ISmelterAuxiliary) {
            return (ISmelterAuxiliary) state.getBlock();
        } else {
            TileEntity tile = world.getTileEntity(pos);
            if(tile instanceof ISmelterAuxiliary) {
                return (ISmelterAuxiliary) tile;
            }
        }
        return null;
    }

    @Nullable
    public static ISmelterVent getSmelterVent(World world, BlockPos pos, IBlockState state) {
        if(state.getBlock() instanceof ISmelterVent) {
            return (ISmelterVent) state.getBlock();
        } else {
            TileEntity tile = world.getTileEntity(pos);
            if(tile instanceof ISmelterVent) {
                return (ISmelterVent) tile;
            }
        }
        return null;
    }
}
