package com.wonginnovations.arcana.api.aspects;

import com.wonginnovations.arcana.Arcana;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.text.WordUtils;

import java.util.*;

public class Aspect {

    String name;
    Aspect[] components;
    int color;
    private String chatcolor;
    ResourceLocation image;
    int blend;
    public static HashMap<Integer, Aspect> mixList = new HashMap<>();
    private static ArrayList<Aspect> primals = new ArrayList<>();
    private static ArrayList<Aspect> compounds = new ArrayList<>();
    public static LinkedHashMap<String, Aspect> aspects = new LinkedHashMap<>();
    public static final Aspect AIR = new Aspect("aer", 0xFFFF7E, "e", 1);
    public static final Aspect EARTH = new Aspect("terra", 0x56C000, "2", 1);
    public static final Aspect FIRE = new Aspect("ignis", 0xFF5A01, "c", 1);
    public static final Aspect WATER = new Aspect("aqua", 0x3CD4FC, "3", 1);
    public static final Aspect ORDER = new Aspect("ordo", 0xD5D4EC, "7", 1);
    public static final Aspect ENTROPY = new Aspect("perditio", 0x404040, "8", 771);
    public static final Aspect VOID;
    public static final Aspect LIGHT;
    public static final Aspect MOTION;
    public static final Aspect COLD;
    public static final Aspect CRYSTAL;
    public static final Aspect METAL;
    public static final Aspect LIFE;
    public static final Aspect DEATH;
    public static final Aspect ENERGY;
    public static final Aspect EXCHANGE;
    public static final Aspect MAGIC;
    public static final Aspect AURA;
    public static final Aspect ALCHEMY;
    public static final Aspect FLUX;
    public static final Aspect DARKNESS;
    public static final Aspect ELDRITCH;
    public static final Aspect FLIGHT;
    public static final Aspect PLANT;
    public static final Aspect TOOL;
    public static final Aspect CRAFT;
    public static final Aspect MECHANISM;
    public static final Aspect TRAP;
    public static final Aspect SOUL;
    public static final Aspect MIND;
    public static final Aspect SENSES;
    public static final Aspect AVERSION;
    public static final Aspect PROTECT;
    public static final Aspect DESIRE;
    public static final Aspect UNDEAD;
    public static final Aspect BEAST;
    public static final Aspect MAN;

    public Aspect(String name, int color, Aspect[] components, ResourceLocation image, int blend) {
        if (aspects.containsKey(name)) {
            throw new IllegalArgumentException(name + " already registered!");
        } else {
            this.name = name;
            this.components = components;
            this.color = color;
            this.image = image;
            this.blend = blend;
            aspects.put(name, this);
            //ScanningManager.addScannableThing(new ScanAspect("!" + tag, this));
            if (components != null) {
                int h = (components[0].getName() + components[1].getName()).hashCode();
                mixList.put(h, this);
            }

        }
    }

    public Aspect(String name, int color, Aspect[] components) {
        this(name, color, components, new ResourceLocation(Arcana.MODID, "textures/aspects/" + name.toLowerCase() + ".png"), 1);
    }

    public Aspect(String name, int color, Aspect[] components, int blend) {
        this(name, color, components, new ResourceLocation(Arcana.MODID, "textures/aspects/" + name.toLowerCase() + ".png"), blend);
    }

    public Aspect(String name, int color, String chatcolor, int blend) {
        this(name, color, (Aspect[])null, blend);
        this.setChatcolor(chatcolor);
    }

    public int getColor() {
        return this.color;
    }

//    public String getName() {
//        return WordUtils.capitalizeFully(this.name);
//    }

    public String getLocalizedDescription() {
        return I18n.get(Arcana.MODID + ".aspect." + this.name);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Aspect[] getComponents() {
        return this.components;
    }

    public void setComponents(Aspect[] components) {
        this.components = components;
    }

    public ResourceLocation getImage() {
        return this.image;
    }

    public static Aspect getAspect(String tag) {
        return (Aspect)aspects.get(tag);
    }

    public int getBlend() {
        return this.blend;
    }

    public void setBlend(int blend) {
        this.blend = blend;
    }

    public boolean isPrimal() {
        return this.getComponents() == null || this.getComponents().length != 2;
    }

    public static ArrayList<Aspect> getPrimalAspects() {
        if (primals.isEmpty()) {
            for (Aspect aspect : aspects.values()) {
                if (aspect.isPrimal()) {
                    primals.add(aspect);
                }
            }
        }

        return primals;
    }

    public static ArrayList<Aspect> getCompoundAspects() {
        if (compounds.isEmpty()) {
            for (Aspect aspect : aspects.values()) {
                if (!aspect.isPrimal()) {
                    compounds.add(aspect);
                }
            }
        }

        return compounds;
    }

    public String getChatcolor() {
        return this.chatcolor;
    }

    public void setChatcolor(String chatcolor) {
        this.chatcolor = chatcolor;
    }

    public CompoundTag toCompoundTag() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putString("aspect", this.name);
        compoundTag.putInt("color", this.color);
        return compoundTag;
    }

    static {
        VOID = new Aspect("vacuos", 0x888888, new Aspect[]{AIR, ENTROPY}, 771);
        LIGHT = new Aspect("lux", 0xFFFFC0, new Aspect[]{AIR, FIRE});
        MOTION = new Aspect("motus", 0xCDCCF4, new Aspect[]{AIR, ORDER});
        COLD = new Aspect("gelum", 0xE1FFFF, new Aspect[]{FIRE, ENTROPY});
        CRYSTAL = new Aspect("vitreus", 0x80FFFF, new Aspect[]{EARTH, AIR});
        METAL = new Aspect("metallum", 0xB5B5CD, new Aspect[]{EARTH, ORDER});
        LIFE = new Aspect("victus", 0xDE0005, new Aspect[]{EARTH, WATER});
        DEATH = new Aspect("mortuus", 0x6A0005, new Aspect[]{WATER, ENTROPY});
        ENERGY = new Aspect("potentia", 0xC0FFFF, new Aspect[]{ORDER, FIRE});
        EXCHANGE = new Aspect("permutatio", 0x578357, new Aspect[]{ENTROPY, ORDER});
        MAGIC = new Aspect("praecantatio", 0xCF00FF, new Aspect[]{ENERGY, AIR});
        AURA = new Aspect("auram", 0xFFC0FF, new Aspect[]{MAGIC, AIR});
        ALCHEMY = new Aspect("alkimia", 0x23AC9D, new Aspect[]{MAGIC, WATER});
        FLUX = new Aspect("vitium", 0x800080, new Aspect[]{ENTROPY, MAGIC});
        DARKNESS = new Aspect("tenebrae", 0x222222, new Aspect[]{VOID, LIGHT});
        ELDRITCH = new Aspect("alienis", 0x805080, new Aspect[]{VOID, DARKNESS});
        FLIGHT = new Aspect("volatus", 0xE7E7D7, new Aspect[]{AIR, MOTION});
        PLANT = new Aspect("herba", 0x01AC00, new Aspect[]{LIFE, EARTH});
        TOOL = new Aspect("instrumentum", 0x4040EE, new Aspect[]{METAL, ENERGY});
        CRAFT = new Aspect("fabrico", 0x809D80, new Aspect[]{EXCHANGE, TOOL});
        MECHANISM = new Aspect("machina", 0x8080A0, new Aspect[]{MOTION, TOOL});
        TRAP = new Aspect("vinculum", 0x9A8080, new Aspect[]{MOTION, ENTROPY});
        SOUL = new Aspect("spiritus", 0xEBEBFB, new Aspect[]{LIFE, DEATH});
        MIND = new Aspect("cognitio", 0xF9967F, new Aspect[]{FIRE, SOUL});
        SENSES = new Aspect("sensus", 0xC0FFC0, new Aspect[]{AIR, SOUL});
        AVERSION = new Aspect("aversio", 0xC05050, new Aspect[]{SOUL, ENTROPY});
        PROTECT = new Aspect("praemunio", 0x00C0C0, new Aspect[]{SOUL, EARTH});
        DESIRE = new Aspect("desiderium", 0xE6BE44, new Aspect[]{SOUL, VOID});
        UNDEAD = new Aspect("exanimis", 0x3A4000, new Aspect[]{MOTION, DEATH});
        BEAST = new Aspect("bestia", 0x9F6409, new Aspect[]{MOTION, LIFE});
        MAN = new Aspect("humanus", 0xFFD7C0, new Aspect[]{SOUL, LIFE});
    }
}
