package arcana.common.items.curios;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import arcana.common.items.ItemBase;

import java.util.List;

public class ItemCelestialNotes extends ItemBase {

    public ItemCelestialNotes() {
        this(new Properties());
    }

    public ItemCelestialNotes(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        try {
            pTooltipComponents.add(Component.translatable(getDescriptionId(pStack) + ".text").withStyle(ChatFormatting.AQUA));
        } catch (Exception ignored) {
        }
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack pStack) {
        return Component.translatable("item.thaumcraft.celestial_notes");
    }
}
