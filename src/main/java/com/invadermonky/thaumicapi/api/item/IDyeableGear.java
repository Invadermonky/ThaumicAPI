package com.invadermonky.thaumicapi.api.item;

import net.minecraft.item.ItemStack;

public interface IDyeableGear {
    /**
     * Gets the current item dye color.
     */
    int getDyedColor(ItemStack stack);

    /**
     * Sets the current item dye color.
     */
    void setDyedColor(ItemStack stack, int color);

    /**
     * Gets the default undyed item color from ItemStack.
     */
    ItemStack getDefaultDyedColor(ItemStack stack);
}
