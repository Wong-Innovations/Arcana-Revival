package com.wonginnovations.arcana.world;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.wonginnovations.arcana.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.Map;

import static com.wonginnovations.arcana.world.WorldInteractions.freezable;

public class WorldInteractionsRegistry extends SimpleJsonResourceReloadListener {
	
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	
	public WorldInteractionsRegistry() {
		super(GSON, "arcana/interactions");
	}
	
	public static void applyJson(ResourceLocation location, JsonElement e) {
		if (e.isJsonObject()) {
			JsonObject object = e.getAsJsonObject();
			if (location.getPath().equals("freeze"))
				for (JsonElement element : object.get("values").getAsJsonArray()) {
					String from = element.getAsJsonObject().get("from").getAsString();
					String to = element.getAsJsonObject().get("to").getAsString();
					String cover = "minecraft:air";
					if (element.getAsJsonObject().has("cover"))
						cover = element.getAsJsonObject().get("cover").getAsString();
					
					freezable.put(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(from)),
							Pair.of(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(to)),
									ForgeRegistries.BLOCKS.getValue(new ResourceLocation(cover))));
				}
		}
	}
	
	@Override
	protected void apply(Map<ResourceLocation, JsonElement> objects, @Nonnull ResourceManager resourceManager, @Nonnull ProfilerFiller profiler) {
		objects.forEach(WorldInteractionsRegistry::applyJson);
	}
}
