package com.solegendary.reignofnether.block;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class CrackedStoneBrickStairs extends StairBlock {

    public CrackedStoneBrickStairs() {
        super(() -> Blocks.STONE.defaultBlockState(), BlockBehaviour.Properties.of(Material.STONE, MaterialColor.STONE)
                .strength(1.5F, 6.0F)
                .sound(SoundType.STONE));
    }
}
