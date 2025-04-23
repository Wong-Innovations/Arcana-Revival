package com.wonginnovations.arcana.systems.spell.casts;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DelayedCast {
	public static List<DelayedCast.Impl> delayedCasts = new ArrayList<>();

	public static class Impl {
		public Consumer<Integer> spellEvent;
		public int ticks;
		public int ticksPassed;

		public Impl(Consumer<Integer> spellEvent, int ticks) {
			this.spellEvent = spellEvent;
			this.ticks = ticks;
		}
	}
}