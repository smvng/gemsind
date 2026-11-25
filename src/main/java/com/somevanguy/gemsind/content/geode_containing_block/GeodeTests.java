package com.somevanguy.gemsind.content.geode_containing_block;

import com.somevanguy.gemsind.content.AllBlocks;
import com.somevanguy.gemsind.content.AllItems;
import com.somevanguy.gemsind.content.AllTags;
import com.somevanguy.gemsind.content.items.GeodeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class GeodeTests {
    public static boolean isExposed(ServerLevel serverLevel, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            BlockPos attachedPos = pos.relative(direction);
            BlockState attachedState = serverLevel.getBlockState(attachedPos);

            if (!attachedState.isFaceSturdy(serverLevel, attachedPos, direction.getOpposite())) {
                return true;
            }
        }
        return false;
    }

    public static final ArrayList<Test> TESTS = new ArrayList<>();

    public record TestContext(int pickPower, ServerLevel serverLevel, BlockPos pos, BlockState state) {}

    public record Test(DeferredItem<GeodeItem> targetGeodeHolder,
                       @Nullable Integer minY,
                       @Nullable Integer maxY,
                       @Nullable TagKey<Block> containingBlocks,
                       @Nullable TagKey<Biome> spawnsInBiome,
                       @NotNull ResourceKey<Level> dimension) {
        public Test {
            TESTS.add(this);
        }

        public boolean test(TestContext testContext, boolean ignoreExposed) {
            if (!ignoreExposed && isExposed(testContext.serverLevel, testContext.pos)) {
                return false;
            }
            if (testContext.serverLevel.dimension() != dimension) {
                return false;
            }
            if (minY != null && testContext.pos.getY() < minY) {
                return false;
            }
            if (maxY != null && testContext.pos.getY() > maxY) {
                return false;
            }
            if (spawnsInBiome != null && !testContext.serverLevel.getBiome(testContext.pos).is(spawnsInBiome)) {
                return false;
            }
            if (containingBlocks != null && !testContext.state.is(containingBlocks)) {
                return false;
            }
            return true;
        }

        public boolean test(TestContext testContext) {
            return test(testContext, false);
        }

        public boolean testAndReplace(TestContext testContext) {
            if (test(testContext)) {
                AllBlocks.GEODE_CONTAINING_BLOCK.get().replaceWith(testContext.serverLevel,
                        testContext.pos,testContext.state, targetGeodeHolder.get());
                return true;
            }
            return false;
        }
    }

    // found in granite
    public static final Test GRANITE_GEODE_TEST = new Test(
            AllItems.GRANITE_GEODE,
            -10,
            50,
            AllTags.PickTests.CONTAINS_GRANITE_GEODE,
            null,
            Level.OVERWORLD);

    // found in clay under rivers
    public static final Test RIVER_GEODE_TEST = new Test(
            AllItems.RIVER_GEODE,
            45,
            65,
            AllTags.PickTests.CONTAINS_RIVER_GEODE,
            AllTags.PickTests.SPAWNS_RIVER_GEODE,
            Level.OVERWORLD);

    // found in netherrack
    public static final Test NETHERRACK_GEODE_TEST = new Test(
            AllItems.NETHERRACK_GEODE,
            40,
            null,
            AllTags.PickTests.CONTAINS_NETHERRACK_GEODE,
            null,
            Level.NETHER);

    // found on top of mountains
    public static final Test SLATED_GEODE_TEST = new Test(
            AllItems.SLATED_GEODE,
            null,
            -40,
            AllTags.PickTests.CONTAINS_SLATED_GEODE,
            null,
            Level.OVERWORLD);

    // found on top of mountains
    public static final Test MOUNTAIN_GEODE_TEST = new Test(
            AllItems.MOUNTAIN_GEODE,
            135,
            null,
            AllTags.PickTests.CONTAINS_MOUNTAIN_GEODE,
            null,
            Level.OVERWORLD);

    // found in lava oceans in the nether
    public static final Test LAVA_GEODE_TEST = new Test(
            AllItems.LAVA_GEODE,
            null,
            24,
            AllTags.PickTests.CONTAINS_LAVA_GEODE,
            AllTags.PickTests.SPAWNS_LAVA_GEODE,
            Level.NETHER);

    // found under the end islands
    public static final Test UNEARTHLY_GEODE_TEST = new Test(
            AllItems.UNEARTHLY_GEODE,
            null,
            24,
            AllTags.PickTests.CONTAINS_UNEARTHLY_GEODE,
            null,
            Level.END);
}
