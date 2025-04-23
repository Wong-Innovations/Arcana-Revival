package com.wonginnovations.arcana.aspects;

/**
 * Stores a set of colors.
 */
public class ColorRange {
	
	private final int[] colors;

	private ColorRange(int[] colors) {
		this.colors = colors;
	}

	public static ColorRange create(int... colors) {
		return new ColorRange(colors);
	}
	
	public int get(int color) {
		return colors[Math.min(color, colors.length - 1)];
	}
}