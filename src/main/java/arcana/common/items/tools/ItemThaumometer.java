package arcana.common.items.tools;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import arcana.api.capabilities.ModCapabilities;
import arcana.api.research.ScanningManager;
import arcana.client.fx.FXDispatcher;
import arcana.common.items.ItemBase;
import arcana.common.lib.ModSounds;
import arcana.common.lib.network.PacketHandler;
import arcana.common.lib.network.misc.PacketAuraToClient;
import arcana.common.lib.research.ResearchManager;
import arcana.common.world.aura.AuraChunk;
import arcana.common.world.aura.AuraHandler;
import org.jetbrains.annotations.NotNull;

public class ItemThaumometer extends ItemBase {

    public ItemThaumometer() {
        this(new Properties());
    }

    public ItemThaumometer(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        if (pLevel.isClientSide) {
            drawFX(pLevel, pPlayer);
            pPlayer.level().playLocalSound(pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), ModSounds.scan.get(), SoundSource.PLAYERS, 0.5f, 1.0f, false);
        } else {
            doScan(pLevel, pPlayer);
        }
        return InteractionResultHolder.consume(pPlayer.getItemInHand(pUsedHand));
    }

    @Override
    public void inventoryTick(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull Entity pEntity, int pSlotId, boolean pIsSelected) {
        boolean held = pIsSelected || pSlotId == 0;
        if (held && !pLevel.isClientSide && pEntity.tickCount % 20 == 0 && pEntity instanceof ServerPlayer) {
            updateAura(pStack, pLevel, (ServerPlayer) pEntity);
        }
        if (held && pLevel.isClientSide && pEntity.tickCount % 5 == 0 && pEntity instanceof Player) {
            BlockHitResult mop = getRayTraceResultFromPlayerWild(pLevel, (Player) pEntity, true);
            if (mop != null && mop.getBlockPos() != null && ScanningManager.isThingStillScannable((Player) pEntity, mop.getBlockPos())) {
                FXDispatcher.INSTANCE.scanHighlight(mop.getBlockPos());
            }
        }
    }

    protected BlockHitResult getRayTraceResultFromPlayerWild(Level worldIn, Player playerIn, boolean useLiquids) {
        float f = playerIn.xRotO + (playerIn.getXRot() - playerIn.xRotO) + worldIn.random.nextInt(25) - worldIn.random.nextInt(25);
        float f2 = playerIn.yRotO + (playerIn.getYRot() - playerIn.yRotO) + worldIn.random.nextInt(25) - worldIn.random.nextInt(25);
        double d0 = playerIn.xo + (playerIn.getX() - playerIn.xo);
        double d2 = playerIn.yo + (playerIn.getY() - playerIn.yo) + playerIn.getEyeHeight();
        double d3 = playerIn.zo + (playerIn.getZ() - playerIn.zo);
        Vec3 vec3 = new Vec3(d0, d2, d3);
        float f3 = Mth.cos(-f2 * 0.017453292f - 3.1415927f);
        float f4 = Mth.sin(-f2 * 0.017453292f - 3.1415927f);
        float f5 = -Mth.cos(-f * 0.017453292f);
        float f6 = Mth.sin(-f * 0.017453292f);
        float f7 = f4 * f5;
        float f8 = f3 * f5;
        double d4 = 16.0;
        Vec3 vec4 = vec3.add(f7 * d4, f6 * d4, f8 * d4);
        return worldIn.clip(new ClipContext(vec3, vec4, ClipContext.Block.COLLIDER, useLiquids ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE, playerIn));
    }

    private void updateAura(ItemStack stack, Level world, ServerPlayer player) {
        AuraChunk ac = AuraHandler.getAuraChunk(world.dimension(), player.getOnPos().getX() >> 4, player.getOnPos().getZ() >> 4);
        if (ac != null) {
            if ((ac.getFlux() > ac.getVis() || ac.getFlux() > (float) ac.getBase() / 3) && !ModCapabilities.knowsResearch(player, "FLUX")) {
                ResearchManager.startResearchWithPopup(player, "FLUX");
                player.sendSystemMessage(Component.translatable("research.FLUX.warn").withStyle(ChatFormatting.DARK_PURPLE), true);
            }
            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new PacketAuraToClient(ac));
        }
    }

    private void drawFX(Level worldIn, Player playerIn) {
        BlockHitResult mop = getPlayerPOVHitResult(worldIn, playerIn, ClipContext.Fluid.ANY);
        if (mop.getType() == HitResult.Type.BLOCK) {
            for (int a2 = 0; a2 < 10; ++a2) {
                FXDispatcher.INSTANCE.blockRunes(mop.getBlockPos().getX(), mop.getBlockPos().getY() + 0.25, mop.getBlockPos().getZ(), 0.3f + worldIn.random.nextFloat() * 0.7f, 0.0f, 0.3f + worldIn.random.nextFloat() * 0.7f, 15, 0.03f);
            }
        }
    }

    public void doScan(Level worldIn, Player playerIn) {
        if (!worldIn.isClientSide) {
            BlockHitResult mop = getPlayerPOVHitResult(worldIn, playerIn, ClipContext.Fluid.ANY);
            if (mop.getType() == HitResult.Type.BLOCK) {
                ScanningManager.scanTheThing(playerIn, mop.getBlockPos());
            } else if (mop.getType() == HitResult.Type.MISS) {
                ScanningManager.scanTheThing(playerIn, null);
            }
        }
    }
}
