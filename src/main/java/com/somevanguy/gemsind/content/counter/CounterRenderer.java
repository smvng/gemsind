package com.somevanguy.gemsind.content.counter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.somevanguy.gemsind.helpers.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import static com.somevanguy.gemsind.helpers.RenderHelper.getLightLevel;

public class CounterRenderer implements BlockEntityRenderer<CounterEntity> {
    public CounterRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(CounterEntity counterEntity, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, int packedOverlay) {
        if (!Minecraft.useFancyGraphics()) {
            return;
        }
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        var itemHandler = counterEntity.getItemHandler();
        var level = counterEntity.getLevel();
        if (itemHandler != null && level != null) {
            ItemStack stack = itemHandler.getStackInSlot(0);

            poseStack.pushPose();
            poseStack.translate(0.5f, 1.02f, 0.5f);
            poseStack.scale(0.75f, 0.75f, 0.75f);
            poseStack.mulPose(Axis.XP.rotationDegrees(90));
            itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED,
                    RenderHelper.getLightLevel(level, counterEntity.getBlockPos().above()),
                    OverlayTexture.NO_OVERLAY, poseStack, multiBufferSource, level, 1);
            poseStack.popPose();
        }
    }
}
