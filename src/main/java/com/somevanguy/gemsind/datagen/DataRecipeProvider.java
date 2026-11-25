package com.somevanguy.gemsind.datagen;

import com.somevanguy.gemsind.GemstoneIndustryMod;
import com.somevanguy.gemsind.content.AllBlocks;
import com.somevanguy.gemsind.content.AllItems;
import com.somevanguy.gemsind.content.AllTags;
import com.somevanguy.gemsind.content.items.AlloyItem;
import com.somevanguy.gemsind.content.items.GemItem;
import com.somevanguy.gemsind.content.items.HammerItem;
import com.somevanguy.gemsind.content.items.PickItem;
import com.somevanguy.gemsind.datagen.recipe_builders.DataAlloyingRecipeBuilder;
import com.somevanguy.gemsind.datagen.recipe_builders.DataDesalinatingRecipeBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.NotNull;

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

    public static ResourceLocation locationPickCrafting(String name) {
        return ResourceLocation.fromNamespaceAndPath(GemstoneIndustryMod.MODID,
                "crafting/picks/"+name);
    }

    public static ResourceLocation locationHammerCrafting(String name) {
        return ResourceLocation.fromNamespaceAndPath(GemstoneIndustryMod.MODID,
                "crafting/hammers/"+name);
    }

    public static ResourceLocation locationAlloying(String name) {
        return ResourceLocation.fromNamespaceAndPath(GemstoneIndustryMod.MODID,
                "alloying/"+name);
    }

    public static ResourceLocation locationDesalinating(String name) {
        return ResourceLocation.fromNamespaceAndPath(GemstoneIndustryMod.MODID,
                "desalinating/"+name);
    }

    public static ResourceLocation locationBlockCrafting(String name) {
        return ResourceLocation.fromNamespaceAndPath(GemstoneIndustryMod.MODID,
                "crafting/blocks/"+name);
    }

    public static ResourceLocation locationMiscCrafting(String name) {
        return ResourceLocation.fromNamespaceAndPath(GemstoneIndustryMod.MODID,
                "crafting/misc/"+name);
    }

    public void genericHammer(HammerItem hammer, ItemLike resource, RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, hammer)
                .pattern("rrr")
                .pattern("rs ")
                .pattern(" s ")
                .define('s', Items.STICK)
                .define('r', resource)
                .unlockedBy(getSafeNameOfItem(resource.asItem()), has(resource))
                .save(recipeOutput, locationHammerCrafting(getSafeNameOfItem(hammer)));
    }

    public void genericHammerFromTag(HammerItem hammer, TagKey<Item> resourceTag, RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, hammer)
                .pattern("rrr")
                .pattern("rs ")
                .pattern(" s ")
                .define('s', Items.STICK)
                .define('r', resourceTag)
                .unlockedBy(resourceTag.registry().registry().getPath(), has(resourceTag))
                .save(recipeOutput, locationHammerCrafting(getSafeNameOfItem(hammer)));
    }

    public void genericPick(PickItem pick, ItemLike resource, RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, pick)
                .pattern("rr")
                .pattern("r ")
                .define('r', resource)
                .unlockedBy(getSafeNameOfItem(resource.asItem()), has(resource))
                .save(recipeOutput, locationPickCrafting(getSafeNameOfItem(pick)));
    }

    public void genericPickFromTag(PickItem pick, TagKey<Item> resourceTag, RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, pick)
                .pattern("rr")
                .pattern("r ")
                .define('r', resourceTag)
                .unlockedBy(resourceTag.registry().registry().getPath(), has(resourceTag))
                .save(recipeOutput, locationPickCrafting(getSafeNameOfItem(pick)));
    }

    private void genericDesalination(RecipeOutput recipeOutput, int craftingTime, Fluid fluid, ItemStack result) {
        new DataDesalinatingRecipeBuilder(craftingTime, fluid, result)
                .unlockedBy(getSafeNameOfItem(AllBlocks.COUNTER.asItem()), has(AllBlocks.COUNTER.asItem()))
                .save(recipeOutput, locationDesalinating(getSafeNameOfItem(result.getItem())));
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
                .save(recipeOutput, locationHammerCrafting(getSafeNameOfItem(AllItems.REINFORCED_HAMMER.get())));
    }

    private void buildPicks(RecipeOutput recipeOutput) {
        genericPickFromTag(AllItems.WOODEN_PICK.get(), ItemTags.PLANKS, recipeOutput);
        genericPick(AllItems.IRON_PICK.get(), Items.IRON_INGOT, recipeOutput);
        genericPick(AllItems.GOLDEN_PICK.get(), Items.GOLD_INGOT, recipeOutput);
        genericPick(AllItems.DIAMOND_PICK.get(), Items.DIAMOND, recipeOutput);
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
            builder.save(recipeOutput, locationAlloying(getSafeNameOfItem(alloyItem.get())));
        }
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
                .save(recipeOutput, locationBlockCrafting(getSafeNameOfItem(AllBlocks.COUNTER.asItem())));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AllBlocks.ACID_PURIFIER.get())
                .pattern("ggg")
                .pattern("gbg")
                .pattern("dGd")
                .define('b', Items.BUCKET)
                .define('d', Items.DIAMOND)
                .define('g', Items.GOLD_INGOT)
                .define('G', Blocks.GOLD_BLOCK.asItem())
                .unlockedBy(getSafeNameOfItem(AllBlocks.COUNTER.asItem()), has(AllBlocks.COUNTER.get().asItem()))
                .save(recipeOutput, locationBlockCrafting(getSafeNameOfItem(AllBlocks.ACID_PURIFIER.asItem())));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AllBlocks.DESALINATOR.get())
                .pattern("ICI")
                .pattern("chc")
                .pattern(" h ")
                .define('c', Items.COPPER_INGOT)
                .define('h', Blocks.HOPPER.asItem())
                .define('I', Blocks.IRON_BLOCK.asItem())
                .define('C', Blocks.COPPER_BLOCK.asItem())
                .unlockedBy(getSafeNameOfItem(AllBlocks.ACID_PURIFIER.asItem()), has(AllBlocks.ACID_PURIFIER.get().asItem()))
                .save(recipeOutput, locationBlockCrafting(getSafeNameOfItem(AllBlocks.DESALINATOR.asItem())));

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
                .save(recipeOutput, locationBlockCrafting(getSafeNameOfItem(AllBlocks.CENTRIFUGE.asItem())));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AllBlocks.TUMBLER.get())
                .pattern("rdr")
                .pattern("ICI")
                .pattern("rdr")
                .define('r', Blocks.REDSTONE_BLOCK.asItem())
                .define('d', Blocks.DIAMOND_BLOCK.asItem())
                .define('I', Blocks.IRON_BLOCK.asItem())
                .define('C', AllBlocks.CENTRIFUGE.asItem())
                .unlockedBy(getSafeNameOfItem(AllBlocks.CENTRIFUGE.asItem()), has(AllBlocks.CENTRIFUGE.asItem()))
                .save(recipeOutput, locationBlockCrafting(getSafeNameOfItem(AllBlocks.TUMBLER.asItem())));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AllBlocks.ALLOYING_FURNACE.get())
                .pattern("IbI")
                .pattern("IbI")
                .pattern("rCr")
                .define('r', Blocks.REDSTONE_BLOCK.asItem())
                .define('b', Blocks.BLAST_FURNACE.asItem())
                .define('I', Blocks.IRON_BLOCK.asItem())
                .define('C', AllBlocks.TUMBLER.asItem())
                .unlockedBy(getSafeNameOfItem(AllBlocks.TUMBLER.asItem()), has(AllBlocks.TUMBLER.asItem()))
                .save(recipeOutput, locationBlockCrafting(getSafeNameOfItem(AllBlocks.ALLOYING_FURNACE.asItem())));
    }

    private void buildMisc(RecipeOutput recipeOutput) {
        genericDesalination(recipeOutput, 3*60*20, Fluids.WATER, AllItems.CHLORINATING_SALT.toStack());
        genericDesalination(recipeOutput, 10*60*20, Fluids.LAVA, AllItems.TEST_ALLOY.toStack()); // TODO:CHANGE TO IMBUEMENT INGREDIENT

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AllItems.EMPTY_ACID_PACK.get())
                .pattern("gng")
                .pattern("g g")
                .pattern("nnn")
                .define('g', Blocks.GLASS.asItem())
                .define('n', Items.GOLD_NUGGET)
                .unlockedBy(getSafeNameOfItem(AllBlocks.ACID_PURIFIER.asItem()), has(AllBlocks.ACID_PURIFIER.asItem()))
                .save(recipeOutput, locationMiscCrafting(getSafeNameOfItem(AllItems.EMPTY_ACID_PACK.get())));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AllItems.ACETIC_ACID_PACK.get())
                .pattern("aba")
                .pattern(" e ")
                .define('a', Items.APPLE)
                .define('b', Items.SWEET_BERRIES)
                .define('e', AllItems.EMPTY_ACID_PACK.get())
                .unlockedBy(getSafeNameOfItem(AllBlocks.ACID_PURIFIER.asItem()), has(AllBlocks.ACID_PURIFIER.asItem()))
                .save(recipeOutput, locationMiscCrafting(getSafeNameOfItem(AllItems.ACETIC_ACID_PACK.get())));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AllItems.OXALIC_ACID_PACK.get())
                .pattern("bpb")
                .pattern(" e ")
                .define('b', Items.BEETROOT)
                .define('p', Items.POISONOUS_POTATO)
                .define('e', AllItems.EMPTY_ACID_PACK.get())
                .unlockedBy(getSafeNameOfItem(AllBlocks.ACID_PURIFIER.asItem()), has(AllBlocks.ACID_PURIFIER.asItem()))
                .save(recipeOutput, locationMiscCrafting(getSafeNameOfItem(AllItems.OXALIC_ACID_PACK.get())));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AllItems.HYDROCHLORIC_ACID_PACK.get())
                .pattern("sSs")
                .pattern(" e ")
                .define('S', Blocks.SOUL_SAND)
                .define('s', AllItems.CHLORINATING_SALT.get())
                .define('e', AllItems.EMPTY_ACID_PACK.get())
                .unlockedBy(getSafeNameOfItem(AllBlocks.ACID_PURIFIER.asItem()), has(AllBlocks.ACID_PURIFIER.asItem()))
                .save(recipeOutput, locationMiscCrafting(getSafeNameOfItem(AllItems.HYDROCHLORIC_ACID_PACK.get())));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AllItems.ANTHRACITE.get())
                .requires(ItemTags.COALS)
                .requires(ItemTags.COALS)
                .requires(ItemTags.COALS)
                .requires(ItemTags.COALS)
                .requires(ItemTags.COALS)
                .requires(ItemTags.COALS)
                .requires(ItemTags.COALS)
                .requires(ItemTags.COALS)
                .requires(Blocks.COAL_BLOCK.asItem())
                .unlockedBy(getSafeNameOfItem(AllBlocks.CENTRIFUGE.asItem()), has(AllBlocks.CENTRIFUGE.asItem()))
                .save(recipeOutput, locationMiscCrafting(getSafeNameOfItem(AllItems.ANTHRACITE.get())));
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput recipeOutput) {
        buildHammers(recipeOutput);
        buildPicks(recipeOutput);
        buildAlloys(recipeOutput);
        buildBlocks(recipeOutput);
        buildMisc(recipeOutput);
    }
}
