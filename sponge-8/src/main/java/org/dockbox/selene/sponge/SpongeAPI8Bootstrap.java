package org.dockbox.selene.sponge;

import org.dockbox.selene.core.MinecraftVersion;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.server.SeleneInjectConfiguration;
import org.dockbox.selene.core.server.ServerType;
import org.dockbox.selene.core.server.bootstrap.SeleneBootstrap;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Platform.Component;
import org.spongepowered.api.Sponge;

public class SpongeAPI8Bootstrap extends SeleneBootstrap
{

    /**
     * Instantiates {@link Selene}, creating a local injector based on the provided {@link SeleneInjectConfiguration}.
     * Also verifies dependency artifacts and injector bindings. Proceeds to {@link SeleneBootstrap#construct()} once
     * verified.
     *
     * @param moduleConfiguration
     *         the injector provided by the Selene implementation
     */
    protected SpongeAPI8Bootstrap(SeleneInjectConfiguration moduleConfiguration)
    {
        super(moduleConfiguration);
    }

    @Override
    public @NotNull ServerType getServerType()
    {
        return ServerType.SPONGE_API_8;
    }

    @Override
    public String getPlatformVersion()
    {
        return Sponge.getPlatform().getContainer(Component.IMPLEMENTATION).getMetadata().getVersion();
    }

    @Override
    public MinecraftVersion getMinecraftVersion()
    {
        return MinecraftVersion.MC1_16;
    }
}
