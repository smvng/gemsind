package com.somevanguy.gemsind;

import com.somevanguy.gemsind.content.AllBlockEntities;
import com.somevanguy.gemsind.content.AllBlocks;
import com.somevanguy.gemsind.content.counter.CounterBlock;
import com.somevanguy.gemsind.content.items.HammerItem;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = GemstoneIndustryMod.MODID)
public class AllEvents {

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                AllBlockEntities.COUNTER_ENTITY.get(),
                (blockEntity, side) -> blockEntity.getAutomationItemHandler()
        );

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                AllBlockEntities.ACID_PURIFIER_ENTITY.get(),
                (blockEntity, side) -> blockEntity.getAutomationItemHandler()
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                AllBlockEntities.CENTRIFUGE_ENTITY.get(),
                (blockEntity, side) -> blockEntity.getAutomationItemHandler()
        );
//        event.registerBlockEntity( // TODO: TUMBLER CAPABILITY
//                Capabilities.ItemHandler.BLOCK,
//                AllBlockEntities.TUMBLER_ENTITY.get(),
//                (blockEntity, side) -> blockEntity.getAutomationItemHandler()
//        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                AllBlockEntities.ALLOYING_FURNACE_ENTITY.get(),
                (blockEntity, side) -> blockEntity.getAutomationItemHandler()
        );
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        var level = event.getLevel();
        BlockState blockState = level.getBlockState(event.getPos());
        Player player = event.getEntity();
        ItemStack heldItem = player.getMainHandItem();
        var counterBlock = AllBlocks.COUNTER.get();
        if (blockState.is(counterBlock)) {
            if (event.getFace() == Direction.UP) {
                if (heldItem.getItem() instanceof HammerItem hammerItem) {
                    if (!event.getEntity().getCooldowns().isOnCooldown(hammerItem)) {
                        if (!level.isClientSide()) {
                            counterBlock.onHammerHit(event.getPos(), heldItem, (ServerLevel) level, (ServerPlayer) player);
                        }
                        event.setCanceled(true);
                        event.setCancellationResult(InteractionResult.SUCCESS);
                    }
                }
            }
        }
    }
}
