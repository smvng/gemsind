package com.somevanguy.gemsind.content.alloying_furnace;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.somevanguy.gemsind.GemstoneIndustryMod;
import com.somevanguy.gemsind.content.AllRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.RecipeMatcher;

import java.util.ArrayList;
import java.util.List;

// recipe file contents
public record AlloyingRecipe(List<String> displayElements, List<Ingredient> ingredients, int minFluxCount, int craftingTime, ItemStack result) implements Recipe<AlloyingRecipeInput> {
    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.copyOf(ingredients);
    }

    public int ingredientCount() {
        return ingredients.size();
    }

    @Override
    public boolean matches(AlloyingRecipeInput input, Level level) {
        if (level.isClientSide()) {
            return false;
        }
        if (ingredients.size() != input.elementGivers().size()) {
            return false;
        }
        if (minFluxCount > input.fluxCount()) {
            return false;
        }

        ArrayList<ItemStack> validStacks = new ArrayList<>(input.elementGivers().size());

        for(ItemStack ingredient : input.elementGivers()) {
            if (!ingredient.isEmpty()) {
                validStacks.add(ingredient);
            }
        }

        return RecipeMatcher.findMatches(validStacks, ingredients) != null;
    }

    @Override
    public ItemStack assemble(AlloyingRecipeInput input, HolderLookup.Provider provider) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return AllRecipes.ALLOYING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return AllRecipes.ALLOYING_TYPE.get();
    }

    public AlloyingRecipe {
        if (minFluxCount < 1 || minFluxCount > 4) {
            GemstoneIndustryMod.LOGGER.warn("Instance of {} exceeded fluxCount limit (1<=n<=4)", this.getClass().getName());
        }
    }

    public static class Serializer implements RecipeSerializer<AlloyingRecipe> {
        public static final MapCodec<AlloyingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.list(Codec.STRING, 2, 4).fieldOf("displayElements").forGetter(AlloyingRecipe::displayElements),
                Codec.list(Ingredient.CODEC_NONEMPTY, 2, 4).fieldOf("ingredients").forGetter(AlloyingRecipe::getIngredients),
                Codec.intRange(1, 4).fieldOf("minFluxCount").forGetter(AlloyingRecipe::minFluxCount),
                Codec.intRange(1, 86400*20).fieldOf("craftingTime").forGetter(AlloyingRecipe::craftingTime),
                ItemStack.CODEC.fieldOf("result").forGetter(AlloyingRecipe::result)
        ).apply(instance, AlloyingRecipe::new));

        @Override
        public MapCodec<AlloyingRecipe> codec() {
            return CODEC;
        }

        public static final StreamCodec<RegistryFriendlyByteBuf, AlloyingRecipe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.collection(ArrayList::new, ByteBufCodecs.STRING_UTF8), AlloyingRecipe::displayElements,
                ByteBufCodecs.collection(ArrayList::new, Ingredient.CONTENTS_STREAM_CODEC), AlloyingRecipe::ingredients,
                ByteBufCodecs.INT, AlloyingRecipe::minFluxCount,
                ByteBufCodecs.INT, AlloyingRecipe::craftingTime,
                ItemStack.STREAM_CODEC, AlloyingRecipe::result,
                AlloyingRecipe::new
        );

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, AlloyingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
