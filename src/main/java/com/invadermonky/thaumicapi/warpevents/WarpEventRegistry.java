package com.invadermonky.thaumicapi.warpevents;

import com.invadermonky.thaumicapi.ThaumicAPIMod;
import com.invadermonky.thaumicapi.api.warpevent.IWarpEvent;
import com.invadermonky.thaumicapi.api.warpevent.WarpEvent;
import net.minecraft.entity.player.EntityPlayer;
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
    public static IWarpEvent getWarpEvent(EntityPlayer player, int playerWarp) {
        List<IWarpEvent> potentialEvents = new ArrayList<>();
        int eventWeight = 0;
        while(playerWarp >= 0 && potentialEvents.isEmpty()) {
            for (IWarpEvent event : WARP_EVENTS.values()) {
                if (playerWarp > event.getMinimumWarp() && playerWarp <= event.getMaximumWarp() && event.shouldEventProcess(player)) {
                    potentialEvents.add(event);
                    eventWeight += event.getEventWeight();
                }
            }
            playerWarp--;
        }

        if(!potentialEvents.isEmpty() && eventWeight > 0) {
            int weight = player.world.rand.nextInt(eventWeight);
            for (IWarpEvent event : potentialEvents) {
                if (weight < event.getEventWeight()) {
                    return event;
                }
                weight -= event.getEventWeight();
            }
        }

        return !potentialEvents.isEmpty() ? potentialEvents.get(0) : null;
    }

    @Nullable
    public static IWarpEvent getWarpEvent(String eventName) {
        return WARP_EVENTS.get(eventName);
    }

    public static void registerWarpEvent(IWarpEvent event) {
        if(event != null && event.isEnabled()) {
            if(WARP_EVENTS.containsKey(event.getEventName())) {
                ThaumicAPIMod.LOGGER.warn("Warp Event {} is being overwritten by {}", WARP_EVENTS.get(event.getEventName()).getClass(), event.getClass());
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
                ThaumicAPIMod.LOGGER.error("Error registering Warp Event {}", found.getClassName(), e);
            }
        }
    }
}
