package com.solegendary.reignofnether.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.solegendary.reignofnether.util.SchematicMetadata;
import com.solegendary.reignofnether.util.SchematicUtils;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class VoteScreen extends Screen {
    private final Consumer<String> onVote;
    private final List<String> schematicFiles;
    private static final String SCHEMATICS_FOLDER = "assets/reignofnether/schematics/";
    private static final Logger LOGGER = LogManager.getLogger();

    public VoteScreen(Consumer<String> onVote) {
        super(Component.literal("Vote for a Schematic Build"));
        this.onVote = onVote;
        LOGGER.info("Initializing VoteScreen...");
        this.schematicFiles = loadSchematicFiles();
        LOGGER.debug("Loaded {} schematic files: {}", schematicFiles.size(), schematicFiles);
    }

    @Override
    protected void init() {
        LOGGER.info("VoteScreen init() called. Found {} schematics.", schematicFiles.size());
        int y = this.height / 4;

        for (String fileName : schematicFiles) {
            LOGGER.info("Processing schematic: {}", fileName);
            Optional<SchematicMetadata> metadataOpt = SchematicUtils.loadMetadata(fileName);

            if (metadataOpt.isPresent()) {
                LOGGER.debug("Schematic metadata: {}", metadataOpt.get());
            } else {
                LOGGER.warn("No metadata found for schematic: {}", fileName);
            }

            String displayName = metadataOpt.map(SchematicMetadata::getName).orElse(fileName);
            String playerCount = metadataOpt.map(meta -> "Players: " + meta.getPlayers()).orElse("Players: N/A");
            String description = metadataOpt.map(SchematicMetadata::getDescription).orElse("No description available.");

            ResourceLocation previewImage = new ResourceLocation("reignofnether", "schematics/" + fileName.replace(".schem", ".png"));
            LOGGER.info("Rendering preview for image: {}", previewImage);

            renderSchematicPreview(previewImage, this.width / 2 - 110, y, 100, 100);

            PoseStack poseStack = new PoseStack();
            this.font.draw(poseStack, displayName, this.width / 2 - 100, y + 110, 0xFFFFFF);
            this.font.draw(poseStack, playerCount, this.width / 2 - 100, y + 130, 0xAAAAAA);
            this.font.draw(poseStack, description, this.width / 2 - 100, y + 150, 0xAAAAAA);

            this.addRenderableWidget(new Button(this.width / 2 + 50, y + 80, 100, 20,
                    Component.literal("Vote"),
                    button -> {
                        LOGGER.info("Vote button clicked for: {}", fileName);
                        onVote.accept(fileName);
                    }));

            y += 180;
        }
    }

    private void renderSchematicPreview(ResourceLocation image, int x, int y, int width, int height) {
        try {
            LOGGER.debug("Attempting to render image: {}", image);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, image);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            blit(new PoseStack(), x, y, 0, 0, width, height, width, height);
            LOGGER.info("Successfully rendered image: {}", image);
        } catch (Exception e) {
            LOGGER.error("Failed to render image: {}", image, e);
        } finally {
            RenderSystem.disableBlend();
        }
    }

    private List<String> loadSchematicFiles() {
        List<String> files = new ArrayList<>();
        File directory = new File(SCHEMATICS_FOLDER);
        LOGGER.info("Loading schematics from folder: {}", SCHEMATICS_FOLDER);

        if (directory.exists() && directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                if (file.getName().endsWith(".schem")) {
                    files.add(file.getName());
                    LOGGER.info("Found schematic file: {}", file.getName());
                } else {
                    LOGGER.warn("Skipping non-schematic file: {}", file.getName());
                }
            }
        } else {
            LOGGER.error("Schematic folder not found or is not a directory: {}", SCHEMATICS_FOLDER);
        }
        return files;
    }
}
