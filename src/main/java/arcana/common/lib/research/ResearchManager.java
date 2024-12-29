package arcana.common.lib.research;

import arcana.Arcana;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.PacketDistributor;
import arcana.api.ArcanaApi;
import arcana.api.capabilities.IPlayerKnowledge;
import arcana.api.capabilities.IPlayerWarp;
import arcana.api.capabilities.ModCapabilities;
import arcana.api.internal.CommonInternals;
import arcana.api.research.*;
import arcana.common.config.ModConfig;
import arcana.common.lib.network.PacketHandler;
import arcana.common.lib.network.misc.PacketKnowledgeGain;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.concurrent.ConcurrentHashMap;

public class ResearchManager {
    public static ConcurrentHashMap<String, Boolean> syncList = new ConcurrentHashMap<>();
    public static boolean noFlags = false;
    public static LinkedHashSet<Integer> craftingReferences = new LinkedHashSet<>();

    public static boolean addKnowledge(Player player, IPlayerKnowledge.EnumKnowledgeType type, ResearchCategory category, int amount) {
        IPlayerKnowledge knowledge = ModCapabilities.getKnowledge(player);
        if (!type.hasFields()) {
            category = null;
        }
        if (MinecraftForge.EVENT_BUS.post(new ResearchEvent.Knowledge(player, type, category, amount))) {
            return false;
        }
        int kp = knowledge.getKnowledge(type, category);
        knowledge.addKnowledge(type, category, amount);
        int kr = knowledge.getKnowledge(type, category) - kp;
        if (amount > 0) {
            for (int a = 0; a < kr; ++a) {
                PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new PacketKnowledgeGain((byte) type.ordinal(), (category == null) ? null : category.key));
            }
        }
        ResearchManager.syncList.put(player.getName().getString(), true);
        return true;
    }

    public static boolean completeResearch(Player player, String researchkey, boolean sync) {
        boolean b = false;
        while (progressResearch(player, researchkey, sync)) {
            b = true;
        }
        return b;
    }

    public static boolean completeResearch(Player player, String researchkey) {
        boolean b = false;
        while (progressResearch(player, researchkey, true)) {
            b = true;
        }
        return b;
    }

    public static boolean startResearchWithPopup(Player player, String researchkey) {
        boolean b = progressResearch(player, researchkey, true);
        if (b) {
            IPlayerKnowledge knowledge = ModCapabilities.getKnowledge(player);
            knowledge.setResearchFlag(researchkey, IPlayerKnowledge.EnumResearchFlag.POPUP);
            knowledge.setResearchFlag(researchkey, IPlayerKnowledge.EnumResearchFlag.RESEARCH);
        }
        return b;
    }

    public static boolean progressResearch(Player player, String researchkey) {
        return progressResearch(player, researchkey, true);
    }

    public static boolean progressResearch(Player player, String researchkey, boolean sync) {
        IPlayerKnowledge knowledge = ModCapabilities.getKnowledge(player);
        if (knowledge.isResearchComplete(researchkey) || !doesPlayerHaveRequisites(player, researchkey)) {
            return false;
        }
        if (MinecraftForge.EVENT_BUS.post(new ResearchEvent.Research(player, researchkey))) {
            return false;
        }
        if (!knowledge.isResearchKnown(researchkey)) {
            knowledge.addResearch(researchkey);
        }
        ResearchEntry re = ResearchCategories.getResearch(researchkey);
        if (re != null) {
            boolean popups = true;
            if (re.getStages() != null) {
                int cs = knowledge.getResearchStage(researchkey);
                ResearchStage currentStage = null;
                if (cs > 0) {
                    cs = Math.min(cs, re.getStages().length);
                    currentStage = re.getStages()[cs - 1];
                }
                if (re.getStages().length == 1 && cs == 0 && re.getStages()[0].getCraft() == null && re.getStages()[0].getObtain() == null && re.getStages()[0].getKnow() == null && re.getStages()[0].getResearch() == null) {
                    ++cs;
                } else if (re.getStages().length > 1 && re.getStages().length <= cs + 1 && cs < re.getStages().length && re.getStages()[cs].getCraft() == null && re.getStages()[cs].getObtain() == null && re.getStages()[cs].getKnow() == null && re.getStages()[cs].getResearch() == null) {
                    ++cs;
                }
                knowledge.setResearchStage(researchkey, Math.min(re.getStages().length + 1, cs + 1));
                popups = (cs >= re.getStages().length);
                int warp = 0;
                if (currentStage != null) {
                    warp = currentStage.getWarp();
                }
                if (popups) {
                    cs = Math.min(cs, re.getStages().length);
                    currentStage = re.getStages()[cs - 1];
                }
                if (currentStage != null) {
                    warp += currentStage.getWarp();
                    if (warp > 0 && !ModConfig.CONFIG_MISC.wussMode.get() && !player.level().isClientSide) {
                        if (warp > 1) {
                            IPlayerWarp pw = ModCapabilities.getWarp(player);
                            int w2 = warp / 2;
                            if (warp - w2 > 0) {
                                ArcanaApi.internalMethods.addWarpToPlayer(player, warp - w2, IPlayerWarp.EnumWarpType.PERMANENT);
                            }
                            if (w2 > 0) {
                                ArcanaApi.internalMethods.addWarpToPlayer(player, w2, IPlayerWarp.EnumWarpType.NORMAL);
                            }
                        } else {
                            ArcanaApi.internalMethods.addWarpToPlayer(player, warp, IPlayerWarp.EnumWarpType.PERMANENT);
                        }
                    }
                }
            }
            if (popups) {
                if (sync) {
                    knowledge.setResearchFlag(researchkey, IPlayerKnowledge.EnumResearchFlag.POPUP);
                    if (!ResearchManager.noFlags) {
                        knowledge.setResearchFlag(researchkey, IPlayerKnowledge.EnumResearchFlag.RESEARCH);
                    } else {
                        ResearchManager.noFlags = false;
                    }
                    if (re.getRewardItem() != null) {
                        for (ItemStack rs : re.getRewardItem()) {
                            if (!player.getInventory().add(rs.copy())) {
                                player.drop(rs.copy(), false);
                            }
                        }
                    }
                    if (re.getRewardKnow() != null) {
                        for (ResearchStage.Knowledge rk : re.getRewardKnow()) {
                            addKnowledge(player, rk.type, rk.category, rk.type.getProgression() * rk.amount);
                        }
                    }
                }
                for (String rc : ResearchCategories.researchCategories.keySet()) {
                    for (ResearchEntry ri : ResearchCategories.getResearchCategory(rc).research.values()) {
                        if (ri != null && ri.getAddenda() != null) {
                            if (!knowledge.isResearchComplete(ri.getKey())) {
                                continue;
                            }
                            for (ResearchAddendum addendum : ri.getAddenda()) {
                                if (addendum.getResearch() != null && Arrays.asList(addendum.getResearch()).contains(researchkey)) {
                                    Component text = Component.translatable("tc.addaddendum", ri.getLocalizedName());
                                    player.sendSystemMessage(text);
                                    knowledge.setResearchFlag(ri.getKey(), IPlayerKnowledge.EnumResearchFlag.PAGE);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (re != null && re.getSiblings() != null) {
            for (String sibling : re.getSiblings()) {
                if (!knowledge.isResearchComplete(sibling) && doesPlayerHaveRequisites(player, sibling)) {
                    completeResearch(player, sibling, sync);
                }
            }
        }
        if (sync) {
            ResearchManager.syncList.put(player.getName().getString(), true);
            if (re != null) {
                player.giveExperiencePoints(5);
            }
        }
        return true;
    }

    public static boolean doesPlayerHaveRequisites(Player player, String key) {
        ResearchEntry ri = ResearchCategories.getResearch(key);
        if (ri == null) {
            return true;
        }
        String[] parents = ri.getParentsStripped();
        return parents == null || ModCapabilities.knowsResearchStrict(player, parents);
    }

    public static void parseAllResearch() {
        JsonParser parser = new JsonParser();
        for (ResourceLocation loc : CommonInternals.jsonLocs.values()) {
            String s = "/assets/" + loc.getNamespace() + "/" + loc.getPath();
            if (!s.endsWith(".json")) {
                s += ".json";
            }
            InputStream stream = ResearchManager.class.getResourceAsStream(s);
            if (stream != null) {
                try {
                    InputStreamReader reader = new InputStreamReader(stream);
                    JsonObject obj = parser.parse(reader).getAsJsonObject();
                    JsonArray entries = obj.get("entries").getAsJsonArray();
                    for (JsonElement element : entries) {
                        try {
                            JsonObject entry = element.getAsJsonObject();
                            ResearchEntry researchEntry = parseResearchJson(entry);
                            addResearchToCategory(researchEntry);
                        } catch (Exception ignored) {}
                    }
                } catch (Exception e) {
                    Arcana.log.warn("Invalid research file: {}", loc.toString());
                }
            }
        }
    }

    private static ResearchEntry parseResearchJson(JsonObject obj) throws Exception {
        ResearchEntry entry = new ResearchEntry();
        entry.setKey(obj.getAsJsonPrimitive("key").getAsString());
        if (entry.getKey() == null) {
            throw new Exception("Invalid key in research JSon");
        }
        entry.setName(obj.getAsJsonPrimitive("name").getAsString());
        entry.setCategory(obj.getAsJsonPrimitive("category").getAsString());
        if (entry.getCategory() == null) {
            throw new Exception("Invalid category in research JSon");
        }
        if (obj.has("icons")) {
            String[] icons = arrayJsonToString(obj.get("icons").getAsJsonArray());
            if (icons != null && icons.length > 0) {
                Object[] ir = new Object[icons.length];
                for (int a = 0; a < icons.length; ++a) {
                    ItemStack stack = parseJSONtoItemStack(icons[a]);
                    if (stack != null && !stack.isEmpty()) {
                        ir[a] = stack;
                    } else if (icons[a].startsWith("focus")) {
                        ir[a] = icons[a];
                    } else {
                        ir[a] = new ResourceLocation(icons[a]);
                    }
                }
                entry.setIcons(ir);
            }
        }
        if (obj.has("parents")) {
            entry.setParents(arrayJsonToString(obj.get("parents").getAsJsonArray()));
        }
        if (obj.has("siblings")) {
            entry.setSiblings(arrayJsonToString(obj.get("siblings").getAsJsonArray()));
        }
        if (obj.has("meta")) {
            String[] meta = arrayJsonToString(obj.get("meta").getAsJsonArray());
            if (meta != null && meta.length > 0) {
                ArrayList<ResearchEntry.EnumResearchMeta> metas = new ArrayList<ResearchEntry.EnumResearchMeta>();
                for (String s : meta) {
                    ResearchEntry.EnumResearchMeta en = ResearchEntry.EnumResearchMeta.valueOf(s.toUpperCase());
                    if (en == null) {
                        throw new Exception("Illegal metadata in research JSon");
                    }
                    metas.add(en);
                }
                entry.setMeta(metas.toArray(new ResearchEntry.EnumResearchMeta[0]));
            }
        }
        if (obj.has("location")) {
            final Integer[] location = arrayJsonToInt(obj.get("location").getAsJsonArray());
            if (location != null && location.length == 2) {
                entry.setDisplayColumn(location[0]);
                entry.setDisplayRow(location[1]);
            }
        }
        if (obj.has("reward_item")) {
            entry.setRewardItem(parseJsonItemList(entry.getKey(), arrayJsonToString(obj.get("reward_item").getAsJsonArray())));
        }
        if (obj.has("reward_knowledge")) {
            final String[] sl = arrayJsonToString(obj.get("reward_knowledge").getAsJsonArray());
            if (sl != null && sl.length > 0) {
                final ArrayList<ResearchStage.Knowledge> kl = new ArrayList<ResearchStage.Knowledge>();
                for (final String s : sl) {
                    final ResearchStage.Knowledge k = ResearchStage.Knowledge.parse(s);
                    if (k != null) {
                        kl.add(k);
                    }
                }
                if (!kl.isEmpty()) {
                    entry.setRewardKnow(kl.toArray(new ResearchStage.Knowledge[0]));
                }
            }
        }
        JsonArray stagesJson = obj.get("stages").getAsJsonArray();
        ArrayList<ResearchStage> stages = new ArrayList<>();
        for (JsonElement element : stagesJson) {
            JsonObject stageObj = element.getAsJsonObject();
            ResearchStage stage = new ResearchStage();
            stage.setText(stageObj.getAsJsonPrimitive("text").getAsString());
            if (stage.getText() == null) {
                throw new Exception("Illegal stage text in research JSon");
            }
            if (stageObj.has("recipes")) {
                stage.setRecipes(arrayJsonToResourceLocations(stageObj.get("recipes").getAsJsonArray()));
            }
            if (stageObj.has("required_item")) {
                stage.setObtain(parseJsonOreList(entry.getKey(), arrayJsonToString(stageObj.get("required_item").getAsJsonArray())));
            }
            if (stageObj.has("required_craft")) {
                String[] s2 = arrayJsonToString(stageObj.get("required_craft").getAsJsonArray());
                stage.setCraft(parseJsonOreList(entry.getKey(), s2));
                if (stage.getCraft() != null && stage.getCraft().length > 0) {
                    int[] refs = new int[stage.getCraft().length];
                    int q = 0;
                    for (Object stack2 : stage.getCraft()) {
                        int code = (stack2 instanceof ItemStack) ? createItemStackHash((ItemStack) stack2) : ("oredict:" + stack2).hashCode();
                        ResearchManager.craftingReferences.add(code);
                        refs[q] = code;
                        ++q;
                    }
                    stage.setCraftReference(refs);
                }
            }
            if (stageObj.has("required_knowledge")) {
                String[] sl2 = arrayJsonToString(stageObj.get("required_knowledge").getAsJsonArray());
                if (sl2 != null && sl2.length > 0) {
                    ArrayList<ResearchStage.Knowledge> kl2 = new ArrayList<ResearchStage.Knowledge>();
                    for (String s3 : sl2) {
                        ResearchStage.Knowledge i = ResearchStage.Knowledge.parse(s3);
                        if (i != null) {
                            kl2.add(i);
                        }
                    }
                    if (!kl2.isEmpty()) {
                        stage.setKnow(kl2.toArray(new ResearchStage.Knowledge[0]));
                    }
                }
            }
            if (stageObj.has("required_research")) {
                stage.setResearch(arrayJsonToString(stageObj.get("required_research").getAsJsonArray()));
                if (stage.getResearch() != null && stage.getResearch().length > 0) {
                    String[] rKey = new String[stage.getResearch().length];
                    String[] rIcn = new String[stage.getResearch().length];
                    for (int a2 = 0; a2 < stage.getResearch().length; ++a2) {
                        String[] ss = stage.getResearch()[a2].split(";");
                        rKey[a2] = ss[0];
                        if (ss.length > 1) {
                            rIcn[a2] = ss[1];
                        } else {
                            rIcn[a2] = null;
                        }
                    }
                    stage.setResearch(rKey);
                    stage.setResearchIcon(rIcn);
                }
            }
            if (stageObj.has("warp")) {
                stage.setWarp(stageObj.getAsJsonPrimitive("warp").getAsInt());
            }
            stages.add(stage);
        }
        if (!stages.isEmpty()) {
            entry.setStages(stages.toArray(new ResearchStage[0]));
        }
        if (obj.get("addenda") != null) {
            JsonArray addendaJson = obj.get("addenda").getAsJsonArray();
            ArrayList<ResearchAddendum> addenda = new ArrayList<>();
            for (JsonElement element2 : addendaJson) {
                JsonObject addendumObj = element2.getAsJsonObject();
                ResearchAddendum addendum = new ResearchAddendum();
                addendum.setText(addendumObj.getAsJsonPrimitive("text").getAsString());
                if (addendum.getText() == null) {
                    throw new Exception("Illegal addendum text in research JSon");
                }
                if (addendumObj.has("recipes")) {
                    addendum.setRecipes(arrayJsonToResourceLocations(addendumObj.get("recipes").getAsJsonArray()));
                }
                if (addendumObj.has("required_research")) {
                    addendum.setResearch(arrayJsonToString(addendumObj.get("required_research").getAsJsonArray()));
                }
                addenda.add(addendum);
            }
            if (!addenda.isEmpty()) {
                entry.setAddenda(addenda.toArray(new ResearchAddendum[0]));
            }
        }
        return entry;
    }

    public static int createItemStackHash(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return 0;
        }
        stack.setCount(1);
        return stack.toString().hashCode();
    }

    private static String[] arrayJsonToString(JsonArray jsonArray) {
        ArrayList<String> out = new ArrayList<>();
        for (JsonElement element : jsonArray) {
            out.add(element.getAsString());
        }
        return (out.isEmpty()) ? null : out.toArray(new String[0]);
    }

    private static ResourceLocation[] arrayJsonToResourceLocations(JsonArray jsonArray) {
        ArrayList<ResourceLocation> out = new ArrayList<>();
        for (JsonElement element : jsonArray) {
            out.add(new ResourceLocation(element.getAsString()));
        }
        return (out.isEmpty()) ? null : out.toArray(new ResourceLocation[0]);
    }

    private static Integer[] arrayJsonToInt(JsonArray jsonArray) {
        ArrayList<Integer> out = new ArrayList<Integer>();
        for (JsonElement element : jsonArray) {
            out.add(element.getAsInt());
        }
        return (out.isEmpty()) ? null : out.toArray(new Integer[0]);
    }

    private static ItemStack[] parseJsonItemList(String key, String[] stacks) {
        if (stacks == null || stacks.length == 0) {
            return null;
        }
        ItemStack[] work = new ItemStack[stacks.length];
        int idx = 0;
        for (String s : stacks) {
            s = s.replace("'", "\"");
            ItemStack stack = parseJSONtoItemStack(s);
            if (stack != null && !stack.isEmpty()) {
                work[idx] = stack;
                ++idx;
            }
        }
        ItemStack[] out = null;
        if (idx > 0) {
            out = Arrays.copyOf(work, idx);
        }
        return out;
    }

    private static Object[] parseJsonOreList(String key, String[] stacks) {
        if (stacks == null || stacks.length == 0) {
            return null;
        }
        Object[] work = new Object[stacks.length];
        int idx = 0;
        for (String s : stacks) {
            s = s.replace("'", "\"");
            if (s.startsWith("oredict:")) {
                String[] st = s.split(":");
                if (st.length > 1) {
                    work[idx] = st[1];
                    ++idx;
                }
            } else {
                ItemStack stack = parseJSONtoItemStack(s);
                if (stack != null && !stack.isEmpty()) {
                    work[idx] = stack;
                    ++idx;
                }
            }
        }
        Object[] out = null;
        if (idx > 0) {
            out = Arrays.copyOf(work, idx);
        }
        return out;
    }

    public static ItemStack parseJSONtoItemStack(String entry) {
        if (entry == null) {
            return null;
        }
        String[] split = entry.split(";");
        String name = split[0];
        int num = -1;
        int dam = -1;
        String nbt = null;
        for (int a = 1; a < split.length; ++a) {
            if (split[a].startsWith("{")) {
                nbt = split[a];
                nbt.replaceAll("'", "\"");
                break;
            }
            int q = -1;
            try {
                q = Integer.parseInt(split[a]);
            } catch (NumberFormatException e) {
                continue;
            }
            if (q >= 0 && num < 0) {
                num = q;
            } else if (q >= 0 && dam < 0) {
                dam = q;
            }
        }
        if (num < 0) {
            num = 1;
        }
        if (dam < 0) {
            dam = 0;
        }
        ItemStack stack = ItemStack.EMPTY;
        try {

            Item it = ForgeRegistries.ITEMS.getValue(new ResourceLocation(name));
            if (it != null) {
                stack = new ItemStack(it, num);
//                if (nbt != null) {
//                    Tag.TagType.
//                    stack.setTag(Gson JsonToNBT.getTagFromJson(nbt));
//                }
            }
        } catch (Exception ignored) {}
        return stack;
    }

    private static void addResearchToCategory(ResearchEntry ri) {
        ResearchCategory rl = ResearchCategories.getResearchCategory(ri.getCategory());
        rl.research.put(ri.getKey(), ri);
    }
}
