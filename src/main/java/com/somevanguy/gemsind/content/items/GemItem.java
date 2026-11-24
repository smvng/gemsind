package com.somevanguy.gemsind.content.items;

import com.somevanguy.gemsind.GemstoneIndustryMod;
import com.somevanguy.gemsind.content.AllDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class GemItem extends Item {
    public static final float GEM_GAIN_LIMIT = 1f;

    public boolean showFormula() {
        return formula != null && !formula.isEmpty();
    }

    public record GainInfo(boolean doWarn, int baseValue, float maxPurityGain, float maxPolishGain) {
        public GainInfo(int baseValue, float maxPurityGain, float maxPolishGain) {
            this(true, baseValue, maxPurityGain, maxPolishGain);
        }

        public GainInfo(boolean doWarn, int baseValue, float maxPurityGain, float maxPolishGain) {
            this.doWarn = doWarn;
            this.baseValue = baseValue;
            this.maxPurityGain = maxPurityGain;
            this.maxPolishGain = maxPolishGain;
            if (doWarn) {
                if (maxPurityGain + maxPolishGain != GEM_GAIN_LIMIT) {
                    GemstoneIndustryMod.LOGGER.warn("A gem with maxPurityGain of {} and maxPolishGain {} doesn't add up to {}", maxPurityGain, maxPolishGain, GEM_GAIN_LIMIT);
                }
            }
        }
    }

    // separate for each GemItem instance (HALITE, MALACHITE, etc)
    public GainInfo gainInfo;
    public String name;
    public String formula;
    public int maxAcidTolerance;

    public static final List<String> PROMINENT_ELEMENTS = List.of(
            "??", "Fe", "Si", "Au", "Ag", "Cu", "As", "Co", "Al", "Ti", "Ba", "Cr", "Ni", "Os", "P", "C", "Pt"
    );

    private static final String alphaRegex = "[A-Za-z]+";
    public boolean hasElement(String element) {
        var arr = formula.toCharArray();
        if (element.length() == 1) {
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] == element.charAt(0)) {
                    if (i + 1 == arr.length) {
                        return true;
                    }
                    var n = String.valueOf(arr[i + 1]);
                    if (n.equals(n.toUpperCase())) {
                        return true;
                    }
                }
            }
        } else if (element.length() == 2) {
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] == element.charAt(0)) {
                    if (i + 1 == arr.length) {
                        return false;
                    }
                    if (arr[i+1] == element.charAt(1)) {
                        if (i + 2 == arr.length) {
                            return true;
                        }
                        var n = String.valueOf(arr[i + 2]);
                        if (n.equals(n.toUpperCase())) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public static String safeElementName(String element) {
        return element.equals("??") ? "unknown" : element.toLowerCase();
    }

    public boolean isFlux() {
        return (hasElement("C") && hasElement("O")) // carbonate
                || (hasElement("Si") && hasElement("O")) // silica
                || (hasElement("Cl")); // chloride
    }

    public GemItem(String name, Properties properties, String formula, int acidTolerance, GainInfo gainInfo) {
        this(name, properties, gainInfo);
        this.formula = formula;
        this.maxAcidTolerance = acidTolerance;
        if (formula.isEmpty()) {
            GemstoneIndustryMod.LOGGER.warn("{} has no formula", name);
        }
    }

    public GemItem(String name, Properties properties, GainInfo gainInfo) {
        super(properties
                .component(AllDataComponents.GEM_PURITY, 0f)
                .component(AllDataComponents.GEM_POLISH, 0f)
                .component(AllDataComponents.GEM_IMBUEMENTS, 0)
                .component(AllDataComponents.GEM_BONUS_VALUE, 0)
                .component(AllDataComponents.ACIDITY, 0)
                .component(AllDataComponents.CENTRIFUGE_USED, false)
        );
        this.name = name;
        this.gainInfo = gainInfo;
    }

    public int getValue(ItemStack stack) {
        var purity = stack.getOrDefault(AllDataComponents.GEM_PURITY, 0f);
        var polish = stack.getOrDefault(AllDataComponents.GEM_POLISH, 0f);

        var purityGain = gainInfo.maxPurityGain * purity;
        var polishGain = gainInfo.maxPolishGain * polish;

        var gain = gainInfo.baseValue * (purityGain+polishGain);
        var bonusGain = stack.getOrDefault(AllDataComponents.GEM_BONUS_VALUE, 0);

        return (int)(gainInfo.baseValue + gain + bonusGain);
    }

    public static final ChatFormatting MAX_STAT_FORMAT = ChatFormatting.LIGHT_PURPLE;
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        int value = getValue(stack);
        float purity = stack.getOrDefault(AllDataComponents.GEM_PURITY, 0f);
        float polish = stack.getOrDefault(AllDataComponents.GEM_POLISH, 0f);

        tooltipComponents.add(Component.literal(value+"$").withStyle(ChatFormatting.GRAY));

        tooltipComponents.add(Component.translatable("item.gemsind.gem.purity")
                .withStyle(ChatFormatting.GRAY)
                .append(Component.literal((int)(purity*100)+"%")
                        .withStyle(purity >= 1f ? MAX_STAT_FORMAT : ChatFormatting.GRAY)));

        tooltipComponents.add(Component.translatable("item.gemsind.gem.polish")
                .withStyle(ChatFormatting.GRAY)
                .append(Component.literal((int)(polish*100)+"%")
                        .withStyle(polish >= 1f ? MAX_STAT_FORMAT : ChatFormatting.GRAY)));

        tooltipComponents.add(Component.translatable("item.gemsind.gem.acid_tolerance")
                .append(Component.literal(String.valueOf(maxAcidTolerance)))
                .withStyle(ChatFormatting.GRAY));

        if (showFormula()) {
            tooltipComponents.add(Component.literal(formula).withStyle(ChatFormatting.GRAY));
        }

        if (isFlux()) {
            tooltipComponents.add(Component.translatable("item.gemsind.gem.flux").withStyle(ChatFormatting.GRAY));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
