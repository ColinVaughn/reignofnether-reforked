package com.solegendary.reignofnether.util;

import com.google.gson.Gson;

import java.util.Optional;

import com.solegendary.reignofnether.ReignOfNether;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.apache.logging.log4j.Logger;

import com.mojang.logging.LogUtils;

import java.io.InputStream;
import java.io.InputStreamReader;

public class SchematicUtils {
    private static final Gson GSON = new Gson();
    private static final Logger LOGGER = (Logger) LogUtils.getLogger();

    public static Optional<SchematicMetadata> loadMetadata(String schematicName) {
        String jsonFileName = schematicName.replace(".schem", ".json");
        ResourceLocation resourceLocation = new ResourceLocation(ReignOfNether.MOD_ID, "schematics/" + jsonFileName);

        LOGGER.info("Loading metadata from: {}", resourceLocation);

        try {
            // Retrieve the resource from the resource manager
            Optional<Resource> resourceOpt = Minecraft.getInstance().getResourceManager().getResource(resourceLocation);
            if (resourceOpt.isPresent()) {
                try (InputStream inputStream = resourceOpt.get().open();
                     InputStreamReader reader = new InputStreamReader(inputStream)) {

                    // Deserialize the JSON into SchematicMetadata
                    SchematicMetadata metadata = GSON.fromJson(reader, SchematicMetadata.class);
                    LOGGER.info("Successfully loaded metadata for: {}", schematicName);
                    return Optional.of(metadata);
                }
            } else {
                LOGGER.warn("Resource not found: {}", resourceLocation);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load metadata for: {}", schematicName, e);
        }

        // Return empty if the resource is not found or an error occurs
        return Optional.empty();
    }
}

