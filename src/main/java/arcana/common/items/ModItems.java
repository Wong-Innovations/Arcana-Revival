package arcana.common.items;

import arcana.Arcana;
import arcana.common.items.consumables.ItemPhial;
import arcana.common.items.curios.ItemCelestialNotes;
import arcana.common.items.curios.ItemCheatersThaumonomicon;
import arcana.common.items.curios.ItemThaumonomicon;
import arcana.common.items.resources.ItemCrystalEssence;
import arcana.common.items.resources.ItemMagicDust;
import arcana.common.items.tools.ItemScribingTools;
import arcana.common.items.tools.ItemThaumometer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Arcana.MODID);

    public static final RegistryObject<Item> thaumonomicon = ITEMS.register("thaumonomicon", ItemThaumonomicon::new);
    public static final RegistryObject<Item> cheatersThaumonomicon = ITEMS.register("cheaters_thaumonomicon", ItemCheatersThaumonomicon::new);
    public static final RegistryObject<Item> celestialNotesSun = ITEMS.register("celestial_notes_sun", ItemCelestialNotes::new);
    public static final RegistryObject<Item> celestialNotesStars1 = ITEMS.register("celestial_notes_stars_1", ItemCelestialNotes::new);
    public static final RegistryObject<Item> celestialNotesStars2 = ITEMS.register("celestial_notes_stars_2", ItemCelestialNotes::new);
    public static final RegistryObject<Item> celestialNotesStars3 = ITEMS.register("celestial_notes_stars_3", ItemCelestialNotes::new);
    public static final RegistryObject<Item> celestialNotesStars4 = ITEMS.register("celestial_notes_stars_4", ItemCelestialNotes::new);
    public static final RegistryObject<Item> celestialNotesMoon1 = ITEMS.register("celestial_notes_moon_1", ItemCelestialNotes::new);
    public static final RegistryObject<Item> celestialNotesMoon2 = ITEMS.register("celestial_notes_moon_2", ItemCelestialNotes::new);
    public static final RegistryObject<Item> celestialNotesMoon3 = ITEMS.register("celestial_notes_moon_3", ItemCelestialNotes::new);
    public static final RegistryObject<Item> celestialNotesMoon4 = ITEMS.register("celestial_notes_moon_4", ItemCelestialNotes::new);
    public static final RegistryObject<Item> celestialNotesMoon5 = ITEMS.register("celestial_notes_moon_5", ItemCelestialNotes::new);
    public static final RegistryObject<Item> celestialNotesMoon6 = ITEMS.register("celestial_notes_moon_6", ItemCelestialNotes::new);
    public static final RegistryObject<Item> celestialNotesMoon7 = ITEMS.register("celestial_notes_moon_7", ItemCelestialNotes::new);
    public static final RegistryObject<Item> celestialNotesMoon8 = ITEMS.register("celestial_notes_moon_8", ItemCelestialNotes::new);
    public static final RegistryObject<Item> crystalEssence = ITEMS.register("crystal_essence", ItemCrystalEssence::new);
    public static final RegistryObject<Item> salisMundus = ITEMS.register("salis_mundus", ItemMagicDust::new);
    public static final RegistryObject<Item> phial = ITEMS.register("phial", ItemPhial::new);
    public static final RegistryObject<Item> scribingTools = ITEMS.register("scribing_tools", ItemScribingTools::new);
    public static final RegistryObject<Item> thaumometer = ITEMS.register("thaumometer", ItemThaumometer::new);
//    public static final RegistryObject<Item> enchantedPlaceholder;

    public static final ItemStack START_BOOK = new ItemStack(Items.WRITTEN_BOOK);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);

        CompoundTag contents = new CompoundTag();
        contents.putInt("generation", 3);
        ListTag pages = new ListTag();
        pages.add(StringTag.valueOf(Component.Serializer.toJson(Component.translatable("book.start.1"))));
        pages.add(StringTag.valueOf(Component.Serializer.toJson(Component.translatable("book.start.2"))));
        pages.add(StringTag.valueOf(Component.Serializer.toJson(Component.translatable("book.start.3"))));
        contents.put("pages", pages);
        START_BOOK.setTag(contents);
    }
}
