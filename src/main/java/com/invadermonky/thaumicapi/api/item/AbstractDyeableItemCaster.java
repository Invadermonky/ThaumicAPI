package com.invadermonky.thaumicapi.api.item;

import net.minecraft.block.BlockCauldron;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import thaumcraft.api.casters.ICaster;
import thaumcraft.common.items.casters.ItemFocus;

import javax.annotation.Nullable;
import java.util.List;

public abstract class AbstractDyeableItemCaster extends AbstractItemCaster implements IDyeableGear {
    @SideOnly(Side.CLIENT)
    public static void initClient(AbstractItemCaster casterItem) {
        ItemColors itemColors = Minecraft.getMinecraft().getItemColors();
        IItemColor itemCasterColourHandler = (stack, tintIndex) -> {
            if (tintIndex == 1 && stack.getItem() instanceof ICaster && ((ICaster) stack.getItem()).getFocus(stack) != null)
                return ((ItemFocus) ((ICaster) stack.getItem()).getFocus(stack)).getFocusColor(((ICaster) stack.getItem()).getFocusStack(stack));
            else if (tintIndex == 2 && stack.getItem() instanceof IDyeableGear) {
                return ((IDyeableGear) stack.getItem()).getDyedColor(stack);
            }

            return -1;
        };
        itemColors.registerItemColorHandler(itemCasterColourHandler, casterItem);
    }

    @Override
    public int getDyedColor(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }

        assert stack.getTagCompound() != null;
        if (stack.getTagCompound().hasKey("color", Constants.NBT.TAG_INT))
            return stack.getTagCompound().getInteger("color");
        else {
            stack.getTagCompound().setInteger("color", getDefaultDyedColorForMeta(stack.getMetadata()));
            return stack.getTagCompound().getInteger("color");
        }
    }

    @Override
    public void setDyedColor(ItemStack stack, int color) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }

        assert stack.getTagCompound() != null;
        stack.getTagCompound().setInteger("color", color);
    }

    @Override
    public int getDefaultDyedColorForMeta(int meta) {
        return 0;
    }

    @Override
    public @NotNull EnumActionResult onItemUseFirst(EntityPlayer player, World world, @NotNull BlockPos pos, @NotNull EnumFacing side, float hitX, float hitY, float hitZ, @NotNull EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        IBlockState state = world.getBlockState(pos);

        // Right-clicking a filled cauldron with the dyed item will wash it out
        if (state.getBlock() == Blocks.CAULDRON && state.getValue(BlockCauldron.LEVEL) > 0 && getDyedColor(stack) != getDefaultDyedColorForMeta(stack.getMetadata())) {
            setDyedColor(stack, getDefaultDyedColorForMeta(stack.getMetadata()));
            world.setBlockState(pos, state.withProperty(BlockCauldron.LEVEL, state.getValue(BlockCauldron.LEVEL) - 1));
            world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 0.5F, 1.0F);
            return EnumActionResult.SUCCESS;
        }

        return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(@NotNull ItemStack stack, @Nullable World world, @NotNull List<String> list, @NotNull ITooltipFlag tooltip) {
        int color = getDyedColor(stack);

        // If it's dyed, show it on the tooltip
        if (color != getDefaultDyedColorForMeta(stack.getMetadata())) {
            if (tooltip.isAdvanced())
                list.add(new TextComponentTranslation("item.color", TextFormatting.GRAY + String.format("#%06X", color)).getFormattedText());
            else {
                list.add(TextFormatting.ITALIC + new TextComponentTranslation("item.dyed").getFormattedText());
            }
        }

        super.addInformation(stack, world, list, tooltip);
    }
}
