/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.core.server;

import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.ProvisionException;

import org.dockbox.selene.core.DiscordUtils;
import org.dockbox.selene.core.MinecraftVersion;
import org.dockbox.selene.core.annotations.event.Listener;
import org.dockbox.selene.core.annotations.extension.Extension;
import org.dockbox.selene.core.command.CommandBus;
import org.dockbox.selene.core.events.EventBus;
import org.dockbox.selene.core.events.server.ServerEvent;
import org.dockbox.selene.core.events.server.ServerEvent.ServerStartedEvent;
import org.dockbox.selene.core.extension.ExtensionContext;
import org.dockbox.selene.core.extension.ExtensionManager;
import org.dockbox.selene.core.i18n.common.ResourceService;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.server.config.GlobalConfig;
import org.dockbox.selene.core.util.SeleneUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public abstract class SeleneBootstrap {

    private static SeleneBootstrap instance;
    private static final Logger log = LoggerFactory.getLogger(Selene.class);
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
    protected SeleneBootstrap(SeleneInjectConfiguration moduleConfiguration) {
        SeleneUtils.INJECT.registerGlobal(moduleConfiguration);
        this.construct();
    }

    public static SeleneBootstrap getInstance() {
        return instance;
    }

    /**
     * Loads various properties from selene.properties, including the latest update and version.
     * Once done sets the static instance equal to this instance.
     */
    protected void construct() {
        String tVer = "dev";
        LocalDateTime tLU = LocalDateTime.now();

        try {
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
        } catch (IOException e) {
            Selene.handle("Failed to convert resource file", e);
        }

        this.version = tVer;
        this.lastUpdate = tLU;

        instance = this;
    }

    /**
     * Gets the log instance created by {@link SeleneBootstrap}.
     *
     * @return The {@link Logger}
     */
    protected static Logger log() {
        return log;
    }

    /**
     * Initiates integrated extensions and performs a given consumer on each loaded extension.
     *
     * @param consumer
     *         The consumer to apply
     */
    private void initIntegratedExtensions(Consumer<ExtensionContext> consumer) {
        SeleneUtils.INJECT.getInstance(ExtensionManager.class).initialiseExtensions().forEach(consumer);
    }

    /**
     * Initiates a {@link Selene} instance. Collecting integrated extensions and registering them to the
     * appropriate {@link EventBus}, {@link CommandBus}, and {@link DiscordUtils} instances.
     */
    protected void init() {
        log().info("\u00A7e ,-,");
        log().info("\u00A7e/.(");
        log().info("\u00A7e\\ {");
        log().info("\u00A7e `-`");
        log().info("     \u00A77Initiating \u00A7bSelene " + this.getVersion());

        EventBus eb = SeleneUtils.INJECT.getInstance(EventBus.class);
        CommandBus cb = SeleneUtils.INJECT.getInstance(CommandBus.class);
        DiscordUtils du = SeleneUtils.INJECT.getInstance(DiscordUtils.class);

        eb.subscribe(this);

        this.initIntegratedExtensions(this.getExtensionContextConsumer(cb, eb, du));
        this.initResources();
        cb.apply();

        SeleneUtils.INJECT.getInstance(EventBus.class).post(new ServerEvent.ServerInitEvent());
    }

    private void initResources() {
        SeleneUtils.INJECT.getInstance(ResourceService.class).init();
    }

    /**
     * Prints information about registered instances. This includes injection bindings, extensions, and event handlers.
     * This method is typically only used when starting the server.
     *
     * @param event
     *         The server event indicating the server started
     */
    @Listener
    protected void debugRegisteredInstances(ServerStartedEvent event) {
        log().info("\u00A77(\u00A7bSelene\u00A77) \u00A7fLoaded bindings: ");
        AtomicInteger unprovisionedTypes = new AtomicInteger();
        SeleneUtils.INJECT.getAllBindings().forEach((Key<?> key, Binding<?> binding) -> {
            try {
                Class<?> keyType = binding.getKey().getTypeLiteral().getRawType();
                Class<?> providerType = binding.getProvider().get().getClass();

                if (!keyType.equals(providerType) && null != providerType)
                    log().info("  - \u00A77" + keyType.getSimpleName() + ": \u00A78" + providerType.getCanonicalName());
            } catch (ProvisionException | AssertionError e) {
                unprovisionedTypes.getAndIncrement();
            }
        });
        log().info("  \u00A77.. and " + unprovisionedTypes.get() + " unprovisioned types.");

        log().info("\u00A77(\u00A7bSelene\u00A77) \u00A7fLoaded extensions: ");
        ExtensionManager em = SeleneUtils.INJECT.getInstance(ExtensionManager.class);
        em.getRegisteredExtensionIds().forEach(ext -> {
            Exceptional<Extension> header = em.getHeader(ext);
            if (header.isPresent()) {
                Extension ex = header.get();
                log().info("  - \u00A77" + ex.name());
                log().info("  | - \u00A77ID: \u00A78" + ex.id());
                log().info("  | - \u00A77Authors: \u00A78" + Arrays.toString(ex.authors()));
                log().info("  | - \u00A77Dependencies: \u00A78" + Arrays.toString(ex.dependencies()));
            } else {
                log().info("  - \u00A77" + ext + " \u00A78(No header)");
            }
        });

        log().info("\u00A77(\u00A7bSelene\u00A77) \u00A7fLoaded event handlers: ");
        SeleneUtils.INJECT.getInstance(EventBus.class).getListenersToInvokers().forEach((listener, invokers) -> {
            Class<?> type;
            if (listener instanceof Class) type = (Class<?>) listener;
            else type = listener.getClass();

            log().info("  - \u00A77" + type.getCanonicalName());
            invokers.forEach(invoker -> log().info("  | - \u00A77" + invoker.getEventType().getSimpleName() + ": \u00A78" + invoker.getMethod().getName()));
        });
    }

    private Consumer<ExtensionContext> getExtensionContextConsumer(CommandBus cb, EventBus eb, DiscordUtils du) {
        return (ExtensionContext ctx) -> {
            Class<?> type = ctx.getExtensionClass();
            log().info("Found type [" + type.getCanonicalName() + "] in integrated context");
            Exceptional<?> oi = SeleneUtils.INJECT.getInstanceSafe(type);
            oi.ifPresent(i -> {
                Package pkg = i.getClass().getPackage();
                if (null != pkg) {
                    log().info("Registering [" + type.getCanonicalName() + "] as Event and Command listener");
                    eb.subscribe(i);
                    cb.register(i);
                    du.registerCommandListener(i);
                }
            });
        };
    }

    /**
     * Gets the {@link Selene} version, based on the injected value in {@link #construct()}.
     *
     * @return The version
     */
    @NotNull
    public String getVersion() {
        return this.version;
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
    public LocalDateTime getLastUpdate() {
        return this.lastUpdate;
    }

    /**
     * Gets the array of authors as defined by {@link SeleneInformation#AUTHORS}.
     *
     * @return A non-null array of authors
     */
    @NotNull
    public String @NotNull [] getAuthors() {
        return SeleneInformation.AUTHORS;
    }

    /**
     * Gets the {@link GlobalConfig} instance as injected by the {@link Selene} implementation.
     *
     * @return The global config
     */
    @NotNull
    public GlobalConfig getGlobalConfig() {
        return SeleneUtils.INJECT.getInstance(GlobalConfig.class);
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
