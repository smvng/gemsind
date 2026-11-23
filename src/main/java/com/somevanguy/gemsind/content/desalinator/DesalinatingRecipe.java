package com.somevanguy.gemsind.content.desalinator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.somevanguy.gemsind.content.AllRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;

// recipe file contents
public record DesalinatingRecipe(int craftingTime, TagKey<Fluid> fluid, ItemStack result) implements Recipe<DesalinatingRecipeInput> {
    @Override
    public boolean matches(DesalinatingRecipeInput input, Level level) {
        return input.fluidState().is(fluid);
    }

    @Override
    public ItemStack assemble(DesalinatingRecipeInput input, HolderLookup.Provider provider) {
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
        return AllRecipes.DESALINATING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return AllRecipes.DESALINATING_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<DesalinatingRecipe> {
        public static final MapCodec<DesalinatingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.intRange(1, 86400*20).fieldOf("craftingTime").forGetter(DesalinatingRecipe::craftingTime),
                TagKey.codec(Registries.FLUID).fieldOf("fluid").forGetter(DesalinatingRecipe::fluid),
                ItemStack.CODEC.fieldOf("result").forGetter(DesalinatingRecipe::result)
        ).apply(instance, DesalinatingRecipe::new));

        @Override
        public MapCodec<DesalinatingRecipe> codec() {
            return CODEC;
        }

        public static final StreamCodec<RegistryFriendlyByteBuf, DesalinatingRecipe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT, DesalinatingRecipe::craftingTime,
                ByteBufCodecs.fromCodec(TagKey.codec(Registries.FLUID)), DesalinatingRecipe::fluid,
                ItemStack.STREAM_CODEC, DesalinatingRecipe::result,
                DesalinatingRecipe::new
        );

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, DesalinatingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
