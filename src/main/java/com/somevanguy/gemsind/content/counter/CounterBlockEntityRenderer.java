package com.somevanguy.gemsind.content.counter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

public class CounterBlockEntityRenderer implements BlockEntityRenderer<CounterBlockEntity> {
    public CounterBlockEntityRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(CounterBlockEntity counterBlockEntity, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, int packedOverlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        var itemHandler = counterBlockEntity.getItemHandler();
        var level = counterBlockEntity.getLevel();
        if (itemHandler != null && level != null) {
            ItemStack stack = itemHandler.getStackInSlot(0);

            poseStack.pushPose();
            poseStack.translate(0.5f, 1.025f, 0.5f);
            poseStack.scale(0.5f, 0.5f, 0.5f);
            poseStack.mulPose(Axis.XP.rotationDegrees(90));
            itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED,
                    getLightLevel(level, counterBlockEntity.getBlockPos().above()),
                    OverlayTexture.NO_OVERLAY, poseStack, multiBufferSource, level, 1);
            poseStack.popPose();
        }
    }

    private int getLightLevel(Level level, BlockPos pos) {
        int blockLight = level.getBrightness(LightLayer.BLOCK, pos);
        int skyLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(blockLight, skyLight);
    }
}
