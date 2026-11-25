package com.somevanguy.gemsind.content.items;

import com.somevanguy.gemsind.GemstoneIndustryMod;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
public class AlloyItem extends ValuableItem {
    public final CraftingData crafting;

    public AlloyItem(String name, Properties properties, CraftingData craftingData, GainInfo info) {
        super(name, info, 2.5f, 3, properties);
        this.crafting = craftingData;
    }

    public static int getBonusValue(ArrayList<ItemStack> elementsAndFluxes) {
        int bonusValue = 0;
        for (ItemStack stack : elementsAndFluxes)  {
            int thisValue = ((ValuableItem)stack.getItem()).getValue(stack);
            bonusValue += thisValue;
        }
        bonusValue = (int)(bonusValue * 1.05);
        return bonusValue;
    }

    public record CraftingData(int minFluxCount, int craftingTime, String el1, String el2, @Nullable String el3, @Nullable String el4) {
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

        public CraftingData {
            minFluxCount = Mth.clamp(minFluxCount, 1, 4);
            craftingTime = Mth.clamp(craftingTime, 1, 86400*20);
        }

        public CraftingData(int minFluxCount, int alloyingTime, String el1, String el2) {
            this(minFluxCount, alloyingTime, el1, el2, null, null);
        }

        public CraftingData(int minFluxCount, int alloyingTime, String el1, String el2, String el3) {
            this(minFluxCount, alloyingTime, el1, el2, el3, null);
        }
    };
}
