package com.wonginnovations.arcana.util.annotations;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
/**
 * \@GenLootTable
 */
public @interface GLT {
	/**
	 * replacement() Item as ResourceLocation.toString()
	 */
	String replacement() default "";
}
