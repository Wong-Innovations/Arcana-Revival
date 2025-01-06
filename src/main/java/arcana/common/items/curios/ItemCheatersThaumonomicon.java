package arcana.common.items.curios;

import arcana.api.capabilities.ModCapabilities;
import arcana.api.research.ResearchCategories;
import arcana.api.research.ResearchCategory;
import arcana.api.research.ResearchEntry;
import arcana.client.gui.GuiResearchBrowser;
import arcana.common.commands.ResearchCommand;
import arcana.common.items.ItemBase;
import arcana.common.lib.ModSounds;
import arcana.common.lib.research.ResearchManager;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class ItemCheatersThaumonomicon extends ItemThaumonomicon {

    public ItemCheatersThaumonomicon() {
        super();
    }

    public ItemCheatersThaumonomicon(Properties pProperties) {
        super(pProperties.stacksTo(1));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level world, @NotNull Player player, @NotNull InteractionHand hand) {
        if (!world.isClientSide && player instanceof ServerPlayer) {
            Collection<ResearchCategory> rc = ResearchCategories.researchCategories.values();
            for (ResearchCategory cat : rc) {
                Collection<ResearchEntry> rl = cat.research.values();
                for (ResearchEntry ri : rl) {
                    ResearchCommand.giveRecursiveResearch((ServerPlayer) player, ri.getKey());
                }
            }
            ModCapabilities.getKnowledge(player).sync((ServerPlayer) player);
        } else {
            world.playLocalSound(player.getX(), player.getY(), player.getZ(), ModSounds.page.get(), SoundSource.PLAYERS, 1.0f, 1.0f, false);
        }

        if (world.isClientSide) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                Minecraft.getInstance().setScreen(new GuiResearchBrowser());
            });
        }

        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    @Override
    public @NotNull Rarity getRarity(@NotNull ItemStack itemstack) {
        return Rarity.EPIC;
    }
}