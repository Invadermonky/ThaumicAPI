package com.invadermonky.thaumicapi.mixins.tile;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.tiles.essentia.TileAlembic;

@Mixin(value = TileAlembic.class, remap = false)
public interface TileAlembicAccessor {
    @Invoker("processAlembics")
    static boolean invokeProcessAlembics(World world, BlockPos pos, Aspect aspect) {
        throw new AssertionError();
    }
}
