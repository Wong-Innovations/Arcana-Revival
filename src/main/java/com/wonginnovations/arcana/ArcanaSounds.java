package com.wonginnovations.arcana;

import com.wonginnovations.arcana.sounds.PositionalSound;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.ForgeSoundType;
import net.minecraftforge.registries.*;

import java.util.Random;

@SuppressWarnings({"ConstantConditions", "DanglingJavadoc"})
/**
 *	Sound class that contains:
 * 	- Impl (SoundEvent registration)
 * 	- SoundTypes
 * 	- Play SoundEvent functions
 */
public class ArcanaSounds {

	public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Arcana.MODID);

	// SoundEvents
	public static final RegistryObject<SoundEvent> jar_break = registerSoundEvent("jar_break");
	public static final RegistryObject<SoundEvent> jar_step = registerSoundEvent("jar_step");
	public static final RegistryObject<SoundEvent> jar_place = registerSoundEvent("jar_place");
	public static final RegistryObject<SoundEvent> phialshelf_slide = registerSoundEvent("phialshelf_slide");
	public static final RegistryObject<SoundEvent> phial_corkpop = registerSoundEvent("phial_corkpop");

	public static final RegistryObject<SoundEvent> taint_break = registerSoundEvent("taint_break");
	public static final RegistryObject<SoundEvent> taint_step = registerSoundEvent("taint_step");
	public static final RegistryObject<SoundEvent> taint_place = registerSoundEvent("taint_place");
	public static final RegistryObject<SoundEvent> taint_stone_break = registerSoundEvent("taint_stone_break");
	public static final RegistryObject<SoundEvent> taint_stone_step = registerSoundEvent("taint_stone_step");
	public static final RegistryObject<SoundEvent> taint_stone_place = registerSoundEvent("taint_stone_place");

	public static final RegistryObject<SoundEvent> arcana_taint_portal_extended = registerSoundEvent("arcana_taint_portal_extended");
	public static final RegistryObject<SoundEvent> crystal_break = registerSoundEvent("crystal_break");
	public static final RegistryObject<SoundEvent> crystal_place = registerSoundEvent("crystal_place");
	public static final RegistryObject<SoundEvent> crystal_break_negative = registerSoundEvent("crystal_break_negative");
	public static final RegistryObject<SoundEvent> crystal_place_negative = registerSoundEvent("crystal_place_negative");

	public static final RegistryObject<SoundEvent> music_arcana_theme = registerSoundEvent("music_arcana_theme");
	public static final RegistryObject<SoundEvent> music_arcana_green_sleeves = registerSoundEvent("music_arcana_green_sleeves");

	public static final RegistryObject<SoundEvent> arcanainfusionpart1d = registerSoundEvent("arcanainfusionpart1d");
	public static final RegistryObject<SoundEvent> arcanainfusionpart4de = registerSoundEvent("arcanainfusionpart4de");

	public static final RegistryObject<SoundEvent> spell_cast = registerSoundEvent("spell_cast");

	public static final RegistryObject<SoundEvent> arcana_mana_creeper = registerSoundEvent("arcana_mana_creeper");

	public static final RegistryObject<SoundEvent> arcananodes = registerSoundEvent("arcananodes");
	public static final RegistryObject<SoundEvent> arcananodesnegative = registerSoundEvent("arcananodesnegative");
	public static final RegistryObject<SoundEvent> arcana_hunger_node = registerSoundEvent("arcana_hunger_node");

	// SoundTypes
	public static SoundType JAR = new ForgeSoundType(0.6F, 1.0F, jar_break, jar_step, jar_place, jar_break, jar_step);
	public static SoundType TAINT = new ForgeSoundType(0.6F, 1.4F, taint_break, taint_step, taint_place, taint_break, taint_step);
	public static SoundType TAINT_STONE = new ForgeSoundType(0.6F, 1.4F, taint_stone_break, taint_stone_step, taint_stone_place, taint_stone_break, taint_stone_break);
	public static SoundType CRYSTAL = new ForgeSoundType(0.6F, 1.0F, crystal_break, crystal_place, crystal_place, crystal_break, crystal_place);

	private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
		return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Arcana.MODID, name)));
	}

	// SoundEvents
	@SuppressWarnings("ConstantConditions")
	public static void playPhialshelfSlideSound(Player playerEntity, BlockPos pos) {
		playSound(playerEntity, pos, ArcanaSounds.phialshelf_slide.get(), SoundSource.BLOCKS,0.4f,1.2f);
	}

	@SuppressWarnings("ConstantConditions")
	public static void playPhialCorkpopSound(Player playerEntity) {
		playSound(playerEntity, playerEntity.blockPosition(), ArcanaSounds.phial_corkpop.get(), SoundSource.BLOCKS,0.4f,1.4f);
	}

	public static void playSpellCastSound(Player playerEntity) {
		playSound(playerEntity, playerEntity.blockPosition(), ArcanaSounds.spell_cast.get(), SoundSource.PLAYERS,0.4f,1.0f);
	}

	public static void playSoundOnce(Player playerEntity, BlockPos pos, SoundEvent evt, SoundSource source, float v, float p, float distance, Runnable onStop) {
		Minecraft.getInstance().getSoundManager().play(new PositionalSound(pos, playerEntity, evt, source, v, p, distance, onStop));
	}

	public static void playSound(Player playerEntity, BlockPos pos, SoundEvent evt, SoundSource source, float v, float p) {
		if (evt.equals(null)) return;
		playerEntity.level().playSound(playerEntity, pos, evt, source, v, p);
	}

}