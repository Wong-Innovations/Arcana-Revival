package com.wonginnovations.arcana.blocks;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.ArcanaConfig;
import com.wonginnovations.arcana.aspects.AspectLabel;
import com.wonginnovations.arcana.aspects.AspectUtils;
import com.wonginnovations.arcana.aspects.Aspects;
import com.wonginnovations.arcana.blocks.bases.WaterloggableEntityBlock;
import com.wonginnovations.arcana.blocks.pipes.TubeBlock;
import com.wonginnovations.arcana.blocks.entities.JarBlockEntity;
import com.wonginnovations.arcana.items.ArcanaItems;
import com.wonginnovations.arcana.items.MagicDeviceItem;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class JarBlock extends WaterloggableEntityBlock {
	public static final BooleanProperty UP = BooleanProperty.create("up");
	private final Type type;

	public JarBlock(Properties properties, Type type) {
		super(properties);
		this.type = type;
		registerDefaultState(defaultBlockState()
				.setValue(UP, Boolean.FALSE)
				.setValue(WATERLOGGED, Boolean.FALSE));
	}

	public static final VoxelShape SHAPE = box(3, 0, 3, 13, 14, 13);
//	public static final VoxelShape SHAPE = Shapes.or(box(3, 0, 3, 13, 12, 13), box(5, 12, 5, 11, 14, 11)).optimize();

	@Override
	public @NotNull RenderShape getRenderShape(@NotNull BlockState pState) {
		return RenderShape.MODEL;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new JarBlockEntity(this.type, pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
		return (lvl, pos, st, blockEntity) -> {
			if (blockEntity instanceof JarBlockEntity) {
				((JarBlockEntity) blockEntity).tick(lvl, pos, st);
			}
		};
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(UP);
	}

	@Override
	public @NotNull BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context)
				.setValue(UP, false);
	}

	@Override
	public void neighborChanged(BlockState state, Level levelIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		if (levelIn.getBlockState(pos.above()).getBlock() instanceof TubeBlock)
			levelIn.setBlockAndUpdate(pos, state.setValue(UP, true));
		else
			levelIn.setBlockAndUpdate(pos, state.setValue(UP, false));
	}
	
	@Override
	public void onPlace(BlockState state, Level levelIn, BlockPos pos, BlockState oldState, boolean isMoving) {
		if (levelIn.getBlockState(pos.above()).getBlock() instanceof TubeBlock)
			levelIn.setBlockAndUpdate(pos, state.setValue(UP, true));
		else
			levelIn.setBlockAndUpdate(pos, state.setValue(UP, false));
	}

	@Override
	public void setPlacedBy(Level levelIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		if (placer != null && ((JarBlockEntity) Objects.requireNonNull(levelIn.getBlockEntity(pos))).label != null) {
			((JarBlockEntity) Objects.requireNonNull(levelIn.getBlockEntity(pos))).label.direction = getYaw(placer);
		}
		super.setPlacedBy(levelIn, pos, state, placer, stack);
	}

	@Override
	public InteractionResult use(BlockState state, Level levelIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
		JarBlockEntity jar = ((JarBlockEntity) Objects.requireNonNull(levelIn.getBlockEntity(pos)));
		if (jar.label == null && player.getItemInHand(handIn).getItem() == ArcanaItems.LABEL.get()) {
			if (!player.isCreative()) {
				player.getItemInHand(handIn).setCount(player.getItemInHand(handIn).getCount() - 1);
			}
			if (hit.getDirection() != Direction.UP && hit.getDirection() != Direction.DOWN) {
				jar.label = new AspectLabel(hit.getDirection());
			} else {
				jar.label = new AspectLabel(getYaw(player));
			}
		} else if (player.getItemInHand(handIn).getItem() instanceof MagicDeviceItem && player.isCrouching()) {
			playerWillDestroy(levelIn, pos, state, player);
			levelIn.removeBlock(pos, false);
		} else if (jar.label != null && player.getItemInHand(handIn).getItem() == Blocks.AIR.asItem() && player.isCrouching()) {
			if (!player.isCreative()) {
				if (!player.addItem(new ItemStack(ArcanaItems.LABEL.get()))) {
					ItemEntity itementity = new ItemEntity(levelIn,
							player.getX(),
							player.getY(),
							player.getZ(), new ItemStack(ArcanaItems.LABEL.get()));
					itementity.setNoPickUpDelay();
					levelIn.addFreshEntity(itementity);
				}
			}
			jar.label = null;
		} else if (jar.label != null && player.getItemInHand(handIn).getItem() instanceof MagicDeviceItem) {
			if (hit.getDirection() != Direction.UP && hit.getDirection() != Direction.DOWN) {
				jar.label.direction = hit.getDirection();
			} else {
				jar.label.direction = getYaw(player);
			}
			jar.label.seal = Aspects.EMPTY;
		}
		return super.use(state, levelIn, pos, player, handIn, hit);
	}

	@Override
	public void playerDestroy(Level levelIn, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity te, ItemStack stack) {
		if (te instanceof JarBlockEntity jte) {
            if (!levelIn.isClientSide && jte.vis.getHolder(0).getStack().getAmount() == 0 && jte.label == null) {
//				te.setPos(new BlockPos(0, 0, 0)); // TODO: what was this for???
				if (((JarBlockEntity) te).label != null) {
					((JarBlockEntity) te).label.direction = Direction.NORTH;
				}

				dropResources(state, levelIn, pos, te, player, stack);
			}
		}
	}

	@Override
	public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		if (!player.isCreative()) {
			BlockEntity te = level.getBlockEntity(pos);
			if (te instanceof JarBlockEntity jte) {
                if (!level.isClientSide && jte.vis.getHolder(0).getStack().getAmount() != 0 || jte.label != null) {
//					te.setPos(new BlockPos(0, 0, 0));
					if (((JarBlockEntity) te).label != null) {
						((JarBlockEntity) te).label.direction = Direction.NORTH;
					}
					ItemEntity itementity = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), getCloneItemStack(level, pos, state));
					itementity.setNoPickUpDelay();
					level.addFreshEntity(itementity);
				}
			}
		}
		super.playerWillDestroy(level, pos, state, player);
	}

	public static Direction getYaw(LivingEntity player) {
		int yaw = (int)player.getYRot();
		if (yaw<0)              //due to the yaw running a -360 to positive 360
			yaw+=360;    //not sure why it's that way
		yaw+=22;    //centers coordinates you may want to drop this line
		yaw%=360;  //and this one if you want a strict interpretation of the zones
		int facing = yaw/45;  //  360degrees divided by 45 == 8 zones
        return switch (facing) {
            case 0, 1 -> Direction.NORTH;
            case 2, 3 -> Direction.EAST;
            case 4, 5 -> Direction.SOUTH;
            default -> Direction.WEST;
        };
	}

	@Override
	public ItemStack getCloneItemStack(BlockGetter levelIn, BlockPos pos, BlockState state) {
		ItemStack itemstack = super.getCloneItemStack(levelIn, pos, state);
		JarBlockEntity jarTe = (JarBlockEntity) levelIn.getBlockEntity(pos);
		CompoundTag compoundnbt = new CompoundTag();
		jarTe.saveAdditional(compoundnbt);
		if (!compoundnbt.isEmpty())
			itemstack.getOrCreateTag().put("BlockEntityTag", compoundnbt);
		
		return itemstack;
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState block, Level level, BlockPos pos) {
		BlockEntity te = level.getBlockEntity(pos);
		if (te instanceof JarBlockEntity jar) {
            return (int)Math.ceil((jar.vis.getHolder(0).getStack().getAmount() / 100f) * 15);
		}
		return 0;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter levelIn, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, levelIn, tooltip, flagIn);
		if (stack.getTag() != null)
			if (!stack.getTag().isEmpty()) {
				CompoundTag cell = stack.getTag().getCompound("BlockEntityTag").getCompound("aspects").getCompound("cells").getCompound("cell_0");
				if (stack.getTag().getCompound("BlockEntityTag").contains("label")) {
					tooltip.add(Component.literal("Labelled").setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GRAY)));
				}
				if (cell.getInt("amount") > 0) {
					tooltip.add(Component.literal(AspectUtils.getLocalizedAspectDisplayName(Objects.requireNonNull(
							AspectUtils.getAspectByName(cell.getString("aspect")))) + ": " +
							cell.getInt("amount")).setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA)));
				}
				if (ArcanaConfig.JAR_ANIMATION_SPEED.get()>=299792458D) { // Small easter egg ;)
					tooltip.add(Component.literal("\"being faster than light leaves you in the darkness\" -jar").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
				}
			}
	}

	public enum Type {
		BASIC,
		SECURED,
		VOID,
		VACUUM,
		PRESSURE
	}
}