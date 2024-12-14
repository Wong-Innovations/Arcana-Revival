package com.wonginnovations.arcana.common.item;

import com.wonginnovations.arcana.api.crafting.IDustInteraction;
import com.wonginnovations.arcana.client.fx.FXDispatcher;
import com.wonginnovations.arcana.common.ModSounds;
import com.wonginnovations.arcana.common.utils.EntityUtils;
import com.wonginnovations.arcana.common.utils.MathUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class SalisMundusItem extends Item {

    public SalisMundusItem() {
        super(new Properties());
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        if (context.getPlayer() == null) return InteractionResult.FAIL;
        if (!context.getPlayer().mayUseItemAt(context.getClickedPos(), context.getClickedFace(), stack)) {
            return InteractionResult.FAIL;
        } else if (context.getPlayer().isCrouching()) {
            return InteractionResult.PASS;
        } else {
            context.getPlayer().swing(context.getHand());

            for (IDustInteraction interaction : IDustInteraction.interactions) {
                IDustInteraction.Placement place = interaction.getValidFace(context.getLevel(), context.getPlayer(), context.getClickedPos(), context.getClickedFace());
                if (place != null) {
                    if (!context.getPlayer().isCreative()) {
                        context.getPlayer().getItemInHand(context.getHand()).shrink(1);
                    }

                    interaction.execute(context.getLevel(), context.getPlayer(), context.getClickedPos(), place, context.getClickedFace());
                    if (!context.getLevel().isClientSide()) {
                        return InteractionResult.SUCCESS;
                    }

                    this.doSparkles(context, interaction, place);
                    break;
                }
            }

            return super.onItemUseFirst(stack, context);
        }
    }

    private void doSparkles(UseOnContext context, IDustInteraction interaction, IDustInteraction.Placement place) {
        Vec3 v1 = EntityUtils.posToHand(context.getPlayer(), context.getHand());
        Vec3 v2 = new Vec3(context.getClickedPos().getX(), context.getClickedPos().getY(), context.getClickedPos().getZ());
        v2 = v2.add(0.5, 0.5, 0.5);
        v2 = v2.subtract(v1);
        int count = 50;

        for(int a = 0; a < count; ++a) {
            boolean floaty = a < count / 3;
            float r = (float) MathUtils.getRandomInt(context.getLevel().random, 255, 255) / 255.0F;
            float g = (float) MathUtils.getRandomInt(context.getLevel().random, 189, 255) / 255.0F;
            float b = (float) MathUtils.getRandomInt(context.getLevel().random, 64, 255) / 255.0F;
            FXDispatcher.INSTANCE.drawSimpleSparkle(context.getLevel().random, v1.x, v1.y, v1.z, v2.x / 6.0 + context.getLevel().random.nextGaussian() * 0.05, v2.y / 6.0 + context.getLevel().random.nextGaussian() * 0.05 + (floaty ? 0.05 : 0.15), v2.z / 6.0 + context.getLevel().random.nextGaussian() * 0.05, 0.5F, r, g, b, context.getLevel().random.nextInt(5), floaty ? 0.3F + context.getLevel().random.nextFloat() * 0.5F : 0.85F, floaty ? 0.2F : 0.5F, 16);
        }

        // currently only plays sound and draws dust for the client using the item, maybe change this in the future
        context.getLevel().playSound(context.getPlayer(), context.getClickedPos(), ModSounds.DUST.get(), SoundSource.PLAYERS, 0.33F, 1.0F + (float)context.getLevel().random.nextGaussian() * 0.05F);
        List<BlockPos> sparkles = interaction.sparkle(context.getLevel(), context.getPlayer(), context.getClickedPos(), place);
        if (sparkles != null) {
            Vec3 v = (new Vec3(context.getClickedPos().getX(), context.getClickedPos().getY(), context.getClickedPos().getZ())).add(context.getClickLocation());

            for (BlockPos sparkle : sparkles) {
                FXDispatcher.INSTANCE.drawBlockSparkles(sparkle, v);
            }
        }
    }
}
