package com.invadermonky.thaumicapi.api.block;

import com.invadermonky.thaumicapi.api.tile.AbstractTileEssentiaSmelter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aura.AuraHelper;

import java.util.Random;

public class AbstractBlockEssentiaSmelter<T extends AbstractTileEssentiaSmelter> extends BlockContainer {
    public static final PropertyDirection FACING  = BlockHorizontal.FACING;
    public static final PropertyBool LIT = PropertyBool.create("lit");
    private final Class<T> tileClazz;

    public AbstractBlockEssentiaSmelter(MapColor blockMapColorIn, Class<T> tileClazz) {
        super(Material.IRON, blockMapColorIn);
        this.setupBlock();
        this.tileClazz = tileClazz;
    }

    public AbstractBlockEssentiaSmelter(Class<T> tileClazz) {
        super(Material.IRON);
        this.setupBlock();
        this.tileClazz = tileClazz;
    }

    protected void setupBlock() {
        this.setSoundType(SoundType.METAL);
        this.setHardness(2.0f);
        this.setResistance(20.0f);
        this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(LIT, false);
    }

    @Override
    public boolean onBlockActivated(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull IBlockState state, @NotNull EntityPlayer playerIn, @NotNull EnumHand hand, @NotNull EnumFacing facing, float hitX, float hitY, float hitZ) {
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull IBlockState getStateForPlacement(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull EnumFacing facing, float hitX, float hitY, float hitZ, int meta, @NotNull EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(LIT, false);
    }

    @Override
    public void breakBlock(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull IBlockState state) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if(!worldIn.isRemote && tile instanceof AbstractTileEssentiaSmelter) {
            int essentia = ((AbstractTileEssentiaSmelter) tile).getCurrentEssentiaTotal();
            AuraHelper.polluteAura(worldIn, pos, (float) essentia, true);
            IItemHandler handler = ((AbstractTileEssentiaSmelter) tile).handler;
            for(int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.extractItem(i, handler.getSlotLimit(i), false);
                Block.spawnAsEntity(worldIn, pos, stack);
            }
        }
        super.breakBlock(worldIn, pos, state);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean hasComparatorInputOverride(@NotNull IBlockState state) {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getComparatorInputOverride(@NotNull IBlockState blockState, @NotNull World worldIn, @NotNull BlockPos pos) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if(tile instanceof AbstractTileEssentiaSmelter) {
            int capacity = ((AbstractTileEssentiaSmelter) tile).getMaxEssentiaCapacity();
            int stored = ((AbstractTileEssentiaSmelter) tile).getCurrentEssentiaTotal();
            return (int) (((double) stored / (double) capacity) * 16.0);
        }
        return 0;
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull EnumBlockRenderType getRenderType(@NotNull IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta & -5)).withProperty(LIT, (meta & 4) != 0);
    }

    @Override
    public int getMetaFromState(@NotNull IBlockState state) {
        return (state.getValue(LIT) ? 4 : 0) | state.getValue(FACING).getHorizontalIndex();
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
    }

    @Override
    protected @NotNull BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, LIT);
    }

    @Override
    public boolean hasTileEntity(@NotNull IBlockState state) {
        return this.tileClazz != null;
    }

    @Override
    public @Nullable TileEntity createNewTileEntity(@NotNull World worldIn, int meta) {
        if(this.tileClazz != null) {
            try {
                return tileClazz.newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Failed to initialize essentia smelter tile instance " + this.tileClazz);
            }
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(@NotNull IBlockState stateIn, @NotNull World worldIn, @NotNull BlockPos pos, @NotNull Random rand) {
        if(stateIn.getValue(LIT)) {
            EnumFacing front = stateIn.getValue(FACING);
            float x = (float) pos.getX() + 0.5f;
            float y = (float) pos.getY() + 0.2f + rand.nextFloat() * 5.0f / 16.0f;
            float z = (float) pos.getZ() + 0.5f;
            float smokeOffset = 0.52f;
            float flameOffset = rand.nextFloat() * 0.5f - 0.25f;

            switch (front) {
                case NORTH:
                    x += flameOffset;
                    z -= smokeOffset;
                    break;
                case SOUTH:
                    x += smokeOffset;
                    z += flameOffset;
                    break;
                case WEST:
                    x -= smokeOffset;
                    z += flameOffset;
                    break;
                case EAST:
                    x += flameOffset;
                    z += smokeOffset;
                    break;
            }

            worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0, 0, 0);
            worldIn.spawnParticle(EnumParticleTypes.FLAME, x, y, z, 0, 0, 0);
        }
    }
}
