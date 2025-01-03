package arcana.client.renderers.blockentity;

import arcana.client.lib.UtilsFX;
import arcana.common.blockentities.crafting.BlockEntityCrucible;
import arcana.common.blocks.ModBlocks;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class BlockEntityCrucibleRenderer implements BlockEntityRenderer<BlockEntityCrucible> {

    public BlockEntityCrucibleRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(@NotNull BlockEntityCrucible crucible, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        if (crucible.tank.getFluidAmount() > 0) {
            this.renderFluid(crucible, pPartialTick, pPoseStack, pBufferSource, pPackedLight, pPackedOverlay);
        }
    }

    private void renderFluid(@NotNull BlockEntityCrucible crucible, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        BlockPos pos = crucible.getBlockPos();
        pPoseStack.pushPose();
        pPoseStack.translate(0, crucible.getFluidHeight(), 1.0);
        pPoseStack.mulPose(Axis.of(new Vector3f(-1.0F, 0.0F, 0.0F)).rotationDegrees(90.0F));
        if (crucible.tank.getFluidAmount() > 0) {
            TextureAtlasSprite icon = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getTexture(Blocks.WATER.defaultBlockState(), crucible.getLevel(), pos);
            int i = IClientFluidTypeExtensions.of(crucible.tank.getFluid().getFluid()).getTintColor(crucible.tank.getFluid().getFluid().defaultFluidState(), crucible.getLevel(), pos);
            float alpha = (float)(i >> 24 & 255) / 255.0F;
            float red = (float)(i >> 16 & 255) / 255.0F;
            float green = (float)(i >> 8 & 255) / 255.0F;
            float blue = (float)(i & 255) / 255.0F;
            float visCount = (float)crucible.aspects.visSize();
            float recolor = visCount / 500.0F;
            if (recolor > 0.0F) {
                recolor = 0.5F + recolor / 2.0F;
            }

            if (recolor > 1.0F) {
                recolor = 1.0F;
            }

            UtilsFX.renderQuadFromIcon(pPoseStack, icon, 1.0F, red - (recolor * red) / 3.0F, green - (recolor * green), blue - (recolor * blue) / 2.0F, ModBlocks.crucible.get().getLightBlock(crucible.getLevel().getBlockState(pos), crucible.getLevel(), pos), 771, alpha);
        }

        pPoseStack.popPose();
    }

}
