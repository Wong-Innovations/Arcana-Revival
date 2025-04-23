package com.wonginnovations.arcana.client.model;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Either;
import com.mojang.math.Transformation;
import com.wonginnovations.arcana.client.ClientUtils;
import com.wonginnovations.arcana.items.ScepterItem;
import com.wonginnovations.arcana.items.StaffItem;
import com.wonginnovations.arcana.items.WandItem;
import com.wonginnovations.arcana.items.attachment.Cap;
import com.wonginnovations.arcana.items.attachment.Core;
import com.wonginnovations.arcana.items.attachment.Focus;
import com.wonginnovations.arcana.mixin.BlockModelMixin;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeRenderTypes;
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.client.model.CompositeModel;
import net.minecraftforge.client.model.ElementsModel;
import net.minecraftforge.client.model.geometry.*;
import net.minecraftforge.client.model.pipeline.QuadBakingVertexConsumer;
import net.minecraftforge.client.model.renderable.CompositeRenderable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.wonginnovations.arcana.Arcana.arcLoc;
import static com.wonginnovations.arcana.client.ClientUtils.getDefaultRenderType;
import static com.wonginnovations.arcana.client.ClientUtils.getQuadsForSprite;
import static net.minecraft.client.resources.model.ModelBakery.MISSING_MODEL_LOCATION;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("deprecation")
public record WandModel(ResourceLocation cap, ResourceLocation material, ResourceLocation variant, ResourceLocation focus) implements IUnbakedGeometry<WandModel> {

//	private static BakedModel bakeInternal(IGeometryBakingContext owner, Function<Material, TextureAtlasSprite> spriteGetter, Transformation transform, ItemOverrides overrides, Material core, Material cap, Material focus) {
//		TextureAtlasSprite coreSprite = (core != null)? spriteGetter.apply(core) : spriteGetter.apply(new Material(TextureAtlas.LOCATION_BLOCKS, arcLoc("missing_no")));
//		CompositeModel.Baked.Builder builder = CompositeModel.Baked.builder(owner, coreSprite, overrides, owner.getTransforms());
//
//		builder.addQuads(getDefaultRenderType(owner), getQuadsForSprite(coreSprite, transform));
//		if (cap != null) {
//			builder.addQuads(getDefaultRenderType(owner), getQuadsForSprite(spriteGetter.apply(cap), transform));
//		}
//		if (focus != null) {
//			builder.addQuads(getDefaultRenderType(owner), getQuadsForSprite(spriteGetter.apply(focus), transform));
//		}
//
//		return builder.build();
//	}
//
//	@Override
//	public BakedModel bake(IGeometryBakingContext owner, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
//		ItemOverrides wandOverrides = new AttachmentOverrideHandler(baker, spriteGetter, owner, modelState);
//
//		return bakeInternal(owner, spriteGetter, Transformation.identity(), wandOverrides, null, null, null);
//	}

	@Override
	public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
		ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
		ItemTransforms itemTransforms = context.getTransforms();

		// get core texture
		Material coreMaterial = new Material(TextureAtlas.LOCATION_BLOCKS, (material != null)? material : arcLoc("missing_no"));
//		// get cap texture
		Material capMaterial = new Material(TextureAtlas.LOCATION_BLOCKS, (cap != null)? cap : arcLoc("missing_no"));

		if (baker.getModel(variant) instanceof BlockModel unbakedModel && cap != null && material != null) {
			Map<String, Either<Material, String>> textureMap = new HashMap<>();
			textureMap.put("core", Either.left(coreMaterial));
			textureMap.put("cap", Either.left(capMaterial));
			itemTransforms = unbakedModel.getTransforms();
			List<ItemOverride> itemOverridesList = unbakedModel.getOverrides();
			// TODO: foci rendering
			return new BlockModel(variant, ((BlockModelMixin) unbakedModel).getModelElements(), textureMap, unbakedModel.hasAmbientOcclusion, unbakedModel.getGuiLight(), itemTransforms, itemOverridesList).bake(baker, spriteGetter, modelState, variant);
		}

		return new Baked(builder.build(), spriteGetter.apply(coreMaterial), itemTransforms, new AttachmentOverrideHandler(baker, context, modelState));
	}


//		ResourceLocation variantWithFallback = (variant != null) ? variant : arcLoc("wand");
//		ResourceLocation coreModelLoc = new ResourceLocation(variantWithFallback.getNamespace(), "item/wands/variants/" + variantWithFallback.getPath());
//		final TextureAtlasSprite coreSprite = (variant != null) ? spriteGetter.apply(new Material(TextureAtlas.LOCATION_BLOCKS, variant)) : null;
//		final TextureAtlasSprite capSprite = (cap != null)? spriteGetter.apply(new Material(TextureAtlas.LOCATION_BLOCKS, cap)) : null;
//
//		StandaloneGeometryBakingContext itemContext = StandaloneGeometryBakingContext.builder(context).build(modelLocation);
//		CompositeModel.Baked.Builder builder = CompositeModel.Baked.builder(itemContext, coreSprite, new AttachmentOverrideHandler(baker, spriteGetter, context, modelState), context.getTransforms());
//
//		addQuads(modelState, coreModelLoc, coreSprite, builder);
//		if (capSprite != null) addQuads(modelState, cap, capSprite, builder);
//		if (focus != null) {
//			ResourceLocation fociLoc = new ResourceLocation(focus.getNamespace(), "item/wands/foci/" + focus.getPath());
//			TextureAtlasSprite focusSprite = spriteGetter.apply(new Material(TextureAtlas.LOCATION_BLOCKS, fociLoc));
//			addQuads(modelState, fociLoc, focusSprite, builder);
//		}
//
//		return builder.build();
//	}

//	private static void addQuads(ModelState modelState, ResourceLocation modelLocation, TextureAtlasSprite texture, CompositeModel.Baked.Builder builder) {
//		List<BlockElement> unbaked = UnbakedGeometryHelper.createUnbakedItemElements(0, texture.contents());
//		List<BakedQuad> quads = UnbakedGeometryHelper.bakeElements(unbaked, material -> texture, modelState, modelLocation);
//		builder.addQuads(new RenderTypeGroup(RenderType.solid(), ForgeRenderTypes.ITEM_LAYERED_SOLID.get()), quads);
//	}

	public static class Loader implements IGeometryLoader<WandModel> {
		@Override
		public WandModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
			return new WandModel(null, null, arcLoc("item/wands/variants/wand"), null);
//			return new WandModel();
		}
	}

	protected static final class AttachmentOverrideHandler extends ItemOverrides {
		private final ModelBaker baker;
		private final IGeometryBakingContext owner;
		private final ModelState modelState;

		public AttachmentOverrideHandler(ModelBaker baker, IGeometryBakingContext owner, ModelState modelState){
			this.baker = baker;
			this.owner = owner;
			this.modelState = modelState;
		}

		@Override
		public BakedModel resolve(@NotNull BakedModel model, @NotNull ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
			// get cap
			Cap cap = WandItem.getCap(stack);
			// get material
			Core core = WandItem.getCore(stack);
			// get variant (staff/scepter/wand)
			// TODO: improve this slightly
			ResourceLocation variant = arcLoc("wand");
			if (stack.getItem() instanceof WandItem) {
				variant = arcLoc("wand"); // yes this is redundant, just here for completeness
			} else if(stack.getItem() instanceof ScepterItem) {
				variant = arcLoc("scepter");
			} else if(stack.getItem() instanceof StaffItem) {
				variant = arcLoc("staff");
			}
			ResourceLocation variantModel = new ResourceLocation(variant.getNamespace(), "item/wands/variants/" + variant.getPath());

			// get focus
			// nbt context comes from the focusData tag
			Focus focus = WandItem.getFocus(stack);
			CompoundTag focusData = WandItem.getFocusData(stack);
			return new WandModel(cap.getTextureLocation(), core.getTextureLocation(), variantModel, focus.getModelLocation(focusData))
					.bake(owner, baker, Material::sprite, modelState, ItemOverrides.EMPTY, variantModel);
		}
	}

//	/**
//	 * Dynamic override handler to swap in the core, cap, and focus texture
//	 */
//	protected static final class AttachmentOverrideHandler extends ItemOverrides {
//
//		// parameters needed for rebaking
//		private final ModelBaker bakery;
//		private final Function<Material, TextureAtlasSprite> spriteGetter;
//		private final IGeometryBakingContext owner;
//		private final ModelState modelState;
//
//		public AttachmentOverrideHandler(ModelBaker bakery, Function<Material, TextureAtlasSprite> spriteGetter, IGeometryBakingContext owner, ModelState modelState) {
//			this.bakery = bakery;
//			this.spriteGetter = spriteGetter;
//			this.owner = owner;
//			this.modelState = modelState;
//		}
//
//		@Override
//		public BakedModel resolve(@Nonnull BakedModel originalModel, @Nonnull ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
//			// get cap
//			Cap cap = WandItem.getCap(stack);
//			// get material
//			Core core = WandItem.getCore(stack);
//			// get focus
//			// nbt context comes from the focusData tag
//			Focus focus = WandItem.getFocus(stack);
//			CompoundTag focusData = WandItem.getFocusData(stack);
//			// get variant (staff/scepter/wand)
//			String variant = "wand";
//			if (stack.getItem() instanceof ScepterItem) {
//				variant = "scepter";
//			} else if (stack.getItem() instanceof StaffItem) {
//				variant = "staff";
//			}
//
//			Material capMaterial = new Material(TextureAtlas.LOCATION_BLOCKS, cap.getTextureLocation());
//			Material coreMaterial = new Material(TextureAtlas.LOCATION_BLOCKS, core.getTextureLocation());
//			Material focusMaterial = new Material(TextureAtlas.LOCATION_BLOCKS, focus.getModelLocation(focusData));
//			// NOT A MATERIAL BUT A MODEL
////			new Material(TextureAtlas.LOCATION_BLOCKS, arcLoc("item/wands/variants/" + variant));
//
//			return bakeInternal(owner, Material::sprite, Transformation.identity(), ItemOverrides.EMPTY, coreMaterial, capMaterial, null);
//
//
////			return new WandModel(cap.getTextureLocation(), core.getTextureLocation(), variant, focus.getModelLocation(focusData))
////					.bake(context, bakery, spriteGetter, modelState, ItemOverrides.EMPTY, arcLoc("wand_override"));
//		}
//	}

	public record Baked(ImmutableList<BakedQuad> quads, TextureAtlasSprite particle, ItemTransforms transforms, ItemOverrides overrides) implements BakedModel {

		@Override
		public @NotNull List<BakedQuad> getQuads(@Nullable BlockState pState, @Nullable Direction pDirection, @NotNull RandomSource pRandom) {
			return quads;
		}

		@Override
		public boolean useAmbientOcclusion() {
			return false;
		}

		@Override
		public boolean isGui3d() {
			return false;
		}

		@Override
		public boolean usesBlockLight() {
			return true;
		}

		@Override
		public boolean isCustomRenderer() {
			return false;
		}

		@Override
		public @NotNull TextureAtlasSprite getParticleIcon() {
			return particle;
		}

		@Override
		public @NotNull ItemOverrides getOverrides() {
			return overrides;
		}

		@Override
		public @NotNull ItemTransforms getTransforms() {
			return transforms;
		}
	}
}