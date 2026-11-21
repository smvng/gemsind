package com.somevanguy.gemsind.content.items;

import com.somevanguy.gemsind.content.geode_containing_block.GeodeContainingBlock;
import com.somevanguy.gemsind.content.geode_containing_block.GeodeTests;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;

import java.util.List;

import static com.somevanguy.gemsind.content.geode_containing_block.GeodeTests.isExposed;

public class PickItem extends Item {
    //public record DetectedGeode(int extractionLevel, BlockPos pos, GeodeItem geode) {}
    //public static final RolloverList<DetectedGeode> DETECTED_GEODES = new RolloverList<>(10);
    public static final ImprovedNoise noise = new ImprovedNoise(RandomSource.create(9998));
    public static final double threshold = -0.8; // [-1.0, 1.0] any value below it qualifies as valid for geode

    public final boolean usable;
    public final int power;
    public final int areaRadius;

    public PickItem(boolean usable, int power, int areaRadius, Properties properties) {
        super(properties);
        this.usable = usable;
        this.power = power;
        this.areaRadius = areaRadius;
    }

    public PickItem(int power, int areaRadius, Properties properties)  {
        this(true, power, areaRadius, properties);
    }

    public int getPickPower() {
        return power; // TODO:CHANGE
    }

    // returns success
    public boolean trySpawnGeode(ServerLevel serverLevel, Direction direction, BlockPos hitBlockPos) {
        if (serverLevel.getBlockState(hitBlockPos).getBlock() instanceof GeodeContainingBlock) {
            return true;
        }

        BlockPos areaCenter = hitBlockPos.relative(direction, areaRadius);
        BlockPos areaMin = areaCenter.offset(-areaRadius, -areaRadius, -areaRadius);
        BlockPos areaMax = areaCenter.offset(areaRadius, areaRadius, areaRadius);

        GeodeContainingBlock pickedGeodeBlock = null;
        BlockPos pickedGeodeSpawn = BlockPos.ZERO;
        for (BlockPos pos : BlockPos.betweenClosed(areaMin, areaMax)) {
            if (isExposed(serverLevel, pos)) {
                continue;
            }
            GeodeTests.TestContext testContext = new GeodeTests.TestContext(getPickPower(), serverLevel, pos, serverLevel.getBlockState(pos));
            for (GeodeTests.Test geodeTest : GeodeTests.TESTS) {
                if (geodeTest.testAndReplace(testContext)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (usable) {
            if (Screen.hasShiftDown()) {
                tooltipComponents.add(Component.translatable("item.gemsind.pick.tooltip"));
            } else {
                tooltipComponents.add(Component.translatable("item.gemsind.gem.holdshifttoexpand"));
            }
        } else {
            if (Screen.hasShiftDown()) {
                tooltipComponents.add(Component.translatable("item.gemsind.pick.not_usable_tooltip"));
            } else {
                tooltipComponents.add(Component.translatable("item.gemsind.gem.holdshifttoexpand"));
            }
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
