package com.wonginnovations.arcana.systems.spell;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

import java.util.Optional;

public interface ISpell {
	/**
	 * Cost of spell in AspectStacks.
	 * @return returns cost of spell.
	 */
	SpellCosts getSpellCosts();

	default Optional<Component> getName(CompoundTag nbt) {
		return Optional.empty();
	}
}
