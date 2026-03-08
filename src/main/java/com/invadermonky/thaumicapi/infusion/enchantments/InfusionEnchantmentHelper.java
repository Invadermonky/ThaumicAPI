package com.invadermonky.thaumicapi.infusion.enchantments;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import net.minecraftforge.common.util.EnumHelper;
import thaumcraft.common.lib.enchantment.EnumInfusionEnchantment;

import java.util.Set;

public class InfusionEnchantmentHelper {
    public static EnumInfusionEnchantment registerNewInfusionEnchant(String name, int maxLevel, String requiredResearch, String... slotTypes) {
        Preconditions.checkArgument(maxLevel > 0, "Infusion Enchantment maximum level must be greater than 0.");
        Preconditions.checkArgument(slotTypes.length > 0, "Infusion Enchantment slot type cannot be empty.");
        return EnumHelper.addEnum(EnumInfusionEnchantment.class, name.toUpperCase(), new Class[]{Set.class, int.class, String.class},
                ImmutableSet.copyOf(slotTypes), maxLevel, requiredResearch
        );
    }
}
