package com.invadermonky.thaumicapi.api;

import com.buuz135.thaumicjei.ThaumcraftJEIPlugin;
import com.invadermonky.thaumicapi.jei.AspectIngredientRender;
import com.invadermonky.thaumicapi.jei.AspectListIngredientHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IIngredientType;
import net.minecraftforge.fml.common.Loader;
import org.jetbrains.annotations.NotNull;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@JEIPlugin
public class ThaumicAPIJEIPlugin implements IModPlugin {
    public static final boolean isThaumicJEILoaded;
    public static final IIngredientType<AspectList> ASPECT_INGREDIENT;

    @Override
    public void registerIngredients(@NotNull IModIngredientRegistration registry) {
        if(!isThaumicJEILoaded) {
            registry.register(ASPECT_INGREDIENT, this.getAspectsList(), new AspectListIngredientHelper(), new AspectIngredientRender());
        }
    }

    public List<AspectList> getAspectsList() {
        List<AspectList> aspects = new ArrayList<>();
        aspects.addAll(Aspect.getPrimalAspects().stream().map(aspect -> new AspectList().add(aspect, 1)).collect(Collectors.toList()));
        aspects.addAll(Aspect.getCompoundAspects().stream().map(aspect -> new AspectList().add(aspect, 1)).collect(Collectors.toList()));
        return aspects;
    }

    static {
        isThaumicJEILoaded = Loader.isModLoaded("thaumicjei");
        if(isThaumicJEILoaded) {
            ASPECT_INGREDIENT = ThaumcraftJEIPlugin.ASPECT_LIST;
        } else {
            ASPECT_INGREDIENT = () -> AspectList.class;
        }
    }
}
