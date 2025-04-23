package com.wonginnovations.arcana.entities;

import com.google.common.collect.Lists;
import com.wonginnovations.arcana.items.ArcanaItems;
import com.wonginnovations.arcana.systems.spell.Homeable;
import com.wonginnovations.arcana.systems.spell.casts.Cast;
import com.wonginnovations.arcana.util.FluidRaytraceHelper;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SpellEggEntity extends ThrowableItemProjectile implements Homeable {
	private Cast cast;
	private Player caster;

	private List<Class<? extends Entity>> homeTargets = new ArrayList<>();

	private int ignoreTime;
	private Entity ignoreEntity;
	private int lifespan = ((int)Short.MAX_VALUE) + 1;

	public SpellEggEntity(EntityType<? extends SpellEggEntity> type, Level level) {
		super(type, level);
	}

	public SpellEggEntity(Level level, Player thrower, Cast cast) {
		super(ArcanaEntities.SPELL_EGG.get(), thrower, level);
		this.cast = cast;
		this.caster = thrower;
	}

	public SpellEggEntity(Level level, double x, double y, double z) {
		super(ArcanaEntities.SPELL_EGG.get(), x, y, z, level);
	}

	@OnlyIn(Dist.CLIENT)
	public void handleEntityEvent(byte id) {
		if (id == 3) {
			double d0 = 0.08D;

			for (int i = 0; i < 8; ++i) {
				this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(this.getDefaultItem())), this.getX(), this.getY(), this.getZ(), ((double)this.random.nextFloat() - 0.5D) * 0.08D, ((double)this.random.nextFloat() - 0.5D) * 0.08D, ((double)this.random.nextFloat() - 0.5D) * 0.08D);
			}
		}

	}

	@Override
	protected void onHit(@NotNull HitResult result) {
		if (cast != null) {
			if (result.getType() == BlockHitResult.Type.ENTITY) {
				((EntityHitResult) result).getEntity().hurt(level().damageSources().thrown(this, this.getOwner()), 0.5F);
				if (!level().isClientSide)
					cast.useOnEntity(caster, ((EntityHitResult) result).getEntity());
				remove(RemovalReason.DISCARDED);
			}
			if (result.getType() == BlockHitResult.Type.BLOCK) {
				if (!level().isClientSide)
					cast.useOnBlock(caster, level(), ((BlockHitResult) result).getBlockPos());
			}
		}
	}

	@Override
	public void tick() {
//		if (this.throwableShake > 0) {
//			--this.throwableShake;
//		}

		// TODO: Look into Fix
//		if (this.inGround) {
//			this.inGround = false;
//			this.setMotion(this.getMotion().mul((double)(this.rand.nextFloat() * 0.2F), (double)(this.rand.nextFloat() * 0.2F), (double)(this.rand.nextFloat() * 0.2F)));
//		}

		AABB axisalignedbb = this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D);

		for (Entity entity : this.level().getEntities(this, axisalignedbb, (p_213881_0_) -> !p_213881_0_.isSpectator() && p_213881_0_.canBeCollidedWith())) {
			if (entity == this.ignoreEntity) {
				++this.ignoreTime;
				break;
			}

			if (this.getOwner() != null && this.tickCount < 2 && this.ignoreEntity == null) {
				this.ignoreEntity = entity;
				this.ignoreTime = 3;
				break;
			}
		}

		if (tickCount > lifespan) {
			this.remove(RemovalReason.DISCARDED);
		}

		HitResult raytraceresult = FluidRaytraceHelper.rayTrace(this, axisalignedbb, (p_213880_1_) -> {
			return !p_213880_1_.isSpectator() && p_213880_1_.canBeCollidedWith() && p_213880_1_ != this.ignoreEntity;
		}, ClipContext.Block.OUTLINE, true);
		if (this.ignoreEntity != null && this.ignoreTime-- <= 0) {
			this.ignoreEntity = null;
		}

		if (raytraceresult.getType() != HitResult.Type.MISS) {
			if (raytraceresult.getType() == HitResult.Type.BLOCK && this.level().getBlockState(((BlockHitResult)raytraceresult).getBlockPos()).getBlock() == Blocks.NETHER_PORTAL) {
				this.handleInsidePortal(((BlockHitResult)raytraceresult).getBlockPos());
			} else if (!net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
				this.onHit(raytraceresult);
			}
		}

		Vec3 vec3d = this.getDeltaMovement();
		double d0 = this.getX() + vec3d.x;
		double d1 = this.getY() + vec3d.y;
		double d2 = this.getZ() + vec3d.z;
		float f = Mth.sqrt((float) vec3d.horizontalDistance());
		this.setYRot((float) (Mth.atan2(vec3d.x, vec3d.z) * (double)(180F / (float)Math.PI)));

		while(this.getXRot() - this.xRotO >= 180.0F) {
			this.xRotO += 360.0F;
		}

		while(this.getYRot() - this.yRotO < -180.0F) {
			this.yRotO -= 360.0F;
		}

		while(this.getYRot() - this.yRotO >= 180.0F) {
			this.yRotO += 360.0F;
		}

		this.setXRot(Mth.lerp(0.2F, this.xRotO, this.getXRot()));
		this.setYRot(Mth.lerp(0.2F, this.yRotO, this.getYRot()));
		float f1;
		if (this.isInWater()) {
			for (int i = 0; i < 4; ++i) {
				float f2 = 0.25F;
				this.level().addParticle(ParticleTypes.BUBBLE, d0 - vec3d.x * 0.25D, d1 - vec3d.y * 0.25D, d2 - vec3d.z * 0.25D, vec3d.x, vec3d.y, vec3d.z);
			}

			f1 = 0.8F;
		} else {
			f1 = 0.99F;
		}

		this.setDeltaMovement(vec3d.scale(f1));

		this.setPos(d0, d1, d2);

		if (!this.level().isClientSide) {
			this.setSharedFlag(FLAG_GLOWING, this.isCurrentlyGlowing());
		}

		this.baseTick();

		Homeable.startHoming(this);

		super.tick();
	}

	public void setCast(Player caster, Cast cast) {
		this.cast = cast;
		this.caster = caster;
	}

	@Override
	protected @NotNull Item getDefaultItem() {
		return ArcanaItems.AMBER.get();
	}

	@Override
	public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

    public final void enableHoming(Class<? extends Entity>... targets) {
		homeTargets = Lists.newArrayList(targets);
	}

    public void setLifespan(int time) {
		lifespan = time;
    }

	@Override
	public List<Class<? extends Entity>> getHomeables() {
		return homeTargets;
	}
}