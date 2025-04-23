package com.wonginnovations.arcana.systems.spell.casts;

import com.wonginnovations.arcana.aspects.Aspect;
import com.wonginnovations.arcana.util.Pair;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.UUID;

/**
 * The ISpell Class
 */
public interface ICast {
	
	// TODO: improve docs

	/**
	 * Core aspect in spell.
	 * @return returns core aspect.
	 */
	Aspect getSpellAspect();

	int getSpellDuration();

	/**
	 * Use of that spell.
	 * @param player Player
	 * @param sender Sender
	 * @param action Click Action
	 * @return
	 */
	void use(UUID uuid, Level level, Player player, Object sender, Pair<Aspect,Aspect> cast, ICast.Action action);

	default Optional<Component> getName(CompoundTag nbt) {
		return Optional.empty();
	}
	
	/**
	 * Click action
	 *
	 * USE -> Right click
	 * ALT_USE -> Left Click
	 * SPECIAL -> Shift + Right Click
	 */
	enum Action{
		USE,
		ALT_USE,
		SPECIAL
	}
}
