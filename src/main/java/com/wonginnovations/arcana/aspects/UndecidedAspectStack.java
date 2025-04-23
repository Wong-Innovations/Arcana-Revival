package com.wonginnovations.arcana.aspects;

public class UndecidedAspectStack {
	public AspectStack stack;
	public boolean any;

	private UndecidedAspectStack(AspectStack stack, boolean any) {
		this.stack = stack;
		this.any = any;
	}

	public static UndecidedAspectStack create(Aspect aspect, int amount, boolean any) {
		return new UndecidedAspectStack(new AspectStack(aspect, amount), any);
	}

	public static UndecidedAspectStack createAny(int amount) {
		return new UndecidedAspectStack(new AspectStack(Aspects.AIR, amount), true);
	}
}
