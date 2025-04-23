package com.wonginnovations.arcana.blocks;

import com.wonginnovations.arcana.mixin.BlockAccessor;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DelegatingBlock extends Block {
	protected final Block parentBlock;
	
	public DelegatingBlock(Block blockIn, @Nullable SoundType sound) {
		super(propertiesWithSound(Properties.copy(blockIn), sound));
		this.parentBlock = blockIn;
		
		// Refill the state container - Block does this too early
		StateDefinition.Builder<Block, BlockState> builder = new StateDefinition.Builder<>(this);
//		fillStateContainer(builder);
//		stateDefinition = builder.create(Block::defaultBlockState, BlockState::new);
		registerDefaultState(defaultBlockState());
	}
	
	public DelegatingBlock(Block blockIn) {
		this(blockIn, null);
	}
	
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		if (parentBlock != null)
			((BlockAccessor) parentBlock).invokeCreateBlockStateDefinition(builder);
	}
	
	public FluidState getFluidState(BlockState state) {
		return parentBlock.getFluidState(state);
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static BlockState switchBlock(BlockState state, Block block) {
		BlockState base = block.getStateDefinition().any();
		// A helper method doesn't work here...
		for (Property property : state.getProperties())
			if (base.hasProperty(property))
				base = base.setValue(property, state.getValue(property));
		return base;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState placement = parentBlock.getStateForPlacement(context);
		return placement != null
				? switchBlock(placement, this)
				: null;
	}

	@Override
	public BlockState rotate(BlockState state, LevelAccessor level, BlockPos pos, Rotation direction) {
		return switchBlock(parentBlock.rotate(state, level, pos, direction), this);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return switchBlock(parentBlock.rotate(state, rot), this);
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return switchBlock(parentBlock.mirror(state, mirror), this);
	}

	@Override
	public BlockState getStateAtViewpoint(BlockState state, BlockGetter world, BlockPos pos, Vec3 viewpoint) {
		return switchBlock(parentBlock.getStateAtViewpoint(state, world, pos, viewpoint), this);
	}
	
	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		parentBlock.randomTick(state, level, pos, random);
	}
	
	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
		parentBlock.tick(state, level, pos, rand);
	}

//	@Override
//	public boolean isTransparent(BlockState state) {
//		return parentBlock != null && parentBlock.isTransparent(state);
//	}

	@Override
	public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return parentBlock.getFireSpreadSpeed(state, world, pos, face);
	}
	
	@Override
	public float getExplosionResistance() {
		return parentBlock.getExplosionResistance();
	}
	
	@Override
	public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
		parentBlock.onBlockExploded(state, level, pos, explosion);
	}

	@Override
	public float getExplosionResistance(BlockState state, BlockGetter world, BlockPos pos, Explosion explosion) {
		return parentBlock.getExplosionResistance(state, world, pos, explosion);
	}

	@Override
	public boolean addRunningEffects(BlockState state, Level level, BlockPos pos, Entity entity) {
		return parentBlock.addRunningEffects(state, level, pos, entity);
	}

	@Override
	public void spawnAfterBreak(BlockState pState, ServerLevel pLevel, BlockPos pPos, ItemStack pStack, boolean pDropExperience) {
		parentBlock.spawnAfterBreak(pState, pLevel, pPos, pStack, pDropExperience);
	}
	
//	public boolean onProjectileHit(BlockState state, Level levelObj, RayTraceResult target, ParticleManager manager) {
//		return parentBlock.addHitEffects(state, worldObj, target, manager);
//	}

	@Override
	public boolean addLandingEffects(BlockState state1, ServerLevel level, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
		return parentBlock.addLandingEffects(state1, level, pos, state2, entity, numberOfParticles);
	}
	
	public float getFriction() {
		return parentBlock.getFriction();
	}
	
//	public boolean isToolEffective(BlockState state, ToolType tool) {
//		return parentBlock.isToolEffective(state, tool);
//	}
	
	public boolean canHarvestBlock(BlockState state, BlockGetter world, BlockPos pos, Player player) {
		return parentBlock.canHarvestBlock(state, world, pos, player);
	}
	
	@Override
	public boolean isRandomlyTicking(BlockState state) {
		return parentBlock.isRandomlyTicking(state);
	}
	
	@Override
	public void animateTick(BlockState state, Level level, BlockPos position, RandomSource rand) {
		parentBlock.animateTick(state, level, position, rand);
	}
	
	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
		return parentBlock.propagatesSkylightDown(state, world, pos);
	}
	
	/*@Override
	public int tickRate(IWorldReader world) {
		return parentBlock.tickRate(world);
	}*/
	
	@Override
	public void popExperience(ServerLevel level, BlockPos pos, int amount) {
		parentBlock.popExperience(level, pos, amount);
	}
	
	@Override
	public void wasExploded(Level level, BlockPos pos, Explosion explosion) {
		parentBlock.wasExploded(level, pos, explosion);
	}
	
	@Override
	public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
		parentBlock.stepOn(level, pos, state, entity);
	}
	
//	@Override
//	public boolean canSpawnInBlock() {
//		return parentBlock.canSpawnInBlock();
//	}
	
	@Override
	public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float num) {
		parentBlock.fallOn(level, state, pos, entity, num);
	}
	
	@Override
	public void updateEntityAfterFallOn(BlockGetter level, Entity entity) {
		parentBlock.updateEntityAfterFallOn(level, entity);
	}
	
	@Override
	public float getSpeedFactor() {
		return parentBlock.getSpeedFactor();
	}
	
	@Override
	public float getJumpFactor() {
		return parentBlock.getJumpFactor();
	}
	
	/*@Override
	public void onProjectileCollision(Level level, BlockState state, BlockHitResult trace, Entity entity) {
		parentBlock.onProjectileCollision(world, state, trace, entity);
	}*/
	
//	@Override
//	public void fillWithRain(Level level, BlockPos pos) {
//		parentBlock.fillWithRain(world, pos);
//	}
	
//	@Override
//	public OffsetType getOffsetType() {
//		return parentBlock.getOffsetType();
//	}
//
//	@Override
//	public boolean isVariableOpacity() {
//		return parentBlock.isVariableOpacity();
//	}
//
//	@Nullable
//	@Override
//	public ToolType getHarvestTool(BlockState state) {
//		return parentBlock.getHarvestTool(state);
//	}
//
//	@Override
//	public int getHarvestLevel(BlockState state) {
//		return parentBlock.getHarvestLevel(state);
//	}
	
	@Override
	public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {
		return parentBlock.canSustainPlant(state, world, pos, facing, plantable);
	}
	
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return parentBlock.getRenderShape(state);
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter levelIn, BlockPos pos, CollisionContext context) {
		return parentBlock.getCollisionShape(state, levelIn, pos, context);
	}
	
//	@Override
//	public VoxelShape getRaytraceShape(BlockState state, BlockGetter levelIn, BlockPos pos) {
//		return parentBlock.getRaytraceShape(state, levelIn, pos);
//	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter levelIn, BlockPos pos, CollisionContext context) {
		return parentBlock.getShape(state, levelIn, pos, context);
	}
	
//	@Override
//	public boolean isValidPosition(BlockState state, IWorldReader levelIn, BlockPos pos) {
//		return parentBlock.isValidPosition(state, levelIn, pos);
//	}
//
//	@Override
//	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, ILevel levelIn, BlockPos currentPos, BlockPos facingPos) {
//		return parentBlock.updatePostPlacement(stateIn, facing, facingState, levelIn, currentPos, facingPos);
//	}
//
//	@Override
//	public boolean isReplaceable(BlockState state, BlockPlaceContext useContext) {
//		return parentBlock.isReplaceable(state, useContext);
//	}
//
//	@Override
//	public boolean isReplaceable(BlockState state, Fluid fluid) {
//		return parentBlock.isReplaceable(state, fluid);
//	}
//
//	@Override
//	public void onBlockClicked(BlockState state, Level level, BlockPos pos, Player player) {
//		parentBlock.onBlockClicked(state, world, pos, player);
//	}
//
//	@Override
//	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, Hand hand, BlockHitResult raytrace) {
//		return parentBlock.use(state, world, pos, player, hand, raytrace);
//	}
//
//	@Override
//	public int getExpDrop(BlockState state, IWorldReader world, BlockPos pos, int fortune, int silktouch) {
//		return parentBlock.getExpDrop(state, world, pos, fortune, silktouch);
//	}
//
//	@Override
//	public int getLightValue(BlockState state, BlockGetter world, BlockPos pos) {
//		return parentBlock.getLightValue(state, world, pos);
//	}
	
	private static Properties propertiesWithSound(Properties properties, @Nullable SoundType soundType) {
		// FIXME: state-properties are added too late, so we have to clear these two block-properties to avoid a crash
		properties.lightLevel(__ -> 0);
		properties.mapColor(DyeColor.PURPLE);
		if (soundType == null)
			return properties;
		else
			return properties.sound(soundType);
	}

	@Override
	public MutableComponent getName() {
		return parentBlock.getName();
	}
}