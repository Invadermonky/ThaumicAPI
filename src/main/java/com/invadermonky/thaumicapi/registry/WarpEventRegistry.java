package com.invadermonky.thaumicapi.registry;

import com.invadermonky.thaumicapi.ThaumicAPI;
import com.invadermonky.thaumicapi.api.warpevent.IWarpEvent;
import com.invadermonky.thaumicapi.api.warpevent.WarpEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WarpEventRegistry {
    public static final Map<String,IWarpEvent> WARP_EVENTS = new HashMap<>();

    @Nullable
    public static IWarpEvent getWarpEvent(World world, int warp) {
        List<IWarpEvent> potentialEvents = new ArrayList<>();
        while(warp >= 0 && potentialEvents.isEmpty()) {
            for (IWarpEvent event : WARP_EVENTS.values()) {
                if (event.getMinimumWarp() > warp && event.getMaximumWarp() <= warp) {
                    potentialEvents.add(event);
                }
            }
            warp--;
        }
        return !potentialEvents.isEmpty() ? potentialEvents.get(world.rand.nextInt(potentialEvents.size())) : null;
    }

    @Nullable
    public static IWarpEvent getWarpEvent(String eventName) {
        return WARP_EVENTS.get(eventName);
    }

    public static void registerWarpEvent(IWarpEvent event) {
        if(event != null && event.isEnabled()) {
            if(WARP_EVENTS.containsKey(event.getEventName())) {
                ThaumicAPI.LOGGER.warn("Warp Event {} is being overwritten by {}", WARP_EVENTS.get(event.getEventName()).getClass(), event.getClass());
            }
            WARP_EVENTS.put(event.getEventName(), event);
        }
    }

    public static void loadFromDataTable(ASMDataTable data) {
        for(ASMData found : data.getAll(WarpEvent.class.getName())) {
            try {
                Class<?> discovered = Class.forName(found.getClassName());
                if(IWarpEvent.class.isAssignableFrom(discovered)) {
                    Class<? extends IWarpEvent> warpEventClass = discovered.asSubclass(IWarpEvent.class);
                    IWarpEvent warpEvent = warpEventClass.newInstance();
                    if(warpEvent.isEnabled()) {
                        registerWarpEvent(warpEvent);
                    }
                } else {
                    throw new ClassCastException("WarpEvent class " + found.getClassName() + " does not inherit from IWarpEvent.");
                }
            } catch (Exception e) {
                ThaumicAPI.LOGGER.error("Error registering Warp Event {}", found.getClassName(), e);
            }
        }
    }
}
