package com.somevanguy.gemsind.datagen.recipe_builders;

import com.somevanguy.gemsind.content.alloying_furnace.AlloyingRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DataAlloyingRecipeBuilder implements RecipeBuilder {
    protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    protected final List<String> displayElements;
    protected final List<Ingredient> ingredients;
    protected final int fluxCount;
    protected final int craftingTime;
    protected final ItemStack result;

    public DataAlloyingRecipeBuilder(List<String> displayElements, List<Ingredient> ingredients, int fluxCount, int craftingTime, ItemStack result) {
        this.displayElements = displayElements;
        this.ingredients = ingredients;
        this.fluxCount = fluxCount;
        this.craftingTime = craftingTime;
        this.result = result;
    }

    @Override
    public RecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        criteria.put(name, criterion);
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String group) {
        return this;
    }

    @Override
    public Item getResult() {
        return result.getItem();
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation id) {
        Advancement.Builder advancement = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);
        criteria.forEach(advancement::addCriterion);
        AlloyingRecipe recipe = new AlloyingRecipe(displayElements, ingredients, fluxCount, craftingTime, result);
        recipeOutput.accept(id, recipe, advancement.build(id.withPrefix("recipes/alloying/")));
    }
}
