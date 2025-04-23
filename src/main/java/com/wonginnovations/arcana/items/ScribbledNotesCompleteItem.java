package com.wonginnovations.arcana.items;

import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.systems.research.ResearchBooks;
import com.wonginnovations.arcana.capabilities.Researcher;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.wonginnovations.arcana.Arcana.arcLoc;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ScribbledNotesCompleteItem extends Item {
    
    private static final ResourceLocation ROOT = arcLoc("root");
    
    public ScribbledNotesCompleteItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    // gives players the arcanum on right click
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.setItemInHand(hand, ItemStack.EMPTY);
        player.addItem(new ItemStack(ArcanaItems.ARCANUM.get()));
        Researcher.getFrom(player).advanceEntry(ResearchBooks.getEntry(ROOT));
        return super.use(level, player, hand);
    }
}