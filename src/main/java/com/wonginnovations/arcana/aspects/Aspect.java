package com.wonginnovations.arcana.aspects;

import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.ArcanaVariables;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Consumer;

public class Aspect {
	private static Logger logger = LogManager.getLogger();

	private final int id;
	private final String aspectName;
	private final ColorRange colors;
	private final Consumer<Object> aspectTickConsumer;

	private Aspect(String name, ColorRange colors, Consumer<Object> aspectTickConsumer) {
		this(name,ArcanaVariables.nextAspectId(),colors,aspectTickConsumer);
	}

	private Aspect(String name, int id, ColorRange colors, Consumer<Object> aspectTickConsumer) {
		this.aspectName = name;
		this.id = id;
		this.colors = colors;
		this.aspectTickConsumer = aspectTickConsumer;
	}

	public void aspectTick(Object sender) {
		if (aspectTickConsumer!=null)
			aspectTickConsumer.accept(sender);
	}

	public int getId() {
		return id;
	}

	public ColorRange getColorRange() {
		return colors;
	}

	public String name() {
		return aspectName;
	}

	@Override
	public String toString() {
		return "Aspect: "+aspectName+" ("+id+")";
	}

	public ResourceLocation getVisMeterTexture() {
		// only valid for primals
		return new ResourceLocation(Arcana.MODID, "textures/gui/hud/wand/vis/" + aspectName + ".png");
	}

	// Static Methods

	public static Aspect create(String name, ColorRange colors, Consumer<Object> aspectTickConsumer) {
		Aspect aspect = new Aspect(name, colors,aspectTickConsumer);
		Aspects.ASPECTS.put(ArcanaVariables.arcLoc(name),aspect);
		if (!ArcanaVariables.test) {
			logger.info("Arcana: Added new aspect '" + name + "'");
			try {
				net.minecraftforge.fml.StartupMessageManager.addModMessage("Arcana: Added \"" + name + "\" aspect");
			}catch(Exception ignored) {}
		}
		return aspect;
	}

	public static Aspect fromId(int readInt) {
		return Aspects.ASPECTS.values().stream()
				.filter(entry -> entry.getId() == readInt)
				.findAny().orElse(Aspects.EMPTY);
	}

	public ResourceLocation toResourceLocation() {
		return Aspects.ASPECTS.inverse().get(this);
	}

	public static Aspect fromResourceLocation(ResourceLocation resourceLocation) {
		return Aspects.ASPECTS.get(resourceLocation);
	}
}