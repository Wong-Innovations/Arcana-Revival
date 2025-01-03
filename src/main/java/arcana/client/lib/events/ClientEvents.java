package arcana.client.lib.events;

import arcana.Arcana;
import arcana.client.gui.GuiArcaneWorkbench;
import arcana.client.gui.GuiResearchTable;
import arcana.client.gui.ModMenuTypes;
import arcana.client.lib.UtilsFX;
import arcana.client.renderers.blockentity.BlockEntityCrucibleRenderer;
import arcana.client.renderers.blockentity.BlockEntityResearchTableRenderer;
import arcana.client.renderers.entity.RenderSpecialItem;
import arcana.common.blockentities.ModBlockEntities;
import arcana.common.entities.ModEntityTypes;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.io.IOException;

@Mod.EventBusSubscriber(modid = Arcana.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEvents {

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.SPECIAL_ITEM.get(), RenderSpecialItem::new);
    }

    @SubscribeEvent
    public static void registerCustomShaders(RegisterShadersEvent event) {
        try {
            event.registerShader(new ShaderInstance(event.getResourceProvider(), new ResourceLocation(Arcana.MODID, "position_tex_color_lightmap_normal"), UtilsFX.POSITION_TEX_COLOR_LIGHTMAP_NORMAL),
                    shader -> UtilsFX.SHADER_POSITION_TEX_COLOR_LIGHTMAP_NORMAL = shader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
//        Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(new ResourceLocation(Arcana.MODID, "research/quill"));
        event.enqueueWork(() -> {
            BlockEntityRenderers.register(ModBlockEntities.RESEARCH_TABLE_ENTITY.get(), BlockEntityResearchTableRenderer::new);
            BlockEntityRenderers.register(ModBlockEntities.CRUCIBLE_ENTITY.get(), BlockEntityCrucibleRenderer::new);
            MenuScreens.register(ModMenuTypes.ARCANE_WORKBENCH_MENU.get(), GuiArcaneWorkbench::new);
            MenuScreens.register(ModMenuTypes.RESEARCH_TABLE_MENU.get(), GuiResearchTable::new);
        });
    }

}
