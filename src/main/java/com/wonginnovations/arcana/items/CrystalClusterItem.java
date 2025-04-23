package com.wonginnovations.arcana.items;

import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.blocks.CrystalClusterBlock;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

/**
 * Copy of BlockItem, but allows specifying growth stage & uses different render layer.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CrystalClusterItem extends Item {
	
	private final Block block;
	private int stage;
	
	public CrystalClusterItem(Block block, Item.Properties builder, int stage) {
		super(builder);
		this.block = block;
		this.stage = stage;
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		InteractionResult interactionresult = this.place(new BlockPlaceContext(context));
		if (!interactionresult.consumesAction() && this.isEdible()) {
			InteractionResult interactionresult1 = this.use(context.getLevel(), context.getPlayer(), context.getHand()).getResult();
			return interactionresult1 == InteractionResult.CONSUME ? InteractionResult.CONSUME_PARTIAL : interactionresult1;
		} else {
			return interactionresult;
		}
	}

	public InteractionResult place(BlockPlaceContext context) {
		if (!context.canPlace())
			return InteractionResult.FAIL;
		else {
			BlockPlaceContext ctx = getBlockPlaceContext(context);
			if (ctx == null)
				return InteractionResult.FAIL;
			else {
				BlockState blockstate = getStateForPlacement(ctx);
				if (blockstate == null)
					return InteractionResult.FAIL;
				else if (!placeBlock(ctx, blockstate.setValue(CrystalClusterBlock.AGE, stage)))
					return InteractionResult.FAIL;
				else {
					BlockPos blockpos = ctx.getClickedPos();
					Level level = ctx.getLevel();
					Player playerentity = ctx.getPlayer();
					ItemStack itemstack = ctx.getItemInHand();
					BlockState blockstate1 = level.getBlockState(blockpos);
					Block block = blockstate1.getBlock();
					if (block == blockstate.getBlock()) {
						blockstate1 = updateBlockStateFromTag(blockpos, level, itemstack, blockstate1);
						onBlockPlaced(blockpos, level, playerentity, itemstack, blockstate1);
						block.setPlacedBy(level, blockpos, blockstate1, playerentity, itemstack);
						if (playerentity instanceof ServerPlayer)
							CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)playerentity, blockpos, itemstack);
					}
					
					SoundType soundtype = blockstate1.getSoundType(level, blockpos, context.getPlayer());
					level.playSound(playerentity, blockpos, this.getPlaceSound(blockstate1, level, blockpos, context.getPlayer()), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
					itemstack.shrink(1);
					return InteractionResult.SUCCESS;
				}
			}
		}
	}
	
	protected SoundEvent getPlaceSound(BlockState state, Level level, BlockPos pos, Player entity) {
		return state.getSoundType(level, pos, entity).getPlaceSound();
	}
	
	@Nullable
	public BlockPlaceContext getBlockPlaceContext(BlockPlaceContext context) {
		return context;
	}
	
	protected boolean onBlockPlaced(BlockPos pos, Level levelIn, @Nullable Player player, ItemStack stack, BlockState state) {
		return updateCustomBlockEntityTag(levelIn, player, pos, stack);
	}
	
	@Nullable
	protected BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState blockstate = this.getBlock().getStateForPlacement(context);
		return blockstate != null && this.canPlace(context, blockstate) ? blockstate : null;
	}
	
	private BlockState updateBlockStateFromTag(BlockPos pos, Level level, ItemStack stack, BlockState state) {
		BlockState blockstate = state;
		CompoundTag compoundnbt = stack.getTag();
		if (compoundnbt != null) {
			CompoundTag tag = compoundnbt.getCompound(BlockItem.BLOCK_STATE_TAG);
			StateDefinition<Block, BlockState> statecontainer = state.getBlock().getStateDefinition();
			
			for (String s : tag.getAllKeys()) {
				Property<?> prop = statecontainer.getProperty(s);
				if (prop != null) {
					String s1 = tag.get(s).getAsString();
					blockstate = updateState(blockstate, prop, s1);
				}
			}
		}
		
		if (blockstate != state)
			level.setBlock(pos, blockstate, Block.UPDATE_CLIENTS);
		
		return blockstate;
	}
	
	private static <T extends Comparable<T>> BlockState updateState(BlockState state, Property<T> prop, String str) {
		return prop.getValue(str).map((val) -> state.setValue(prop, val)).orElse(state);
	}
	
	protected boolean canPlace(BlockPlaceContext pContext, BlockState pState) {
		Player player = pContext.getPlayer();
		CollisionContext collisioncontext = player == null ? CollisionContext.empty() : CollisionContext.of(player);
		return (!this.mustSurvive() || pState.canSurvive(pContext.getLevel(), pContext.getClickedPos())) && pContext.getLevel().isUnobstructed(pState, pContext.getClickedPos(), collisioncontext);
	}
	
	protected boolean mustSurvive() {
		return true;
	}
	
	protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
		return context.getLevel().setBlock(context.getClickedPos(), state, Block.UPDATE_ALL_IMMEDIATE);
	}
	
	public static boolean updateCustomBlockEntityTag(Level levelIn, @Nullable Player player, BlockPos pos, ItemStack stackIn) {
		MinecraftServer minecraftserver = levelIn.getServer();
        if (minecraftserver != null) {
            CompoundTag compoundtag = stackIn.getTagElement(BlockItem.BLOCK_ENTITY_TAG);
            if (compoundtag != null) {
                BlockEntity blockentity = levelIn.getBlockEntity(pos);
                if (blockentity != null) {
                    if (!levelIn.isClientSide && blockentity.onlyOpCanSetNbt() && (player == null || !player.canUseGameMasterBlocks())) {
                        return false;
                    }

                    CompoundTag compoundtag1 = blockentity.saveWithoutMetadata();
                    CompoundTag compoundtag2 = compoundtag1.copy();
                    compoundtag1.merge(compoundtag);
                    if (!compoundtag1.equals(compoundtag2)) {
                        blockentity.load(compoundtag1);
                        blockentity.setChanged();
                        return true;
                    }
                }
            }

        }
        return false;
    }
	
	public String getDescriptionId() {
		return stage == 3 ? getBlock().getDescriptionId() : super.getDescriptionId();
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level levelIn, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, levelIn, tooltip, flagIn);
		getBlock().appendHoverText(stack, levelIn, tooltip, flagIn);
	}

	public Block getBlock() {
		return this.getBlockRaw() == null ? null : net.minecraftforge.registries.ForgeRegistries.BLOCKS.getDelegateOrThrow(this.getBlockRaw()).get();
	}
	
	private Block getBlockRaw() {
		return block;
	}
}