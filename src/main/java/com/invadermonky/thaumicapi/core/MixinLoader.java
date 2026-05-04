package com.invadermonky.thaumicapi.core;

import com.google.common.collect.ImmutableMap;
import zone.rong.mixinbooter.Context;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

public class MixinLoader implements ILateMixinLoader {
    private static final Map<String, Predicate<Context>> MIXIN_CONFIGS = ImmutableMap.copyOf(new HashMap<String, Predicate<Context>>() {
        {
            put("mixins.thaumicapi.json", context -> true);
            put("mixins.thaumicapi.mmce.json", context -> context.isModPresent("modularmachinery"));
        }
    });

    @Override
    public List<String> getMixinConfigs() {
        return new ArrayList<>(MIXIN_CONFIGS.keySet());
    }

    @Override
    public boolean shouldMixinConfigQueue(Context context) {
        Predicate<Context> predicate = MIXIN_CONFIGS.get(context.mixinConfig());
        return predicate == null || predicate.test(context);
    }
}
