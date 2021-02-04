/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.core.server.bootstrap;

import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.ProvisionException;

import org.dockbox.selene.core.DiscordUtils;
import org.dockbox.selene.core.MinecraftVersion;
import org.dockbox.selene.core.annotations.RequiresBinding;
import org.dockbox.selene.core.annotations.event.Listener;
import org.dockbox.selene.core.annotations.module.ArgumentProvider;
import org.dockbox.selene.core.annotations.module.Module;
import org.dockbox.selene.core.command.CommandBus;
import org.dockbox.selene.core.events.EventBus;
import org.dockbox.selene.core.events.server.ServerEvent;
import org.dockbox.selene.core.events.server.ServerEvent.ServerStartedEvent;
import org.dockbox.selene.core.i18n.common.ResourceService;
import org.dockbox.selene.core.module.ModuleContext;
import org.dockbox.selene.core.module.ModuleManager;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.server.SeleneInformation;
import org.dockbox.selene.core.server.SeleneInjectConfiguration;
import org.dockbox.selene.core.server.ServerType;
import org.dockbox.selene.core.server.config.GlobalConfig;
import org.dockbox.selene.core.util.Reflect;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * The global bootstrapping component which instantiates all configured modules and provides access to server
 * information.
 */
public abstract class SeleneBootstrap extends InjectableBootstrap
{

    private static SeleneBootstrap instance;
    private String version;
    private LocalDateTime lastUpdate;

    /**
     * Instantiates {@link Selene}, creating a local injector based on the provided {@link SeleneInjectConfiguration}.
     * Also verifies dependency artifacts and injector bindings. Proceeds to {@link SeleneBootstrap#construct()} once
     * verified.
     *
     * @param moduleConfiguration
     *         the injector provided by the Selene implementation
     */
    protected SeleneBootstrap(SeleneInjectConfiguration moduleConfiguration)
    {
        super.registerGlobal(moduleConfiguration);
        this.construct();
    }

    /**
     * Loads various properties from selene.properties, including the latest update and version.
     * Once done sets the static instance equal to this instance.
     */
    protected void construct()
    {
        String tVer = "dev";
        LocalDateTime tLU = LocalDateTime.now();

        try
        {
            Properties properties = new Properties();
            properties.load(this.getClass().getResourceAsStream("/selene.properties"));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm")
                    .withLocale(Locale.getDefault())
                    .withZone(ZoneId.systemDefault());

            tLU = LocalDateTime.parse(
                    properties.getOrDefault(
                            "last_update",
                            formatter.format(Instant.now())
                    ).toString(),
                    formatter
            );
            tVer = properties.getOrDefault("version", "dev").toString();
        }
        catch (IOException e)
        {
            Selene.handle("Failed to convert resource file", e);
        }

        this.version = tVer;
        this.lastUpdate = tLU;

        instance = this;
    }

    public static SeleneBootstrap getInstance()
    {
        return instance;
    }

    /**
     * Initiates a {@link Selene} instance. Collecting integrated modules and registering them to the
     * appropriate {@link EventBus}, {@link CommandBus}, and {@link DiscordUtils} instances.
     */
    protected void init()
    {
        Selene.log().info("\u00A7e ,-,");
        Selene.log().info("\u00A7e/.(");
        Selene.log().info("\u00A7e\\ {");
        Selene.log().info("\u00A7e `-`");
        Selene.log().info("     \u00A77Initiating \u00A7bSelene " + this.getVersion());

        // Register additional argument types early on, before modules are constructed
        Reflect.getAnnotatedTypes(SeleneInformation.PACKAGE_PREFIX, ArgumentProvider.class)
                .forEach(Selene::provide);
        // Register pre-loadable types early on, these typically modify initialisation logic
        Reflect.getSubTypes(SeleneInformation.PACKAGE_PREFIX, Preloadable.class)
                .forEach(t -> Selene.provide(t).preload());
        // Ensure all services requiring a platform implementation have one present
        Reflect.getAnnotatedTypes(SeleneInformation.PACKAGE_PREFIX, RequiresBinding.class).forEach(type -> {
            if (Reflect.getSubTypes(SeleneInformation.PACKAGE_PREFIX, type).isEmpty())
            {
                Selene.log().error("No implementation exists for [" + type
                        .getCanonicalName() + "], this will cause functionality to misbehave or not function!");
            }
        });

        EventBus eb = Selene.provide(EventBus.class);
        CommandBus cb = Selene.provide(CommandBus.class);
        DiscordUtils du = Selene.provide(DiscordUtils.class);

        eb.subscribe(this);

        this.initialiseModules(this.getModuleConsumer(cb, eb, du));
        this.initResources();
        cb.apply();

        Selene.provide(EventBus.class).post(new ServerEvent.ServerInitEvent());
    }

    /**
     * Gets the {@link Selene} version, based on the injected value in {@link #construct()}.
     *
     * @return The version
     */
    @NotNull
    public String getVersion()
    {
        return this.version;
    }

    /**
     * Initiates integrated modules and performs a given consumer on each loaded module.
     *
     * @param consumer
     *         The consumer to apply
     */
    private void initialiseModules(Consumer<ModuleContext> consumer)
    {
        Selene.provide(ModuleManager.class).initialiseModules().forEach(consumer);
    }

    private Consumer<ModuleContext> getModuleConsumer(CommandBus cb, EventBus eb, DiscordUtils du)
    {
        return (ModuleContext ctx) -> {
            Class<?> type = ctx.getType();
            Selene.log().info("Found type [" + type.getCanonicalName() + "] in integrated context");
            Exceptional<?> oi = super.getInstanceSafe(type);
            oi.ifPresent(i -> {
                Package pkg = i.getClass().getPackage();
                if (null != pkg)
                {
                    Selene.log().info("Registering [" + type.getCanonicalName() + "] as Event and Command listener");
                    eb.subscribe(i);
                    cb.register(i);
                    du.registerCommandListener(i);
                }
            });
        };
    }

    private void initResources()
    {
        Selene.provide(ResourceService.class).init();
    }

    /**
     * Prints information about registered instances. This includes injection bindings, modules, and event handlers.
     * This method is typically only used when starting the server.
     *
     * @param event
     *         The server event indicating the server started
     */
    @Listener
    protected void debugRegisteredInstances(ServerStartedEvent event)
    {
        Selene.log().info("\u00A77(\u00A7bSelene\u00A77) \u00A7fLoaded bindings: ");
        AtomicInteger unprovisionedTypes = new AtomicInteger();
        super.getAllBindings().forEach((Key<?> key, Binding<?> binding) -> {
            try
            {
                Class<?> keyType = binding.getKey().getTypeLiteral().getRawType();
                Class<?> providerType = binding.getProvider().get().getClass();

                if (!keyType.equals(providerType) && null != providerType)
                    Selene.log().info("  - \u00A77" + keyType.getSimpleName() + ": \u00A78" + providerType.getCanonicalName());
            }
            catch (ProvisionException | AssertionError e)
            {
                unprovisionedTypes.getAndIncrement();
            }
        });
        Selene.log().info("  \u00A77.. and " + unprovisionedTypes.get() + " unprovisioned types.");

        Selene.log().info("\u00A77(\u00A7bSelene\u00A77) \u00A7fLoaded modules: ");
        ModuleManager em = Selene.provide(ModuleManager.class);
        em.getRegisteredModuleIds().forEach(ext -> {
            Exceptional<Module> header = em.getHeader(ext);
            if (header.isPresent())
            {
                Module ex = header.get();
                Selene.log().info("  - \u00A77" + ex.name());
                Selene.log().info("  | - \u00A77ID: \u00A78" + ex.id());
                Selene.log().info("  | - \u00A77Authors: \u00A78" + Arrays.toString(ex.authors()));
                Selene.log().info("  | - \u00A77Dependencies: \u00A78" + Arrays.toString(ex.dependencies()));
            }
            else
            {
                Selene.log().info("  - \u00A77" + ext + " \u00A78(No header)");
            }
        });

        Selene.log().info("\u00A77(\u00A7bSelene\u00A77) \u00A7fLoaded event handlers: ");
        Selene.provide(EventBus.class).getListenersToInvokers().forEach((listener, invokers) -> {
            Class<?> type;
            if (listener instanceof Class) type = (Class<?>) listener;
            else type = listener.getClass();

            Selene.log().info("  - \u00A77" + type.getCanonicalName());
            invokers.forEach(invoker -> Selene.log()
                    .info("  | - \u00A77" + invoker.getEventType().getSimpleName() + ": \u00A78" + invoker.getMethod().getName()));
        });
    }

    /**
     * Gets the server type as indicated by the {@link Selene} implementation.
     *
     * @return the server type
     */
    @NotNull
    public abstract ServerType getServerType();

    /**
     * Gets the last update of {@link Selene}, based on the injected value in {@link #construct()}
     *
     * @return The last update
     */
    @NotNull
    public LocalDateTime getLastUpdate()
    {
        return this.lastUpdate;
    }

    /**
     * Gets the array of authors as defined by {@link SeleneInformation#AUTHORS}.
     *
     * @return A non-null array of authors
     */
    @NotNull
    public String @NotNull [] getAuthors()
    {
        return SeleneInformation.AUTHORS;
    }

    /**
     * Gets the {@link GlobalConfig} instance as injected by the {@link Selene} implementation.
     *
     * @return The global config
     */
    @NotNull
    public GlobalConfig getGlobalConfig()
    {
        return Selene.provide(GlobalConfig.class);
    }

    /**
     * Gets the used version of the implementation platform.
     *
     * @return The platform version
     */
    public abstract String getPlatformVersion();

    /**
     * Gets the used Minecraft version.
     *
     * @return The Minecraft version
     */
    public abstract MinecraftVersion getMinecraftVersion();

}
