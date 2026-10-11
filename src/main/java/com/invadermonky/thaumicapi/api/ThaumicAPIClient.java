package com.invadermonky.thaumicapi.api;

import com.invadermonky.thaumicapi.api.item.AbstractItemCaster;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ThaumicAPIClient {
    /**
     * Registers the color handler for a dyeable and non-dyeable Caster's Gauntlet. Calling this method
     * will allow the gauntlet to correctly display focus color and dyeable trim color.
     * <p>
     * Thisi method should be called after registering the gauntlet item model.
     */
    public static void registerGauntletColorHandler(AbstractItemCaster casterItem) {
        AbstractItemCaster.initClient(casterItem);
    }
}
