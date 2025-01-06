package arcana.client.lib.events;

import arcana.api.aspects.Aspect;
import arcana.api.aspects.AspectList;
import arcana.client.lib.UtilsFX;
import arcana.common.lib.crafting.CraftingManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;

import static arcana.common.config.ModConfig.CONFIG_GRAPHICS;

@Mod.EventBusSubscriber({Dist.CLIENT})
public class RenderEventHandler {
    public static RenderEventHandler INSTANCE = new RenderEventHandler();
    @OnlyIn(Dist.CLIENT)
    public static HudHandler hudHandler = new HudHandler();

    @SubscribeEvent
    public static void renderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {

        } else {
            Minecraft mc = Minecraft.getInstance();
            if (Minecraft.getInstance().getCameraEntity() instanceof Player player) {
                long time = System.currentTimeMillis();
                RenderEventHandler.hudHandler.renderHuds(new GuiGraphics(mc, mc.renderBuffers().bufferSource()), mc, event.renderTickTime, player, time);
            }
        }
    }

    @SubscribeEvent
    public static void tooltipEvent(@Nonnull ItemTooltipEvent event) {
        if(Screen.hasShiftDown()){
            AspectList aspects = CraftingManager.getObjectTags(event.getItemStack());
            if(aspects.size() > 0){
                // amount of spaces that need inserting
                int filler = aspects.size() * 5;
                // repeat " " *filler
                StringBuilder sb = new StringBuilder();
                for(int __ = 0; __ < filler; __++){
                    String s = " ";
                    sb.append(s);
                }
                String collect = sb.toString();
                event.getToolTip().add(Component.literal(collect));
                event.getToolTip().add(Component.literal(collect));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderTooltipPost(@Nonnull RenderTooltipEvent.Color event){
        if(Screen.hasShiftDown()){
            AspectList aspects = CraftingManager.getObjectTags(event.getItemStack());
            if(aspects.size() > 0){
                GuiGraphics guiGraphics = event.getGraphics();
                PoseStack poseStack = guiGraphics.pose();
                poseStack.pushPose();
                poseStack.translate(0, 0, 500);
                RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
                Minecraft mc = Minecraft.getInstance();
                poseStack.translate(0F, 0F, 1F);

                int x = event.getX();
                int y = 10 * (event.getComponents().size() - 3) + 14 + event.getY();
                for(Aspect aspect : aspects.getAspects()){
                    UtilsFX.renderAspectWithAmount(event.getGraphics(), aspect, aspects.getAmount(aspect), x, y);
                    x += 20;
                }
                poseStack.popPose();
            }
        }
    }

}