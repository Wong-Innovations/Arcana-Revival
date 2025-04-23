package com.wonginnovations.arcana.systems.spell;

import com.wonginnovations.arcana.aspects.AspectStack;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class MDModifier {
	public static class Empty extends MDModifier {}
	public static class ReducedVis extends MDModifier {
		public final List<AspectStack> aspects;
		public ReducedVis(List<AspectStack> aspects) {
			this.aspects = aspects;
		}
	}
	public static class Warping extends MDModifier {
		public static void onUse(Player playerEntity) {
			//do warp things here...
		}
	}
	public static class Creative extends MDModifier {}
	public static class Mechanical extends MDModifier {}
}
