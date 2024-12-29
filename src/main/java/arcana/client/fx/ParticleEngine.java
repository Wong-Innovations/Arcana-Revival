package arcana.client.fx;

import arcana.Arcana;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber({Dist.CLIENT})
public class ParticleEngine {
    public static ResourceLocation particleTexture = new ResourceLocation(Arcana.MODID, "textures/misc/particles.png");
}
