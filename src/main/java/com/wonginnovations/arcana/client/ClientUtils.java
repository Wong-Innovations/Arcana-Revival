package com.wonginnovations.arcana.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Transformation;
import com.wonginnovations.arcana.client.gui.CompletePuzzleToast;
import com.wonginnovations.arcana.client.gui.ResearchBookScreen;
import com.wonginnovations.arcana.client.gui.ResearchEntryScreen;
import com.wonginnovations.arcana.client.gui.ScribbledNoteScreen;
import com.wonginnovations.arcana.event.ResearchEvent;
import com.wonginnovations.arcana.systems.research.ResearchBooks;
import com.wonginnovations.arcana.systems.research.ResearchEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeRenderTypes;
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.pipeline.QuadBakingVertexConsumer;
import net.minecraftforge.client.model.pipeline.TransformingVertexPipeline;

import javax.annotation.Nullable;
import java.util.*;

@OnlyIn(Dist.CLIENT)
public class ClientUtils {

    private static final Direction[] HORIZONTALS = {Direction.UP, Direction.DOWN};
    private static final Direction[] VERTICALS = {Direction.WEST, Direction.EAST};

    public static void openResearchBookUI(ResourceLocation book, Screen parentScreen, ItemStack sender) {
        if (!ResearchBooks.disabled.contains(book))
            Minecraft.getInstance().setScreen(new ResearchBookScreen(ResearchBooks.books.get(book), parentScreen, sender));
        else
            Minecraft.getInstance().player.sendSystemMessage(Component.translatable("message.arcana.disabled").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
    }

    public static void openScribbledNotesUI() {
        Minecraft.getInstance().setScreen(new ScribbledNoteScreen(Component.literal("")));
    }

    public static void displayPuzzleToast(@Nullable ResearchEntry entry) {
        Minecraft.getInstance().getToasts().addToast(new CompletePuzzleToast(entry));
    }

    public static void onResearchChange(ResearchEvent event) {
        if (Minecraft.getInstance().screen instanceof ResearchEntryScreen)
            ((ResearchEntryScreen)Minecraft.getInstance().screen).updateButtons();
    }

    /** Gets the default render type for an item layer */
    public static RenderTypeGroup getDefaultRenderType(IGeometryBakingContext context) {
        ResourceLocation renderTypeHint = context.getRenderTypeHint();
        if (renderTypeHint != null) {
            return context.getRenderType(renderTypeHint);
        } else {
            return new RenderTypeGroup(RenderType.translucent(), ForgeRenderTypes.ITEM_UNSORTED_TRANSLUCENT.get());
        }
    }

    public static List<BakedQuad> getQuadsForSprite(TextureAtlasSprite sprite, Transformation transform) {
        return getQuadsForSprite(0, sprite, transform);
    }

    public static List<BakedQuad> getQuadsForSprite(int tint, TextureAtlasSprite sprite, Transformation transform) {
        return getQuadsForSprite(0xFFFFFFFF, tint, sprite, transform, 0);
    }

    public static List<BakedQuad> getQuadsForSprite(int color, int tint, TextureAtlasSprite sprite, Transformation transform, int emissivity) {
        List<BakedQuad> builder = new ArrayList<>();

        SpriteContents contents = sprite.contents();
        int uMax = contents.width();
        int vMax = contents.height();
        FaceData faceData = new FaceData(uMax, vMax);
        boolean translucent = false;

        PrimitiveIterator.OfInt iterator = sprite.contents().getUniqueFrames().iterator();
        boolean hasFrames = iterator.hasNext();
        while (iterator.hasNext()) {
            int f = iterator.nextInt();
            boolean ptu;
            boolean[] ptv = new boolean[uMax];
            Arrays.fill(ptv, true);
            for(int v = 0; v < vMax; v++) {
                ptu = true;
                for(int u = 0; u < uMax; u++) {
                    int alpha = sprite.getPixelRGBA(f, u, vMax - v - 1) >> 24 & 0xFF;
                    boolean t = alpha / 255f <= 0.1f;

                    if (!t && alpha < 255) {
                        translucent = true;
                    }

                    if(ptu && !t) { // left - transparent, right - opaque
                        faceData.set(Direction.WEST, u, v);
                    }
                    if(!ptu && t) { // left - opaque, right - transparent
                        faceData.set(Direction.EAST, u-1, v);
                    }
                    if(ptv[u] && !t) { // up - transparent, down - opaque
                        faceData.set(Direction.UP, u, v);
                    }
                    if(!ptv[u] && t) { // up - opaque, down - transparent
                        faceData.set(Direction.DOWN, u, v-1);
                    }

                    ptu = t;
                    ptv[u] = t;
                }
                if(!ptu) { // last - opaque
                    faceData.set(Direction.EAST, uMax-1, v);
                }
            }
            // last line
            for(int u = 0; u < uMax; u++) {
                if(!ptv[u]) {
                    faceData.set(Direction.DOWN, u, vMax-1);
                }
            }
        }

        // setup quad builder
        QuadBakingVertexConsumer quadBuilder = new QuadBakingVertexConsumer(builder::add);
        // common settings
        quadBuilder.setSprite(sprite);
        quadBuilder.setTintIndex(tint);
        // TODO: should we customize these?
        quadBuilder.setShade(false);
        quadBuilder.setHasAmbientOcclusion(true);
        // only need to set up transforms once, isn't that nice?
        VertexConsumer quadConsumer = quadBuilder;
        if (!transform.isIdentity()) {
            quadConsumer = new TransformingVertexPipeline(quadBuilder, transform);
        }

        // horizontal quads
        for (Direction facing : HORIZONTALS) {
            for (int v = 0; v < vMax; v++) {
                int uStart = 0, uEnd = uMax;
                boolean building = false;
                for (int u = 0; u < uMax; u++) {
                    boolean canDraw = true;
                    boolean face = faceData.get(facing, u, v);
                    // set the end for translucent to draw right after this pixel
                    if (face) {
                        uEnd = u + 1;
                        // if not currently building and we have data, start new quad
                        if (!building) {
                            building = true;
                            uStart = u;
                        }
                    }
                    // make quad [uStart, u]
                    else if (building) {
                        // finish current quad if translucent (minimize overdraw) or we are forbidden from touching this pixel (previous layer drew here)
                        if (!canDraw || translucent) {
                            int off = facing == Direction.DOWN ? 1 : 0;
                            buildSideQuad(quadBuilder, quadConsumer, facing, color, sprite, uStart, v + off, uEnd - uStart, emissivity);
                            building = false;
                        }
                    }
                }
                if (building) { // build remaining quad
                    // make quad [uStart, uEnd]
                    int off = facing == Direction.DOWN ? 1 : 0;
                    buildSideQuad(quadBuilder, quadConsumer, facing, color, sprite, uStart, v+off, uEnd-uStart, emissivity);
                }
            }
        }

        // vertical quads
        for (Direction facing : VERTICALS) {
            for (int u = 0; u < uMax; u++) {
                int vStart = 0, vEnd = vMax;
                boolean building = false;
                for (int v = 0; v < vMax; v++) {
                    boolean canDraw = true;
                    boolean face = faceData.get(facing, u, v);
                    // set the end for translucent to draw right after this pixel
                    if (face) {
                        vEnd = v + 1;
                        // if not currently building and we have data, start new quad
                        if (!building) {
                            building = true;
                            vStart = v;
                        }
                    }
                    // make quad [vStart, v]
                    else if (building) {
                        // finish current quad if translucent (minimize overdraw) or we are forbidden from touching this pixel (future layer drew here)
                        if (!canDraw || translucent) {
                            int off = facing == Direction.EAST ? 1 : 0;
                            buildSideQuad(quadBuilder, quadConsumer, facing, color, sprite, u + off, vStart, vEnd - vStart, emissivity);
                            building = false;
                        }
                    }
                }
                if (building) { // build remaining quad
                    // make quad [vStart, vEnd]
                    int off = facing == Direction.EAST ? 1 : 0;
                    buildSideQuad(quadBuilder, quadConsumer, facing, color, sprite, u+off, vStart, vEnd-vStart, emissivity);
                }
            }
        }

        // back
        buildQuad(quadBuilder, quadConsumer, Direction.NORTH, color, emissivity,
                0, 0, 7.5f / 16f, sprite.getU0(), sprite.getV1(),
                0, 1, 7.5f / 16f, sprite.getU0(), sprite.getV0(),
                1, 1, 7.5f / 16f, sprite.getU1(), sprite.getV0(),
                1, 0, 7.5f / 16f, sprite.getU1(), sprite.getV1());
        // front
        buildQuad(quadBuilder, quadConsumer, Direction.SOUTH, color, emissivity,
                0, 0, 8.5f / 16f, sprite.getU0(), sprite.getV1(),
                1, 0, 8.5f / 16f, sprite.getU1(), sprite.getV1(),
                1, 1, 8.5f / 16f, sprite.getU1(), sprite.getV0(),
                0, 1, 8.5f / 16f, sprite.getU0(), sprite.getV0());

        return List.copyOf(builder);
    }

    private static void buildSideQuad(QuadBakingVertexConsumer builder, VertexConsumer consumer, Direction side, int color, TextureAtlasSprite sprite, int u, int v, int size, int luminosity) {
        final float eps = 1e-2f;
        SpriteContents contents = sprite.contents();
        int width = contents.width();
        int height = contents.height();
        float x0 = (float) u / width;
        float y0 = (float) v / height;
        float x1 = x0, y1 = y0;
        float z0 = 7.5f / 16f, z1 = 8.5f / 16f;
        switch(side) {
            case WEST:
                z0 = 8.5f / 16f;
                z1 = 7.5f / 16f;
                // continue into EAST
            case EAST:
                y1 = (float) (v + size) / height;
                break;
            case DOWN:
                z0 = 8.5f / 16f;
                z1 = 7.5f / 16f;
                // continue into UP
            case UP:
                x1 = (float) (u + size) / width;
                break;
            default:
                throw new IllegalArgumentException("can't handle z-oriented side");
        }

        // for the side, Y axis's use of getOpposite is related to the swapping of V direction
        float dx = side.getNormal().getX() * eps / width;
        float dy = side.getNormal().getY() * eps / height;
        float u0 = 16f * (x0 - dx);
        float u1 = 16f * (x1 - dx);
        float v0 = 16f * (1f - y0 - dy);
        float v1 = 16f * (1f - y1 - dy);
        buildQuad(builder, consumer, (side.getAxis() == Direction.Axis.Y ? side.getOpposite() : side),
                color, luminosity,
                x0, y0, z0, sprite.getU(u0), sprite.getV(v0),
                x1, y1, z0, sprite.getU(u1), sprite.getV(v1),
                x1, y1, z1, sprite.getU(u1), sprite.getV(v1),
                x0, y0, z1, sprite.getU(u0), sprite.getV(v0));
    }

    protected static void buildQuad(QuadBakingVertexConsumer builder, VertexConsumer consumer, Direction side, int color, int luminosity,
                                    float x0, float y0, float z0, float u0, float v0,
                                    float x1, float y1, float z1, float u1, float v1,
                                    float x2, float y2, float z2, float u2, float v2,
                                    float x3, float y3, float z3, float u3, float v3) {
        builder.setDirection(side);
        putVertex(consumer, side, x0, y0, z0, u0, v0, color, luminosity);
        putVertex(consumer, side, x1, y1, z1, u1, v1, color, luminosity);
        putVertex(consumer, side, x2, y2, z2, u2, v2, color, luminosity);
        putVertex(consumer, side, x3, y3, z3, u3, v3, color, luminosity);
    }

    private static void putVertex(VertexConsumer consumer, Direction side, float x, float y, float z, float u, float v, int color, int luminosity) {
        // format is always DefaultVertexFormat#BLOCK, though order does not matter too much
        consumer.vertex(x, y, z);
        consumer.color(color);
        consumer.normal(side.getStepX(), side.getStepY(), side.getStepZ());
        consumer.uv(u, v);
        int light = (luminosity << 4);
        consumer.uv2(light, light);
        consumer.endVertex();
    }

    /** Cloned from {@link net.minecraftforge.client.model.ItemLayerModel}'s FaceData subclass */
    private static class FaceData {
        private final EnumMap<Direction, BitSet> data = new EnumMap<>(Direction.class);
        private final int vMax;

        FaceData(int uMax, int vMax) {
            this.vMax = vMax;

            data.put(Direction.WEST, new BitSet(uMax * vMax));
            data.put(Direction.EAST, new BitSet(uMax * vMax));
            data.put(Direction.UP,   new BitSet(uMax * vMax));
            data.put(Direction.DOWN, new BitSet(uMax * vMax));
        }

        public void set(Direction facing, int u, int v) {
            data.get(facing).set(getIndex(u, v));
        }

        public boolean get(Direction facing, int u, int v) {
            return data.get(facing).get(getIndex(u, v));
        }

        private int getIndex(int u, int v) {
            return v * vMax + u;
        }
    }
}
