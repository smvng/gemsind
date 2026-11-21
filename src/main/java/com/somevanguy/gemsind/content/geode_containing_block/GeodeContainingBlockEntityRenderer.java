package com.somevanguy.gemsind.content.geode_containing_block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

// i dont know how to fix the lighting issues this is driving me insane
@Deprecated
public class GeodeContainingBlockEntityRenderer implements BlockEntityRenderer<GeodeContainingBlockEntity> {
    private final BlockRenderDispatcher blockRenderer;

    public GeodeContainingBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        blockRenderer = context.getBlockRenderDispatcher();
    }

    public void renderBlock(BlockState state, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        this.blockRenderer.renderSingleBlock(state, poseStack, multiBufferSource, packedLight, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.CUTOUT);
    }

    @Override
    public void render(GeodeContainingBlockEntity geodeContainingBlockEntity, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, int packedOverlay) {

        BlockState replacementState = geodeContainingBlockEntity.getReplacementState();
        var level = geodeContainingBlockEntity.getLevel();
        if (replacementState != null && level != null) {
            var pos = geodeContainingBlockEntity.getBlockPos();

            poseStack.pushPose();
            renderBlock(replacementState, poseStack, multiBufferSource, packedLight);//getLightLevel(level, pos));
            poseStack.popPose();

//            int lightLevelTop = getLightLevel(level, pos.above());
//            poseStack.pushPose();
//            poseStack.translate(0f, 1f, 0f);
//            poseStack.scale(1f, 0.001f, 1f);
//            blockRenderer.renderSingleBlock(replacementState, poseStack, multiBufferSource,
//                    lightLevelTop, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.SOLID);
//            poseStack.popPose();
//
//            int lightLevelBottom = getLightLevel(level, pos.above());
//            poseStack.pushPose();
//            poseStack.translate(0f, 0f, 0f);
//            poseStack.scale(1f, 0.001f, 1f);
//            blockRenderer.renderSingleBlock(replacementState, poseStack, multiBufferSource,
//                    lightLevelBottom, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.SOLID);
//            poseStack.popPose();
//
//            int lightLevelNorth = getLightLevel(level, pos.north());
//            poseStack.pushPose();
//            poseStack.translate(0f, 0f, 0f);
//            poseStack.scale(1f, 1f, 0.001f);
//            blockRenderer.renderSingleBlock(replacementState, poseStack, multiBufferSource,
//                    lightLevelNorth, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.SOLID);
//            poseStack.popPose();
//
//            int lightLevelEast = getLightLevel(level, pos.east());
//            poseStack.pushPose();
//            poseStack.translate(1f, 0f, 0f);
//            poseStack.scale(0.001f, 1f, 1f);
//            blockRenderer.renderSingleBlock(replacementState, poseStack, multiBufferSource,
//                    lightLevelEast, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.SOLID);
//            poseStack.popPose();
//
//            int lightLevelSouth = getLightLevel(level, pos.south());
//            poseStack.pushPose();
//            poseStack.translate(0f, 0f, 1f);
//            poseStack.scale(1f, 1f, 0.001f);
//            blockRenderer.renderSingleBlock(replacementState, poseStack, multiBufferSource,
//                    lightLevelSouth, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.SOLID);
//            poseStack.popPose();
//
//            int lightLevelWest = getLightLevel(level, pos.west());
//            poseStack.pushPose();
//            poseStack.translate(0f, 0f, 0f);
//            poseStack.scale(0.001f, 1f, 1f);
//            blockRenderer.renderSingleBlock(replacementState, poseStack, multiBufferSource,
//                    lightLevelWest, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.SOLID);
//            poseStack.popPose();
        }
    }

    private int getLightLevel(Level level, BlockPos pos) {
        int blockLight = level.getBrightness(LightLayer.BLOCK, pos);
        int skyLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(blockLight, skyLight);
    }
}
