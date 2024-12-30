package arcana.client.renderers.blockentity;

import arcana.Arcana;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import org.jetbrains.annotations.NotNull;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import arcana.api.aspects.Aspect;
import arcana.api.items.IScribeTools;
import arcana.client.lib.UtilsFX;
import arcana.client.renderers.models.block.ModelResearchTable;
import arcana.common.blockentities.crafting.BlockEntityResearchTable;
import arcana.common.blocks.crafting.BlockResearchTable;

public class BlockEntityResearchTableRenderer implements BlockEntityRenderer<BlockEntityResearchTable> {
    private final ModelResearchTable tableModel;
    private static final ResourceLocation TEX = new ResourceLocation(Arcana.MODID, "textures/block/research_table_model.png");

    public BlockEntityResearchTableRenderer(BlockEntityRendererProvider.Context context) {
        tableModel = new ModelResearchTable();
    }

    @Override
    public void render(BlockEntityResearchTable table, float pPartialTick, PoseStack pPoseStack, @NotNull MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        pPoseStack.pushPose();
        pPoseStack.translate(0.5f, 1.0f, 0.5f);
        pPoseStack.mulPose(Axis.XP.rotationDegrees(180.0f));
        switch (table.getBlockState().getValue(BlockResearchTable.FACING)) {
            case EAST: {
                pPoseStack.mulPose(Axis.YP.rotationDegrees(90.0f));
                break;
            }
            case WEST: {
                pPoseStack.mulPose(Axis.YP.rotationDegrees(270.0f));
                break;
            }
            case SOUTH: {
                pPoseStack.mulPose(Axis.YP.rotationDegrees(180.0f));
                break;
            }
        }

        VertexConsumer vertexconsumer = pBufferSource.getBuffer(RenderType.entityTranslucentCull(TEX));
        if (table.data != null) {
            tableModel.renderScroll(pPoseStack, vertexconsumer, pPackedLight, pPackedOverlay, Aspect.ALCHEMY.getColor());
        }
        if (!table.getSyncedStackInSlot(0).isEmpty() && table.getSyncedStackInSlot(0).getItem() instanceof IScribeTools) {
            tableModel.renderInkwell(pPoseStack, vertexconsumer, pPackedLight, pPackedOverlay);
            pPoseStack.pushPose();
            RenderSystem.enableBlend();
            pPoseStack.mulPose(Axis.XP.rotationDegrees(180.0f));
            pPoseStack.translate(-0.5, 0.1, 0.125);
            pPoseStack.mulPose(Axis.YP.rotationDegrees(60.0f));
            pPoseStack.scale(0.5f, 0.5f, 0.5f);
            RenderSystem.enableDepthTest();
            pPoseStack.pushPose();
            RenderSystem.setShaderTexture(0, new ResourceLocation(Arcana.MODID, "textures/research/quill.png"));
            UtilsFX.renderTextureIn3D(pPoseStack, 1.0f, 1.0f, 0.0f, 0.0f, 16, 16, 0.0625f);
            pPoseStack.popPose();
            RenderSystem.disableBlend();
            pPoseStack.popPose();
        }

        pPoseStack.popPose();
    }
}
