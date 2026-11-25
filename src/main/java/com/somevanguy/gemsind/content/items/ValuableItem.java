package com.somevanguy.gemsind.content.items;

import com.somevanguy.gemsind.GemstoneIndustryMod;
import com.somevanguy.gemsind.content.AllDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public abstract class ValuableItem extends Item {
    public final String name;
    public final GainInfo gainInfo;
    public int maxAcidTolerance;

    public ValuableItem(String name, GainInfo gainInfo, float expectedTotalGain, int maxAcidTolerance, Properties properties) {
        super(properties
                .component(AllDataComponents.PURITY, 0f)
                .component(AllDataComponents.POLISH, 0f)
                .component(AllDataComponents.IMBUEMENTS, 0)
                .component(AllDataComponents.BONUS_VALUE, 0)
                .component(AllDataComponents.ACIDITY, 0)
                .component(AllDataComponents.CENTRIFUGE_USED, false)
        );
        this.name = name;
        this.gainInfo = gainInfo;
        this.maxAcidTolerance = maxAcidTolerance;
        gainInfo.test(name, expectedTotalGain);
    }

    public int getValue(ItemStack stack) {
        var purity = stack.getOrDefault(AllDataComponents.PURITY, 0f);
        var polish = stack.getOrDefault(AllDataComponents.POLISH, 0f);

        var purityGain = gainInfo.maxPurityGain * purity;
        var polishGain = gainInfo.maxPolishGain * polish;

        var gain = gainInfo.baseValue * (purityGain+polishGain);
        var bonusGain = stack.getOrDefault(AllDataComponents.BONUS_VALUE, 0);

        return (int)(gainInfo.baseValue + gain + bonusGain);
    }

    public static final ChatFormatting BASIC_STAT_FORMAT = ChatFormatting.GRAY;
    public static final ChatFormatting MAX_STAT_FORMAT = ChatFormatting.LIGHT_PURPLE;
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        int value = getValue(stack);
        float purity = stack.getOrDefault(AllDataComponents.PURITY, 0f);
        float polish = stack.getOrDefault(AllDataComponents.POLISH, 0f);

        tooltipComponents.add(Component.literal(value+"$").withStyle(BASIC_STAT_FORMAT));

        tooltipComponents.add(Component.translatable("item.gemsind.gem.purity")
                .withStyle(BASIC_STAT_FORMAT)
                .append(Component.literal((int)(purity*100)+"%")
                        .withStyle(purity >= 1f ? MAX_STAT_FORMAT : BASIC_STAT_FORMAT)));

        tooltipComponents.add(Component.translatable("item.gemsind.gem.polish")
                .withStyle(BASIC_STAT_FORMAT)
                .append(Component.literal((int)(polish*100)+"%")
                        .withStyle(polish >= 1f ? MAX_STAT_FORMAT : BASIC_STAT_FORMAT)));

        tooltipComponents.add(Component.translatable("item.gemsind.gem.acid_tolerance")
                .append(Component.literal(String.valueOf(maxAcidTolerance)))
                .withStyle(BASIC_STAT_FORMAT));
    }

    public record GainInfo(int baseValue, float maxPurityGain, float maxPolishGain) {
        private void test(String itemName, float expectedTotalGain) {
            if (this.maxPurityGain + this.maxPolishGain != expectedTotalGain) {
                GemstoneIndustryMod.LOGGER.info("{}'s maxPurityGain ({}) and maxPolishGain ({}) don't add up to expected gain {}",
                        itemName, this.maxPurityGain, this.maxPolishGain, expectedTotalGain);
            }
        }
    }
}
