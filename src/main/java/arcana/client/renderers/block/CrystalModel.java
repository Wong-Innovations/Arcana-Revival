package arcana.client.renderers.block;

import arcana.Arcana;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import arcana.client.lib.obj.MeshLoader;
import arcana.client.lib.obj.MeshModel;
import arcana.client.lib.obj.MeshPart;
import arcana.common.blocks.world.ore.BlockCrystal;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class CrystalModel implements IUnbakedGeometry<CrystalModel> {

//    @Override
//    public Collection<Material> resolveResources(IGeometryBakingContext context, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
//        Set<Material> textures = Sets.newHashSet();
//
//        if (context.hasMaterial("all"))
//            textures.add(context.getMaterial("all"));
//
//        return textures;
//    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
        TextureAtlasSprite tex = spriteGetter.apply(context.getMaterial("all"));
        return new Baked(tex);
    }

    public static class Baked implements BakedModel {
        ResourceLocation model;
        MeshModel sourceMesh;
        TextureAtlasSprite tex;

        public Baked(TextureAtlasSprite tex) {
            this.model = new ResourceLocation(Arcana.MODID, "models/obj/crystal.obj");
            this.tex = tex;
            try {
                this.sourceMesh = new MeshLoader().loadFromResource(this.model);
                for (final MeshPart mp : this.sourceMesh.parts) {
                    mp.tintIndex = 0;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType) {
            if (side != null || (renderType != null && !renderType.equals(RenderType.solid()))) {
                return Collections.emptyList();
            }
            return getQuads(state, side, rand);
        }

        @Override
        public @NotNull List<BakedQuad> getQuads(@Nullable BlockState pState, @Nullable Direction pDirection, RandomSource pRandom) {
            ArrayList<BakedQuad> ret = new ArrayList<>();
            int m = ((BlockCrystal) pState.getBlock()).getGrowth(pState) + 1;
            MeshModel mm = this.sourceMesh.clone();

            try {
                if (!(boolean) pState.getValue(BlockCrystal.UP) || !(boolean) pState.getValue(BlockCrystal.DOWN) || !(boolean) pState.getValue(BlockCrystal.EAST) || !(boolean) pState.getValue(BlockCrystal.WEST) || !(boolean) pState.getValue(BlockCrystal.NORTH) || !(boolean) pState.getValue(BlockCrystal.SOUTH)) {
                    if (pState.getValue(BlockCrystal.UP)) {
                        List<Integer> c = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7);
                        Collections.shuffle(c, new Random(pRandom.nextLong()));
                        mm.parts.clear();
                        for (int a = 0; a < m; ++a) {
                            mm.parts.add(this.sourceMesh.parts.get(c.get(a)));
                        }
                        MeshModel mod = mm.clone();
                        mod.rotate(Math.toRadians(180.0), new Vector3f(1.0F, 0.0F, 0.0F), new Vector3f(0.0F, 1.0F, 1.0F));
                        for (BakedQuad quad : mod.bakeModel(this.getParticleIcon())) {
                            ret.add(quad);
                        }
                    }
                    if (pState.getValue(BlockCrystal.DOWN)) {
                        List<Integer> c = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7);
                        Collections.shuffle(c, new Random(pRandom.nextLong() + 5L));
                        mm.parts.clear();
                        for (int a = 0; a < m; ++a) {
                            mm.parts.add(this.sourceMesh.parts.get(c.get(a)));
                        }
                        for (BakedQuad quad2 : mm.bakeModel(this.getParticleIcon())) {
                            ret.add(quad2);
                        }
                    }
                    if (pState.getValue(BlockCrystal.EAST)) {
                        List<Integer> c = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7);
                        Collections.shuffle(c, new Random(pRandom.nextLong() + 10L));
                        mm.parts.clear();
                        for (int a = 0; a < m; ++a) {
                            mm.parts.add(this.sourceMesh.parts.get(c.get(a)));
                        }
                        MeshModel mod = mm.clone();
                        mod.rotate(Math.toRadians(90.0), new Vector3f(1.0F, 0.0F, 0.0F), new Vector3f(0.0F, 0.0F, 0.0F));
                        mod.rotate(Math.toRadians(270.0), new Vector3f(0.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F));
                        for (BakedQuad quad : mod.bakeModel(this.getParticleIcon())) {
                            ret.add(quad);
                        }
                    }
                    if (pState.getValue(BlockCrystal.WEST)) {
                        List<Integer> c = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7);
                        Collections.shuffle(c, new Random(pRandom.nextLong() + 15L));
                        mm.parts.clear();
                        for (int a = 0; a < m; ++a) {
                            mm.parts.add(this.sourceMesh.parts.get(c.get(a)));
                        }
                        MeshModel mod = mm.clone();
                        mod.rotate(Math.toRadians(90.0), new Vector3f(1.0F, 0.0F, 0.0F), new Vector3f(0.0F, 0.0F, 0.0F));
                        mod.rotate(Math.toRadians(90.0), new Vector3f(0.0F, 1.0F, 0.0F), new Vector3f(0.0F, 1.0F, 1.0F));
                        for (BakedQuad quad : mod.bakeModel(this.getParticleIcon())) {
                            ret.add(quad);
                        }
                    }
                    if (pState.getValue(BlockCrystal.NORTH)) {
                        List<Integer> c = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7);
                        Collections.shuffle(c, new Random(pRandom.nextLong() + 20L));
                        mm.parts.clear();
                        for (int a = 0; a < m; ++a) {
                            mm.parts.add(this.sourceMesh.parts.get(c.get(a)));
                        }
                        MeshModel mod = mm.clone();
                        mod.rotate(Math.toRadians(90.0), new Vector3f(1.0F, 0.0F, 0.0F), new Vector3f(0.0F, 1.0F, 0.0F));
                        for (BakedQuad quad : mod.bakeModel(this.getParticleIcon())) {
                            ret.add(quad);
                        }
                    }
                    if (pState.getValue(BlockCrystal.SOUTH)) {
                        List<Integer> c = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7);
                        Collections.shuffle(c, new Random(pRandom.nextLong() + 25L));
                        mm.parts.clear();
                        for (int a = 0; a < m; ++a) {
                            mm.parts.add(this.sourceMesh.parts.get(c.get(a)));
                        }
                        MeshModel mod = mm.clone();
                        mod.rotate(Math.toRadians(90.0), new Vector3f(1.0F, 0.0F, 0.0F), new Vector3f(0.0F, 0.0F, 0.0F));
                        mod.rotate(Math.toRadians(180.0), new Vector3f(0.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 1.0F));
                        for (BakedQuad quad : mod.bakeModel(this.getParticleIcon())) {
                            ret.add(quad);
                        }
                    }
                }
            } catch (Exception ignored) {}

            return ret;
        }

        @Override
        public boolean useAmbientOcclusion() {
            return true;
        }

        @Override
        public boolean isGui3d() {
            return false;
        }

        @Override
        public boolean usesBlockLight() {
            return false;
        }

        @Override
        public boolean isCustomRenderer() {
            return false;
        }

        @Override
        public @NotNull TextureAtlasSprite getParticleIcon() {
            return this.tex;
        }

        @Override
        public @NotNull ItemOverrides getOverrides() {
            return ItemOverrides.EMPTY;
        }
    }

    public static final class Loader implements IGeometryLoader<CrystalModel> {
        public static final CrystalModel.Loader INSTANCE = new CrystalModel.Loader();

        @Override
        public CrystalModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
            return new CrystalModel();
        }
    }
}
