package com.somevanguy.gemsind.compat;

import com.somevanguy.gemsind.content.desalinator.DesalinatingRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

public class DesalinatingRecipeCategory implements IRecipeCategory<RecipeHolder<DesalinatingRecipe>> {
    public DesalinatingRecipeCategory(IGuiHelper guiHelper) {

    }

    @Override
    public RecipeType<RecipeHolder<DesalinatingRecipe>> getRecipeType() {
        return null;
    }

    @Override
    public Component getTitle() {
        return null;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return null;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<DesalinatingRecipe> recipe, IFocusGroup focuses) {

    }
}
