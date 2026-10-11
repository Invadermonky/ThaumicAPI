package com.invadermonky.thaumicapi.api;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import com.invadermonky.thaumicapi.infusion.enchantments.InfusionEnchantmentHelper;
import com.invadermonky.thaumicapi.utils.helpers.ItemHelper;
import com.invadermonky.thaumicapi.utils.helpers.ResearchHelper;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.common.lib.enchantment.EnumInfusionEnchantment;

public class ThaumicAPI {

    /**
     * Registers and returns a new infusion enchantment using the specified values.
     *<p>
     * Valid slot type strings:
     * <ul>
     *     <li><code>all</code> - Any item</li>
     *     <li><code>weapon</code> - Any item that has a {@link EntityEquipmentSlot#MAINHAND} attribute map containing {@link SharedMonsterAttributes#ATTACK_DAMAGE}</li>
     *     <li><code>armor</code> - Any item that extends {@link ItemArmor}</li>
     *     <li><code>helm</code> - Any {@link ItemArmor} that has a slot of {@link EntityEquipmentSlot#HEAD}</li>
     *     <li><code>chest</code> - Any {@link ItemArmor} that has a slot of {@link EntityEquipmentSlot#CHEST}</li>
     *     <li><code>legs</code> - Any {@link ItemArmor} that has a slot of {@link EntityEquipmentSlot#LEGS}</li>
     *     <li><code>helm</code> - Any {@link ItemArmor} that has a slot of {@link EntityEquipmentSlot#FEET}</li>
     *     <li><code>bauble</code> - Any item that extends {@link IBauble}</li>
     *     <li><code>amulet</code> - Any item that extends {@link IBauble} and has a bauble type of {@link BaubleType#AMULET}</li>
     *     <li><code>belt</code> - Any item that extends {@link IBauble} and has a bauble type of {@link BaubleType#BELT}</li>
     *     <li><code>ring</code> - Any item that extends {@link IBauble} and has a bauble type of {@link BaubleType#RING}</li>
     *     <li><code>chargable</code> - Any item that extends {@link IRechargable}</li>
     *     <li><code>[toolstring]</code> - Any item that extends {@link ItemTool} and has a tool class matching the [toolstring] (e.g. "pickaxe", "axe", or "shovel")</li>
     * </ul>
     *
     * @param enchantmentName The internal enchantment name. This is used to generate language keys.
     * @param maxLevel The maximum level of the enchantment.
     * @param requiredResearch The research required before this enchantment can be crafted.
     * @param slotTypes The valid item slot types for this infusion enchantment.
     * @return The newly registered infusion enchantment.
     */
    public static EnumInfusionEnchantment registerInfusionEnchantment(String enchantmentName, int maxLevel, String requiredResearch, String... slotTypes) {
        return InfusionEnchantmentHelper.registerNewInfusionEnchant(enchantmentName, maxLevel, requiredResearch, slotTypes);
    }


    /**
     * A helper method to add Vis Discount to any ItemStack.
     *
     * @param stack The ItemStack object
     * @param visDiscount The amount of vis discount to add to the item
     * @return The modified ItemStack
     */
    public static ItemStack addVisDiscountToStack(ItemStack stack, int visDiscount) {
        return ItemHelper.addVisDiscountToStack(stack, visDiscount);
    }

    /**
     * A helper method to add Warping to any ItemStack. Gear warping can only be a byte value
     * (between -128 and 127).
     *
     * @param stack The ItemStack object
     * @param warp The amount of warp to add to the item
     * @return The modified ItemStack
     */
    public static ItemStack addWarpingToStack(ItemStack stack, int warp) {
        return ItemHelper.addWarpToStack(stack, warp);
    }

    /**
     * Returns a list of filtered research categories to prevent returning categories that do not
     * have associated research.
     * <p>
     * If Thaumcraft Fix is installed, this method will automatically redirect to the overhauled
     * research theorycraft filter.
     */
    public static ResearchCategory[] getResearchCategories() {
        return ResearchHelper.getResearchCategories();
    }
}
