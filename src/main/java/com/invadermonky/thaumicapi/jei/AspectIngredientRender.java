package com.invadermonky.thaumicapi.jei;

import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AspectIngredientRender implements IIngredientRenderer<AspectList> {

    @Override
    public void render(@NotNull Minecraft minecraft, int xPosition, int yPosition, @Nullable AspectList aspectList) {
        if(aspectList != null && aspectList.size() > 0) {
            this.renderAspectList(aspectList, xPosition, yPosition, minecraft);
        }
    }

    @Override
    public @NotNull List<String> getTooltip(@NotNull Minecraft minecraft, @NotNull AspectList ingredient, @NotNull ITooltipFlag tooltipFlag) {
        if(ingredient.size() > 0) {
            return Arrays.asList(
                    TextFormatting.AQUA + ingredient.getAspects()[0].getName(),
                    TextFormatting.GRAY + ingredient.getAspects()[0].getLocalizedDescription()
            );
        }
        return Collections.emptyList();
    }

    public void renderAspectList(AspectList ingredient, int xPosition, int yPosition, Minecraft minecraft) {
        Aspect aspect = ingredient.getAspects()[0];
        GlStateManager.pushMatrix();
        minecraft.renderEngine.bindTexture(aspect.getImage());
        GlStateManager.enableBlend();
        Color color = new Color(aspect.getColor());
        GlStateManager.color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, 1.0f);
        Gui.drawModalRectWithCustomSizedTexture(xPosition, yPosition, 0, 0, 16, 16, 16, 16);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.scale(0.5, 0.5, 0.5);
        if(ingredient.getAmount(aspect) > 1 && minecraft.currentScreen != null) {
            minecraft.currentScreen.drawCenteredString(minecraft.fontRenderer, TextFormatting.WHITE + "" + ingredient.getAmount(aspect), (xPosition + 16) * 2, (yPosition + 12) * 2, 0);
        }
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}
