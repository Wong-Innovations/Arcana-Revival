package arcana.common.blockentities.crafting;

import arcana.common.blockentities.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import arcana.api.ArcanaApi;
import arcana.api.capabilities.IPlayerKnowledge;
import arcana.api.items.IScribeTools;
import arcana.api.research.ResearchCategories;
import arcana.api.research.ResearchCategory;
import arcana.api.research.theorycraft.ITheorycraftAid;
import arcana.api.research.theorycraft.ResearchTableData;
import arcana.api.research.theorycraft.TheorycraftManager;
import arcana.common.blockentities.BlockEntityInventory;
import arcana.common.container.ContainerResearchTable;
import arcana.common.lib.utils.EntityUtils;

import java.util.*;
import java.util.stream.Collectors;

public class BlockEntityResearchTable extends BlockEntityInventory implements MenuProvider {
    public ResearchTableData data;

    public BlockEntityResearchTable(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.RESEARCH_TABLE_ENTITY.get(), pPos, pBlockState, 2);
        data = null;
        syncedSlots = new int[]{0, 1};
    }

    @Override
    public void readSyncNBT(CompoundTag nbt) {
        super.readSyncNBT(nbt);
        if (nbt.contains("note")) {
            (data = new ResearchTableData(this)).deserialize(nbt.getCompound("note"));
        } else {
            data = null;
        }
    }

    @Override
    public CompoundTag writeSyncNBT(CompoundTag nbttagcompound) {
        if (data != null) {
            nbttagcompound.put("note", data.serialize());
        } else {
            nbttagcompound.remove("note");
        }
        return super.writeSyncNBT(nbttagcompound);
    }

    public void startNewTheory(Player player, Set<String> mutators) {
        (data = new ResearchTableData(player, this)).initialize(player, mutators);
        syncTile(false);
        setChanged();
    }

    public void finishTheory(Player player) {
        Comparator<Map.Entry<String, Integer>> valueComparator = (e1, e2) -> e2.getValue().compareTo(e1.getValue());
        Map<String, Integer> sortedMap = data.categoryTotals.entrySet().stream().sorted(valueComparator).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        int i = 0;
        for (String cat : sortedMap.keySet()) {
            int tot = Math.round(sortedMap.get(cat) / 100.0f * IPlayerKnowledge.EnumKnowledgeType.THEORY.getProgression());
            if (i > data.penaltyStart) {
                tot = (int) Math.max(1.0, tot * 0.666666667);
            }
            ResearchCategory rc = ResearchCategories.getResearchCategory(cat);
            ArcanaApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.THEORY, rc, tot);
            ++i;
        }
        data = null;
    }

    public Set<String> checkSurroundingAids() {
        HashMap<String, ITheorycraftAid> mutators = new HashMap<>();
        for (int y = -1; y <= 1; ++y) {
            for (int x = -4; x <= 4; ++x) {
                for (int z = -4; z <= 4; ++z) {
                    for (String muk : TheorycraftManager.aids.keySet()) {
                        ITheorycraftAid mu = TheorycraftManager.aids.get(muk);
                        BlockState state = level.getBlockState(getBlockPos().offset(x, y, z));
                        if (mu.getAidObject() instanceof Block) {
                            if (state.getBlock() != mu.getAidObject()) {
                                continue;
                            }
                            mutators.put(muk, mu);
                        } else {
                            if (!(mu.getAidObject() instanceof ItemStack)) {
                                continue;
                            }
                            ItemStack is = state.getBlock().getCloneItemStack(getLevel(), getBlockPos().offset(x, y, z), state);

                            if (is.isEmpty() || !ItemStack.isSameItem(is, (ItemStack) mu.getAidObject())) {
                                continue;
                            }
                            mutators.put(muk, mu);
                        }
                    }
                }
            }
        }
        List<Entity> l = EntityUtils.getEntitiesInRange(getLevel(), getBlockPos(), null, Entity.class, 5.0);
        if (l != null && !l.isEmpty()) {
            for (Entity e : l) {
                for (String muk : TheorycraftManager.aids.keySet()) {
                    ITheorycraftAid mu = TheorycraftManager.aids.get(muk);
                    if (mu.getAidObject() instanceof Class && e.getClass().isAssignableFrom((Class<?>) mu.getAidObject())) {
                        mutators.put(muk, mu);
                    }
                }
            }
        }
        return mutators.keySet();
    }

    public boolean consumeInkFromTable() {
        if (getItem(0).getItem() instanceof IScribeTools && getItem(0).getDamageValue() < getItem(0).getMaxDamage()) {
            getItem(0).setDamageValue(getItem(0).getDamageValue() + 1);
            syncTile(false);
            setChanged();
            return true;
        }
        return false;
    }

    public boolean consumepaperFromTable() {
        if (getItem(1).getItem() == Items.PAPER && getItem(1).getCount() > 0) {
            removeItem(1, 1);
            syncTile(false);
            setChanged();
            return true;
        }
        return false;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.empty();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory pPlayerInventory, @NotNull Player pPlayer) {
        return new ContainerResearchTable(pContainerId, pPlayerInventory, this);
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        switch (i) {
            case 0: {
                if (itemstack.getItem() instanceof IScribeTools) {
                    return true;
                }
                break;
            }
            case 1: {
                if (itemstack.getItem() == Items.PAPER) {
                    return true;
                }
                break;
            }
        }
        return false;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
        if (level != null && level.isClientSide) {
            syncTile(false);
        }
    }
}
