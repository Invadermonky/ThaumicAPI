package com.invadermonky.thaumicapi.network;

import com.invadermonky.thaumicapi.ThaumicAPI;
import com.invadermonky.thaumicapi.network.messages.MessageWarpEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkHandlerTAPI {
    public static final SimpleNetworkWrapper INSTANCE = new SimpleNetworkWrapper(ThaumicAPI.MOD_ID);

    public static void init() {
        int id = 0;
        INSTANCE.registerMessage(MessageWarpEvent.MessageHandler.class, MessageWarpEvent.class, id++, Side.CLIENT);
    }
}
