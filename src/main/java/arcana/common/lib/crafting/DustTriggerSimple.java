package arcana.common.lib.crafting;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.ForgeEventFactory;
import arcana.api.capabilities.ModCapabilities;
import arcana.api.crafting.IDustTrigger;
import arcana.common.InventoryFake;
import arcana.common.lib.events.ServerEvents;


public class DustTriggerSimple implements IDustTrigger {
    Block target;
    ItemStack result;
    String research;

    public DustTriggerSimple(String research, Block target, ItemStack result) {
        this.target = target;
        this.result = result;
        this.research = research;
    }

    @Override
    public Placement getValidFace(Level world, Player player, BlockPos pos, Direction facing) {
        var block = world.getBlockState(pos).getBlock();
        var isResearchKnown = ModCapabilities.getKnowledge(player).isResearchKnown(this.research);
        return (block == this.target && (this.research == null || isResearchKnown)) ? new Placement(0, 0, 0, null) : null;
    }

    @Override
    public void execute(Level world, Player player, BlockPos pos, Placement placement, Direction facing) {
        ForgeEventFactory.firePlayerCraftingEvent(player, this.result, new InventoryFake(1));
        BlockState state = world.getBlockState(pos);
        ServerEvents.addRunnableServer(world, () -> ServerEvents.addSwapper(world, pos, state, DustTriggerSimple.this.result, false, 0, player, true, true, -9999, false, false, 0, ServerEvents.DEFAULT_PREDICATE, 0.0f), 50);
    }
}
