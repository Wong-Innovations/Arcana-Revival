package arcana.common.lib.research;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import arcana.api.ArcanaApi;
import arcana.api.capabilities.ModCapabilities;
import arcana.common.items.ModItems;
import arcana.api.research.IScanThing;
import arcana.common.lib.utils.InventoryUtils;

import java.util.ArrayList;

public class ScanSky implements IScanThing {
    private static final Item[] NOTES = {
            ModItems.celestialNotesSun.get(),
            ModItems.celestialNotesStars1.get(),
            ModItems.celestialNotesStars2.get(),
            ModItems.celestialNotesStars3.get(),
            ModItems.celestialNotesStars4.get(),
            ModItems.celestialNotesMoon1.get(),
            ModItems.celestialNotesMoon2.get(),
            ModItems.celestialNotesMoon3.get(),
            ModItems.celestialNotesMoon4.get(),
            ModItems.celestialNotesMoon5.get(),
            ModItems.celestialNotesMoon6.get(),
            ModItems.celestialNotesMoon7.get(),
            ModItems.celestialNotesMoon8.get(),
    };

    @Override
    public boolean checkThing(Player player, Object obj) {
        if (obj != null || player.getXRot() > 0.0f || !player.level().canSeeSky(player.getOnPos().above()) || player.level().dimension() != Level.OVERWORLD || !ModCapabilities.knowsResearchStrict(player, "CELESTIALSCANNING")) {
            return false;
        }
        int yaw = (int) (player.getYRot() + 90.0f) % 360;
        int pitch = (int) Math.abs(player.getXRot());
        int ca = (int) ((player.level().getTimeOfDay(0.0f) + 0.25) * 360.0) % 360;
        boolean night = ca > 180;
        boolean inRangeYaw;
        boolean inRangePitch;
        if (night) {
            ca -= 180;
        }
        if (ca > 90) {
            inRangeYaw = (Math.abs(Math.abs(yaw) - 180) < 10);
            inRangePitch = (Math.abs(180 - ca - pitch) < 7);
        } else {
            inRangeYaw = (Math.abs(yaw) < 10);
            inRangePitch = (Math.abs(ca - pitch) < 7);
        }
        return (inRangeYaw && inRangePitch) || night;
    }

    @Override
    public void onSuccess(Player player, Object object) {
        if (object != null || player.getXRot() > 0.0f || !player.level().canSeeSky(player.getOnPos().above()) || !ModCapabilities.knowsResearchStrict(player, "CELESTIALSCANNING")) {
            return;
        }
        int yaw = (int) (player.getYRot() + 90.0f) % 360;
        int pitch = (int) Math.abs(player.getXRot());
        int ca = (int) ((player.level().getTimeOfDay(0.0f) + 0.25) * 360.0) % 360;
        boolean night = ca > 180;
        boolean inRangeYaw = false;
        boolean inRangePitch = false;
        if (night) {
            ca -= 180;
        }
        if (ca > 90) {
            inRangeYaw = (Math.abs(Math.abs(yaw) - 180) < 10);
            inRangePitch = (Math.abs(180 - ca - pitch) < 7);
        } else {
            inRangeYaw = (Math.abs(yaw) < 10);
            inRangePitch = (Math.abs(ca - pitch) < 7);
        }
        int worldDay = (int) (player.level().getGameTime() / 24000L);
        if (inRangeYaw && inRangePitch) {
            String pk = "CEL_" + worldDay + "_";
            String key = pk + (night ? ("Moon" + player.level().getMoonPhase()) : "Sun");
            if (ModCapabilities.knowsResearch(player, key)) {
                ((ServerPlayer) player).sendSystemMessage(Component.translatable("tc.celestial.fail.1"), true);
                return;
            }
            if (InventoryUtils.isPlayerCarryingAmount(player, new ItemStack(ModItems.scribingTools.get(), 1), true) && InventoryUtils.consumePlayerItem(player, new ItemStack(Items.PAPER), false, true)) {
                ItemStack stack = new ItemStack(NOTES[night ? (5 + player.level().getMoonPhase()) : 0], 1);
                if (!player.getInventory().add(stack)) {
                    player.drop(stack, false);
                }
                ArcanaApi.internalMethods.progressResearch(player, key);
            } else {
                ((ServerPlayer) player).sendSystemMessage(Component.translatable("tc.celestial.fail.2"), true);
            }
            cleanResearch(player, pk);
        } else {
            if (!night) {
                return;
            }
            Direction face = player.getDirection();
            int num = face.get3DDataValue() - 2;
            String pk2 = "CEL_" + worldDay + "_";
            String key2 = pk2 + "Star" + num;
            if (ModCapabilities.knowsResearch(player, key2)) {
                ((ServerPlayer) player).sendSystemMessage(Component.translatable("tc.celestial.fail.1"), true);
                return;
            }
            if (InventoryUtils.isPlayerCarryingAmount(player, new ItemStack(ModItems.scribingTools.get(), 1), true) && InventoryUtils.consumePlayerItem(player, new ItemStack(Items.PAPER), false, true)) {
                ItemStack stack2 = new ItemStack(NOTES[1 + num], 1);
                if (!player.getInventory().add(stack2)) {
                    player.drop(stack2, false);
                }
                ArcanaApi.internalMethods.progressResearch(player, key2);
            } else {
                ((ServerPlayer) player).sendSystemMessage(Component.translatable("tc.celestial.fail.2"), true);
            }
            cleanResearch(player, pk2);
        }
    }

    private void cleanResearch(Player player, String pk) {
        ArrayList<String> list = new ArrayList<>();
        for (String key : ModCapabilities.getKnowledge(player).getResearchList()) {
            if (key.startsWith("CEL_") && !key.startsWith(pk)) {
                list.add(key);
            }
        }
        for (String key : list) {
            ModCapabilities.getKnowledge(player).removeResearch(key);
        }
        ResearchManager.syncList.put(player.getName().getString(), true);
    }

    @Override
    public String getResearchKey(Player player, Object object) {
        return "";
    }
}
