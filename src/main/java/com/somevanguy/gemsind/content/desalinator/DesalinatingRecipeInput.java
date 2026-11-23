package com.somevanguy.gemsind.content.desalinator;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.material.FluidState;

// inventory of an existing desalinator
public record DesalinatingRecipeInput(FluidState fluidState) implements RecipeInput {
    @Override
    public ItemStack getItem(int i) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }
}
