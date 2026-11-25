package com.somevanguy.gemsind.content.items;

import com.somevanguy.gemsind.content.AllItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.EffectCures;

import javax.print.DocFlavor;
import java.util.List;

public class AcidPackItem extends Item {
    public static final int MAX_ACID_STRENGTH = 3;
    public final int acidStrength;
    public final int RGB;
    public final int r; // 0-255
    public final int g;
    public final int b;
    public static final int DULL_R = 255;
    public static final int DULL_G = 255;
    public static final int DULL_B = 255;

    public AcidPackItem(Properties properties, int acidStrength, int r, int g, int b) {
        super(properties.food(new FoodProperties.Builder().nutrition(0).saturationModifier(0).alwaysEdible().build()));
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
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 45;
    }

    @Override
    public SoundEvent getDrinkingSound() {
        return SoundEvents.GENERIC_DRINK;
    }

    @Override
    public SoundEvent getEatingSound() {
        return SoundEvents.GENERIC_DRINK;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        livingEntity.hurt(livingEntity.damageSources().lava(), 25*this.acidStrength);
        return super.finishUsingItem(stack, level, livingEntity);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("item.gemsind.acid_pack.strength")
                .append(Component.literal(String.valueOf(acidStrength)))
                .withStyle(ChatFormatting.GRAY));

        if (Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("item.gemsind.acid_pack.tooltip").withStyle(ChatFormatting.GRAY));
        } else {
            tooltipComponents.add(Component.translatable("item.gemsind.gem.holdshifttoexpand").withStyle(ChatFormatting.GRAY));
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
