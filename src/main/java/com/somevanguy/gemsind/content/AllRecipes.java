package com.somevanguy.gemsind.content;

import com.somevanguy.gemsind.GemstoneIndustryMod;
import com.somevanguy.gemsind.content.alloying_furnace.AlloyingRecipe;
import com.somevanguy.gemsind.content.desalinator.DesalinatingRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AllRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, GemstoneIndustryMod.MODID);
    public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, GemstoneIndustryMod.MODID);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AlloyingRecipe>> ALLOYING_SERIALIZER = SERIALIZERS.register("alloying", AlloyingRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<AlloyingRecipe>> ALLOYING_TYPE = TYPES.register("alloying", () -> new RecipeType<AlloyingRecipe>() {
        @Override
        public String toString() {
            return "alloying";
        }
    });

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<DesalinatingRecipe>> DESALINATING_SERIALIZER = SERIALIZERS.register("desalinating", DesalinatingRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<DesalinatingRecipe>> DESALINATING_TYPE = TYPES.register("desalinating", () -> new RecipeType<DesalinatingRecipe>() {
        @Override
        public String toString() {
            return "desalinating";
        }
    });
}
