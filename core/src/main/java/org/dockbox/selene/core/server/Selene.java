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

package org.dockbox.selene.core.server;

import com.google.inject.AbstractModule;
import com.google.inject.Binding;
import com.google.inject.ConfigurationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;

import org.dockbox.selene.core.command.CommandBus;
import org.dockbox.selene.core.events.server.ServerEvent;
import org.dockbox.selene.core.server.config.ExceptionLevels;
import org.dockbox.selene.core.server.config.GlobalConfig;
import org.dockbox.selene.core.util.discord.DiscordUtils;
import org.dockbox.selene.core.util.events.EventBus;
import org.dockbox.selene.core.util.exceptions.ExceptionHelper;
import org.dockbox.selene.core.util.extension.Extension;
import org.dockbox.selene.core.util.extension.ExtensionContext;
import org.dockbox.selene.core.util.extension.ExtensionManager;
import org.dockbox.selene.core.util.inject.AbstractCommonInjector;
import org.dockbox.selene.core.util.inject.AbstractExceptionInjector;
import org.dockbox.selene.core.util.inject.AbstractModuleInjector;
import org.dockbox.selene.core.util.inject.AbstractUtilInjector;
import org.dockbox.selene.core.util.library.LibraryArtifact;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Consumer;

@SuppressWarnings("ClassWithTooManyMethods")
public abstract class Selene {

    public enum ServerType {
        SPONGE(true, "1.12.2-2555-7.1.0-BETA-2815", "1.12.2-2838-7.2.2-RC0"),
        MAGMA(true, "Not (yet) supported", "Not (yet) supported"),
        SPIGOT(true, "Not (yet) supported", "Not (yet) supported"),
        PAPER(true, "Not (yet) supported", "Not (yet) supported"),
        OTHER(true, "Not (yet) supported", "Not (yet) supported");

        private final boolean hasNMSAccess;
        private final String minimumVersion;
        private final String preferredVersion;

        ServerType(boolean hasNMSAccess, String minimumVersion, String preferredVersion) {
            this.hasNMSAccess = hasNMSAccess;
            this.minimumVersion = minimumVersion;
            this.preferredVersion = preferredVersion;
        }

        public boolean isHasNMSAccess() {
            return this.hasNMSAccess;
        }

        public String getMinimumVersion() {
            return this.minimumVersion;
        }

        public String getPreferredVersion() {
            return this.preferredVersion;
        }
    }

    private final Logger log = LoggerFactory.getLogger(Selene.class);
    private String version;
    private LocalDate lastUpdate;
    protected static final String[] authors = {"GuusLieben"};

    private static Selene instance;

    private Injector injector;

    protected Selene(AbstractCommonInjector injector) {
        this.injector = Guice.createInjector(injector);
        this.construct();
    }

    protected Selene(
            AbstractModuleInjector moduleInjector,
            AbstractExceptionInjector exceptionInjector,
            AbstractUtilInjector utilInjector
    ) {
        this.verifyArtifacts();

        this.injector = Guice.createInjector();
        if (null != moduleInjector) this.injector = this.injector.createChildInjector(moduleInjector);
        if (null != exceptionInjector) this.injector = this.injector.createChildInjector(exceptionInjector);
        if (null != utilInjector) this.injector = this.injector.createChildInjector(utilInjector);

        this.verifyInjectorBindings();
        this.construct();
    }

    private void verifyArtifacts() {
        // TODO: Maven repository verification
//        for (LibraryArtifact artifact : this.getAllArtifacts()) { }
    }

    private void verifyInjectorBindings() {
        for (Class<?> bindingType : AbstractCommonInjector.Companion.getRequiredBindings()) {
            try {
                this.injector.getBinding(bindingType);
            } catch (ConfigurationException e) {
                log().error("Missing binding for " + bindingType.getCanonicalName() + "! While it is possible to inject it later, it is recommended to do so through the default platform injector!");
            }
        }
    }

    protected void construct() {
        String tVer = "dev";
        LocalDate tLU = LocalDate.now();

        try {
            Properties properties = new Properties();
            properties.load(this.getClass().getResourceAsStream("/selene.properties"));

            // LocalDate can be parsed directly, as it is generated using LocalDate when building with Gradle
            tLU = LocalDate.parse(properties.getOrDefault("last_update", Instant.now().toString()).toString());
            tVer = properties.getOrDefault("version", "dev").toString();
        } catch (IOException e) {
            this.except("Failed to convert resource file", e);
        }

        this.version = tVer;
        this.lastUpdate = tLU;

        Selene.instance = this;
    }

    public static <T> T getInstance(Class<T> type) {
        if (type.isAnnotationPresent(Extension.class)) {
            return getInstance(ExtensionManager.class).getInstance(type).orElse(null);
        }
        return instance.injector.getInstance(type);
    }

    public static <T> void bindUtility(Class<T> contract, Class<? extends T> implementation) {
        AbstractModule localModule = new AbstractModule() {
            @Override
            protected void configure() {
                super.configure();
                this.bind(contract).to(implementation);
            }
        };
        instance.injector = instance.injector.createChildInjector(localModule);
    }

    public Injector getInjector() {
        return this.injector;
    }

    protected void initIntegratedExtensions(Consumer<ExtensionContext> consumer) {
        getInstance(ExtensionManager.class).collectIntegratedExtensions().forEach(consumer);
    }

    protected void initExternalExtensions(Consumer<ExtensionContext> consumer) {
        getInstance(ExtensionManager.class).getExternalExtensions().forEach(consumer);
    }

    protected void init() {
        EventBus eb = getInstance(EventBus.class);
        CommandBus cb = getInstance(CommandBus.class);
        ExtensionManager cm = getInstance(ExtensionManager.class);
        DiscordUtils du = getInstance(DiscordUtils.class);

        this.initIntegratedExtensions(this.getConsumer("integrated", cb, eb, cm, du));
        this.initExternalExtensions(this.getConsumer("external", cb, eb, cm, du));

        getInstance(EventBus.class).post(new ServerEvent.Init());
    }

    protected void debugRegisteredInstances() {
        log().info("\u00A77(\u00A7bSelene\u00A77) \u00A7fLoaded bindings: ");
        this.getInjector().getAllBindings().forEach((Key<?> key, Binding<?> binding) -> {
            Class<?> keyType = binding.getKey().getTypeLiteral().getRawType();
            Class<?> providerType = binding.getProvider().get().getClass();

            if (!keyType.equals(providerType))
                log().info("  - \u00A77" + keyType.getSimpleName() + ": \u00A78" + providerType.getCanonicalName());
        });

        log().info("\u00A77(\u00A7bSelene\u00A77) \u00A7fLoaded extensions: ");
        ExtensionManager em = getInstance(ExtensionManager.class);
        em.getRegisteredExtensionIds().forEach(ext -> {
            Optional<Extension> header = em.getHeader(ext);
            if (header.isPresent()) {
                Extension ex = header.get();
                log().info("  - \u00A77" + ex.name());
                log().info("  | - \u00A77ID: \u00A78" + ex.id());
                log().info("  | - \u00A77Authors: \u00A78" + Arrays.toString(ex.authors()));
                log().info("  | - \u00A77Version: \u00A78" + ex.version());
                log().info("  | - \u00A77URL: \u00A78" + ex.url());
                log().info("  | - \u00A77Requires NMS: \u00A78" + ex.requiresNMS());
                log().info("  | - \u00A77Dependencies: \u00A78" + Arrays.toString(ex.dependencies()));
            } else {
                log().info("  - \u00A77" + ext + " \u00A78(No header)");
            }
        });

        log().info("\u00A77(\u00A7bSelene\u00A77) \u00A7fLoaded event handlers: ");
        getInstance(EventBus.class).getListenerToInvokers().forEach((listener, invokers) -> {
            Class<?> type;
            if (listener instanceof Class) type = (Class<?>) listener;
            else type = listener.getClass();

            log().info("  - \u00A77" + type.getCanonicalName());
            invokers.forEach(invoker -> log().info("  | - \u00A77" + invoker.getEventType().getSimpleName() + ": \u00A78" + invoker.getMethod().getName()));
        });
    }

    private Consumer<ExtensionContext> getConsumer(String contextType, CommandBus cb, EventBus eb, ExtensionManager em, DiscordUtils du) {
        return (ExtensionContext ctx) -> ctx.getClasses().values().forEach(type -> {
            log().info("Found type [" + type.getCanonicalName() + "] in " + contextType + " context");
            Optional<?> oi = em.getInstance(type);
            oi.ifPresent(i -> {
                Package pkg = i.getClass().getPackage();
                if (null != pkg) {
                    log().info("Registering [" + type.getCanonicalName() + "] as Event and Command listener");
                    eb.subscribe(i);
                    cb.register(i);
                    du.registerCommandListener(i);

                    Reflections ref = new Reflections(pkg.getName());
                    ref.getTypesAnnotatedWith(org.dockbox.selene.core.annotations.Listener.class).stream()
                            .filter(it -> !it.isAnnotationPresent(Extension.class))
                            .forEach(eb::subscribe);
                }
            });
        });
    }

    @NotNull
    public Logger getLog() {
        return this.log;
    }

    @NotNull
    public String getVersion() {
        return this.version;
    }

    @NotNull
    public abstract ServerType getServerType();

    @NotNull
    public LocalDate getLastUpdate() {
        return this.lastUpdate;
    }

    @NotNull
    public String @NotNull [] getAuthors() {
        return authors;
    }

    public void except(@Nullable String msg, @Nullable Throwable... e) {
        for (Throwable throwable : e) {
            boolean stacktraces = this.getGlobalConfig().getStacktracesAllowed();
            ExceptionHelper eh = getInstance(ExceptionHelper.class);

            if (ExceptionLevels.FRIENDLY == this.getGlobalConfig().getExceptionLevel()) {
                eh.printFriendly(msg, throwable, stacktraces);
            } else {
                eh.printMinimal(msg, throwable, stacktraces);
            }
        }
    }

    @NotNull
    public GlobalConfig getGlobalConfig() {
        return getInstance(GlobalConfig.class);
    }

    public static Logger log() {
        return getServer().getLog();
    }

    public static Selene getServer() {
        return instance;
    }

    private LibraryArtifact[] getAllArtifacts() {
        List<LibraryArtifact> artifacts = new ArrayList<>(Arrays.asList(this.getPlatformArtifacts()));
        artifacts.add(new LibraryArtifact("org.reflections", "reflections", "0.9.11"));
        artifacts.add(new LibraryArtifact("com.fasterxml.jackson.core", "jackson-databind", "2.9.8"));
        artifacts.add(new LibraryArtifact("com.fasterxml.jackson.dataformat", "jackson-dataformat-yaml", "2.9.8"));
        artifacts.add(new LibraryArtifact("com.fasterxml.jackson.datatype", "jackson-datatype-jsr310", "2.9.8"));
        artifacts.add(new LibraryArtifact("org.apache.commons", "commons-collections4", "4.1"));
        return artifacts.toArray(new LibraryArtifact[0]);
    }

    protected abstract LibraryArtifact[] getPlatformArtifacts();

}
