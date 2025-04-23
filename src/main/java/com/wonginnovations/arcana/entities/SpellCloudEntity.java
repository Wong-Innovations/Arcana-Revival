package com.wonginnovations.arcana.entities;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.wonginnovations.arcana.systems.spell.Homeable;
import com.wonginnovations.arcana.systems.spell.casts.Cast;
import com.wonginnovations.arcana.systems.spell.casts.Casts;
import com.wonginnovations.arcana.systems.spell.casts.ICast;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

public class SpellCloudEntity extends Entity implements Homeable {
	private static final Logger PRIVATE_LOGGER = LogManager.getLogger();
	private static final EntityDataAccessor<Float> RADIUS;
	private static final EntityDataAccessor<Integer> COLOR;
	private static final EntityDataAccessor<Boolean> IGNORE_RADIUS;
	private static final EntityDataAccessor<ParticleOptions> PARTICLE;
	private ICast spell;
	private final Map<Entity, Integer> reapplicationDelayMap = Maps.newHashMap();
	private int duration;
	private int waitTime;
	private int reapplicationDelay;
	private boolean colorSet;
	private int durationOnUse;
	private float radiusOnUse;
	private float radiusPerTick;
	private LivingEntity owner;
	private UUID ownerUniqueId;

	private List<Class<? extends Entity>> homeTargets = new ArrayList<>();

	public void enableHoming(Class<? extends Entity>... targets) {
		this.homeTargets = Lists.newArrayList(targets);
	}

	@Override
	public List<Class<? extends Entity>> getHomeables() {
		return homeTargets;
	}

	public static class CloudVariableGrid {
		public Player player;
		Level level;
		Vec3 area;
		int rMultP;

		public CloudVariableGrid(Player player, Level level, Vec3 positionVec, int i) {
		}
	}

	public SpellCloudEntity(EntityType<? extends SpellCloudEntity> entityType, Level level) {
		super(entityType, level);
		this.noPhysics = true;
		this.setRadius(3.0F);
	}

	public SpellCloudEntity(Level level, double x, double y, double z) {
		this(ArcanaEntities.SPELL_CLOUD.get(), level);
		this.setPos(x, y, z);
	}

	public SpellCloudEntity(Level level, Vec3 vec) {
		this(ArcanaEntities.SPELL_CLOUD.get(), level);
		this.setPos(vec.x,vec.y,vec.z);
	}

	public void setRadius(float radius) {
		if (!this.level().isClientSide) {
			this.getEntityData().set(RADIUS, radius);
		}

	}

	@Override
	protected void defineSynchedData() {
		this.entityData.define(COLOR, 0);
		this.entityData.define(RADIUS, 0.5F);
		this.entityData.define(IGNORE_RADIUS, false);
		this.entityData.define(PARTICLE, ParticleTypes.ENTITY_EFFECT);
	}

	@Override
	public void refreshDimensions() {
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		super.refreshDimensions();
		this.setPos(x, y, z);
	}

	public float getRadius() {
		return this.getEntityData().get(RADIUS);
	}

	public void setSpell(ICast spell) {
		this.spell = spell;
		if (!this.colorSet) {
			this.updateFixedColor();
		}
	}

	private void updateFixedColor() {
		if (this.spell == null) {
			this.getEntityData().set(COLOR, 0);
		} else {
			this.getEntityData().set(COLOR, this.spell.getSpellAspect().getColorRange().get(3));
		}
	}

	public int getColor() {
		return this.getEntityData().get(COLOR);
	}

	public void setColor(int color) {
		this.colorSet = true;
		this.getEntityData().set(COLOR, color);
	}

	public ParticleOptions getParticleOptions() {
		return this.getEntityData().get(PARTICLE);
	}

	public void setParticleData(ParticleOptions options) {
		this.getEntityData().set(PARTICLE, options);
	}

	protected void setIgnoreRadius(boolean radius) {
		this.getEntityData().set(IGNORE_RADIUS, radius);
	}

	public boolean shouldIgnoreRadius() {
		return this.getEntityData().get(IGNORE_RADIUS);
	}

	public int getDuration() {
		return this.duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public void tick() {
		super.tick();

		boolean ignores = this.shouldIgnoreRadius();
		float radius = this.getRadius();
		if (this.level().isClientSide) {
			ParticleOptions particleOptions = this.getParticleOptions();
			float scalingFactor;
			float xOffset;
			float zOffset;
			int xSpeed;
			int ySpeed;
			int zSpeed;
			if (ignores) {
				if (this.random.nextBoolean()) {
					for (int lvt_4_1_ = 0; lvt_4_1_ < 2; ++lvt_4_1_) {
						float lvt_5_1_ = this.random.nextFloat() * 6.2831855F;
						scalingFactor = Mth.sqrt(this.random.nextFloat()) * 0.2F;
						xOffset = Mth.cos(lvt_5_1_) * scalingFactor;
						zOffset = Mth.sin(lvt_5_1_) * scalingFactor;
						if (particleOptions.getType() == ParticleTypes.ENTITY_EFFECT) {
							int lvt_9_1_ = this.random.nextBoolean() ? 16777215 : this.getColor();
							xSpeed = lvt_9_1_ >> 16 & 255;
							ySpeed = lvt_9_1_ >> 8 & 255;
							zSpeed = lvt_9_1_ & 255;
							this.level().addParticle(particleOptions, this.getX() + (double) xOffset, this.getY(), this.getZ() + (double) zOffset, (float) xSpeed / 255.0F, (float) ySpeed / 255.0F, (float) zSpeed / 255.0F);
						} else {
							this.level().addParticle(particleOptions, this.getX() + (double) xOffset, this.getY(), this.getZ() + (double) zOffset, 0.0D, 0.0D, 0.0D);
						}
					}
				}
			} else {
				float lvt_4_2_ = 3.1415927F * radius * radius;

				for (int lvt_5_2_ = 0; (float) lvt_5_2_ < lvt_4_2_; ++lvt_5_2_) {
					scalingFactor = this.random.nextFloat() * 6.2831855F;
					xOffset = Mth.sqrt(this.random.nextFloat()) * radius;
					zOffset = Mth.cos(scalingFactor) * xOffset;
					float lvt_9_2_ = Mth.sin(scalingFactor) * xOffset;
					if (particleOptions.getType() == ParticleTypes.ENTITY_EFFECT) {
						xSpeed = this.getColor();
						ySpeed = xSpeed >> 16 & 255;
						zSpeed = xSpeed >> 8 & 255;
						int lvt_13_1_ = xSpeed & 255;
						this.level().addParticle(particleOptions, this.getX() + (double) zOffset, this.getY(), this.getZ() + (double) lvt_9_2_, (float) ySpeed / 255.0F, (float) zSpeed / 255.0F, (float) lvt_13_1_ / 255.0F);
					} else {
						this.level().addParticle(particleOptions, this.getX() + (double) zOffset, this.getY(), this.getZ() + (double) lvt_9_2_, (0.5D - this.random.nextDouble()) * 0.15D, 0.009999999776482582D, (0.5D - this.random.nextDouble()) * 0.15D);
					}
				}
			}
		} else {
			Homeable.startHoming(this);
			
			if (this.tickCount >= this.waitTime + this.duration) {
				this.remove(RemovalReason.DISCARDED);
				return;
			}

			boolean lvt_3_2_ = this.tickCount < this.waitTime;
			if (ignores != lvt_3_2_) {
				this.setIgnoreRadius(lvt_3_2_);
			}

			if (lvt_3_2_) {
				return;
			}

			if (this.radiusPerTick != 0.0F) {
				radius += this.radiusPerTick;
				if (radius < 0.5F) {
					this.remove(RemovalReason.DISCARDED);
					return;
				}

				this.setRadius(radius);
			}

			if (this.tickCount % 5 == 0) {
				Iterator lvt_4_3_ = this.reapplicationDelayMap.entrySet().iterator();

				while (lvt_4_3_.hasNext()) {
					Map.Entry<net.minecraft.world.entity.Entity, Integer> lvt_5_3_ = (Map.Entry) lvt_4_3_.next();
					if (this.tickCount >= lvt_5_3_.getValue()) {
						lvt_4_3_.remove();
					}
				}

				List<LivingEntity> lvt_5_4_ = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox());
				if (!lvt_5_4_.isEmpty()) {
					Iterator<LivingEntity> var25 = lvt_5_4_.iterator();

					while (true) {
						LivingEntity entity;
						double lvt_12_3_;
						do {
							do {
								do {
									if (!var25.hasNext()) {
										return;
									}

									entity = var25.next();
								} while (this.reapplicationDelayMap.containsKey(entity));
							} while (!entity.isAffectedByPotions());

							double lvt_8_3_ = entity.getX() - this.getX();
							double lvt_10_3_ = entity.getZ() - this.getZ();
							lvt_12_3_ = lvt_8_3_ * lvt_8_3_ + lvt_10_3_ * lvt_10_3_;
						} while (lvt_12_3_ > (double) (radius * radius));

						this.reapplicationDelayMap.put(entity, this.tickCount + this.reapplicationDelay);

						if (spell != null)
							((Cast) spell).useOnEntity((Player) owner, entity);

						if (this.radiusOnUse != 0.0F) {
							radius += this.radiusOnUse;
							if (radius < 0.5F) {
								this.remove(RemovalReason.DISCARDED);
								return;
							}

							this.setRadius(radius);
						}

						if (this.durationOnUse != 0) {
							this.duration += this.durationOnUse;
							if (this.duration <= 0) {
								this.remove(RemovalReason.DISCARDED);
								return;
							}
						}
					}
				}
			}
		}

	}

	public void setRadiusOnUse(float radius) {
		this.radiusOnUse = radius;
	}

	public void setRadiusPerTick(float radius) {
		this.radiusPerTick = radius;
	}

	public void setWaitTime(int time) {
		this.waitTime = time;
	}

	public void setOwner(@Nullable LivingEntity owner) {
		this.owner = owner;
		this.ownerUniqueId = owner == null ? null : owner.getUUID();
	}


	public @Nullable LivingEntity getOwner() {
		if (this.owner == null && this.ownerUniqueId != null && this.level() instanceof ServerLevel) {
			Entity entity = ((ServerLevel) this.level()).getEntity(this.ownerUniqueId);
			if (entity instanceof LivingEntity) {
				this.owner = (LivingEntity) entity;
			}
		}

		return this.owner;
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compoundNBT) {
		this.tickCount = compoundNBT.getInt("Age");
		this.duration = compoundNBT.getInt("Duration");
		this.waitTime = compoundNBT.getInt("WaitTime");
		this.reapplicationDelay = compoundNBT.getInt("ReapplicationDelay");
		this.durationOnUse = compoundNBT.getInt("DurationOnUse");
		this.radiusOnUse = compoundNBT.getFloat("RadiusOnUse");
		this.radiusPerTick = compoundNBT.getFloat("RadiusPerTick");
		this.setRadius(compoundNBT.getFloat("Radius"));
		this.ownerUniqueId = compoundNBT.getUUID("OwnerUUID");
		if (compoundNBT.contains("Particle", 8)) {
			try {
				this.setParticleData(ParticleArgument.readParticle(new StringReader(compoundNBT.getString("Particle")), BuiltInRegistries.PARTICLE_TYPE.asLookup()));
			} catch (CommandSyntaxException var5) {
				PRIVATE_LOGGER.warn("Couldn't load custom particle {}", compoundNBT.getString("Particle"), var5);
			}
		}

		if (compoundNBT.contains("Color", 99)) {
			this.setColor(compoundNBT.getInt("Color"));
		}

		if (compoundNBT.contains("Spell", 8)) {
			this.setSpell(Casts.castMap.get(new ResourceLocation(compoundNBT.getString("Spell"))));
		}

	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compoundNBT) {
		compoundNBT.putInt("Age", this.tickCount);
		compoundNBT.putInt("Duration", this.duration);
		compoundNBT.putInt("WaitTime", this.waitTime);
		compoundNBT.putInt("ReapplicationDelay", this.reapplicationDelay);
		compoundNBT.putInt("DurationOnUse", this.durationOnUse);
		compoundNBT.putFloat("RadiusOnUse", this.radiusOnUse);
		compoundNBT.putFloat("RadiusPerTick", this.radiusPerTick);
		compoundNBT.putFloat("Radius", this.getRadius());
		compoundNBT.putString("Particle", this.getParticleOptions().writeToString());
		if (this.ownerUniqueId != null) {
			compoundNBT.putUUID("OwnerUUID", this.ownerUniqueId);
		}

		if (this.colorSet) {
			compoundNBT.putInt("Color", this.getColor());
		}

		if (this.spell != null) {
			compoundNBT.putString("spell", ((Cast) spell).getId().toString()); // TODO: REPLACE (SPELL) wit (ISPELL)
		}
	}

	@Override
	public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> dataParameter) {
		if (RADIUS.equals(dataParameter)) {
			this.refreshDimensions();
		}

		super.onSyncedDataUpdated(dataParameter);
	}

	@Override
	public @NotNull PushReaction getPistonPushReaction() {
		return PushReaction.IGNORE;
	}

	@Override
	public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	public EntityDimensions getSize(Pose pose) {
		return EntityDimensions.scalable(this.getRadius() * 2.0F, 0.5F);
	}

	static {
		// TODO: used to be AreaEffectCloud.class?
		RADIUS = SynchedEntityData.defineId(SpellCloudEntity.class, EntityDataSerializers.FLOAT);
		COLOR = SynchedEntityData.defineId(SpellCloudEntity.class, EntityDataSerializers.INT);
		IGNORE_RADIUS = SynchedEntityData.defineId(SpellCloudEntity.class, EntityDataSerializers.BOOLEAN);
		PARTICLE = SynchedEntityData.defineId(SpellCloudEntity.class, EntityDataSerializers.PARTICLE);
	}
}