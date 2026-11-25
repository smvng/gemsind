package com.somevanguy.gemsind.helpers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;

public class RenderHelper {
    public static float inQuintEase(float delta, float a, float b) {
        float m = 1f - (float) Math.pow(1 - delta, 5);//(float) Math.sin(Math.PI * delta / 2);
        return a + m * (b - a);
    }

    public static Quaternionf fromDegreesXZY(float rotX, float rotZ, float rotY) {
        float radX = (float) Math.toRadians(rotX) ;
        float radY = (float) Math.toRadians(rotY) ;
        float radZ = (float) Math.toRadians(rotZ) ;
        return new Quaternionf().rotationXYZ(radX, radY, radZ);
    }

    public static class DebugContainedList extends ArrayList<ItemStack> {
        public static final List<ItemStack> DEBUG_ITEMS = List.of(
                Items.LIME_STAINED_GLASS_PANE.getDefaultInstance(),
                Items.WHITE_STAINED_GLASS_PANE.getDefaultInstance(),
                Items.RED_STAINED_GLASS_PANE.getDefaultInstance(),
                Items.BLUE_STAINED_GLASS_PANE.getDefaultInstance(),
                Items.YELLOW_STAINED_GLASS_PANE.getDefaultInstance(),
                Items.CYAN_STAINED_GLASS_PANE.getDefaultInstance(),
                Items.MAGENTA_STAINED_GLASS_PANE.getDefaultInstance(),
                Items.BLACK_STAINED_GLASS_PANE.getDefaultInstance(),
                Items.ORANGE_STAINED_GLASS_PANE.getDefaultInstance()
        );

        @Override
        public ItemStack get(int index) {
            return DEBUG_ITEMS.get(index % DEBUG_ITEMS.size());
        }

        @Override
        public int size() {
            return DEBUG_ITEMS.size();
        }
    }

    public static ArrayList<ItemStack> getContainedDebug() {
        return new DebugContainedList();
    }

    public static void renderBox(PoseStack poseStack, VertexConsumer consumer,
                                 float minX, float minY, float minZ,
                                 float maxX, float maxY, float maxZ,
                                 float textureAtlasU0, float textureAtlasV0,
                                 float textureAtlasU1, float textureAtlasV1,
                                 int packedLight,
                                 float a, float r, float g, float b) {
        // up / maxY
        renderQuad(poseStack, consumer,
                maxX, maxY, minZ,
                minX, maxY, minZ,
                minX, maxY, maxZ,
                maxX, maxY, maxZ,
                textureAtlasU0, textureAtlasV0, textureAtlasU1, textureAtlasV1,
                packedLight,
                a, r, g, b);
        // down / minY
        renderQuad(poseStack, consumer,
                minX, minY, minZ,
                maxX, minY, minZ,
                maxX, minY, maxZ,
                minX, minY, maxZ,
                textureAtlasU0, textureAtlasV0, textureAtlasU1, textureAtlasV1,
                packedLight,
                a, r, g, b);
        // north / minZ
        renderQuad(poseStack, consumer,
                minX, maxY, minZ,
                maxX, maxY, minZ,
                maxX, minY, minZ,
                minX, minY, minZ,
                textureAtlasU0, textureAtlasV0, textureAtlasU1, textureAtlasV1,
                packedLight,
                a, r, g, b);
        // east / maxX
        renderQuad(poseStack, consumer,
                maxX, maxY, minZ,
                maxX, maxY, maxZ,
                maxX, minY, maxZ,
                maxX, minY, minZ,
                textureAtlasU0, textureAtlasV0, textureAtlasU1, textureAtlasV1,
                packedLight,
                a, r, g, b);
        // south / maxZ
        renderQuad(poseStack, consumer,
                maxX, maxY, maxZ,
                minX, maxY, maxZ,
                minX, minY, maxZ,
                maxX, minY, maxZ,
                textureAtlasU0, textureAtlasV0, textureAtlasU1, textureAtlasV1,
                packedLight,
                a, r, g, b);
        // west / minX
        renderQuad(poseStack, consumer,
                minX, maxY, maxZ,
                minX, maxY, minZ,
                minX, minY, minZ,
                minX, minY, maxZ,
                textureAtlasU0, textureAtlasV0, textureAtlasU1, textureAtlasV1,
                packedLight,
                a, r, g, b);
    }

    public static void renderQuad(PoseStack poseStack, VertexConsumer consumer,
                                 float x1, float y1, float z1,
                                 float x2, float y2, float z2,
                                 float x3, float y3, float z3,
                                 float x4, float y4, float z4,
                                 float u0, float v0, float u1, float v1,
                                  int packedLight,
                                 float a, float r, float g, float b) {
        consumer.addVertex(poseStack.last().pose(), x1, y1, z1)
                .setColor(r, g, b, a)
                .setUv(u0, v0)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(1, 0, 0);
        consumer.addVertex(poseStack.last().pose(), x2, y2, z2)
                .setColor(r, g, b, a)
                .setUv(u1, v0)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(1, 0, 0);
        consumer.addVertex(poseStack.last().pose(), x3, y3, z3)
                .setColor(r, g, b, a)
                .setUv(u1, v1)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(1, 0, 0);
        consumer.addVertex(poseStack.last().pose(), x4, y4, z4)
                .setColor(r, g, b, a)
                .setUv(u0, v1)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(1, 0, 0);
    }

    public static int getLightLevel(Level level, BlockPos pos) {
        int blockLight = level.getBrightness(LightLayer.BLOCK, pos);
        int skyLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(blockLight, skyLight);
    }
}
