package com.wonginnovations.arcana.blocks.pipes;

import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.aspects.AspectStack;
import com.wonginnovations.arcana.aspects.handlers.AspectHandler;
import com.wonginnovations.arcana.blocks.entities.ArcanaBlockEntities;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.minecraft.core.Direction.*;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TubeBlockEntity extends BlockEntity {
	
	protected static final int MAX_SPECKS = 1000;
	
	List<AspectSpeck> specks = new ArrayList<>();
	
	public TubeBlockEntity(BlockPos pos, BlockState state) {
		this(ArcanaBlockEntities.ASPECT_TUBE.get(), pos, state);
	}
	
	protected TubeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public void tick(Level level, BlockPos pos, BlockState state) {
		// Move every speck along by (speed / 20f).
		// If there is a connection in the speck's direction, keep moving it until its position exceeds 0.5f.
			// When it does, pass it to pipes or insert it into AspectHandlers.
		// If not, keep moving it until its position exceeds SIZE.
			// Then make it move in the direction of a connection.
			// Prefer down, then random horizontals, then up.
		// If a speck exceeds SIZE perpendicularly to their direction (how?), bring it back to the center.
		// If there's too many specks (1000?), explode.
		if (specks.size() > MAX_SPECKS) {
			specks.clear();
			// also explode or smth
		}
		List<AspectSpeck> toRemove = new ArrayList<>();
		for (AspectSpeck speck : specks) {
			Direction dir = speck.direction;
			speck.pos += speck.speed / 20f;
			speck.stuck = false;
			boolean connected = connectedTo(dir);
			float max = connected ? 1 : .5f;
			Optional<Direction> forcedDir = redirect(speck, connected);
			if (forcedDir.isPresent() && speck.pos >= .5f && speck.pos <= max) {
				speck.direction = forcedDir.get();
				setChanged();
			} else if (speck.pos > max) {
				setChanged();
				// transfer, pass, or bounce
				BlockPos dest = pos.offset(dir.getNormal());
				BlockEntity te = level.getBlockEntity(dest);
				if (te instanceof TubeBlockEntity tube && connected) {
                    if (tube.enabled()) {
						toRemove.add(speck);
						tube.addSpeck(speck);
						speck.pos = speck.pos % 1;
					}
				} else if (AspectHandler.getOptional(te).isPresent() && connected) {
					float inserted = AspectHandler.getFrom(te).insert(speck.payload);
					if (inserted >= speck.payload.getAmount())
						toRemove.add(speck);
					else {
						speck.payload = new AspectStack(speck.payload.getAspect(), speck.payload.getAmount() - inserted);
						speck.direction = speck.direction.getOpposite();
						speck.pos = 1 - speck.pos;
						if (speck.payload.getAmount() < 0.5) // remove specks that can't output
							toRemove.add(speck);
					}
				} else if (forcedDir.isEmpty()) { // random bounce
					if (connectedTo(DOWN) && dir != UP)
						speck.direction = DOWN;
					else if (connectedTo(NORTH) || connectedTo(SOUTH) || connectedTo(EAST) || connectedTo(WEST)) {
						List<Direction> directions = new ArrayList<>();
						if (connectedTo(NORTH)) directions.add(NORTH);
						if (connectedTo(SOUTH)) directions.add(SOUTH);
						if (connectedTo(EAST)) directions.add(EAST);
						if (connectedTo(WEST)) directions.add(WEST);
						if (directions.size() > 1) directions.remove(dir.getOpposite()); // don't bounce back if possible
						speck.direction = directions.get(level.random.nextInt(directions.size()));
					} else if (connectedTo(UP))
						speck.direction = UP;
				} else // forced direction
					if (connectedTo(forcedDir.get()))
						speck.direction = forcedDir.get();
				
				if (!toRemove.contains(speck) && speck.direction == dir) {
					// We can't output or redirect it
					speck.pos = 0.5f;
					speck.stuck = true;
				}
			}
		}
		specks.removeAll(toRemove);
	}
	
	protected boolean connectedTo(Direction dir) {
		BlockState state = getLevel().getBlockState(getBlockPos());
		if (!state.getValue(PipeBlock.PROPERTY_BY_DIRECTION.get(dir)))
			return false;
		BlockEntity target = level.getBlockEntity(getBlockPos().offset(dir.getNormal()));
		if (target instanceof TubeBlockEntity tube) {
            return tube.enabled();
		} else if (target != null) {
			AspectHandler vis = AspectHandler.getFrom(target);
			// add up the available space (capacity - amount) of all holders
			// voiding holders are always considered to have 1 space available - we only check == 0 anyways
			return vis == null || vis.getHolders().stream().mapToDouble(holder -> holder.voids() ? 1 : holder.getCapacity() - holder.getStack().getAmount()).sum() != 0;
		}
		return true;
	}

	protected Optional<Direction> redirect(AspectSpeck speck, boolean canPass) {
		return Optional.empty();
	}
	
	public void addSpeck(AspectSpeck speck) {
		// don't add specks that can't transfer
		if (speck.payload.getAmount() >= 0.5)
			specks.add(speck);
	}
	
	public List<AspectSpeck> getSpecks() {
		return specks;
	}
	
	public boolean enabled() {
		return true;
	}

	@Override
	public void saveAdditional(@NotNull CompoundTag compound) {
		super.saveAdditional(compound);
		ListTag specks = new ListTag();
		for (AspectSpeck speck : this.specks)
			specks.add(speck.toNbt());
		compound.put("specks", specks);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		ListTag specksList = tag.getList("specks", Tag.TAG_COMPOUND);
		specks.clear();
		for (Tag speckInbt : specksList) {
			CompoundTag speckTag = (CompoundTag)speckInbt;
			specks.add(AspectSpeck.fromNbt(speckTag));
		}
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = new CompoundTag();
		saveAdditional(tag);
		return tag;
	}

}