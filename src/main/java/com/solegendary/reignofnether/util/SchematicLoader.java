package com.solegendary.reignofnether.util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.io.File;
import java.io.IOException;

public class SchematicLoader {

    public void placeSchematic(ServerLevel world, String schematicName, BlockPos startPos) {
        try {
            File schematicFile = new File("assets/reignofnether/schematics/" + schematicName);
            CompoundTag nbtData = NbtIo.read(schematicFile);

            if (nbtData != null) {
                int width = nbtData.getInt("Width");
                int height = nbtData.getInt("Height");
                int length = nbtData.getInt("Length");
                byte[] blockIds = nbtData.getByteArray("BlockData");

                int index = 0;
                for (int y = 0; y < height; y++) {
                    for (int z = 0; z < length; z++) {
                        for (int x = 0; x < width; x++) {
                            int blockId = blockIds[index++] & 255;
                            Block block = Block.stateById(blockId).getBlock();

                            if (block != null) {
                                BlockPos blockPos = startPos.offset(x, y, z);
                                BlockState blockState = block.defaultBlockState();
                                world.setBlock(blockPos, blockState, 3);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load schematic: " + schematicName);
            e.printStackTrace();
        }
    }
}

