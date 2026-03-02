package com.invadermonky.thaumicapi;

import com.invadermonky.thaumicapi.api.impetus.CapabilityImpetusHandler;
import com.invadermonky.thaumicapi.commands.CommandThaumicAPI;
import com.invadermonky.thaumicapi.network.NetworkHandlerTAPI;
import com.invadermonky.thaumicapi.warpevents.WarpEventRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = ThaumicAPIMod.MOD_ID,
        name = ThaumicAPIMod.MOD_NAME,
        version = ThaumicAPIMod.MOD_VERSION,
        acceptedMinecraftVersions = ThaumicAPIMod.MC_VERSION,
        dependencies = ThaumicAPIMod.DEPENDENCIES
)
public class ThaumicAPIMod {
    public static final String MOD_ID = Tags.MOD_ID;
    public static final String MOD_NAME = Tags.MOD_NAME;
    public static final String MOD_VERSION = Tags.VERSION;
    public static final String MC_VERSION = "[1.12.2]";
    public static final String DEPENDENCIES = "required-after:thaumcraft" +
            ";required-after:mixinbooter";

    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        NetworkHandlerTAPI.init();
        CapabilityImpetusHandler.register();
        WarpEventRegistry.loadFromDataTable(event.getAsmData());
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandThaumicAPI());
    }
}
