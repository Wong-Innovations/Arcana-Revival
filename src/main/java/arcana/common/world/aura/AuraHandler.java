package arcana.common.world.aura;

import arcana.Arcana;
import arcana.api.capabilities.ModCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import arcana.common.lib.utils.PosXY;
import arcana.common.world.biomes.BiomeHandler;

import java.util.concurrent.ConcurrentHashMap;

public class AuraHandler {
    static ConcurrentHashMap<ResourceKey<Level>, AuraWorld> auras = new ConcurrentHashMap<>();

    public static AuraChunk getAuraChunk(ResourceKey<Level> dim, int x, int y) {
        if (AuraHandler.auras.containsKey(dim)) {
            return AuraHandler.auras.get(dim).getAuraChunkAt(x, y);
        }
        addAuraWorld(dim);
        if (AuraHandler.auras.containsKey(dim)) {
            return AuraHandler.auras.get(dim).getAuraChunkAt(x, y);
        }
        return null;
    }

    public static void addAuraWorld(ResourceKey<Level> dim) {
        if (!AuraHandler.auras.containsKey(dim)) {
            AuraHandler.auras.put(dim, new AuraWorld(dim));
            Arcana.log.info("Creating aura cache for level {}", dim);
        }
    }

    public static void removeAuraWorld(ResourceKey<Level> dim) {
        AuraHandler.auras.remove(dim);
        Arcana.log.info("Removing aura cache for level {}", dim);
    }

    public static void addAuraChunk(ResourceKey<Level> dim, LevelChunk chunk, short base, float vis, float flux) {
        AuraWorld aw = AuraHandler.auras.get(dim);
        if (aw == null) {
            aw = new AuraWorld(dim);
        }
        aw.getAuraChunks().put(new PosXY(chunk.getPos().x, chunk.getPos().z), new AuraChunk(chunk, base, vis, flux));
        AuraHandler.auras.put(dim, aw);
    }

    public static void removeAuraChunk(ResourceKey<Level> dim, int x, int y) {
        AuraWorld aw = AuraHandler.auras.get(dim);
        if (aw != null) {
            aw.getAuraChunks().remove(new PosXY(x, y));
        }
    }

    public static float getVis(Level level, BlockPos pos) {
        AuraChunk ac = getAuraChunk(level.dimension(), pos.getX() >> 4, pos.getZ() >> 4);
        return (ac != null) ? ac.getVis() : 0.0f;
    }

    public static float getFlux(Level level, BlockPos pos) {
        AuraChunk ac = getAuraChunk(level.dimension(), pos.getX() >> 4, pos.getZ() >> 4);
        return ac != null ? ac.getFlux() : 0.0F;
    }

    public static int getAuraBase(Level level, BlockPos pos) {
        AuraChunk ac = getAuraChunk(level.dimension(), pos.getX() >> 4, pos.getZ() >> 4);
        return ac != null ? ac.getBase() : 0;
    }

    public static float drainVis(Level level, BlockPos pos, float amount, boolean simulate) {
        boolean didit = false;
        try {
            AuraChunk ac = getAuraChunk(level.dimension(), pos.getX() >> 4, pos.getZ() >> 4);
            if (amount > ac.getVis()) {
                amount = ac.getVis();
            }
            didit = modifyVisInChunk(ac, -amount, !simulate);
        } catch (Exception ignored) {
        }
        return didit ? amount : 0.0f;
    }

    public static float drainFlux(Level level, BlockPos pos, float amount, boolean simulate) {
        boolean didit = false;

        try {
            AuraChunk ac = getAuraChunk(level.dimension(), pos.getX() >> 4, pos.getZ() >> 4);
            if (amount > ac.getFlux()) {
                amount = ac.getFlux();
            }

            didit = modifyFluxInChunk(ac, -amount, !simulate);
        } catch (Exception ignored) {
        }

        return didit ? amount : 0.0F;
    }

    public static void addVis(Level level, BlockPos pos, float amount) {
        if (amount >= 0.0F) {
            try {
                AuraChunk ac = getAuraChunk(level.dimension(), pos.getX() >> 4, pos.getZ() >> 4);
                modifyVisInChunk(ac, amount, true);
            } catch (Exception ignored) {
            }
        }
    }

    public static void addFlux(Level level, BlockPos pos, float amount) {
        if (amount >= 0.0F) {
            try {
                AuraChunk ac = getAuraChunk(level.dimension(), pos.getX() >> 4, pos.getZ() >> 4);
                modifyFluxInChunk(ac, amount, true);
            } catch (Exception ignored) {
            }
        }
    }

    public static boolean modifyVisInChunk(AuraChunk ac, float amount, boolean doit) {
        if (ac != null) {
            if (doit) {
                ac.setVis(Math.max(0.0f, ac.getVis() + amount));
            }
            return true;
        }
        return false;
    }

    private static boolean modifyFluxInChunk(AuraChunk ac, float amount, boolean doit) {
        if (ac != null) {
            if (doit) {
                ac.setFlux(Math.max(0.0F, ac.getFlux() + amount));
            }

            return true;
        } else {
            return false;
        }
    }

    public static boolean shouldPreserveAura(Level level, Player player, BlockPos pos) {
        return (player == null || ModCapabilities.getKnowledge(player).isResearchComplete("AURAPRESERVE")) && (double)(getVis(level, pos) / (float)getAuraBase(level, pos)) < 0.1;
    }

    public static void generateAura(LevelChunk chunk, RandomSource rand) {
        Holder<Biome> bgb = chunk.getNoiseBiome((chunk.getPos().x * 16 + 8) / 4, 50 / 4, (chunk.getPos().z * 16 + 8) / 4);
        float life = BiomeHandler.getBiomeAuraModifier(bgb);
        for (int a = 0; a < 4; ++a) {
            Direction dir = Direction.from2DDataValue(a);
            Holder<Biome> bgb2 = chunk.getNoiseBiome(((chunk.getPos().x + dir.getStepX()) * 16 + 8) / 4, 50 / 4, ((chunk.getPos().z + dir.getStepZ()) * 16 + 8) / 4);
            life += BiomeHandler.getBiomeAuraModifier(bgb2);
        }
        life /= 5.0f;
        float noise = (float) (1.0 + rand.nextGaussian() * 0.10000000149011612);
        short base = (short) (life * 500.0f * noise);
        base = (short) Mth.clamp(base, 0, 500);
        addAuraChunk(chunk.getLevel().dimension(), chunk, base, base, 0.0f);
    }
}
