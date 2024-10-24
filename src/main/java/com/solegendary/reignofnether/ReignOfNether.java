package com.solegendary.reignofnether;

import com.solegendary.reignofnether.registrars.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("reignofnether")
public class ReignOfNether {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "reignofnether";

    public ReignOfNether() {
        ItemRegistrar.init();
        EntityRegistrar.init();
        ContainerRegistrar.init();
        SoundRegistrar.init();
        BlockRegistrar.init();

        final ClientEventRegistrar clientRegistrar = new ClientEventRegistrar();
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> clientRegistrar::registerClientEvents);

        final ServerEventRegistrar serverRegistrar = new ServerEventRegistrar();
        DistExecutor.safeRunWhenOn(Dist.DEDICATED_SERVER, () -> serverRegistrar::registerServerEvents);
    }
}

