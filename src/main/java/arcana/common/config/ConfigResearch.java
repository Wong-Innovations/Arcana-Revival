package arcana.common.config;

import arcana.Arcana;
import net.minecraft.resources.ResourceLocation;
import arcana.api.ArcanaApi;
import arcana.api.aspects.Aspect;
import arcana.api.aspects.AspectList;
import arcana.api.research.ResearchCategories;
import arcana.api.research.ScanningManager;
import arcana.api.research.theorycraft.*;
import arcana.common.lib.research.ResearchManager;
import arcana.common.lib.research.ScanGeneric;
import arcana.common.lib.research.ScanSky;
import arcana.common.lib.research.theorycraft.CardCelestial;

public class ConfigResearch {
    public static String[] TCCategories = new String[]{"BASICS", "ALCHEMY", "AUROMANCY", "ARTIFICE", "INFUSION", "GOLEMANCY", "ELDRITCH"};
    private static final ResourceLocation BACK_OVER = new ResourceLocation(Arcana.MODID, "textures/gui/gui_research_back_over.png");

    public static void postInit() {
        ResearchManager.parseAllResearch();
    }

    public static void init() {
        initCategories();
        initScannables();
        initTheorycraft();
        for (String cat : ConfigResearch.TCCategories) {
            ArcanaApi.registerResearchLocation(new ResourceLocation(Arcana.MODID, "research/" + cat.toLowerCase()));
        }
    }

    private static void initCategories() {
        ResearchCategories.registerCategory("BASICS", null, new AspectList().add(Aspect.PLANT, 5).add(Aspect.ORDER, 5).add(Aspect.ENTROPY, 5).add(Aspect.AIR, 5).add(Aspect.FIRE, 5).add(Aspect.EARTH, 3).add(Aspect.WATER, 5), new ResourceLocation(Arcana.MODID, "textures/item/cheaters_thaumonomicon.png"), new ResourceLocation(Arcana.MODID, "textures/gui/gui_research_back_1.jpg"), ConfigResearch.BACK_OVER);
        ResearchCategories.registerCategory("AUROMANCY", "UNLOCKAUROMANCY", new AspectList().add(Aspect.AURA, 20).add(Aspect.MAGIC, 20).add(Aspect.FLUX, 15).add(Aspect.CRYSTAL, 5).add(Aspect.COLD, 5).add(Aspect.AIR, 5), new ResourceLocation(Arcana.MODID, "textures/research/cat_auromancy.png"), new ResourceLocation(Arcana.MODID, "textures/gui/gui_research_back_2.jpg"), ConfigResearch.BACK_OVER);
        ResearchCategories.registerCategory("ALCHEMY", "UNLOCKALCHEMY", new AspectList().add(Aspect.ALCHEMY, 30).add(Aspect.FLUX, 10).add(Aspect.MAGIC, 10).add(Aspect.LIFE, 5).add(Aspect.AVERSION, 5).add(Aspect.DESIRE, 5).add(Aspect.WATER, 5), new ResourceLocation(Arcana.MODID, "textures/research/cat_alchemy.png"), new ResourceLocation(Arcana.MODID, "textures/gui/gui_research_back_3.jpg"), ConfigResearch.BACK_OVER);
        ResearchCategories.registerCategory("ARTIFICE", "UNLOCKARTIFICE", new AspectList().add(Aspect.MECHANISM, 10).add(Aspect.CRAFT, 10).add(Aspect.METAL, 10).add(Aspect.TOOL, 10).add(Aspect.ENERGY, 10).add(Aspect.LIGHT, 5).add(Aspect.FLIGHT, 5).add(Aspect.TRAP, 5).add(Aspect.FIRE, 5), new ResourceLocation(Arcana.MODID, "textures/research/cat_artifice.png"), new ResourceLocation(Arcana.MODID, "textures/gui/gui_research_back_4.jpg"), ConfigResearch.BACK_OVER);
        ResearchCategories.registerCategory("INFUSION", "UNLOCKINFUSION", new AspectList().add(Aspect.MAGIC, 30).add(Aspect.PROTECT, 10).add(Aspect.TOOL, 10).add(Aspect.FLUX, 5).add(Aspect.CRAFT, 5).add(Aspect.SOUL, 5).add(Aspect.EARTH, 3), new ResourceLocation(Arcana.MODID, "textures/research/cat_infusion.png"), new ResourceLocation(Arcana.MODID, "textures/gui/gui_research_back_7.jpg"), ConfigResearch.BACK_OVER);
        ResearchCategories.registerCategory("GOLEMANCY", "UNLOCKGOLEMANCY", new AspectList().add(Aspect.MAN, 20).add(Aspect.MOTION, 10).add(Aspect.MIND, 10).add(Aspect.MECHANISM, 10).add(Aspect.EXCHANGE, 5).add(Aspect.SENSES, 5).add(Aspect.BEAST, 5).add(Aspect.ORDER, 5), new ResourceLocation(Arcana.MODID, "textures/research/cat_golemancy.png"), new ResourceLocation(Arcana.MODID, "textures/gui/gui_research_back_5.jpg"), ConfigResearch.BACK_OVER);
        ResearchCategories.registerCategory("ELDRITCH", "UNLOCKELDRITCH", new AspectList().add(Aspect.ELDRITCH, 20).add(Aspect.DARKNESS, 10).add(Aspect.MAGIC, 5).add(Aspect.MIND, 5).add(Aspect.VOID, 5).add(Aspect.DEATH, 5).add(Aspect.UNDEAD, 5).add(Aspect.ENTROPY, 5), new ResourceLocation(Arcana.MODID, "textures/research/cat_eldritch.png"), new ResourceLocation(Arcana.MODID, "textures/gui/gui_research_back_6.jpg"), ConfigResearch.BACK_OVER);
    }

    private static void initScannables() {
        ScanningManager.addScannableThing(new ScanGeneric());
        ScanningManager.addScannableThing(new ScanSky());
    }

    private static void initTheorycraft() {
        TheorycraftManager.registerAid(new AidBookshelf());
        TheorycraftManager.registerCard(CardStudy.class);
        TheorycraftManager.registerCard(CardAnalyze.class);
        TheorycraftManager.registerCard(CardBalance.class);
        TheorycraftManager.registerCard(CardNotation.class);
        TheorycraftManager.registerCard(CardPonder.class);
        TheorycraftManager.registerCard(CardRethink.class);
        TheorycraftManager.registerCard(CardReject.class);
        TheorycraftManager.registerCard(CardExperimentation.class);
        TheorycraftManager.registerCard(CardCelestial.class);
    }
}
