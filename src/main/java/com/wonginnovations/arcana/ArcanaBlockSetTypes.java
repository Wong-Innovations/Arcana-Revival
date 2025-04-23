package com.wonginnovations.arcana;

import net.minecraft.world.level.block.state.properties.BlockSetType;

public class ArcanaBlockSetTypes {

    // WOODS
    public static BlockSetType DAIR = new BlockSetType("dair");
    public static BlockSetType GREATWOOD = new BlockSetType("greatwood");
    public static BlockSetType EUCALYPTUS = new BlockSetType("eucalyptus");
    public static BlockSetType HAWTHORN = new BlockSetType("hawthorn");
    public static BlockSetType SILVERWOOD = new BlockSetType("silverwood");
    public static BlockSetType WILLOW = new BlockSetType("willow");
    public static BlockSetType DEAD = new BlockSetType("dead");
    public static BlockSetType TRYPOPHOBIUS = new BlockSetType("trypophobius");

    // STONES
    public static BlockSetType ARCANE_STONE = new BlockSetType("arcane_stone");
    public static BlockSetType ARCANE_STONE_BRICK = new BlockSetType("arcane_stone");
    public static BlockSetType DUNGEON_BRICK = new BlockSetType("dungeon_brick");
    public static BlockSetType CRACKED_DUNGEON_BRICK = new BlockSetType("cracked_dungeon_brick");
    public static BlockSetType MOSSY_DUNGEON_BRICK = new BlockSetType("mossy_dungeon_brick");

    static {
        // WOODS
        BlockSetType.register(DAIR);
        BlockSetType.register(GREATWOOD);
        BlockSetType.register(EUCALYPTUS);
        BlockSetType.register(HAWTHORN);
        BlockSetType.register(SILVERWOOD);
        BlockSetType.register(WILLOW);
        BlockSetType.register(DEAD);
        BlockSetType.register(TRYPOPHOBIUS);

        // STONES
        BlockSetType.register(ARCANE_STONE);
        BlockSetType.register(ARCANE_STONE_BRICK);
        BlockSetType.register(DUNGEON_BRICK);
        BlockSetType.register(CRACKED_DUNGEON_BRICK);
        BlockSetType.register(MOSSY_DUNGEON_BRICK);
    }
}
