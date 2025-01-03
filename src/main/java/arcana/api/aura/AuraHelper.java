package arcana.api.aura;

import arcana.api.ArcanaApi;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class AuraHelper {

    public static float drainVis(Level level, BlockPos pos, float amount, boolean simulate) {
        return ArcanaApi.internalMethods.drainVis(level, pos, amount, simulate);
    }

    public static float drainFlux(Level level, BlockPos pos, float amount, boolean simulate) {
        return ArcanaApi.internalMethods.drainFlux(level, pos, amount, simulate);
    }

    public static void addVis(Level level, BlockPos pos, float amount) {
        ArcanaApi.internalMethods.addVis(level, pos, amount);
    }

    public static float getVis(Level level, BlockPos pos) {
        return ArcanaApi.internalMethods.getVis(level, pos);
    }

    public static void polluteAura(Level level, BlockPos pos, float amount, boolean showEffect) {
        ArcanaApi.internalMethods.addFlux(level, pos, amount, showEffect);
    }

    public static float getFlux(Level level, BlockPos pos) {
        return ArcanaApi.internalMethods.getFlux(level, pos);
    }

    public static int getAuraBase(Level level, BlockPos pos) {
        return ArcanaApi.internalMethods.getAuraBase(level, pos);
    }

    public static boolean shouldPreserveAura(Level level, Player player, BlockPos pos) {
        return ArcanaApi.internalMethods.shouldPreserveAura(level, player, pos);
    }
    
}
