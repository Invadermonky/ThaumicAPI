package com.invadermonky.thaumicapi.mixins.tile;

import com.invadermonky.thaumicapi.api.impetus.CapabilityImpetusHandler;
import com.invadermonky.thaumicapi.api.impetus.IImpetusStorage;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import thaumcraft.common.tiles.TileThaumcraftInventory;
import thaumcraft.common.tiles.crafting.TileVoidSiphon;

@Mixin(value = TileVoidSiphon.class, priority = 1001, remap = false)
public abstract class TileVoidSiphonMixin extends TileThaumcraftInventory implements IImpetusStorage {
    @Shadow public int progress;
    @Shadow @Final public int PROGREQ;

    public TileVoidSiphonMixin(int size) {
        super(size);
    }

    @ModifyConstant(method = "update", constant = @Constant(intValue = 2000), remap = true)
    private int modifyProgressRequired(int constant) {
        return this.getMaxImpetusStored();
    }

    @Override
    public @Nullable <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if(capability == CapabilityImpetusHandler.IMPETUS_HANDLER_CAPABILITY) {
            return CapabilityImpetusHandler.IMPETUS_HANDLER_CAPABILITY.cast(this);
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return super.hasCapability(capability, facing) || capability == CapabilityImpetusHandler.IMPETUS_HANDLER_CAPABILITY;
    }

    @Override
    public int receiveImpetus(int maxReceive, boolean simulate) {
        if(!this.canReceive())
            return 0;

        if(!simulate) {
            this.progress += maxReceive;
        }
        return maxReceive;
    }

    @Override
    public int extractImpetus(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getImpetusStored() {
        return this.progress;
    }

    @Override
    public int getMaxImpetusStored() {
        return PROGREQ;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return this.getImpetusStored() < this.getMaxImpetusStored();
    }
}
