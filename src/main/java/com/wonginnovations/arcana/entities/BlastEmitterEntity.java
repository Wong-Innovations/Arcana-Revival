package com.wonginnovations.arcana.entities;

import com.wonginnovations.arcana.systems.spell.casts.Cast;
import com.wonginnovations.arcana.systems.spell.casts.ICast;
import com.wonginnovations.arcana.util.Pair;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@SuppressWarnings("unchecked") // Yes IntelliJ I checked that don't scream at me
public class BlastEmitterEntity extends Entity {
	private static final EntityDataAccessor<Float> RADIUS;
	private static final EntityDataAccessor<Float> CURRENT_RADIUS;
	private static final EntityDataAccessor<Integer> COOLDOWN;
	
	private final List<LivingEntity> wasDamaged = new ArrayList<>();
	private int cooldown = 0;
	private ICast spell;
	private Player caster;
	private Pair<Boolean, Class<? extends LivingEntity>[]> blackWhiteTargetList = Pair.of(true,new Class[]{LivingEntity.class});
	private int autodestructionCooldown = 0;
	private boolean extendable = false;
	
	public BlastEmitterEntity(Level levelIn, Player caster, float radius) {
		super(ArcanaEntities.BLAST_EMITTER.get(), levelIn);
		this.setRadius(radius);
		this.setCaster(caster);
	}
	
	public BlastEmitterEntity(EntityType<BlastEmitterEntity> type, Level levelIn) {
		super(type, levelIn);
	}
	
	public void setSpell(ICast spell) {
		this.spell = spell;
	}
	
	public void setCaster(Player caster) {
		this.caster = caster;
	}

	@Override
	protected void defineSynchedData() {
		this.entityData.define(RADIUS, 0.8F);
		this.entityData.define(CURRENT_RADIUS, 0.0F);
		this.entityData.define(COOLDOWN, 0);
	}

	@Override
	public void tick() {
		super.tick();
		if (cooldown >= getCooldown()) {
			
			setCurrentRadius(getCurrentRadius() + 0.4f);
			
			Random rand = new Random();
			ParticleOptions particle = ParticleTypes.BUBBLE;
			float currRadius = getCurrentRadius();
			float surface = (3.1415927F * getRadius() * getRadius()) / ((currRadius + 0.1f) / 2);
			float randomizedPi;
			float spread;
			float offsetX;
			int color;
			int r;
			int g;
			if (currRadius < (getRadius() * 2)) {
				if (level().isClientSide) {
					for (int i = 0; (float) i < surface; ++i) {
						randomizedPi = rand.nextFloat() * 6.2831855F;
						spread = 0.8f * currRadius;
						offsetX = Mth.cos(randomizedPi) * spread;
						float offsetZ = Mth.sin(randomizedPi) * spread;
						if (particle.getType() == ParticleTypes.ENTITY_EFFECT) {
							color = Color.CYAN.getRGB();
							r = color >> 16 & 255;
							g = color >> 8 & 255;
							int b = color & 255;
							level().addParticle(particle, getX() + (double) offsetX, getY(), getZ() + (double) offsetZ, (float) r / 255.0F, (float) g / 255.0F, (float) b / 255.0F);
						} else {
							level().addParticle(particle, getX() + (double) offsetX, getY(), getZ() + (double) offsetZ, (0.5D - rand.nextDouble()) * 0.15D, 0.009999999776482582D, (0.5D - rand.nextDouble()) * 0.15D);
						}
					}
				} else {
					if (currRadius < getRadius()) {
						if (blackWhiteTargetList.getFirst())
							for (Class<? extends LivingEntity> selEntity : blackWhiteTargetList.getSecond()) {
								executeSpellOnEntitiesInAABB(currRadius, selEntity);
							}
						else executeSpellOnEntitiesInAABB(currRadius, LivingEntity.class);
					}
				}
			} else {
				if (autodestructionCooldown >= 160)
					this.remove(RemovalReason.DISCARDED);
				else autodestructionCooldown++;
			}
		}
		cooldown++;
		if (cooldown >= Short.MAX_VALUE)
			this.remove(RemovalReason.DISCARDED);
	}

	@Override
	protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
	}

	@Override
	protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
	}

	private void executeSpellOnEntitiesInAABB(float currRadius, Class<? extends LivingEntity> selEntity) {
		List<LivingEntity> entities = (List<LivingEntity>) level().getEntitiesOfClass(selEntity,
				new AABB(getX() - currRadius, getY() - currRadius, getZ() - currRadius, getX() + currRadius, getY() + currRadius, getZ() + currRadius),
				(LivingEntity entity) -> entity.isAlive()
		);
		for (LivingEntity leInBox : entities) {
			if (blackWhiteTargetList.getFirst() || Arrays.stream(blackWhiteTargetList.getSecond()).noneMatch(streamed -> streamed == leInBox.getClass()))
				if (!wasDamaged.contains(leInBox)) {
					leInBox.hurt(level().damageSources().magic(), 0.6f);
					if (extendable)
						setRadius(getRadius()+0.4f);
					((Cast)spell).useOnEntity(caster,leInBox);
					wasDamaged.add(leInBox);
				}
		}
	}
	
	private static void onCloudhDeath() {
	}
	
	@Override
	public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
	
	public void allowToExtend() {
		extendable = true;
	}
	
	public float getRadius() {
		try{
			return this.getEntityData().get(RADIUS);
		} catch (NullPointerException exception) {
			exception.printStackTrace();
			return 0;
		}
	}
	public void setRadius(float radius) {
		if (!this.level().isClientSide) {
			this.getEntityData().set(RADIUS, radius);
		}
	}
	
	public float getCooldown() {
		try{
			return this.getEntityData().get(COOLDOWN);
		} catch (NullPointerException exception) {
			exception.printStackTrace();
			return 0;
		}
	}
	public void setCooldown(int ticks) {
		if (!this.level().isClientSide) {
			this.getEntityData().set(COOLDOWN, ticks);
		}
	}
	
	public float getCurrentRadius() {
		try{
			return this.getEntityData().get(CURRENT_RADIUS);
		} catch (NullPointerException exception) {
			exception.printStackTrace();
			return 0;
		}
	}
	public void setCurrentRadius(float radius) {
		if (!this.level().isClientSide) {
			this.getEntityData().set(CURRENT_RADIUS, radius);
		}
	}
	
	static {
		RADIUS = SynchedEntityData.defineId(BlastEmitterEntity.class, EntityDataSerializers.FLOAT);
		CURRENT_RADIUS = SynchedEntityData.defineId(BlastEmitterEntity.class, EntityDataSerializers.FLOAT);
		COOLDOWN = SynchedEntityData.defineId(BlastEmitterEntity.class, EntityDataSerializers.INT);
	}
	
	@SafeVarargs
	public final void makeBlackWhiteList(boolean whitelistMode, Class<? extends LivingEntity>... targets) {
		blackWhiteTargetList = Pair.of(whitelistMode,targets);
	}
}
