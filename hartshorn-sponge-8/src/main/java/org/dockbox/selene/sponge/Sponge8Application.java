package org.dockbox.selene.sponge;

import com.google.inject.Inject;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.HartshornApplication;
import org.dockbox.hartshorn.di.Modifier;
import org.dockbox.hartshorn.di.annotations.Activator;
import org.dockbox.hartshorn.di.annotations.InjectConfig;
import org.dockbox.hartshorn.server.minecraft.MinecraftServerBootstrap;
import org.dockbox.selene.sponge.inject.SpongeInjector;
import org.spongepowered.api.event.lifecycle.StartingEngineEvent;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.jvm.Plugin;

@Plugin(Hartshorn.PROJECT_ID)
@Activator(
        value = MinecraftServerBootstrap.class,
        prefix = Hartshorn.PACKAGE_PREFIX,
        configs = @InjectConfig(SpongeInjector.class)
)
public class Sponge8Application extends HartshornApplication {

    protected static Sponge8Application instance;
    protected Runnable init;

    // Uses Sponge injection
    @Inject
    private PluginContainer container;

    public Sponge8Application() {
        Sponge8Application.instance = this;
        this.init = HartshornApplication.create(Sponge8Application.class, Modifier.ACTIVATE_ALL);
    }

    public static PluginContainer container() {
        return instance.container;
    }

    public void on(StartingEngineEvent<?> event) {
        this.init.run();
    }
}
