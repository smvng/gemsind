package com.somevanguy.gemsind.compat;

import com.somevanguy.gemsind.GemstoneIndustryMod;
import com.somevanguy.gemsind.content.AllBlocks;
import com.somevanguy.gemsind.content.AllItems;
import com.somevanguy.gemsind.content.AllTags;
import com.somevanguy.gemsind.content.alloying_furnace.AlloyingRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

public class AlloyingRecipeCategory implements IRecipeCategory<RecipeHolder<AlloyingRecipe>> {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(GemstoneIndustryMod.MODID, "alloying");
    public static final ResourceLocation BACKGROUND =  ResourceLocation.fromNamespaceAndPath(GemstoneIndustryMod.MODID,
            "textures/gui/jei_alloying.png");

    public static final RecipeType<RecipeHolder<AlloyingRecipe>> ALLOYING_TYPE =
            RecipeType.createRecipeHolderType(ID);

    private final IDrawable background;
    private final IDrawable icon;

    public AlloyingRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(BACKGROUND, 0, 0, 103, 63);
        this.icon = helper.createDrawableItemLike(AllBlocks.ALLOYING_FURNACE.get());
    }

    @Override
    public RecipeType<RecipeHolder<AlloyingRecipe>> getRecipeType() {
        return ALLOYING_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei_tab.gemsind.alloying");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    private void addSlotIfExists(Ingredient ingredient, IRecipeLayoutBuilder builder, RecipeIngredientRole role, int x, int y) {
        if (!ingredient.isEmpty()) {
            builder.addSlot(role, x, y).addIngredients(ingredient);
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<AlloyingRecipe> recipeHolder, IFocusGroup focuses) {
        var recipe = recipeHolder.value();
        for (int i = 0; i < 4; i++) {
            int x = 28 + 18 * i;
            if (recipe.ingredientCount() >= i + 1) {
                addSlotIfExists(recipe.getIngredients().get(i), builder, RecipeIngredientRole.INPUT, x, 6);
            }
            if (recipe.minFluxCount() >= i + 1) {
                builder.addSlot(RecipeIngredientRole.INPUT, x, 42).addIngredients(Ingredient.of(AllTags.Items.FLUX));
            }
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, 6, 6).addItemStack(recipe.result());
    }

    @Override
    public int getWidth() {
        return background.getWidth();
    }

    @Override
    public int getHeight() {
        return background.getHeight();
    }

    @Override
    public void draw(RecipeHolder<AlloyingRecipe> recipeHolder, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        IRecipeCategory.super.draw(recipeHolder, recipeSlotsView, guiGraphics, mouseX, mouseY);
        var recipe = recipeHolder.value();
        background.draw(guiGraphics);
        Font font = Minecraft.getInstance().font;
        StringBuilder elementsText = new StringBuilder();
        for (String element : recipe.displayElements()) {
            elementsText.append(element).append(" ");
        }
        guiGraphics.drawString(font, elementsText.toString(), 32, 28, 0xff8b8b8b, false);
    }
}
