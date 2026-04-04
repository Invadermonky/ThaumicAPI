package com.invadermonky.thaumicapi.jei;

import mezz.jei.api.ingredients.IIngredientHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.AspectList;

import java.util.List;

public class AspectListIngredientHelper implements IIngredientHelper<AspectList> {

    @Override
    public @NotNull List<AspectList> expandSubtypes(@NotNull List<AspectList> ingredients) {
        return ingredients;
    }

    @Override
    public @Nullable AspectList getMatch(@NotNull Iterable<AspectList> ingredients, @NotNull AspectList toMatch) {
        if(toMatch.size() > 0) {
            for(AspectList aspectList : ingredients) {
                if(aspectList.size() > 0 && aspectList.getAspects()[0].getName().equalsIgnoreCase(toMatch.getAspects()[0].getName())) {
                    return aspectList;
                }
            }
        }
        return null;
    }

    @Override
    public @NotNull String getDisplayName(@NotNull AspectList aspectList) {
        return aspectList.size() > 0 ? aspectList.getAspects()[0].getName() : "";
    }

    @Override
    public @NotNull String getUniqueId(@NotNull AspectList aspectList) {
        return aspectList.size() > 0 ? aspectList.getAspects()[0].getName() : "";
    }

    @Override
    public @NotNull String getWildcardId(@NotNull AspectList aspectList) {
        return "/";
    }

    @Override
    public @NotNull String getModId(@NotNull AspectList aspectList) {
        return "thaumcraft";
    }

    @Override
    public @NotNull String getResourceId(AspectList aspectList) {
        return aspectList.size() > 0 ? aspectList.getAspects()[0].getName() : "";
    }

    @Override
    public @NotNull AspectList copyIngredient(AspectList aspectList) {
        return aspectList.copy();
    }

    @Override
    public @NotNull String getErrorInfo(@Nullable AspectList aspectList) {
        return "";
    }
}
