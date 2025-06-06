package com.wonginnovations.arcana.systems.spell;

import com.wonginnovations.arcana.aspects.Aspect;
import com.wonginnovations.arcana.aspects.Aspects;
import com.wonginnovations.arcana.client.gui.UiUtil;
import com.wonginnovations.arcana.items.MagicDeviceItem;
import com.wonginnovations.arcana.items.attachment.Focus;
import com.wonginnovations.arcana.systems.spell.casts.Casts;
import com.wonginnovations.arcana.systems.spell.casts.ICast;
import com.wonginnovations.arcana.systems.spell.modules.SpellModule;
import com.wonginnovations.arcana.systems.spell.modules.circle.DoubleModifierCircle;
import com.wonginnovations.arcana.systems.spell.modules.core.*;
import com.wonginnovations.arcana.util.Pair;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nullable;
import java.util.*;

import static com.wonginnovations.arcana.util.Pair.of;

/**
 * Spell is made of SpellModules that are bound together.
 */
public class Spell implements ISpell {
	public SpellModule mainModule;
	public UUID spellUUID;

	public Spell(SpellModule beginModule) {
		mainModule = beginModule;
		spellUUID = UUID.randomUUID();
	}

	public SpellModule getBeginModule() {
		return mainModule;
	}

	/**
	 * Run Spell.
	 * Goes trough all spell modules and executes {@link ICast}.
	 * @param spell Spell to run.
	 * @param caster Player that uses the Spell.
	 * @param sender {@link net.minecraft.world.item.ItemStack} that {@link net.minecraft.world.item.Item} extends {@link MagicDeviceItem}
	 * @param action Spell use Action.
	 */
	public static void runSpell(Spell spell, Level level, Player caster, Object sender, ICast.Action action) {
		// This method already loops through all of the modules, old loop wasn't necessary
		Logic.runSpellModule(spell, level, spell.mainModule, caster, sender, action, new ArrayList<>(),new ArrayList<>());
	}

	public static void updateSpellStatusBar(Player player) {
		if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof MagicDeviceItem) {
			if (MagicDeviceItem.getFocus(player.getItemInHand(InteractionHand.MAIN_HAND)) != Focus.NO_FOCUS) {
				if (MagicDeviceItem.getFocus(player.getItemInHand(InteractionHand.MAIN_HAND)).getSpell(player.getItemInHand(InteractionHand.MAIN_HAND))!=null) {
					for (SpellModule module : MagicDeviceItem.getFocus(player.getItemInHand(InteractionHand.MAIN_HAND)).getSpell(player.getItemInHand(InteractionHand.MAIN_HAND)).mainModule.bound) {
						Logic.updateSpellStatusBarRecursive(module, player, new ArrayList<>());
					}
				}
			}
		}
	}

	/**
	 * Cost of spell in AspectStacks.
	 *
	 * @return returns cost of spell.
	 */
	@Override
	public SpellCosts getSpellCosts() {
		return Logic.getSpellCost(mainModule,new SpellCosts(0,0,0,0,0,0,0));
	}

	public Optional<Component> getName(CompoundTag nbt) {
		// TODO: add tooltip
		return Optional.of(Component.literal("//FIXME: NAME NOT IMPLEMENTED!!!")/*Component.translatable("spell." + getId().getNamespace() + "." + getId().getPath())*/);
	}

	public int getSpellColor() {
		Integer color = null;
		Logic.blendAndGetColor(mainModule, color, new ArrayList<>());
		if (color == null) return 0xFFFFFF;
		return color;
	}

	/**
	 * Spell NBT to Spell Object
	 * @param compound Spell NBT
	 * @return Deserialized Spell
	 */
	public static Spell fromNBT(CompoundTag compound) {
		Spell spell = new Spell(null);
		if (compound.get("spell") != null) {
			spell.mainModule = SpellModule.fromNBTFull(compound.getCompound("spell"), 0);
		}
		return spell;
	}

	/**
	 * Spell to Spell NBT
	 * @param compound Existing CompoundTag or new.
	 * @return Serialized Spell
	 */
	public CompoundTag toNBT(CompoundTag compound) {
		if (mainModule != null) {
			compound.put("spell", mainModule.toNBTFull(new CompoundTag(), 0));
		}
		return compound;
	}

	private static class Logic {
		private static SpellModule updateSpellStatusBarRecursive(SpellModule toUnbound, Player player,
																 List<Pair<Aspect,Aspect>> castMethodsAspects) {
			/*if (toUnbound.bound.size() > 0) {
				for (SpellModule module : toUnbound.bound) {
					if (module instanceof CastMethod)
						castMethodsAspects.add(of(((CastMethod) module).aspect, Aspects.EMPTY));
					else if (module instanceof CastMethodSin) {
						castMethodsAspects.get(castMethodsAspects.size() - 1).setSecond(((CastMethodSin) module).aspect);
						return updateSpellStatusBarRecursive(module, player, castMethodsAspects);
					}
				}
			} else {
				for (Pair<Aspect,Aspect> castMethodsAspect : castMethodsAspects)
					if (castMethodsAspect.getFirst() == Aspects.EARTH && castMethodsAspect.getSecond() == Aspects.LUST)
						if (player.isCrouching()) {
							player.sendStatusMessage(Component.translatable("status.arcana.selection_mode"), true);
						} else {
							player.sendStatusMessage(Component.translatable("status.arcana.break_mode"), true);
						}
			}*/
			return null;
		}

		/**
		 * Run spell Recursion.
		 */
		private static SpellModule runSpellModule(Spell spell, Level level, SpellModule toUnbound, Player caster, Object sender, ICast.Action action,
												  List<Pair<Aspect,Aspect>> castMethodsAspects, List<ICast> casts) {
			SpellModule mod = null;
			if (toUnbound == null) return reportNullSpell();
			if (!toUnbound.bound.isEmpty()) {
				for (SpellModule module : toUnbound.bound) {
					if (module instanceof CastMethod)
						castMethodsAspects.add(of(((CastMethod) module).aspect,Aspects.EMPTY));
					else if (module instanceof CastMethodSin) {
						if (!castMethodsAspects.isEmpty())
							castMethodsAspects.get(castMethodsAspects.size()-1).setSecond(((CastMethodSin) module).aspect);
					} else if (module instanceof CastCircle)
						casts.add(((CastCircle) module).cast);
					mod = runSpellModule(spell, level, module, caster, sender, action, castMethodsAspects, casts);
				}
			} else {
				for (ICast cast : casts) {
					for (Pair<Aspect,Aspect> castMethodsAspect : castMethodsAspects)
						cast.use(spell.spellUUID, level, caster, sender, castMethodsAspect, action);
				}
			}
			return mod;
		}

		public static SpellModule blendAndGetColor(SpellModule toUnbound, @Nullable Integer color, List<ICast> casts) {
			SpellModule mod = null;
			if (toUnbound == null) return reportNullSpell();
			if (!toUnbound.bound.isEmpty()) {
				for (SpellModule module : toUnbound.bound) {
					if (module instanceof CastCircle)
						casts.add(((CastCircle) module).cast);
					mod = blendAndGetColor(module, color, casts);
				}
			} else {
				for (ICast cast : casts) {
					if (color == null) color = cast.getSpellAspect().getColorRange().get(3);
					else UiUtil.blend(color,cast.getSpellAspect().getColorRange().get(3),0.5f);
				}
			}
			return mod;
		}

		public static SpellCosts getSpellCost(SpellModule toUnbound, SpellCosts cost) {
			if (toUnbound != null && !toUnbound.bound.isEmpty()) {
				for (SpellModule module : toUnbound.bound) {
					if (module instanceof CastMethod) {
						Aspect aspect = ((CastMethod)module).aspect;
						if (aspect==Aspects.EARTH)
							cost.earth.setAmount(cost.earth.getAmount()+1);
						if (aspect==Aspects.AIR)
							cost.air.setAmount(cost.air.getAmount()+1);
						if (aspect==Aspects.WATER)
							cost.water.setAmount(cost.water.getAmount()+1);
						if (aspect==Aspects.FIRE)
							cost.fire.setAmount(cost.fire.getAmount()+1);
						if (aspect==Aspects.ORDER)
							cost.order.setAmount(cost.order.getAmount()+1);
						if (aspect==Aspects.CHAOS)
							cost.chaos.setAmount(cost.chaos.getAmount()+1);
					}
					return getSpellCost(module, cost);
				}
			}
			return cost;
		}
	}

	/**
	 * Example spells.
	 */
	public static class Samples{
		/**
		 * Create example basic Spell that is used for testing.
		 * @return a basic Spell
		 */
		public static Spell createBasicSpell() {
			Connector startToCastMethod_connector = new Connector();
			Connector castMethodToCastCircle_connector = new Connector();
			DoubleModifierCircle doubleModifierCircle = new DoubleModifierCircle();
			CastCircle castCircle = new CastCircle();
			doubleModifierCircle.firstAspect = Aspects.AIR;
			doubleModifierCircle.secondAspect = Aspects.FIRE;
			castCircle.cast = Casts.LIFE_CAST;
			
			CastMethodSin castMethodSin = new CastMethodSin();
			castMethodSin.aspect = Aspects.GREED;
			castMethodSin.bindModule(castMethodToCastCircle_connector);
			
			castCircle.bindModule(doubleModifierCircle);
			castMethodToCastCircle_connector.bindModule(castCircle);

			CastMethod castMethod = new CastMethod();
			castMethod.aspect = Aspects.AIR;
			castMethod.bindModule(castMethodSin);

			startToCastMethod_connector.bindModule(castMethod);
			Spell spell = new Spell(new StartCircle());
			spell.mainModule.bindModule(startToCastMethod_connector);
			return spell;
		}

		/**
		 * Create example advanced Spell that is used for testing.
		 * @return a basic Spell
		 */
		public static Spell createAdvancedSpell() {
			// Create connectors
			Connector startToCastMethod_connector = new Connector();
			Connector castMethodToCastCircle0_connector = new Connector();
			Connector castMethodToCastCircle1_connector = new Connector();

			// Create Double Modifier Circle
			DoubleModifierCircle doubleModifierCircle = new DoubleModifierCircle();

			// Create Cast Circles
			CastCircle castCircle0 = new CastCircle();
			CastCircle castCircle1 = new CastCircle();

			// Bind "castMethodToCastCircle1_connector" to "castCircle1" connector
			castMethodToCastCircle1_connector.bindModule(castCircle1);

			// Add modifiers to doubleModifierCircle
			doubleModifierCircle.firstAspect = Aspects.AIR;
			doubleModifierCircle.secondAspect = Aspects.FIRE;

			// Set MINING_CAST to castCircle0
			castCircle0.cast = Casts.EXCHANGE_CAST;

			// Bind "castCircle0" to "doubleModifierCircle" connector
			castCircle0.bindModule(doubleModifierCircle);

			// Set FABRIC_CAST to castCircle0
			castCircle1.cast = Casts.MINING_CAST;

			// Bind "castMethodToCastCircle0_connector" to "castCircle0" connector
			castMethodToCastCircle0_connector.bindModule(castCircle0);

			// Create Cast Method
			CastMethod castMethod = new CastMethod();

			// Set aspect to cast method
			castMethod.aspect = Aspects.EARTH;

			// Bind "castMethod" to connectors
			castMethod.bindModule(castMethodToCastCircle0_connector);
			castMethod.bindModule(castMethodToCastCircle1_connector);

			// Bind "startToCastMethod_connector" to "castMethod"
			startToCastMethod_connector.bindModule(castMethod);

			// Create spell
			Spell spell = new Spell(new StartCircle());

			// Create mainModule and bind modules to mainModule
			spell.mainModule.bindModule(startToCastMethod_connector);

			// Return spell
			return spell;
		}

		/**
		 * Create example debug Spell that is used for testing.
		 * @return a basic Spell
		 */
		public static Spell createDebugSpell() {
			CastCircle castCircle0 = new CastCircle();
			CastCircle castCircle1 = new CastCircle();

			castCircle0.cast = Casts.MINING_CAST;
			castCircle1.cast = Casts.FABRIC_CAST;
			CastMethod castMethod = new CastMethod();
			castMethod.aspect = Aspects.EARTH;
			castMethod.bindModule(castCircle0);
			castMethod.bindModule(castCircle1);

			Spell spell = new Spell(new StartCircle());

			spell.mainModule.bindModule(castMethod);
			return spell;
		}
	}

	protected static boolean isReported;
	protected static SpellModule reportNullSpell() {
		if (!isReported) LogManager.getLogger().fatal("Null spell is present. Please report this issue to Arcana github page.");
		isReported = true;
		return new StartCircle();
	}
}
