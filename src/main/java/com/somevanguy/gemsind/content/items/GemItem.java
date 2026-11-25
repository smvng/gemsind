package com.somevanguy.gemsind.content.items;

import com.somevanguy.gemsind.GemstoneIndustryMod;
import com.somevanguy.gemsind.content.AllDataComponents;
import com.sun.jna.platform.unix.solaris.LibKstat;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GemItem extends ValuableItem {
    public String formula;
    private String owner;

    public void trySetOwner(String owner) {
        if (this.owner.isEmpty()) {
            this.owner = owner;
        }
    }

    public String getOwner() {
        return this.owner;
    }

    public static final List<String> PROMINENT_ELEMENTS = List.of(
            "??", "Fe", "Si", "Au", "Ag", "Cu", "As", "Co", "Al", "Ti", "Ba", "Cr", "Ni", "Os", "P", "C", "Pt"
    );

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

    public GemItem(String name, Properties properties, String formula, int maxAcidTolerance, GainInfo gainInfo) {
        super(name, gainInfo, 1f, maxAcidTolerance, properties);
        this.formula = formula;
        if (formula.isEmpty()) {
            GemstoneIndustryMod.LOGGER.warn("{} has no formula", name);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        tooltipComponents.add(Component.literal(formula).withStyle(BASIC_STAT_FORMAT));

        if (isFlux()) {
            tooltipComponents.add(Component.translatable("item.gemsind.gem.flux").withStyle(BASIC_STAT_FORMAT));
        }
    }
}
