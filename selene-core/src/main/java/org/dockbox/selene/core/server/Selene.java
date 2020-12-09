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
import com.google.inject.ProvisionException;

import org.dockbox.selene.core.DiscordUtils;
import org.dockbox.selene.core.ExceptionHelper;
import org.dockbox.selene.core.MinecraftVersion;
import org.dockbox.selene.core.SeleneUtils;
import org.dockbox.selene.core.annotations.event.Listener;
import org.dockbox.selene.core.annotations.extension.Extension;
import org.dockbox.selene.core.command.CommandBus;
import org.dockbox.selene.core.events.EventBus;
import org.dockbox.selene.core.events.server.ServerEvent;
import org.dockbox.selene.core.events.server.ServerEvent.ServerStartedEvent;
import org.dockbox.selene.core.extension.ExtensionContext;
import org.dockbox.selene.core.extension.ExtensionManager;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.server.config.ExceptionLevels;
import org.dockbox.selene.core.server.config.GlobalConfig;
import org.dockbox.selene.core.server.properties.InjectableType;
import org.dockbox.selene.core.server.properties.InjectorProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 The global {@link Selene} instance used to grant access to various components.
 */
@SuppressWarnings({"ClassWithTooManyMethods", "ConstantDeclaredInAbstractClass"})
public abstract class Selene {

    public static final String GLOBAL_BYPASS = "selene.admin.bypass-all";

    private static final Logger log = LoggerFactory.getLogger(Selene.class);
    private String version;
    private LocalDateTime lastUpdate;
    /**
     Constant value holding the GitHub username(s) of the author(s) of {@link Selene}. This does not include names of
     extension developers.
     */
    protected static final String[] authors = {"GuusLieben"};

    private static Selene instance;

    private final transient List<AbstractModule> injectorModules = SeleneUtils.emptyConcurrentList();

    /**
     Instantiates {@link Selene}, creating a local injector based on the provided {@link SeleneInjectConfiguration}.
     Also verifies dependency artifacts and injector bindings. Proceeds to {@link Selene#construct()} once verified.

     @param moduleConfiguration
     the injector provided by the Selene implementation
     */
    protected Selene(SeleneInjectConfiguration moduleConfiguration) {
        this.injectorModules.add(moduleConfiguration);
        this.construct();
    }

    private void verifyInjectorBindings() {
        for (Class<?> bindingType : SeleneInjectConfiguration.REQUIRED_BINDINGS) {
            try {
                this.createInjector().getBinding(bindingType);
            } catch (ConfigurationException e) {
                log().error("Missing binding for " + bindingType.getCanonicalName() + "! While it is possible to inject it later, it is recommended to do so through the default platform injector!");
            }
        }
    }

    private Injector createInjector(AbstractModule... additionalModules) {
        Collection<AbstractModule> modules = new ArrayList<>(this.injectorModules);
        modules.addAll(Arrays.stream(additionalModules)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        return Guice.createInjector(modules);
    }

    /**
     Loads various properties from selene.properties, including the latest update and version.
     Once done sets the static instance equal to this instance.
     */
    protected void construct() {
        this.verifyInjectorBindings();

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
            this.except("Failed to convert resource file", e);
        }

        this.version = tVer;
        this.lastUpdate = tLU;

        Selene.instance = this;
    }

    public static <T> Exceptional<T> getInstanceSafe(Class<T> type, InjectorProperty<?>... additionalProperties) {
        return Exceptional.ofNullable(getInstance(type, additionalProperties));
    }

    public static <T> Exceptional<T> getInstanceSafe(Class<T> type, Object extension, InjectorProperty<?>... additionalProperties) {
        return Exceptional.ofNullable(getInstance(type, extension, additionalProperties));
    }

    public static <T> Exceptional<T> getInstanceSafe(Class<T> type, Class<?> extension, InjectorProperty<?>... additionalProperties) {
        return Exceptional.ofNullable(getInstance(type, extension, additionalProperties));
    }

    public static <T> T getInstance(Class<T> type, InjectorProperty<?>... additionalProperties) {
        return getInstance(type, type, additionalProperties);
    }

    public static <T> T getInstance(Class<T> type, Object extension, InjectorProperty<?>... additionalProperties) {
        if (null != extension) {
            return getInstance(type, extension.getClass(), additionalProperties);
        } else {
            return getInstance(type, additionalProperties);
        }
    }

    /**
     Gets an instance of a provided {@link Class} type. If the type is annotated with {@link Extension} it is ran
     through the {@link ExtensionManager} instance to obtain the instance. If it is not annotated as such, it is ran
     through the instance {@link Injector} to obtain the instance based on implementation, or manually, provided
     mappings.

     @param <T>
     The type parameter for the instance to return
     @param type
     The type of the instance
     @param extension
     The type of the extension if extension specific bindings are to be used
     @param additionalProperties
     The properties to be passed into the type either during or after construction

     @return The instance, if present. Otherwise returns null
     */
    public static <T> T getInstance(Class<T> type, Class<?> extension, InjectorProperty<?>... additionalProperties) {
        T typeInstance = null;

        // Extensions are initially created using #getInstance here (assuming SimpleExtensionManager or a derivative is
        // bound to ExtensionManager). Therefore it may not yet be present in the ExtensionManager's registry. In which
        // case `null` is (re-)assigned to typeInstance so that it can be created through the generated
        // extension-specific injector.
        if (type.isAnnotationPresent(Extension.class)) {
            typeInstance = getInstanceSafe(ExtensionManager.class)
                    .map(extensionManager -> extensionManager.getInstance(type).orNull())
                    .orNull();

        }

        // Prepare modules
        ExtensionModule extensionModule = null;
        if (extension.isAnnotationPresent(Extension.class)) {
            extensionModule = getServer().getExtensionModule(extension, extension.getAnnotation(Extension.class), null);
        }

        AbstractModule propertyModule = new AbstractModule() {
            @Override
            protected void configure() {
                this.bind(InjectorProperty[].class).toInstance(additionalProperties);
            }
        };

        Injector injector = getServer().createInjector(extensionModule, propertyModule);
        // Type instance can be present if it is a extension. These instances are also created using Guice injectors
        // and therefore do not need late member injection here.
        if (null == typeInstance) {
            try {
                typeInstance = injector.getInstance(type);
            } catch (ProvisionException e) {
                log().error("Could not create instance using registered injector " + injector + " for [" + type + "]", e);
            } catch (ConfigurationException ignored) {
                log().warn("Configuration error while attempting to create instance for [" + type + "] : " + injector);
            }
        }

        // Inject properties if applicable
        if (typeInstance instanceof InjectableType && ((InjectableType) typeInstance).canEnable()) {
            ((InjectableType) typeInstance).stateEnabling(additionalProperties);
        }

        // May be null, but we have used all possible injectors, it's up to the developer now
        return typeInstance;
    }

    public Injector createExtensionInjector(Object instance) {
        if (null != instance && instance.getClass().isAnnotationPresent(Extension.class)) {
            Exceptional<ExtensionContext> context = getInstance(ExtensionManager.class).getContext(instance.getClass());
            Extension extension;
            extension = context
                    .map(ExtensionContext::getExtension)
                    .orElseGet(() -> instance.getClass().getAnnotation(Extension.class));
            return this.createExtensionInjector(instance, extension, context.orNull());
        }
        return this.createInjector();
    }

    @SuppressWarnings("unchecked")
    private <T> ExtensionModule getExtensionModule(T instance, Extension header, ExtensionContext context) {
        ExtensionModule module = new ExtensionModule();

        if (!(null == instance || instance instanceof Class<?>))
            module.acceptBinding((Class<T>) instance.getClass(), instance);
        if (null != header)
            module.acceptBinding(Extension.class, header);
        if (null != context)
            module.acceptBinding(ExtensionContext.class, context);

        return module;
    }

    public Injector createExtensionInjector(Object instance, Extension header, ExtensionContext context) {
        return this.createInjector(this.getExtensionModule(instance, header, context));
    }

    /**
     Creates a custom binding for a given contract and implementation using a custom {@link AbstractModule}. Requires
     the implementation to extend the contract type.
     <p>
     The binding is created by Guice, and can be annotated using Guice supported annotations (e.g.
     {@link com.google.inject.Singleton})

     @param <T>
     The type parameter of the contract
     @param contract
     The class type of the contract
     @param implementation
     The class type of the implementation
     */
    public static <T> void bindUtility(Class<T> contract, Class<? extends T> implementation) {
        AbstractModule localModule = new AbstractModule() {
            @Override
            protected void configure() {
                super.configure();
                this.bind(contract).to(implementation);
            }
        };
        getServer().injectorModules.add(localModule);
    }

    public <T> T injectMembers(T type) {
        if (null != type) {
            createInjector().injectMembers(type);
        }

        return type;
    }

    public <T> T injectMembers(T type, Object extensionInstance) {
        if (null != type) {
            if (null != extensionInstance && extensionInstance.getClass().isAnnotationPresent(Extension.class)) {
                this.createExtensionInjector(extensionInstance).injectMembers(type);
            } else {
                this.createInjector().injectMembers(type);
            }
        }
        return type;
    }

    private Map<Key<?>, Binding<?>> getAllBindings() {
        Map<Key<?>, Binding<?>> bindings = SeleneUtils.emptyConcurrentMap();
        this.createInjector().getAllBindings().forEach((Key<?> key, Binding<?> binding) -> {
            try {
                Class<?> keyType = binding.getKey().getTypeLiteral().getRawType();
                Class<?> providerType = binding.getProvider().get().getClass();

                if (!keyType.equals(providerType) && null != providerType)
                    bindings.put(key, binding);
            } catch (ProvisionException | AssertionError ignored) {
            }
        });
        return bindings;
    }

    /**
     Initiates integrated extensions and performs a given consumer on each loaded extension.

     @param consumer
     The consumer to apply
     */
    private void initIntegratedExtensions(Consumer<ExtensionContext> consumer) {
        getInstance(ExtensionManager.class).initialiseExtensions().forEach(consumer);
    }

    /**
     Initiates a {@link Selene} instance. Collecting integrated extensions and registering them to the
     appropriate {@link EventBus}, {@link CommandBus}, and {@link DiscordUtils} instances.
     */
    protected void init() {
        log().info("\u00A7e ,-,");
        log().info("\u00A7e/.(");
        log().info("\u00A7e\\ {");
        log().info("\u00A7e `-`");
        log().info("     \u00A77Initiating \u00A7bSelene " + this.getVersion());

        EventBus eb = getInstance(EventBus.class);
        CommandBus cb = getInstance(CommandBus.class);
        ExtensionManager cm = getInstance(ExtensionManager.class);
        DiscordUtils du = getInstance(DiscordUtils.class);

        eb.subscribe(this);

        this.initIntegratedExtensions(this.getExtensionContextConsumer(cb, eb, cm, du));

        cb.apply();

        getInstance(EventBus.class).post(new ServerEvent.ServerInitEvent());
    }

    /**
     Prints information about registered instances. This includes injection bindings, extensions, and event handlers.
     This method is typically only used when starting the server.

     @param event
     The server event indicating the server started
     */
    @Listener
    protected void debugRegisteredInstances(ServerStartedEvent event) {
        log().info("\u00A77(\u00A7bSelene\u00A77) \u00A7fLoaded bindings: ");
        AtomicInteger unprovisionedTypes = new AtomicInteger();
        this.getAllBindings().forEach((Key<?> key, Binding<?> binding) -> {
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
        ExtensionManager em = getInstance(ExtensionManager.class);
        em.getRegisteredExtensionIds().forEach(ext -> {
            Exceptional<Extension> header = em.getHeader(ext);
            if (header.isPresent()) {
                Extension ex = header.get();
                log().info("  - \u00A77" + ex.name());
                log().info("  | - \u00A77ID: \u00A78" + ex.id());
                log().info("  | - \u00A77UUID: \u00A78" + ex.uniqueId());
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
        getInstance(EventBus.class).getListenersToInvokers().forEach((listener, invokers) -> {
            Class<?> type;
            if (listener instanceof Class) type = (Class<?>) listener;
            else type = listener.getClass();

            log().info("  - \u00A77" + type.getCanonicalName());
            invokers.forEach(invoker -> log().info("  | - \u00A77" + invoker.getEventType().getSimpleName() + ": \u00A78" + invoker.getMethod().getName()));
        });
    }

    private Consumer<ExtensionContext> getExtensionContextConsumer(CommandBus cb, EventBus eb, ExtensionManager em, DiscordUtils du) {
        return (ExtensionContext ctx) -> {
            Class<?> type = ctx.getExtensionClass();
            log().info("Found type [" + type.getCanonicalName() + "] in integrated context");
            Exceptional<?> oi = getInstanceSafe(type);
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
     Gets the log instance created by {@link Selene}.

     @return The log
     */
    @NotNull
    public Logger getLog() {
        return log;
    }

    /**
     Gets the {@link Selene} version, based on the injected value in {@link #construct()}.

     @return The version
     */
    @NotNull
    public String getVersion() {
        return this.version;
    }

    /**
     Gets the server type as indicated by the {@link Selene} implementation.

     @return the server type
     */
    @NotNull
    public abstract ServerType getServerType();

    /**
     Gets the last update of {@link Selene}, based on the injected value in {@link #construct()}

     @return The last update
     */
    @NotNull
    public LocalDateTime getLastUpdate() {
        return this.lastUpdate;
    }

    /**
     Gets the array of authors as defined by {@link #authors}.

     @return A non-null array of authors
     */
    @NotNull
    public String @NotNull [] getAuthors() {
        return authors;
    }

    /**
     Handles a given exception and message using the injected {@link ExceptionHelper} instance. Uses the global
     preferences as defined in {@link GlobalConfig} to use either the {@link ExceptionLevels#FRIENDLY} or
     {@link ExceptionLevels#MINIMAL} level. Additionally this also performs the preference for whether or not to
     print stacktraces, using {@link GlobalConfig#getStacktracesAllowed()}

     @param msg
     The message, usually provided by the developer causing the exception. Can be null.
     @param e
     Zero or more exceptions (varargs)
     */
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

    /**
     Gets the {@link GlobalConfig} instance as injected by the {@link Selene} implementation.

     @return The global config
     */
    @NotNull
    public GlobalConfig getGlobalConfig() {
        return getInstance(GlobalConfig.class);
    }

    /**
     Provides quick access to {@link #getLog()} through the {@link Selene} instance (using {@link #getServer()}).

     @return The {@link Logger}
     */
    public static Logger log() {
        return log;
    }

    /**
     Gets the instance of {@link Selene}.

     @return The {@link Selene} instance
     */
    public static Selene getServer() {
        return instance;
    }

    /**
     Gets the used version of the implementation platform.

     @return The platform version
     */
    public abstract String getPlatformVersion();

    /**
     Gets the used Minecraft version.

     @return The Minecraft version
     */
    public abstract MinecraftVersion getMinecraftVersion();

    public static final List<UUID> GLOBALLY_PERMITTED = SeleneUtils.asList(
            UUID.fromString("6047d264-7769-4e50-a11e-c8b83f65ccc4"),
            UUID.fromString("cb6411bb-31c9-4d69-8000-b98842ce0a0a"),
            UUID.fromString("b7fb5e32-73ee-4f25-b256-a763c8739192")
    );

    public static Exceptional<Path> getResourceFile(String name) {
        try {
            URL resourceUrl = Selene.class.getClassLoader().getResource(name);
            // Warning suppressed relates to potential NPE on `resourceUrl#toURI`. This is caught and handled directly
            // and can thus be suppressed safely.
            @SuppressWarnings("ConstantConditions") Path resourcePath = Paths.get(resourceUrl.toURI());
            File resourceFile = resourcePath.toFile();
            if (resourceFile.isFile()) {
                return Exceptional.of(resourcePath);
            }
        } catch (URISyntaxException | NullPointerException e) {
            return Exceptional.of(e);
        }
        return Exceptional.empty();
    }

}
