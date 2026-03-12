package com.invadermonky.thaumicapi.api.item;

import com.invadermonky.thaumicapi.ThaumicAPIMod;
import com.invadermonky.thaumicapi.network.NetworkHandlerTAPI;
import com.invadermonky.thaumicapi.network.messages.MessageAuraToClient;
import com.invadermonky.thaumicapi.utils.helpers.PlayerHelper;
import com.invadermonky.thaumicapi.utils.libs.ModIds;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.casters.*;
import thaumcraft.api.items.IArchitect;
import thaumcraft.common.items.casters.CasterManager;
import thaumcraft.common.items.casters.ItemFocus;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.world.aura.AuraChunk;
import thaumcraft.common.world.aura.AuraHandler;
import thecodex6824.thaumicaugmentation.ThaumicAugmentation;
import thecodex6824.thaumicaugmentation.api.augment.AugmentableItem;
import thecodex6824.thaumicaugmentation.api.augment.CapabilityAugmentableItem;
import thecodex6824.thaumicaugmentation.api.augment.IAugmentableItem;
import thecodex6824.thaumicaugmentation.api.event.CastEvent;
import thecodex6824.thaumicaugmentation.api.util.FocusUtils;
import thecodex6824.thaumicaugmentation.api.util.FocusWrapper;
import thecodex6824.thaumicaugmentation.common.capability.provider.SimpleCapabilityProvider;
import thecodex6824.thaumicaugmentation.common.util.ItemHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public abstract class AbstractItemCaster extends Item implements IArchitect, ICaster {
    public static final String TAG_FOCUS = "storedFocus";
    public static final String TAG_BLOCK = "pickedBlock";
    protected static final DecimalFormat DECIMAL_FORMATTER = new DecimalFormat("#######.#");
    protected static final Method CASTER_IS_ON_COOLDOWN;
    protected int augmentSlots;

    /**
     * A basic builder for the gauntlet item. This constructor automatically adds the focus property override.
     * <p>
     * The generic item model json:
     * <blockquote><pre>
     * {@code
     * {
     *   "parent": "item/generated",
     *   "overrides": [
     *     {
     *       "predicate": {"thaumicapi:focus": 0},
     *       "model": "examplemod:item/caster_new_model"
     *     },
     *     {
     *       "predicate": {"thaumicapi:focus": 1},
     *       "model": "examplemod:item/caster_new_focus_model"
     *     }
     *   ]
     * }
     * }
     * </pre></blockquote>
     *
     * <p>
     * The no focus item model json:
     * <blockquote><pre>
     * {@code
     * {
     *   "parent": "thaumcraft:item/caster_gauntlet",
     *   "textures": {
     *     "skin": "examplemod:models/caster_new_inactive"
     *   }
     * }
     * }
     * </pre></blockquote>
     *
     * <p>
     * The gauntlet model with a focus attached:
     * <blockquote><pre>
     * {@code
     * {
     *   "parent": "thaumcraft:item/caster_gauntlet_focus",
     *   "textures": {
     *     "skin": "examplemod:models/caster_new_active"
     *   }
     * }
     * }
     * </pre></blockquote>
     *
     */
    public AbstractItemCaster(int augmentSlots) {
        this.setMaxStackSize(1);
        this.addPropertyOverride(new ResourceLocation(ThaumicAPIMod.MOD_ID, "focus"), (stack, worldIn, entityIn) -> this.hasFocusStack(stack) ? 1.0f : 0);
        this.augmentSlots = augmentSlots;
    }

    /**
     * A default implementation for people who want full control over the item.
     */
    public AbstractItemCaster() {}

    /**
     * A helper method used to register gauntlet focus color tinting. Simply call this method
     * when you register the item model.
     */
    @SideOnly(Side.CLIENT)
    public static void initClient(AbstractItemCaster casterItem) {
        ItemColors itemColors = Minecraft.getMinecraft().getItemColors();
        IItemColor itemCasterColourHandler = (stack, tintIndex) -> {
            AbstractItemCaster item = (AbstractItemCaster)stack.getItem();
            ItemFocus focus = item.getFocus(stack);
            return (tintIndex > 0 && focus != null) ? focus.getFocusColor(item.getFocusStack(stack)) : -1;
        };
        itemColors.registerItemColorHandler(itemCasterColourHandler, casterItem);
    }

    /**
     * The radius, in chunks, this gaunlet can draw vis from. A value of 2 will draw vis from a 3x3
     * chunk area centered o the player.
     *
     * @param player The player
     * @param stack The caster gauntlet stack
     * @return The chunk radius the gauntlet can draw vis from.
     */
    public abstract int getChunkDrainRadius(EntityPlayer player, ItemStack stack);

    /**
     * Gets the consumption modifier for the caster gauntlet.
     * <p>
     * Common implementation is as follows:
     * <blockquote><pre>
     * {@code
     *     @Override
     *     public float getConsumptionModifier(ItemStack stack, EntityPlayer player, boolean crafting) {
     *         float base = 1.0f;
     *         if(player != null) {
     *             base -= CasterManager.getTotalVisDiscount(player);
     *         }
     *         return Math.max(0.1f, base);
     *     }
     * }
     * </pre></blockquote>
     *
     * @param stack The gauntlet stack
     * @param player The player
     * @param crafting If this operation is a crafting operation
     * @return The gauntlet vis cost modifier.
     */
    @Override
    public abstract float getConsumptionModifier(ItemStack stack, EntityPlayer player, boolean crafting);


    @Override
    public @Nullable ICapabilityProvider initCapabilities(@NotNull ItemStack stack, @Nullable NBTTagCompound nbt) {
        if(this.isAugmentable() && ModIds.thaumic_augmentation.isLoaded) {
            SimpleCapabilityProvider<IAugmentableItem> provider = new SimpleCapabilityProvider<>(new AugmentableItem(this.augmentSlots), CapabilityAugmentableItem.AUGMENTABLE_ITEM);
            if (nbt != null && nbt.hasKey("Parent", 10)) {
                provider.deserializeNBT(nbt.getCompoundTag("Parent"));
            }
            return provider;
        }
        return super.initCapabilities(stack, nbt);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void getSubItems(@NotNull CreativeTabs tab, @NotNull NonNullList<ItemStack> items) {
        if(this.isAugmentable() && ModIds.thaumic_augmentation.isLoaded) {
            if (tab == this.getCreativeTab() || tab == CreativeTabs.SEARCH) {
                ItemStack stack = new ItemStack(this);
                if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT && !ThaumicAugmentation.proxy.isSingleplayer()) {
                    stack.setTagInfo("cap", ((AugmentableItem) stack.getCapability(CapabilityAugmentableItem.AUGMENTABLE_ITEM, null)).serializeNBT());
                }
                items.add(stack);
            }
        } else {
            super.getSubItems(tab, items);
        }
    }

    public boolean isAugmentable() {
        return this.augmentSlots > 0;
    }

    @Override
    public void onUpdate(@NotNull ItemStack stack, World worldIn, @NotNull Entity entityIn, int itemSlot, boolean isSelected) {
        if(!worldIn.isRemote && entityIn.ticksExisted % 10 == 0 && entityIn instanceof EntityPlayerMP) {
            for(ItemStack heldStack : entityIn.getHeldEquipment()) {
                if(!heldStack.isEmpty() && heldStack.getItem() instanceof ICaster) {
                    AuraChunk chunk = AuraHandler.getAuraChunk(worldIn.provider.getDimension(), entityIn.chunkCoordX, entityIn.chunkCoordZ);
                    if(chunk != null) {
                        NetworkHandlerTAPI.INSTANCE.sendTo(new MessageAuraToClient(chunk), (EntityPlayerMP) entityIn);
                    }
                }
            }
        }
    }

    @Override
    public @NotNull ActionResult<ItemStack> onItemRightClick(@NotNull World worldIn, @NotNull EntityPlayer player, @NotNull EnumHand hand) {
        ItemStack heldStack = player.getHeldItem(hand);
        if(this.hasFocusStack(heldStack) && !this.isCasterOnCooldown(player)) {
            ItemStack focusStack = this.getFocusStack(heldStack);
            FocusPackage focusPackage = this.getFocusPackage(focusStack);
            if(!focusStack.isEmpty() && focusPackage != null) {
                //Resetting Focus block picker
                if (player.isSneaking()) {
                    for (IFocusElement element : focusPackage.nodes) {
                        if (element instanceof IFocusBlockPicker) {
                            return new ActionResult<>(EnumActionResult.PASS, heldStack);
                        }
                    }
                }

                float visCost = ((ItemFocus) focusStack.getItem()).getVisCost(focusStack) * this.getConsumptionModifier(heldStack, player, false);
                int activationTime = ((ItemFocus) focusStack.getItem()).getActivationTime(focusStack);

                boolean consumeVis = this.consumeVis(heldStack, player, visCost, false, true);
                if (consumeVis) {
                    boolean isSuccess;
                    if (ModIds.thaumic_augmentation.isLoaded) {
                        isSuccess = this.castFocusSpellTA(worldIn, player, heldStack, focusPackage, activationTime, visCost);
                    } else {
                        isSuccess = this.castFocusSpell(worldIn, player, heldStack, focusPackage, activationTime, visCost);
                    }

                    if (isSuccess) {
                        if (!worldIn.isRemote) {
                            player.swingArm(hand);
                        }
                        return new ActionResult<>(EnumActionResult.SUCCESS, heldStack);
                    }
                }
                return new ActionResult<>(EnumActionResult.FAIL, heldStack);
            }
        }
        return super.onItemRightClick(worldIn, player, hand);
    }

    protected boolean castFocusSpell(World world, EntityPlayer player, ItemStack casterStack, FocusPackage focusPackage, int activationTime, float visCost) {
        //TODO: See about moving the cast event into ThaumicAPI so it can be called here.
        if(!world.isRemote) {
            this.consumeVis(casterStack, player, visCost, false, false);
            FocusEngine.castFocusPackage(player, focusPackage);
        }
        return true;
    }

    @Optional.Method(modid = ModIds.ConstIds.thaumic_augmentation)
    protected boolean castFocusSpellTA(World world, EntityPlayer player, ItemStack casterStack, FocusPackage focusPackage, int activationTime, float visCost) {
        CastEvent.Pre preEvent = new CastEvent.Pre(player, casterStack, new FocusWrapper(focusPackage, activationTime, visCost));
        MinecraftForge.EVENT_BUS.post(preEvent);
        if(!preEvent.isCanceled()) {
            if(world.isRemote) {
                CasterManager.setCooldown(player, preEvent.getFocus().getCooldown());
            } else {
                this.consumeVis(casterStack, player, visCost, false, false);
                FocusUtils.replaceAndFixFoci(focusPackage, player);
                FocusEngine.castFocusPackage(player, focusPackage);
                //This was the problem right here. For some reason copying the focus package results
                // in the projectiles hitting the player.
                //FocusEngine.castFocusPackage(player, focusPackage, true);
                CasterManager.setCooldown(player, preEvent.getFocus().getCooldown());
                MinecraftForge.EVENT_BUS.post(new CastEvent.Post(player, casterStack, preEvent.getFocus()));
            }
            return true;
        }
        return false;
    }

    protected boolean isCasterOnCooldown(EntityLivingBase entityLiving) {
        try {
            return (Boolean)CASTER_IS_ON_COOLDOWN.invoke(null, entityLiving);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            FMLCommonHandler.instance().raiseException(ex, "Failed to invoke Thaumcraft's CasterManager#isOnCooldown", true);
            return true;
        }
    }

    @Override
    public @NotNull EnumActionResult onItemUseFirst(@NotNull EntityPlayer player, @NotNull World world, @NotNull BlockPos pos, @NotNull EnumFacing side, float hitX, float hitY, float hitZ, @NotNull EnumHand hand) {
        ItemStack heldStack = player.getHeldItem(hand);
        IBlockState state = world.getBlockState(pos);
        if(state.getBlock() instanceof IInteractWithCaster && ((IInteractWithCaster) state.getBlock()).onCasterRightClick(world, heldStack, player, pos, side, hand)) {
            return EnumActionResult.SUCCESS;
        } else {
            TileEntity tile = world.getTileEntity(pos);
            if(tile instanceof IInteractWithCaster && ((IInteractWithCaster) tile).onCasterRightClick(world, heldStack, player, pos, side, hand)) {
                return EnumActionResult.SUCCESS;
            } else if(CasterTriggerRegistry.hasTrigger(state)) {
                return CasterTriggerRegistry.performTrigger(world, heldStack, player, pos, side, state) ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
            } else if(this.hasFocusStack(heldStack)) {
                ItemStack focusStack = this.getFocusStack(heldStack);
                FocusPackage focusPackage = this.getFocusPackage(focusStack);
                if(focusPackage != null) {
                    for(IFocusElement element : focusPackage.nodes) {
                        if(element instanceof IFocusBlockPicker && player.isSneaking() && tile != null) {
                            if(!world.isRemote) {
                                RayTraceResult trace = PlayerHelper.rayTrace(player, 0);
                                ItemStack toStore = state.getBlock().getPickBlock(state, trace, world, pos, player);
                                if(toStore.isEmpty() && state.getBlock().canSilkHarvest(world, pos, state, player)) {
                                    toStore = BlockUtils.getSilkTouchDrop(state).copy();
                                }
                                if(!toStore.isEmpty()) {
                                    focusStack.setTagInfo(TAG_BLOCK, toStore.writeToNBT(new NBTTagCompound()));
                                    return EnumActionResult.SUCCESS;
                                }
                            }
                            player.swingArm(hand);
                            return EnumActionResult.PASS;
                        }
                    }
                }
            }
        }
        return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
    }

    @Override
    public boolean shouldCauseReequipAnimation(@NotNull ItemStack oldStack, @NotNull ItemStack newStack, boolean slotChanged) {
        if(!slotChanged && oldStack.getItem() instanceof ICaster && newStack.getItem() instanceof ICaster && this.hasFocusStack(oldStack) && this.hasFocusStack(newStack)) {
            ItemStack oldFocus = this.getFocusStack(oldStack);
            ItemStack newFocus = this.getFocusStack(newStack);
            if ((oldFocus.isEmpty() && !newFocus.isEmpty()) || (!oldFocus.isEmpty() && newFocus.isEmpty())) {
                return true;
            } else {
                return ((ItemFocus) oldFocus.getItem()).getSortingHelper(oldFocus).hashCode() != ((ItemFocus) newFocus.getItem()).getSortingHelper(newFocus).hashCode();
            }
        } else {
            return slotChanged || oldStack.getItem() != newStack.getItem() || oldStack.getMetadata() != newStack.getMetadata();
        }
    }

    @Override
    public @NotNull EnumAction getItemUseAction(@NotNull ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    public int getMaxItemUseDuration(@NotNull ItemStack stack) {
        return 72000;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<String> tooltip, @NotNull ITooltipFlag flagIn) {
        if(this.hasFocusStack(stack)) {
            ItemStack focusStack = this.getFocusStack(stack);
            ItemFocus focus = (ItemFocus) focusStack.getItem();
            float visCost = focus.getVisCost(focusStack);
            if(visCost > 0) {
                tooltip.add(TextFormatting.AQUA + I18n.format("tc.vis.cost") + " " +
                        TextFormatting.RESET + DECIMAL_FORMATTER.format(visCost) + " " +
                        I18n.format("item.Focus.cost1"));
            }
            tooltip.add(TextFormatting.GREEN + focusStack.getDisplayName());
            focus.addFocusInformation(focusStack, worldIn, tooltip, flagIn);
        }
    }

    @Override
    public @NotNull IRarity getForgeRarity(@NotNull ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public @Nullable NBTTagCompound getNBTShareTag(@NotNull ItemStack stack) {
        if(this.isAugmentable() && ModIds.thaumic_augmentation.isLoaded) {
            NBTTagCompound tag = new NBTTagCompound();
            if(stack.hasTagCompound()) {
                NBTTagCompound itemTag = stack.getTagCompound().copy();
                if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT && !ThaumicAugmentation.proxy.isSingleplayer()) {
                    itemTag.removeTag("cap");
                }
                tag.setTag("item", itemTag);
            }
            NBTTagCompound cap = ItemHelper.tryMakeCapabilityTag(stack, CapabilityAugmentableItem.AUGMENTABLE_ITEM);
            if(cap != null) {
                tag.setTag("cap", cap);
            }
            return tag;
        }
        return super.getNBTShareTag(stack);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void readNBTShareTag(@NotNull ItemStack stack, @Nullable NBTTagCompound tag) {
        if(this.isAugmentable() && ModIds.thaumic_augmentation.isLoaded) {
            if(tag != null) {
                if(tag.hasKey("cap", 10)) {
                    ((AugmentableItem) stack.getCapability(CapabilityAugmentableItem.AUGMENTABLE_ITEM, null)).deserializeNBT(tag.getCompoundTag("cap"));
                }
                if(tag.hasKey("item", 10)) {
                    stack.setTagCompound(tag.getCompoundTag("item"));
                } else if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
                    tag.removeTag("cap");
                    if(!tag.isEmpty()) {
                        stack.setTagCompound(tag);
                    }
                }

                if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT && !ThaumicAugmentation.proxy.isSingleplayer()) {
                    stack.setTagInfo("cap", tag.getCompoundTag("cap"));
                }
            }
        } else {
            super.readNBTShareTag(stack, tag);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean isFull3D() {
        return true;
    }

    //##########################################################
    // ICaster

    @Override
    public boolean consumeVis(ItemStack casterStack, EntityPlayer player, float visDrain, boolean crafting, boolean simulate) {
        //Do not call getConsumptionModifier() here. It is already called and calculated prior to this method being fired.
        int drainRange = this.getChunkDrainRadius(player, casterStack);
        float aura = this.getAuraPool(player.world, player.getPosition(), drainRange);
        if(aura >= visDrain) {
            if(!simulate) {
                this.drainAuraPool(player.world, player.getPosition(), drainRange, visDrain);
            }
            return true;
        }
        return false;
    }

    protected float getAuraPool(World world, BlockPos pos, int chunkRange) {
        float total = 0;
        for(int x = -chunkRange; x <= chunkRange; x++) {
            for(int z = -chunkRange; z <= chunkRange; z++) {
                total += AuraHandler.getTotalAura(world, pos.add(x << 4, 0, z << 4));
            }
        }
        return total;
    }

    protected void drainAuraPool(World world, BlockPos pos, int chunkRange, float visDrain) {
        int chunks = (int) Math.pow((chunkRange * 2.0) + 1.0, 2.0);
        float chunkDrain = visDrain / (float) chunks;
        Map<Float, BlockPos> drainPositions = new TreeMap<>();
        for(int x = -chunkRange; x <= chunkRange; x++) {
            for(int z = -chunkRange; z <= chunkRange; z++) {
                BlockPos drainPos = pos.add(x << 4, 0, z << 4);
                float vis = AuraHandler.getVis(world, drainPos);
                drainPositions.put(vis, drainPos);
            }
        }
        for (Map.Entry<Float, BlockPos> entry : drainPositions.entrySet()) {
            Float chunkVis = entry.getKey();
            BlockPos chunkPos = entry.getValue();
            if (chunkDrain > chunkVis) {
                AuraHandler.drainVis(world, chunkPos, chunkVis, false);
                chunkDrain = (visDrain - chunkVis) / --chunks;
            } else {
                AuraHandler.drainVis(world, chunkPos, chunkDrain, false);
            }
        }
    }

    @Nullable
    @Override
    public ItemFocus getFocus(ItemStack casterStack) {
        ItemStack focusStack = this.getFocusStack(casterStack);
        return !focusStack.isEmpty() && focusStack.getItem() instanceof ItemFocus ? (ItemFocus) focusStack.getItem() : null;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public ItemStack getFocusStack(ItemStack casterStack) {
        if(casterStack.hasTagCompound() && casterStack.getTagCompound().hasKey(TAG_FOCUS)) {
            return new ItemStack(casterStack.getTagCompound().getCompoundTag(TAG_FOCUS));
        }
        return ItemStack.EMPTY;
    }

    /**
     * Returns the FocusPackage of the ItemFocus. The ItemStack passed to this method can either be
     * the caster ItemStack or the focus ItemStack.
     *
     * @param stack the caster or focus ItemStack instance
     * @return the focus package associated with the passed ItemStack
     */
    @Nullable
    public FocusPackage getFocusPackage(ItemStack stack) {
        if(!stack.isEmpty()) {
            if (!(stack.getItem() instanceof ItemFocus)) {
                stack = this.getFocusStack(stack);
            }
            return ItemFocus.getPackage(stack);
        }
        return null;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void setFocus(ItemStack caster, ItemStack focus) {
        if(focus != null && !focus.isEmpty()) {
            caster.setTagInfo(TAG_FOCUS, focus.writeToNBT(new NBTTagCompound()));
        } else if(caster.hasTagCompound()) {
            caster.getTagCompound().removeTag(TAG_FOCUS);
        }
    }

    public boolean hasFocusStack(ItemStack stack) {
        return stack.getItem() instanceof ICaster && this.getFocus(stack) instanceof ItemFocus;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public ItemStack getPickedBlock(ItemStack stack) {
        ItemStack ret = ItemStack.EMPTY;
        if(stack != null && !stack.isEmpty()) {
            ItemStack focusStack = this.getFocusStack(stack);
            FocusPackage focusPackage = this.getFocusPackage(focusStack);
            if (!focusStack.isEmpty() && focusPackage != null) {
                for (IFocusElement element : focusPackage.nodes) {
                    if (element instanceof IFocusBlockPicker) {
                        try {
                            return new ItemStack(focusStack.getTagCompound().getCompoundTag(TAG_BLOCK));
                        } catch (Exception e) {
                            return ItemStack.EMPTY;
                        }
                    }
                }
            }
        }
        return ret;
    }

    //##########################################################
    // IArchitect

    @Override
    public RayTraceResult getArchitectMOP(ItemStack stack, World world, EntityLivingBase user) {
        if(this.hasFocusStack(stack)) {
            FocusPackage focusPackage = this.getFocusPackage(stack);
            if(focusPackage != null && FocusEngine.doesPackageContainElement(focusPackage, "thaumcraft.PLAN")) {
                return ((IArchitect)FocusEngine.getElement("thaumcraft.PLAN")).getArchitectMOP(stack, world, user);
            }
        }
        return null;
    }

    @Override
    public boolean useBlockHighlight(ItemStack itemStack) {
        return false;
    }

    @Override
    public ArrayList<BlockPos> getArchitectBlocks(ItemStack stack, World world, BlockPos pos, EnumFacing facing, EntityPlayer player) {
        ItemStack focusStack = this.getFocusStack(stack);
        FocusPackage focusPackage = this.getFocusPackage(focusStack);
        if (!focusStack.isEmpty() && focusPackage != null) {
            for (IFocusElement element : focusPackage.nodes) {
                if (element instanceof IArchitect) {
                    return ((IArchitect) element).getArchitectBlocks(stack, world, pos, facing, player);
                }
            }
        }
        return null;
    }

    @Override
    public boolean showAxis(ItemStack stack, World world, EntityPlayer player, EnumFacing facing, IArchitect.EnumAxis axis) {
        ItemStack focusStack = this.getFocusStack(stack);
        FocusPackage focusPackage = this.getFocusPackage(focusStack);
        if (!focusStack.isEmpty() && focusPackage != null) {
            for (IFocusElement element : focusPackage.nodes) {
                if (element instanceof IArchitect) {
                    return ((IArchitect) element).showAxis(stack, world, player, facing, axis);
                }
            }
        }
        return false;
    }

    static {
        Method cooldown = null;

        try {
            cooldown = CasterManager.class.getDeclaredMethod("isOnCooldown", EntityLivingBase.class);
            cooldown.setAccessible(true);
        } catch (Exception ex) {
            ThaumicAPIMod.LOGGER.error("Failed to access Thaumcraft's CasterManager#isOnCooldown");
            throw new RuntimeException(ex);
        }

        CASTER_IS_ON_COOLDOWN = cooldown;
    }
}
