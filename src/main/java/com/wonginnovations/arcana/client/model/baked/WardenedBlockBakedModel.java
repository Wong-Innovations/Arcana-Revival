package com.wonginnovations.arcana.client.model.baked;

import com.wonginnovations.arcana.blocks.entities.WardenedBlockBlockEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.client.model.pipeline.QuadBakingVertexConsumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@SuppressWarnings("deprecation")
public class WardenedBlockBakedModel implements BakedModel {

	public WardenedBlockBakedModel(BakedModel unCamouflagedModel) {
		modelWhenNotCamouflaged = unCamouflagedModel;
	}

	public static ModelProperty<Optional<BlockState>> COPIED_BLOCK = new ModelProperty<>();
	public static ModelProperty<Boolean> HOLDING_SPELL = new ModelProperty<>();

	public static ModelData.Builder getEmptyModelData() {
		ModelData.Builder builder = ModelData.builder();
		builder.with(COPIED_BLOCK, Optional.empty());
		builder.with(HOLDING_SPELL, false);
		return builder;
	}

	private void putVertex(QuadBakingVertexConsumer builder, Vec3 normal,
						   double x, double y, double z, float u, float v, TextureAtlasSprite sprite, float r, float g, float b) {

		builder.vertex(x, y, z);
		builder.color(r, g, b, 1);
		builder.uv(u, v);
		builder.normal((float) normal.x, (float) normal.y, (float) normal.z);

//		ImmutableList<VertexFormatElement> elements = builder.getVertexFormat().getElements().asList();
//		for (int j = 0 ; j < elements.size() ; j++) {
//			VertexFormatElement e = elements.get(j);
//			switch (e.getUsage()) {
//				case POSITION:
//					builder.put(j, (float) x, (float) y, (float) z, 1.0f);
//					break;
//				case COLOR:
//					builder.put(j, r, g, b, 1.0f);
//					break;
//				case UV:
//					switch (e.getIndex()) {
//						case 0:
//							float iu = sprite.getInterpolatedU(u);
//							float iv = sprite.getInterpolatedV(v);
//							builder.put(j, iu, iv);
//							break;
//						case 2:
//							builder.put(j, (short) 0, (short) 0);
//							break;
//						default:
//							builder.put(j);
//							break;
//					}
//					break;
//				case NORMAL:
//					builder.put(j, (float) normal.x, (float) normal.y, (float) normal.z);
//					break;
//				default:
//					builder.put(j);
//					break;
//			}
//		}
	}

	private void createQuad(Consumer<BakedQuad> consumer, Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4, TextureAtlasSprite sprite) {
		Vec3 normal = v3.subtract(v2).cross(v1.subtract(v2)).normalize();
		int tw = sprite.contents().width();
		int th = sprite.contents().height();

		// do something with sprite
		QuadBakingVertexConsumer builder = new QuadBakingVertexConsumer(consumer);
		builder.setSprite(sprite);
		builder.setDirection(Direction.getNearest(normal.x, normal.y, normal.z));
		putVertex(builder, normal, v1.x, v1.y, v1.z, 0, 0, sprite, 1.0f, 1.0f, 1.0f);
		putVertex(builder, normal, v2.x, v2.y, v2.z, 0, th, sprite, 1.0f, 1.0f, 1.0f);
		putVertex(builder, normal, v3.x, v3.y, v3.z, tw, th, sprite, 1.0f, 1.0f, 1.0f);
		putVertex(builder, normal, v4.x, v4.y, v4.z, tw, 0, sprite, 1.0f, 1.0f, 1.0f);
		builder.endVertex();
	}

	private static Vec3 v(double x, double y, double z) {
		return new Vec3(x, y, z);
	}

	@Override
	public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType) {
		double l = -.001;
		double r = 1+.001;
		ArrayList<BakedQuad> ql = (ArrayList<BakedQuad>) ((ArrayList<BakedQuad>) getActualBakedModelFromIModelData(data).getQuads(state, side, rand)).clone(); // Casting object to object removes crashes when spell used on some blocks. but why?
		if (data.has(HOLDING_SPELL) && data.get(HOLDING_SPELL)) {
			createQuad(ql::add, v(l, r, l), v(l, r, r), v(r, r, r), v(r, r, l), modelWhenNotCamouflaged.getParticleIcon());
			createQuad(ql::add, v(l, l, l), v(r, l, l), v(r, l, r), v(l, l, r), modelWhenNotCamouflaged.getParticleIcon());
			createQuad(ql::add, v(r, r, r), v(r, l, r), v(r, l, l), v(r, r, l), modelWhenNotCamouflaged.getParticleIcon());
			createQuad(ql::add, v(l, r, l), v(l, l, l), v(l, l, r), v(l, r, r), modelWhenNotCamouflaged.getParticleIcon());
			createQuad(ql::add, v(r, r, l), v(r, l, l), v(l, l, l), v(l, r, l), modelWhenNotCamouflaged.getParticleIcon());
			createQuad(ql::add, v(l, r, r), v(l, l, r), v(r, l, r), v(r, r, r), modelWhenNotCamouflaged.getParticleIcon());
		}
		return ql;
	}

	@Override
	public @Nonnull ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ModelData modelData) {
		Optional<BlockState> bestAdjacentBlock = ((WardenedBlockBlockEntity)level.getBlockEntity(pos)).getState();
		ModelData.Builder emptyModelData = getEmptyModelData();
		emptyModelData.with(COPIED_BLOCK, bestAdjacentBlock);
		emptyModelData.with(HOLDING_SPELL, ((WardenedBlockBlockEntity)level.getBlockEntity(pos)).isHoldingWand());
		return emptyModelData.build();
	}

	@Override
	public @NotNull TextureAtlasSprite getParticleIcon(@Nonnull ModelData data) {
		return getActualBakedModelFromIModelData(data).getParticleIcon();
	}

	private BakedModel getActualBakedModelFromIModelData(@Nonnull ModelData data) {
		BakedModel retval = modelWhenNotCamouflaged;  // default
		if (!data.has(COPIED_BLOCK)) {
			if (!loggedError) {
				LOGGER.error("IModelData did not have expected property COPIED_BLOCK");
				loggedError = true;
			}
			return retval;
		}
		Optional<BlockState> copiedBlock = data.get(COPIED_BLOCK);
        if (copiedBlock.isEmpty()) return retval;

		Minecraft mc = Minecraft.getInstance();
		BlockRenderDispatcher blockRendererDispatcher = mc.getBlockRenderer();
		retval = blockRendererDispatcher.getBlockModel(copiedBlock.get());
		return retval;
	}

	private BakedModel modelWhenNotCamouflaged;
	private Boolean hasWand;


	// ---- All these methods are required by the interface, but we don't do anything special with them.

	@Override
	public @NotNull List<BakedQuad> getQuads(@Nullable BlockState pState, @Nullable Direction pDirection, @NotNull RandomSource pRandom) {
		throw new AssertionError("BakedModel::getQuads should never be called, only IForgeBakedModel::getQuads");
	}

	// getTexture is used directly when player is inside the block. The game will crash if you don't use something
	//   meaningful here.
	@Override
	public @NotNull TextureAtlasSprite getParticleIcon() {
		return modelWhenNotCamouflaged.getParticleIcon();
	}


	// ideally, this should be changed for different blocks being camouflaged, but this is not supported by vanilla or forge
	@Override
	public boolean useAmbientOcclusion() {
		return modelWhenNotCamouflaged.useAmbientOcclusion();
	}

	@Override
	public boolean isGui3d()
	{
		return modelWhenNotCamouflaged.isGui3d();
	}

	@Override
	public boolean usesBlockLight() {
		return modelWhenNotCamouflaged.usesBlockLight();
	}
	
	@Override
	public boolean isCustomRenderer() {
		return modelWhenNotCamouflaged.isCustomRenderer();
	}

	@Override
	public @NotNull ItemOverrides getOverrides() {
		return modelWhenNotCamouflaged.getOverrides();
	}

	@Override
	public @NotNull ItemTransforms getTransforms()
	{
		return modelWhenNotCamouflaged.getTransforms();
	}

	private static final Logger LOGGER = LogManager.getLogger();
	private static boolean loggedError = false; // prevent spamming console
}