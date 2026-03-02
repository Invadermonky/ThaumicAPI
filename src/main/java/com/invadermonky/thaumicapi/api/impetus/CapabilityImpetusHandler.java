package com.invadermonky.thaumicapi.api.impetus;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import org.jetbrains.annotations.Nullable;

public class CapabilityImpetusHandler {
    @CapabilityInject(IImpetusStorage.class)
    public static Capability<IImpetusStorage> IMPETUS_HANDLER_CAPABILITY = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(IImpetusStorage.class, new DefaultImpetusHandlerStorage<>(), () -> new ImpetusStorage(2000));
    }

    private static class DefaultImpetusHandlerStorage<T extends IImpetusStorage> implements Capability.IStorage<T> {
        @Override
        public @Nullable NBTBase writeNBT(Capability<T> capability, T instance, EnumFacing side) {
            if(!(instance instanceof IImpetusStorage))
                throw new RuntimeException("IImpetusHandler does not implement IImpetusStorage");
            return new NBTTagInt(instance.getImpetusStored());
        }

        @Override
        public void readNBT(Capability<T> capability, T instance, EnumFacing side, NBTBase nbt) {
            if(!(instance instanceof ImpetusStorage))
                throw new RuntimeException("Can not deserialize to an instance that isn't the default implementation.");
            ((ImpetusStorage) instance).stored = ((NBTTagInt) nbt).getInt();
        }
    }
}
