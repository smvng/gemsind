package com.somevanguy.gemsind.content.items;

import com.somevanguy.gemsind.content.AllItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class AcidPackItem extends Item {
    public final int acidStrength;
    public final int RGB;
    public final int r; // 0-255
    public final int g;
    public final int b;

    public AcidPackItem(Properties properties, int acidStrength, int r, int g, int b) {
        super(properties);
        this.acidStrength = acidStrength;
        this.r = r;
        this.g = g;
        this.b = b;
        // 0xRRGGBB
        this.RGB =  r << 16 | g << 8 | b;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
        return AllItems.EMPTY_ACID_PACK.toStack();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("item.gemsind.acid_pack.strength")
                .append(Component.literal(String.valueOf(acidStrength)))
                .withStyle(ChatFormatting.GRAY));

        if (Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("item.gemsind.acid_pack.tooltip"));
        } else {
            tooltipComponents.add(Component.translatable("item.gemsind.gem.holdshifttoexpand"));
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
