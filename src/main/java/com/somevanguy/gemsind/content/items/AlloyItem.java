package com.somevanguy.gemsind.content.items;

import com.somevanguy.gemsind.GemstoneIndustryMod;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
public class AlloyItem extends GemItem {
    public final Crafting crafting;

    @Override
    public boolean isFlux() {
        return false;
    }

    public AlloyItem(String name, Properties properties, Crafting crafting, GainInfo info) {
        super(name, properties, info);
        this.maxAcidTolerance = 3;
        this.crafting = crafting;
        if (gainInfo.maxPolishGain() + gainInfo.maxPurityGain() != 2.5f) {
            GemstoneIndustryMod.LOGGER.warn("{} maxPurityGain and maxPolish gain don't add up to {}", name, 2.5f);
        }
    }

    public static int getBonusValue(ItemStack result, ArrayList<ItemStack> elementsAndFluxes) {
        //int baseValue = ((AlloyItem)result.getItem()).gainInfo.baseValue();
        int bonusValue = 0;
        for (ItemStack ingredient : elementsAndFluxes)  {
            int thisValue = ((GemItem)ingredient.getItem()).getValue(ingredient);
            bonusValue += thisValue;
        }
        bonusValue = (int)(bonusValue * 1.15);
        return bonusValue;
    }

    public record Crafting(int minFluxCount, int craftingTime, String el1, String el2, @Nullable String el3, @Nullable String el4) {
        public @NotNull ArrayList<String> getRequiredElements() {
            var list = new ArrayList<String>();
            list.add(el1);
            list.add(el2);
            if (el3 != null) {
                list.add(el3);
            }
            if (el4 != null) {
                list.add(el4);
            }
            return list;
        }

        public Crafting {
            minFluxCount = Mth.clamp(minFluxCount, 1, 4);
            craftingTime = Mth.clamp(craftingTime, 1, 86400*20);
        }

        public Crafting(int minFluxCount, int alloyingTime, String el1, String el2) {
            this(minFluxCount, alloyingTime, el1, el2, null, null);
        }

        public Crafting(int minFluxCount, int alloyingTime, String el1, String el2, String el3) {
            this(minFluxCount, alloyingTime, el1, el2, el3, null);
        }
    };
}
