package com.wonginnovations.arcana.blocks;

import com.wonginnovations.arcana.ArcanaBlockSetTypes;
import net.minecraft.world.level.block.state.properties.WoodType;

public class ArcanaWoodTypes {

    public static WoodType DAIR = new WoodType("dair", ArcanaBlockSetTypes.DAIR);
    public static WoodType GREATWOOD = new WoodType("greatwood", ArcanaBlockSetTypes.GREATWOOD);
    public static WoodType EUCALYPTUS = new WoodType("eucalyptus", ArcanaBlockSetTypes.EUCALYPTUS);
    public static WoodType HAWTHORN = new WoodType("hawthorn", ArcanaBlockSetTypes.HAWTHORN);
    public static WoodType SILVERWOOD = new WoodType("silverwood", ArcanaBlockSetTypes.SILVERWOOD);
    public static WoodType WILLOW = new WoodType("willow", ArcanaBlockSetTypes.WILLOW);
    public static WoodType DEAD = new WoodType("dead", ArcanaBlockSetTypes.DEAD);
    public static WoodType TRYPOPHOBIUS = new WoodType("trypophobius", ArcanaBlockSetTypes.TRYPOPHOBIUS);

    static {
        WoodType.register(DAIR);
        WoodType.register(GREATWOOD);
        WoodType.register(EUCALYPTUS);
        WoodType.register(HAWTHORN);
        WoodType.register(SILVERWOOD);
        WoodType.register(WILLOW);
        WoodType.register(DEAD);
        WoodType.register(TRYPOPHOBIUS);
    }

}
