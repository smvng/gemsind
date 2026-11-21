package com.somevanguy.gemsind.datagen;

import com.somevanguy.gemsind.content.AllBlocks;
import com.somevanguy.gemsind.content.AllItems;
import com.somevanguy.gemsind.content.AllTags;
import com.somevanguy.gemsind.content.items.AlloyItem;
import com.somevanguy.gemsind.content.items.GemItem;
import com.somevanguy.gemsind.content.items.HammerItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DataRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public DataRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    public static String getSafeNameOfItem(Item item) {
        return BuiltInRegistries.ITEM.getKey(item).getPath();
    }

    public void genericHammer(HammerItem hammer, ItemLike resource, RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, hammer)
                .pattern("rrr")
                .pattern("rs ")
                .pattern(" s ")
                .define('s', Items.STICK)
                .define('r', resource)
                .unlockedBy(getSafeNameOfItem(resource.asItem()), has(resource))
                .save(recipeOutput);
    }

    public void genericHammerFromTag(HammerItem hammer, TagKey<Item> resourceTag, RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, hammer)
                .pattern("rrr")
                .pattern("rs ")
                .pattern(" s ")
                .define('s', Items.STICK)
                .define('r', resourceTag)
                .unlockedBy(resourceTag.registry().registry().getPath(), has(resourceTag))
                .save(recipeOutput);
    }

    private void buildHammers(RecipeOutput recipeOutput) {
        genericHammerFromTag(AllItems.WOODEN_HAMMER.get(), ItemTags.PLANKS, recipeOutput);
        genericHammer(AllItems.IRON_HAMMER.get(), Items.IRON_INGOT, recipeOutput);
        genericHammer(AllItems.GOLDEN_HAMMER.get(), Items.GOLD_INGOT, recipeOutput);
        genericHammer(AllItems.DIAMOND_HAMMER.get(), Items.DIAMOND, recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, AllItems.REINFORCED_HAMMER.get())
                .pattern("dId")
                .pattern("iii")
                .pattern("dDd")
                .define('D', AllItems.DIAMOND_HAMMER.get())
                .define('i', Items.IRON_INGOT)
                .define('I', Blocks.IRON_BLOCK.asItem())
                .define('d', Items.DIAMOND)
                .unlockedBy(getSafeNameOfItem(AllItems.DIAMOND_HAMMER.get()), has(AllItems.DIAMOND_HAMMER.get()))
                .save(recipeOutput);
    }

    private void buildBlocks(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AllBlocks.COUNTER.get())
                .pattern("sss")
                .pattern("ici")
                .pattern("ccc")
                .define('s', Blocks.SMOOTH_STONE.asItem())
                .define('i', Items.IRON_INGOT)
                .define('c', Blocks.COBBLESTONE.asItem())
                .unlockedBy(getSafeNameOfItem(Items.IRON_INGOT), has(Items.IRON_INGOT))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AllBlocks.ACID_PURIFIER.get())
                .pattern("iii")
                .pattern("ibi")
                .pattern("gCg")
                .define('b', Items.BUCKET)
                .define('i', Items.IRON_INGOT)
                .define('g', Items.GOLD_INGOT)
                .define('C', AllBlocks.COUNTER.asItem())
                .unlockedBy(getSafeNameOfItem(AllBlocks.COUNTER.asItem()), has(AllBlocks.COUNTER.get().asItem()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AllBlocks.CENTRIFUGE.get())
                .pattern("rdr")
                .pattern("IrI")
                .pattern("gCg")
                .define('r', Items.REDSTONE)
                .define('d', Blocks.DIAMOND_BLOCK.asItem())
                .define('g', Blocks.GOLD_BLOCK.asItem())
                .define('I', Items.IRON_INGOT)
                .define('C', AllBlocks.COUNTER.asItem())
                .unlockedBy(getSafeNameOfItem(AllBlocks.ACID_PURIFIER.asItem()), has(AllBlocks.ACID_PURIFIER.asItem()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AllBlocks.TUMBLER.get())
                .pattern("rdr")
                .pattern("ICI")
                .pattern("rdr")
                .define('r', Blocks.REDSTONE_BLOCK.asItem())
                .define('d', Blocks.DIAMOND_BLOCK.asItem())
                .define('I', Blocks.IRON_BLOCK.asItem())
                .define('C', AllBlocks.CENTRIFUGE.asItem())
                .unlockedBy(getSafeNameOfItem(AllBlocks.CENTRIFUGE.asItem()), has(AllBlocks.CENTRIFUGE.asItem()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AllBlocks.ALLOYING_FURNACE.get())
                .pattern("IbI")
                .pattern("IbI")
                .pattern("rCr")
                .define('r', Blocks.REDSTONE_BLOCK.asItem())
                .define('b', Blocks.BLAST_FURNACE.asItem())
                .define('I', Blocks.IRON_BLOCK.asItem())
                .define('C', AllBlocks.TUMBLER.asItem())
                .unlockedBy(getSafeNameOfItem(AllBlocks.TUMBLER.asItem()), has(AllBlocks.TUMBLER.asItem()))
                .save(recipeOutput);
    }

    private void buildAlloys(RecipeOutput recipeOutput) {
        for (DeferredItem<AlloyItem> alloyItem : AllItems.ALLOYS) {
            List<Ingredient> ingredients = new ArrayList<>();
            for (String element : alloyItem.get().crafting.getRequiredElements()) {
                var tag = AllTags.Items.getElementTag(element);
                ingredients.add(Ingredient.of(tag));
            }

            var builder = new DataAlloyingRecipeBuilder(
                    alloyItem.get().crafting.getRequiredElements(),
                    ingredients,
                    alloyItem.get().crafting.minFluxCount(),
                    alloyItem.get().crafting.craftingTime(),
                    alloyItem.get().getDefaultInstance());

            for (String element : alloyItem.get().crafting.getRequiredElements()) {
                builder.unlockedBy(GemItem.safeElementName(element), has(AllTags.Items.getElementTag(element)));
            }
            builder.save(recipeOutput);
        }
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        buildHammers(recipeOutput);
        buildBlocks(recipeOutput);
        buildAlloys(recipeOutput);
    }
}
