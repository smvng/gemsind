package com.somevanguy.gemsind.helpers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

public class RenderHelper {
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
                .setLight(packedLight)
                .setNormal(1, 0, 0);
        consumer.addVertex(poseStack.last().pose(), x2, y2, z2)
                .setColor(r, g, b, a)
                .setUv(u1, v0)
                .setLight(packedLight)
                .setNormal(1, 0, 0);
        consumer.addVertex(poseStack.last().pose(), x3, y3, z3)
                .setColor(r, g, b, a)
                .setUv(u1, v1)
                .setLight(packedLight)
                .setNormal(1, 0, 0);
        consumer.addVertex(poseStack.last().pose(), x4, y4, z4)
                .setColor(r, g, b, a)
                .setUv(u0, v1)
                .setLight(packedLight)
                .setNormal(1, 0, 0);
    }
}
