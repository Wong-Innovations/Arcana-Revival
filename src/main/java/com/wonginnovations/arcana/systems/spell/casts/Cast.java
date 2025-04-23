package com.wonginnovations.arcana.systems.spell.casts;

import com.wonginnovations.arcana.aspects.Aspect;
import com.wonginnovations.arcana.entities.*;
import com.wonginnovations.arcana.items.ArcanaItems;
import com.wonginnovations.arcana.util.Pair;
import com.wonginnovations.arcana.util.RayTraceUtils;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.wonginnovations.arcana.aspects.Aspects.*;

/**
 * ISpell class but it self registers.
 */
@SuppressWarnings("unchecked")
public abstract class Cast implements ICast {

	public CompoundTag data = new CompoundTag();

	public Cast() {
		CastRegistry.addCast(getId(),this);
	}

	public abstract ResourceLocation getId();

	public abstract InteractionResult useOnBlock(Player caster, Level level, BlockPos blockTarget);
	public abstract InteractionResult useOnPlayer(Player playerTarget);
	public abstract InteractionResult useOnEntity(Player caster, Entity entityTarget);

	public static float getArrowVelocity(int charge) {
		float f = (float)charge / 20.0F;
		f = (f * f + f * 2.0F) / 3.0F;
		if (f > 1.0F) {
			f = 1.0F;
		}

		return f;
	}

	@Override
	public void use(UUID spellUUID, Level level, Player player, Object sender, Pair<Aspect, Aspect> cast, ICast.Action action) {
		/*
		TODO LIST OF NOT ADDED CASTS:
		Chaos
		Water
		Fire+SLOTH
		Earth+ENVY
		 */
		if (action == ICast.Action.USE) {
			if (cast.getFirst() == AIR) {
				/*
				- Creates Cloud
				- Targets Entities inside
				- Lingering effect
				 */
				int raytraceDistance = 24;

 				BlockPos pos = RayTraceUtils.getTargetBlockPos(player, level, raytraceDistance);
				Vec3 vec = new Vec3(pos.getX(),pos.getY(),pos.getZ()).add(0,0.51,0);
				if (cast.getSecond() == ENVY) {
					// enemies killed by the cloud spawn another smaller cloud
				} else if (cast.getSecond() == LUST) {
					// slowly moves towards the closest player / passive mob
					createSpellCloud(player,level,vec,1,1.8f,Player.class, PathfinderMob.class);
				} else if (cast.getSecond() == SLOTH) {
					// the cloud last longer
					createSpellCloud(player,level,vec,1,1.8f);
				} else if (cast.getSecond() == PRIDE) {
					// cloud splits and disopates
				} else if (cast.getSecond() == GREED) {
					// slowly moves towards the closest hostile mob
					createSpellCloud(player,level,vec,1,1.8f,Mob.class);
				} else if (cast.getSecond() == GLUTTONY) {
					// the cloud is bigger
					createSpellCloud(player,level,vec,1,0.8f);
				} else if (cast.getSecond() == WRATH) {
					// creates tornado
				} else {
					// Default AIR SPELL
					createSpellCloud(player,level,vec,0,1f);
				}
			}
			if (cast.getFirst() == WATER) {
				/*
				- Creates an AOE blast
				- Targets any entity hit
				 */
				int raytraceDistance = 24;
				BlockPos pos = RayTraceUtils.getTargetBlockPos(player, level, raytraceDistance);
				if (cast.getSecond() == ENVY) {
					// the effect grows the more entities it hits
					createAOEBlast(player,level,pos,16.0f,true,true, 1);
				} else if (cast.getSecond() == LUST) {
					// cant target hostile mobs
					createAOEBlast(player,level,pos,8.0f,false,false, 1, Mob.class);
				} else if (cast.getSecond() == SLOTH) {
					// the AOE slows to a crawl allowing entities to be hit multiple times
				} else if (cast.getSecond() == PRIDE) {
					// creates multiple AOE waves in an AOE
					createAOEBlast(player,level,pos,8.0f,true,false, 3);
				} else if (cast.getSecond() == GREED) {
					// can only target hostile mobs
					createAOEBlast(player,level,pos,8.0f,true,false, 1, Mob.class);
				} else if (cast.getSecond() == GLUTTONY) {
					// the AOE effect is bigger
					createAOEBlast(player,level,pos,16.0f,true,false, 1);
				} else if (cast.getSecond() == WRATH) {
					// the AOE effect becomes diectional wave with a longer range
				} else {
					// Default WATER SPELL
					createAOEBlast(player,level,pos,8.0f,true,false, 1);
				}
			}
			if (cast.getFirst() == FIRE) {
				/*
				- Fires a projectilets with a long range
				- Targets the block / entity hit
				 */
				Random random = new Random();
				if (!level.isClientSide) {

					if (cast.getSecond() == ENVY) {
						// entities killed fire the same spell to the closest entity
					} else if (cast.getSecond() == LUST) {
						// homes to players / passive mobs
						SpellEggEntity eggentity = new SpellEggEntity(level, player, this);
						eggentity.setItem(new ItemStack(ArcanaItems.AMBER.get()));
						eggentity.shoot(player.getLookAngle().x, player.getLookAngle().y, player.getLookAngle().z, 1.5F, 1.0F);
						eggentity.setPos(eggentity.getX(),eggentity.getY()-0.5,eggentity.getZ());
						eggentity.enableHoming(Player.class, PathfinderMob.class);
						level.addFreshEntity(eggentity);
					} else if (cast.getSecond() == SLOTH) {
						// the projectile bounces
						// TODO: IMPLEMENT THIS
					} else if (cast.getSecond() == PRIDE) {
						// shotguns projectiles
						for (int i = 0; i < 3; i++) {
							SpellEggEntity eggentity = new SpellEggEntity(level, player, this);
							eggentity.setItem(new ItemStack(ArcanaItems.AMBER.get()));
							eggentity.shoot(player.getLookAngle().x, player.getLookAngle().y, player.getLookAngle().z, 0.6F, 20.0F);
							eggentity.setPos(eggentity.getX(),eggentity.getY()-0.5,eggentity.getZ());
							level.addFreshEntity(eggentity);
						}
					} else if (cast.getSecond() == GREED) {
						// homes to hostile mobs
						SpellEggEntity eggentity = new SpellEggEntity(level, player, this);
						eggentity.setItem(new ItemStack(ArcanaItems.AMBER.get()));
						eggentity.shoot(player.getLookAngle().x, player.getLookAngle().y, player.getLookAngle().z, 1.5F, 1.0F);
						eggentity.setPos(eggentity.getX(),eggentity.getY()-0.5,eggentity.getZ());
						eggentity.enableHoming(Mob.class);
						level.addFreshEntity(eggentity);
					} else if (cast.getSecond() == GLUTTONY) {
						// acts a flame thrower
						for (int i = 0; i < 100; i++) {
							SpellEggEntity eggentity = new SpellEggEntity(level, player, this);
							eggentity.setItem(new ItemStack(ArcanaItems.AMBER.get()));
							eggentity.shoot(player.getLookAngle().x, player.getLookAngle().y, player.getLookAngle().z, 1.5F, 1.0F);
							eggentity.setPos(eggentity.getX(), eggentity.getY() - 0.5, eggentity.getZ());
							eggentity.setLifespan(80);
							level.addFreshEntity(eggentity);
						}

					} else if (cast.getSecond() == WRATH) {
						// fires a long range beam, longer range than the a lightning bolt but ticks slower
					} else {
						// Default FIRE SPELL
						SpellEggEntity eggentity = new SpellEggEntity(level, player, this);
						eggentity.setItem(new ItemStack(ArcanaItems.AMBER.get()));
						eggentity.shoot(player.getLookAngle().x, player.getLookAngle().y, player.getLookAngle().z, 1.5F, 1.0F);
						eggentity.setPos(eggentity.getX(), eggentity.getY() - 0.5, eggentity.getZ());
						level.addFreshEntity(eggentity);
					}
				}
			}
			if (cast.getFirst() == EARTH) {
				/*
				- Allows the player to select a block from far away
				- Can not target entities
				- Targets the selected block
				 */
				int raytraceDistance = 10;
				int delay = 4000;

				BlockPos pos = RayTraceUtils.getTargetBlockPos(player, level, raytraceDistance);
				if (cast.getSecond() == ENVY) {
					// targets all connected blocks of the same type within a 8 block range
					// TODO: implement this.
				} else if (cast.getSecond() == LUST) {
					// targets marked blocks (blocks can be marked with shift - rightclick while holding the wand
					if (sender instanceof ItemStack) {
						List<BlockPos> markedBlocks;
						if (((ItemStack)sender).getOrCreateTag().contains("MarkedBlocks")) {
							//player.sendMessage(((ListTag) ((ItemStack) sender).getOrCreateTag().get("MarkedBlocks")).toFormattedComponent());
							ListTag listNBT = ((ListTag) ((ItemStack) sender).getOrCreateTag().get("MarkedBlocks"));
							markedBlocks = listNBT.stream()
									.map(inbt -> new BlockPos(((CompoundTag) inbt).getInt("x"), ((CompoundTag) inbt).getInt("y"), ((CompoundTag) inbt).getInt("z"))).collect(Collectors.toList());
							for (BlockPos markedBlock : markedBlocks) {
								useOnBlock(player, level, markedBlock);
							}
							listNBT.clear();
						}
					}
				} else if (cast.getSecond() == SLOTH) {
					// it takes a few seconds for the spell to cast
					DelayedCast.delayedCasts.add(new DelayedCast.Impl(t -> useOnBlock(player, level, pos),delay));

				} else if (cast.getSecond() == PRIDE) {
					// targets random nearby blocks ~5
					BlockPos.betweenClosed(pos.offset(-5, -5, -5), pos.offset(5, 5, 5)).forEach(blockPos -> {
						if (level.random.nextInt(5) == 2) useOnBlock(player, level, blockPos);
					});
				} else if (cast.getSecond() == GREED) {
					// allows players to target entites, targets the block below them
					List<Entity> entities = RayTraceUtils.rayTraceEntities(level,player.position(),player.getLookAngle(), Optional.empty(),Entity.class);
					for (Entity entity : entities) {
						useOnBlock(player, level,entity.blockPosition());
					}
				} else if (cast.getSecond() == GLUTTONY) {
					// selects a 7 * 7 * 7 area
					BlockPos.betweenClosed(pos.offset(-3, -3, -3), pos.offset(3, 3, 3)).forEach(blockPos -> useOnBlock(player, level, blockPos));
				} else if (cast.getSecond() == WRATH) {
					// Allows you to target entities instead
					List<Entity> entities = RayTraceUtils.rayTraceEntities(level,player.position(),player.getLookAngle(),Optional.empty(),Entity.class);
					for (Entity entity : entities) {
						useOnEntity(player, entity);
					}
				} else {
					// Default EARTH SPELL
					useOnBlock(player, level, pos);
				}
			}
			if (cast.getFirst() == ORDER) {
				/*
				- Targets self
				 */
				int delay = 2000;
				int maxDistance = 8;

				if (cast.getSecond() == ENVY) {
					// targets all nearby players
					List<Player> targets = level.getEntities(EntityType.PLAYER,
							new AABB(maxDistance, maxDistance, maxDistance, maxDistance, maxDistance, maxDistance),
							EntitySelector.NO_SPECTATORS);
					for (Player target : targets)
						useOnPlayer(target);
				} else if (cast.getSecond() == LUST) {
					// targets you and nearby pets
					List<Player> targetsP = level.getEntities(EntityType.PLAYER,
							new AABB(maxDistance, maxDistance, maxDistance, maxDistance, maxDistance, maxDistance),
							EntitySelector.NO_SPECTATORS);
					List<TamableAnimal> targetsE = level.getEntitiesOfClass(TamableAnimal.class,
							new AABB(maxDistance, maxDistance, maxDistance, maxDistance, maxDistance, maxDistance),
							EntitySelector.NO_SPECTATORS);
					for (TamableAnimal target : targetsE)
						useOnEntity(player, target);
					for (Player target : targetsP)
						useOnPlayer(target);
				} else if (cast.getSecond() == SLOTH) {
					// lays a mine
					createSpellCloudTrap(new SpellCloudEntity.CloudVariableGrid(player,level,player.position(),0));
				} else if (cast.getSecond() == PRIDE) {
					// targets random nearby entities
					List<Entity> targetsE = level.getEntitiesOfClass(Entity.class,
							new AABB(maxDistance, maxDistance, maxDistance, maxDistance, maxDistance, maxDistance),
							EntitySelector.NO_SPECTATORS);
					for (Entity target : targetsE)
						if (level.random.nextInt(5)==2)
							useOnEntity(player, target);
				} else if (cast.getSecond() == GREED) {
					// targets nearby hostiles
					List<Mob> targetsE = level.getEntitiesOfClass(Mob.class,
							new AABB(maxDistance, maxDistance, maxDistance, maxDistance, maxDistance, maxDistance),
							EntitySelector.NO_SPECTATORS);
					for (Mob target : targetsE)
						useOnEntity(player, target);
				} else if (cast.getSecond() == GLUTTONY) {
					// targets nearby dropped items
					List<ItemEntity> targetsE = level.getEntitiesOfClass(ItemEntity.class,
							new AABB(maxDistance, maxDistance, maxDistance, maxDistance, maxDistance, maxDistance),
							EntitySelector.NO_SPECTATORS);
					for (ItemEntity target : targetsE)
						useOnEntity(player, target);
				} else if (cast.getSecond() == WRATH) {
					// becomes toggalable giving the player a faint glow and repeasts the spell every few seconds
					player.addEffect(new MobEffectInstance(MobEffects.GLOWING,10,1));
					if (ToggleableCast.toggleableCasts.contains(Pair.of(spellUUID,new ToggleableCast.Impl(t -> useOnPlayer(player),delay))))
						ToggleableCast.toggleableCasts.remove(Pair.of(spellUUID,new ToggleableCast.Impl(t -> useOnPlayer(player),delay)));
					ToggleableCast.toggleableCasts.add(Pair.of(spellUUID, new ToggleableCast.Impl(t -> useOnPlayer(player),delay)));
				} else {
					// Default ORDER SPELL
					useOnPlayer(player);
				}
			}
			if (cast.getFirst() == CHAOS) {
				/*
				-A mid ranged lightning bolt
				- Targets the block / entity hit every few ticks
				 */
				// Not implemented yet
			}
		} else if (action == ICast.Action.SPECIAL) {
			if (cast.getFirst() == EARTH && cast.getSecond() == LUST) {
				if (sender instanceof ItemStack stack) {
					int raytraceDistance = 8;
					BlockPos pos = RayTraceUtils.getTargetBlockPos(player, level, raytraceDistance);
                    ListTag markedBlocksNBT = stack.getOrCreateTag().contains("MarkedBlocks") ? (ListTag) stack.getOrCreateTag().get("MarkedBlocks") : new ListTag();
					CompoundTag markedBlock = new CompoundTag();
					markedBlock.putInt("x", pos.getX());
					markedBlock.putInt("y", pos.getY());
					markedBlock.putInt("z", pos.getZ());
					markedBlocksNBT.add(markedBlock);
					stack.getOrCreateTag().put("MarkedBlocks", markedBlocksNBT);
				}
			}
		}
	}

	private void createSpellCloudTrap(SpellCloudEntity.CloudVariableGrid variableGrid) {
		SpellTrapEntity trap = new SpellTrapEntity(variableGrid);
	}

	private void createSpellCloud(Player player, Level level, Vec3 area, int rMultP, float durMultP) {
		SpellCloudEntity cloud = new SpellCloudEntity(level, area);
		cloud.setOwner(player);
		cloud.setDuration((int)((float)(800)*(float)(durMultP)));
		cloud.setRadius(3.0F*(rMultP+1));
		cloud.setRadiusOnUse(-0.5F);
		cloud.setWaitTime(10);
		cloud.setRadiusPerTick(-cloud.getRadius() / (float)cloud.getDuration());
		cloud.setSpell(this);
		level.addFreshEntity(cloud);
	}
	private void createSpellCloud(Player player, Level level, Vec3 area, int rMultP, float durMultP, Class<? extends Entity>... targets) {
		SpellCloudEntity cloud = new SpellCloudEntity(level, area);
		cloud.setOwner(player);
		cloud.setDuration((int)((float)(800)*(float)(durMultP)));
		cloud.setRadius(3.0F*(rMultP+1));
		cloud.setRadiusOnUse(-0.5F);
		cloud.setWaitTime(10);
		cloud.setRadiusPerTick(-cloud.getRadius() / (float)cloud.getDuration());
		cloud.enableHoming(targets);
		cloud.setSpell(this);
		level.addFreshEntity(cloud);
	}
	
	public void createAOEBlast(Player player, Level level, BlockPos epicentre, float radius, boolean whitelistMode, boolean canExtend, int waves, @Nullable Class<? extends LivingEntity>... targets) {
		for (int i = 0; i < waves; i++) {
			BlastEmitterEntity emitter = new BlastEmitterEntity(level,player,radius);
			emitter.setPos(emitter.getX(),epicentre.getY()+0.5f,epicentre.getZ());
			emitter.setCooldown(15*i);
			emitter.setSpell(this);
			emitter.setCaster(player);
			emitter.makeBlackWhiteList(whitelistMode,targets);
			level.addFreshEntity(emitter);
		}
	}
}