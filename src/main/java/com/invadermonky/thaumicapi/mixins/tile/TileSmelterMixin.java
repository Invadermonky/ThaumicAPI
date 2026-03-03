package com.invadermonky.thaumicapi.mixins.tile;

import com.invadermonky.thaumicapi.api.block.ISmelterAuxiliary;
import com.invadermonky.thaumicapi.api.block.ISmelterVent;
import com.invadermonky.thaumicapi.utils.helpers.SmelterHelper;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.TileThaumcraftInventory;
import thaumcraft.common.tiles.essentia.TileSmelter;

@Mixin(value = TileSmelter.class, remap = false)
public abstract class TileSmelterMixin extends TileThaumcraftInventory {

    @Shadow public AspectList aspects;

    @Shadow public abstract boolean takeFromContainer(Aspect tag, int amount);

    public TileSmelterMixin(int size) {
        super(size);
    }

    /**
     * @author Invadermonky
     * @reason Extending Thaumcraft's hardcoded smelter vent behavior to use {@link ISmelterVent}.<br>
     * TODO: this needs a better approach, but TC code is kind of a disaster.
     */
    @Inject(
            method = "smeltItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lthaumcraft/api/aura/AuraHelper;polluteAura(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;FZ)V"
            )
    )
    private void overwriteSmelterVentBehavior(CallbackInfo ci, @Local(ordinal = 0) int flux, @Local(ordinal = 1) LocalIntRef pollution) {
        int newPollution = 0;
        for(int i = 0; i < flux; i++) {
            for(EnumFacing face : EnumFacing.HORIZONTALS) {
                if(BlockStateUtils.getFacing(this.getBlockMetadata()) == face)
                    continue;

                BlockPos checkPos = this.pos.offset(face);
                IBlockState checkState = this.world.getBlockState(checkPos);
                TileEntity checkTile = this.world.getTileEntity(checkPos);
                ISmelterVent vent = SmelterHelper.getSmelterVent(this.world, checkPos, checkState);

                if(vent != null && vent.canVentSmelter(this.world, checkPos, checkState, this)) {
                    if(this.world.rand.nextFloat() < vent.getFluxFilterChance(this.world, checkPos, checkState, this)) {
                        continue;
                    }
                }
                newPollution++;
            }
            if(newPollution != pollution.get()) {
                pollution.set(newPollution);
            }
        }
    }

    /**
     * @author Invadermonky
     * @reason Extending Thaumcraft's hardcoded smelter auxiliary behavior to use {@link ISmelterAuxiliary}.
     */
    @Inject(
            method = "update",
            at = @At(
                    value = "INVOKE",
                    target = "Lthaumcraft/common/lib/utils/BlockStateUtils;getFacing(I)Lnet/minecraft/util/EnumFacing;"
            )
    )
    private void overwriteSmelterAuxBehavior(CallbackInfo ci, @Local(ordinal = 0)LocalRef<EnumFacing> faceRef) {
        EnumFacing smelterFacing = BlockStateUtils.getFacing(this.getBlockMetadata());
        if(smelterFacing != faceRef.get()) {
            BlockPos checkPos = this.pos.offset(faceRef.get());
            IBlockState checkState = this.world.getBlockState(checkPos);
            ISmelterAuxiliary aux = SmelterHelper.getSmelterAuxiliary(this.world, checkPos, checkState);

            if(aux != null && aux.canBoostSmelter(this.world, checkPos, checkState, this)) {
                for(int i = 0; i < aux.getBonusOperations(this.world, checkPos, checkState, this); i++) {
                    for(Aspect aspect : this.aspects.getAspects()) {
                        if (this.aspects.getAmount(aspect) > 0 && SmelterHelper.processAlembics(this.world, this.getPos().offset(faceRef.get()), aspect)) {
                            this.takeFromContainer(aspect, 1);
                            break;
                        }
                    }
                }
            }
        }
        faceRef.set(smelterFacing);
    }

}
