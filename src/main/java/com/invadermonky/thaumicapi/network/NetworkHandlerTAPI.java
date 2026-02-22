package com.invadermonky.thaumicapi.network;

import com.invadermonky.thaumicapi.ThaumicAPIMod;
import com.invadermonky.thaumicapi.network.messages.MessageAuraToClient;
import com.invadermonky.thaumicapi.network.messages.MessageWarpEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkHandlerTAPI {
    public static final SimpleNetworkWrapper INSTANCE = new SimpleNetworkWrapper(ThaumicAPIMod.MOD_ID);

    public static void init() {
        int id = 0;
        INSTANCE.registerMessage(MessageAuraToClient.MessageHandler.class, MessageAuraToClient.class, id++, Side.CLIENT);
        INSTANCE.registerMessage(MessageWarpEvent.MessageHandler.class, MessageWarpEvent.class, id++, Side.CLIENT);
    }
}
