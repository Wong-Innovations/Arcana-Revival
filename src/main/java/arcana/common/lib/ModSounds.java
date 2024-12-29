package arcana.common.lib;

import arcana.Arcana;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.common.util.ForgeSoundType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Arcana.MODID);

    public static final RegistryObject<SoundEvent> clack = registerSoundEvent("clack");
    public static final RegistryObject<SoundEvent> poof = registerSoundEvent("poof");
    public static final RegistryObject<SoundEvent> page = registerSoundEvent("page");
    public static final RegistryObject<SoundEvent> learn = registerSoundEvent("learn");
    public static final RegistryObject<SoundEvent> write = registerSoundEvent("write");
    public static final RegistryObject<SoundEvent> crystal = registerSoundEvent("crystal");
    public static final RegistryObject<SoundEvent> scan = registerSoundEvent("scan");
    public static final RegistryObject<SoundEvent> dust = registerSoundEvent("dust");

    public static SoundType CRYSTAL = new ForgeSoundType(0.5f, 1.0f, ModSounds.crystal, ModSounds.crystal, ModSounds.crystal, ModSounds.crystal, ModSounds.crystal);

    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Arcana.MODID, name)));
    }

    // TODO: Call register somewhere
    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
