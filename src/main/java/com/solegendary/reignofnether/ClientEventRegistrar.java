package com.solegendary.reignofnether;

import com.solegendary.reignofnether.screens.VoteScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;

// src/main/java/com/example/reignofnether/ClientEventRegistrar.java
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;

@EventBusSubscriber(modid = ReignOfNether.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventRegistrar {

    private static final KeyMapping VOTE_KEY = new KeyMapping(
            "key.reignofnether.vote", // Key description
            GLFW.GLFW_KEY_COMMA,      // Default key: Comma
            "key.categories.misc"     // Category for the keybinding
    );

    public void registerClientEvents() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::onClientSetup);
        modEventBus.addListener(this::registerKeyMappings);

        // Register with the Forge event bus
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        // Any client setup logic (if needed)
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (Minecraft.getInstance().screen == null && VOTE_KEY.isDown()) {
            Minecraft mc = Minecraft.getInstance();
            LocalPlayer player = mc.player;

            if (player != null) {
                System.out.println("Opening VoteScreen...");
                mc.setScreen(new VoteScreen(build ->
                        player.sendSystemMessage(Component.literal("/vote " + build))
                ));
            } else {
                System.out.println("Player is null!");
            }
        }
    }

    @SubscribeEvent
    public void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(VOTE_KEY);
    }
}
