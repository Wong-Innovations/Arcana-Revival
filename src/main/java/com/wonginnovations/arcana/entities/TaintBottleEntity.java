package com.wonginnovations.arcana.entities;

import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.blocks.DelegatingBlock;
import com.wonginnovations.arcana.items.ArcanaItems;
import com.wonginnovations.arcana.systems.taint.Taint;
import com.wonginnovations.arcana.world.AuraView;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.wonginnovations.arcana.systems.taint.Taint.UNTAINTED;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TaintBottleEntity extends ThrowableItemProjectile {
	
	public TaintBottleEntity(EntityType<? extends ThrowableItemProjectile> type, Level level) {
		super(type, level);
	}
	
	protected Item getDefaultItem() {
		return ArcanaItems.TAINT_IN_A_BOTTLE.get();
	}
	
	public TaintBottleEntity(LivingEntity thrower, Level level) {
		super(ArcanaEntities.TAINT_BOTTLE.get(), thrower, level);
	}

	@Override
	protected void onHit(HitResult result) {
		if (!level().isClientSide()) {
			// pick some blocks and taint them
			// aim to taint 6 blocks within a 5x3x5 area, fail after 12 attempts
			int tainted = 0;
			BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
			for (int tries = 0; tries < 12 && tainted < 6; tries++) {
				pos.set(this.blockPosition()).move(level().random.nextInt(5) - 2, level().random.nextInt(3) - 1, level().random.nextInt(5) - 2);
				BlockState state = level().getBlockState(pos);
				if (!state.isAir() && !Taint.isTainted(state.getBlock()) && !Taint.isBlockProtectedByPureNode(level(), pos)) {
					Block to = Taint.getTaintedOfBlock(state.getBlock());
					if (to != null) {
						level().setBlockAndUpdate(pos, DelegatingBlock.switchBlock(state, to).setValue(UNTAINTED, false));
						tainted++;
					}
				}
			}
			// add some flux too
			AuraView.SIDED_FACTORY.apply(level()).addFluxAt(this.blockPosition(), level().random.nextInt(3) + 3 + (6 - tainted));
			// add some particles
			level().levelEvent(2007, new BlockPos(this.blockPosition()), 0xa200ff);
			// and die
			this.remove(RemovalReason.KILLED);
		}
	}
	
	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}