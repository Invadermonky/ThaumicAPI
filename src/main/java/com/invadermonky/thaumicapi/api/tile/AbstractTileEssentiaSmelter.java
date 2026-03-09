package com.invadermonky.thaumicapi.api.tile;

import com.invadermonky.thaumicapi.api.block.ISmelterAuxiliary;
import com.invadermonky.thaumicapi.api.block.ISmelterVent;
import com.invadermonky.thaumicapi.utils.helpers.SmelterHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.items.consumables.ItemAlumentum;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.tiles.devices.TileBellows;
import thaumcraft.common.tiles.essentia.TileSmelter;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractTileEssentiaSmelter extends TileEntity implements ITickable {
    public ItemStackHandler handler = new ItemStackHandler(2) {
        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return isItemValid(slot, stack) ? super.insertItem(slot, stack, simulate) : stack;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if(slot == 0 && !simulate) {
                ItemStack slotStack = getStackInSlot(slot);
                if(!slotStack.isEmpty() && amount >= slotStack.getCount()) {
                    resetProgress();
                }
            }
            return super.extractItem(slot, amount, simulate);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if(slot == 0) {
                AspectList aspectList = ThaumcraftCraftingManager.getObjectTags(stack);
                return aspectList != null && aspectList.size() > 0;
            } else if(slot == 1) {
                return TileSmelter.isItemFuel(stack);
            } else {
                return super.isItemValid(slot, stack);
            }
        }
    };
    public RangedWrapper smeltingHandler;
    public RangedWrapper fuelHandler;
    public AspectList aspects;
    public int burnTime;
    public int burnTimeMax;
    public int progress;
    public int progressMax;
    public boolean alumentumBoost;

    public AbstractTileEssentiaSmelter() {
        this.smeltingHandler = new RangedWrapper(this.handler, 0, 1);
        this.fuelHandler = new RangedWrapper(this.handler, 1, 2);
        this.aspects = new AspectList();
    }

    /**
     * The current total amount of essentia stored within the device.
     */
    public int getCurrentEssentiaTotal() {
        return this.aspects != null ? this.aspects.visSize() : 0;
    }

    /**
     * The maximum amount of essentia this smelter can store. This translates to the capacity bar that appears
     * on the left side of the GUI. Default Thaumcraft smelters have a capacity of 256.
     */
    public abstract int getMaxEssentiaCapacity();

    /**
     * Gets the delay, in ticks, between each attempt to transfer essentia into Alembics above the device. This
     * also includes essentia transferred using auxiliary slurry pumps. Thaumcraft uses a transfer speed of 15
     * for the basic smelter and a speed of 10 for the advanced smelter.
     */
    public abstract int getTransferSpeed();

    /**
     * The machine efficiency when converting items into their base aspects. Thaumcraft uses an efficiency of
     * 0.9 for the base smelter and an efficiency of 0.95 for the advanced smelter.
     */
    public abstract float getBaseEfficiency();

    /**
     * A aspect sensitive version of {@link AbstractTileEssentiaSmelter#getBaseEfficiency()}. Used to
     * get the efficiency when converting items into their base aspects.
     */
    public float getAspectEfficiency(Aspect aspect) {
        if(aspect == Aspect.FLUX) {
            return this.getBaseEfficiency() * 0.66f;
        }
        return this.getBaseEfficiency();
    }

    /**
     * Returns the front of the machine. Smelters cannot have addons attached to the front.
     */
    public abstract EnumFacing getMachineFront();

    /**
     * Sets the smelter to active, changing any required block states.
     */
    public abstract void setBlockActive(boolean isActive);

    @Override
    public void readFromNBT(@NotNull NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.handler.deserializeNBT(compound.getCompoundTag("inventory"));
        this.burnTime = compound.getInteger("burnTime");
        this.burnTimeMax = compound.getInteger("burnTimeMax");
        this.progress = compound.getInteger("progress");
        this.progressMax = compound.getInteger("maxProgress");
        this.alumentumBoost = compound.getBoolean("alumentumBoost");
        this.aspects.readFromNBT(compound);
    }

    @Override
    public @NotNull NBTTagCompound writeToNBT(@NotNull NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("inventory", this.handler.serializeNBT());
        compound.setInteger("burnTime", this.burnTime);
        compound.setInteger("burnTimeMax", this.burnTimeMax);
        compound.setInteger("progress", this.progress);
        compound.setInteger("maxProgress", this.progressMax);
        compound.setBoolean("alumentumBoost", this.alumentumBoost);
        this.aspects.writeToNBT(compound);
        return compound;
    }

    @Override
    public void update() {
        boolean did = false;
        if(!this.world.isRemote) {
            did |= this.handleItemSmelting();
            did |= this.handleFuel();
            did |= this.handleEssentiaTransfer();
            this.setBlockActive(this.burnTime > 0);
        }
        if(did) {
            this.markDirty();
        }
    }

    public boolean handleFuel() {
        if (this.burnTime > 0) {
            this.burnTime--;
            return true;
        } else if (this.canSmeltItem() && this.hasFuel()) {
            this.consumeFuel();
            return true;
        }
        return false;
    }

    public boolean handleItemSmelting() {
        if(this.burnTime > 0) {
            if (this.canSmeltItem()) {
                int itemSmeltTime = this.getItemSmeltingTime();
                if (itemSmeltTime != this.progressMax) {
                    this.progress = 0;
                    this.progressMax = itemSmeltTime;
                } else {
                    this.progress++;
                    if(this.progress >= this.progressMax) {
                        this.smeltItem();
                        this.resetProgress();
                    }
                }
                return true;
            } else if(this.progress > 0) {
                this.resetProgress();
                return true;
            }
        } else if (this.progress > 0) {
            this.progress--;
            return true;
        }
        return false;
    }

    public boolean handleEssentiaTransfer() {
        boolean did = false;
        int speed = this.getTransferSpeed();
        if(this.alumentumBoost) {
            speed = (int) (speed * 0.8);
        }
        //TODO: See if this is running correctly
        if(this.world.getTotalWorldTime() % (long) speed == 0) {
            for(Aspect aspect : this.aspects.getAspects()) {
                if(this.aspects.getAmount(aspect) > 0 && SmelterHelper.processAlembics(this.world, this.pos, aspect)) {
                    this.aspects.remove(aspect, 1);
                    did = true;
                    break;
                }
            }

            for(EnumFacing facing : EnumFacing.HORIZONTALS) {
                for(int i = 0; i < this.getBonusOperations(facing); i++) {
                    for(Aspect aspect : this.aspects.getAspects()) {
                        if(this.aspects.getAmount(aspect) > 0 && SmelterHelper.processAlembics(this.world, this.pos.offset(facing), aspect)) {
                            this.aspects.remove(aspect, 1);
                            did = true;
                            break;
                        }
                    }
                }
            }
        }
        return did;
    }

    public boolean canSmeltItem() {
        ItemStack smeltingStack = this.smeltingHandler.getStackInSlot(0);
        if (!smeltingStack.isEmpty()) {
            AspectList aspectList = ThaumcraftCraftingManager.getObjectTags(smeltingStack);
            if(aspectList != null && aspectList.size() > 0) {
                return aspectList.visSize() <= this.getMaxEssentiaCapacity() - this.getCurrentEssentiaTotal();
            }
        }
        return false;
    }

    public void smeltItem() {
        ItemStack stack = this.smeltingHandler.extractItem(0, 1, false);
        if(!stack.isEmpty()) {
            int flux = 0;
            AspectList aspectList = ThaumcraftCraftingManager.getObjectTags(stack);
            if(aspectList != null && aspectList.size() > 0) {
                for(Aspect aspect : aspectList.getAspects()) {
                    float efficiency = this.getAspectEfficiency(aspect);
                    int max = aspectList.getAmount(aspect);
                    int gained = (int) (max * efficiency);
                    if(gained < max) {
                        if(this.world.rand.nextFloat() <= efficiency) {
                            gained++;
                        }
                        flux += max - gained;
                    }
                    this.aspects.add(aspect, gained);
                }
                flux = this.getActualPollution(flux);
                AuraHelper.polluteAura(this.world, this.pos, (float) flux, true);
            }
        }
    }

    public boolean hasFuel() {
        ItemStack stack = this.fuelHandler.getStackInSlot(0);
        if(!stack.isEmpty() && TileEntityFurnace.isItemFuel(stack)) {
            if (stack.getItem().hasContainerItem(stack)) {
                return stack.getCount() == 1;
            } else {
                return true;
            }
        }
        return false;
    }

    public void consumeFuel() {
        ItemStack stack = this.fuelHandler.getStackInSlot(0);
        if(!stack.isEmpty()) {
            int burnTime = TileEntityFurnace.getItemBurnTime(stack);
            if(burnTime > 0) {
                this.alumentumBoost = stack.getItem() instanceof ItemAlumentum;
                if(stack.getItem().hasContainerItem(stack)) {
                    if(stack.getCount() == 1) {
                        stack = this.fuelHandler.extractItem(0, 1, false);
                        stack = stack.getItem().getContainerItem(stack);
                        this.fuelHandler.insertItem(0, stack, false);
                    } else {
                        burnTime = 0;
                    }
                } else {
                    this.fuelHandler.extractItem(0, 1, false);
                }
                this.setBurnTime(burnTime);
                return;
            }
        }
        this.setBurnTime(0);
    }

    public void setBurnTime(int burnTime) {
        this.burnTime = burnTime;
        this.burnTimeMax = burnTime <= 0 ? 200 : burnTime;
    }

    public void resetProgress() {
        this.progress = 0;
        int newMax = this.getItemSmeltingTime();
        this.progressMax = newMax > 0 ? newMax : 200;
    }

    public int getItemSmeltingTime() {
        ItemStack stack = this.smeltingHandler.getStackInSlot(0);
        if(!stack.isEmpty()) {
            AspectList aspectList = ThaumcraftCraftingManager.getObjectTags(stack);
            if(aspectList != null && aspectList.size() > 0) {
                return (int) ((aspectList.visSize() * 2) * this.getBellowsModifier());
            }
        }
        return 0;
    }

    public float getBellowsModifier() {
        //TODO: Change this to a method that uses the IBellows interface.
        EnumFacing front = this.getMachineFront();
        int bellows = TileBellows.getBellows(this.world, this.pos, Arrays.stream(EnumFacing.HORIZONTALS)
                .filter(facing -> facing != front)
                .toArray(EnumFacing[]::new));
        return (float) Math.max(0.1, 1.0 - (0.125 * bellows));
    }

    public int getBonusOperations(EnumFacing facing) {
        if(facing != this.getMachineFront()) {
            BlockPos checkPos = this.pos.offset(facing);
            IBlockState checkState = this.world.getBlockState(checkPos);
            ISmelterAuxiliary auxiliary = SmelterHelper.getSmelterAuxiliary(this.world, checkPos, checkState);
            if (auxiliary != null && auxiliary.canBoostSmelter(this.world, checkPos, checkState, this)) {
                return auxiliary.getBonusOperations(this.world, checkPos, checkState, this);
            }
        }
        return 0;
    }

    public int getActualPollution(int flux) {
        EnumFacing front = this.getMachineFront();
        for(EnumFacing facing : EnumFacing.HORIZONTALS) {
            if(facing == front) continue;
            BlockPos checkPos = this.pos.offset(facing);
            IBlockState checkState = this.world.getBlockState(checkPos);
            ISmelterVent vent = SmelterHelper.getSmelterVent(this.world, checkPos, checkState);
            if(vent != null && vent.canVentSmelter(this.world, checkPos, checkState, this)) {
                if(this.world.rand.nextFloat() < vent.getFluxFilterChance(this.world, checkPos, checkState, this)) {
                    flux--;
                }
            }
        }
        return flux;
    }

    // Capability handlers

    @Override
    public boolean hasCapability(@NotNull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
    }

    @Override
    public @Nullable <T> T getCapability(@NotNull Capability<T> capability, @Nullable EnumFacing facing) {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (facing == null) {
                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.handler);
            } else if (facing == EnumFacing.UP || facing == EnumFacing.DOWN) {
                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.smeltingHandler);
            } else {
                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.fuelHandler);
            }
        }
        return super.getCapability(capability, facing);
    }

    //Tile updated stuff

    @Override
    public void markDirty() {
        super.markDirty();
        IBlockState state = this.world.getBlockState(this.pos);
        this.world.notifyBlockUpdate(this.pos, state, state, 2);
    }

    @Override
    public @Nullable SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, Constants.BlockFlags.DEFAULT, this.getUpdateTag());
    }

    @Override
    public @NotNull NBTTagCompound getUpdateTag() {
        return this.writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(@NotNull NetworkManager net, SPacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.getNbtCompound());
    }
}
