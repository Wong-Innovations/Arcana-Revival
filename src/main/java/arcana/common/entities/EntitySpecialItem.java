package arcana.common.entities;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EntitySpecialItem extends ItemEntity {
    public EntitySpecialItem(EntityType<EntitySpecialItem> entityType, Level level) {
        super(entityType, level);
    }

    public EntitySpecialItem(Level pLevel, double x, double y, double z, ItemStack pItemStack) {
        this(ModEntityTypes.SPECIAL_ITEM.get(), pLevel);
        this.setPos(x, y, z);
        this.setNoGravity(true);
        this.setItem(pItemStack);
    }

    @Override
    public boolean hurt(DamageSource source, float pAmount) {
        return !source.is(DamageTypes.EXPLOSION) && super.hurt(source, pAmount);
    }
}
