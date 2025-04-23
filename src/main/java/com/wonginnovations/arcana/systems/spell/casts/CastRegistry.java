package com.wonginnovations.arcana.systems.spell.casts;

import com.wonginnovations.arcana.systems.spell.casts.Casts;
import com.wonginnovations.arcana.systems.spell.casts.ICast;
import net.minecraft.resources.ResourceLocation;

/**
 * API's can register casts here!
 */
public class CastRegistry {
	public static void addCast(ResourceLocation id, ICast spell) {
		if (!Casts.castMap.containsKey(id))
			Casts.castMap.put(id,spell);
	}
}
