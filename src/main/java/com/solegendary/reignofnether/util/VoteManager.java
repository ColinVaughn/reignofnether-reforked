package com.solegendary.reignofnether.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import java.util.HashMap;
import java.util.Map;

public class VoteManager {
    private final Map<String, Integer> votes = new HashMap<>();
    private final SchematicLoader schematicLoader = new SchematicLoader();

    public void registerVote(String schematicName) {
        votes.put(schematicName, votes.getOrDefault(schematicName, 0) + 1);
    }

    public void concludeVote(ServerLevel world) {
        String winner = votes.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        if (winner != null) {
            BlockPos spawnPos = world.getSharedSpawnPos();
            schematicLoader.placeSchematic(world, winner, spawnPos);
        }
    }
}