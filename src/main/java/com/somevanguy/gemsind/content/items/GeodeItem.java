package com.somevanguy.gemsind.content.items;

import com.somevanguy.gemsind.GemstoneIndustryMod;
import com.somevanguy.gemsind.content.AllDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.List;

public class GeodeItem extends Item {
    private static final int EXPECTED_DROP_COUNT = 4;
    public final int toughness;
    public final String name;
    public final String defaultLootTableName;
    private final List<DeferredItem<?>> defaultDrops; // should only be used by the datagen/tooltip
    public Item bestDrop;

    // should not be used outside datagen
    public List<DeferredItem<?>> getDatagenLootDrops() {
        return defaultDrops;
    }

    public LootTable getLootTable(ServerLevel level, ItemStack stack) {
        var lootTableName = stack.getOrDefault(AllDataComponents.GEODE_LOOT_TABLE, defaultLootTableName);
        ResourceLocation location = ResourceLocation.parse(lootTableName);
        ResourceKey<LootTable> key = ResourceKey.create(Registries.LOOT_TABLE, location);
        return level.getServer().reloadableRegistries().getLootTable(key);
    }

    public GeodeItem(Properties properties, String name, int toughness, List<DeferredItem<?>> drops) {
        super(properties
                .fireResistant()
                .component(AllDataComponents.GEODE_LOOT_TABLE, GemstoneIndustryMod.MODID+":geode_drops/"+name)
                .component(AllDataComponents.GEODE_CRACK_PROGRESS, 0f)
        );
        this.name = name;
        this.toughness = toughness;
        defaultLootTableName = GemstoneIndustryMod.MODID+":geode_drops/"+name;
        defaultDrops = drops;
        var highestValue = 0;
        for (DeferredItem<?> drop : drops) {
            if (drop.get() instanceof ValuableItem valuableItem) {
                if (valuableItem.gainInfo.baseValue() > highestValue) {
                    highestValue = valuableItem.gainInfo.baseValue();
                    bestDrop = valuableItem;
                }
            }
        }
        if (drops.size() != EXPECTED_DROP_COUNT) {
            GemstoneIndustryMod.LOGGER.warn("{} doesn't have the expected drop count ({})", name, EXPECTED_DROP_COUNT);
        }
    }

    public ItemStack getDrop(ItemStack geode, ServerLevel serverLevel, ServerPlayer serverPlayer) {
        LootParams params = new LootParams.Builder(serverLevel)
                .withParameter(LootContextParams.THIS_ENTITY, serverPlayer)
                .withParameter(LootContextParams.ORIGIN, serverPlayer.position())
                .create(LootContextParamSets.GIFT);
        LootTable lootTable = getLootTable(serverLevel, geode);
        List<ItemStack> loot = lootTable.getRandomItems(params);
        if (!loot.isEmpty()) {
            return loot.getFirst();
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (bestDrop instanceof GemItem gemDrop) {
            tooltipComponents.add(Component.translatable("item.gemsind.geode.best_drop")
                    .append(gemDrop.getName(gemDrop.getDefaultInstance()))
                    .append(Component.literal(" ("+gemDrop.gainInfo.baseValue()+"$)"))
                    .withStyle(ChatFormatting.GRAY));
        }
        //if (Screen.hasShiftDown()) {
        tooltipComponents.add(Component.translatable("item.gemsind."+ name +".tooltip")
                .withStyle(ChatFormatting.GRAY));
        //} else {
        //    tooltipComponents.add(Component.translatable("item.gemsind.gem.holdshifttoexpand"));
        //}
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    //    @Override
//    public @NotNull InteractionResult useOn(UseOnContext context) {
//        Level level = context.getLevel();
//        if (!level.isClientSide()) {
//            ServerLevel serverLevel = (ServerLevel) level;
//            ServerPlayer serverPlayer = (ServerPlayer) context.getPlayer();
//            if (serverPlayer != null) {
//                ItemStack stack = context.getItemInHand();
//                LootParams params = new LootParams.Builder(serverLevel)
//                        .withParameter(LootContextParams.THIS_ENTITY, serverPlayer)
//                        .withParameter(LootContextParams.ORIGIN, serverPlayer.position())
//                        .create(LootContextParamSets.GIFT);
//                LootTable lootTable = getLootTable(serverLevel, stack);
//                List<ItemStack> loot = lootTable.getRandomItems(params);
//                if (!loot.isEmpty()) {
//                    ItemStack drop = loot.getFirst();
//                    if (!serverPlayer.addItem(drop)) {
//                        ItemEntity dropEntity = new ItemEntity(serverLevel, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), drop);
//                        serverLevel.addFreshEntity(dropEntity);
//                    }
//                }
//                stack.consume(1, serverPlayer);
//            }
//        }
//        return InteractionResult.CONSUME;
//    }
}
