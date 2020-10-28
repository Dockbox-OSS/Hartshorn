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
import com.google.inject.binder.AnnotatedBindingBuilder;

import org.dockbox.selene.core.command.CommandBus;
import org.dockbox.selene.core.events.server.ServerEvent;
import org.dockbox.selene.core.server.config.ExceptionLevels;
import org.dockbox.selene.core.server.config.GlobalConfig;
import org.dockbox.selene.core.server.properties.InjectableType;
import org.dockbox.selene.core.server.properties.InjectorProperty;
import org.dockbox.selene.core.util.discord.DiscordUtils;
import org.dockbox.selene.core.util.events.EventBus;
import org.dockbox.selene.core.util.exceptions.ExceptionHelper;
import org.dockbox.selene.core.util.extension.Extension;
import org.dockbox.selene.core.util.extension.ExtensionContext;
import org.dockbox.selene.core.util.extension.ExtensionManager;
import org.dockbox.selene.core.util.files.ConfigurateManager;
import org.dockbox.selene.core.util.inject.AbstractExceptionInjector;
import org.dockbox.selene.core.util.inject.AbstractModuleInjector;
import org.dockbox.selene.core.util.inject.AbstractUtilInjector;
import org.dockbox.selene.core.util.inject.SeleneInjectModule;
import org.dockbox.selene.core.util.library.LibraryArtifact;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 The global {@link Selene} instance used to grant access to various components.
 */
@SuppressWarnings("ClassWithTooManyMethods")
public abstract class Selene {

    /**
     Low-level interface, used by the default IntegratedExtension as indicated by the mappings provided by the platform
     implementation. Used to access the extension when {@link Selene} is used
     in a {@link ServerReference} method call.
     */
    public interface IntegratedExtension {
    }

    /**
     Server type definitions containing display names, minimum/preferred versions, and whether or not the platform
     provides access to Native Minecraft Sources (NMS).
     */
    public enum ServerType {
        SPONGE("SpongePowered", true, true, "1.12.2-2555-7.1.0-BETA-2815", "1.12.2-2838-7.2.2-RC0"),
        MAGMA("Magma", true, true, "Not (yet) supported", "Not (yet) supported"),
        SPIGOT("Spigot", true, false, "Not (yet) supported", "Not (yet) supported"),
        PAPER("Paper", true, false, "Not (yet) supported", "Not (yet) supported"),
        JUNIT("JUnit Testing", true, true, "5.3.2", "5.3.2"),
        OTHER("Other", true, false, "Not (yet) supported", "Not (yet) supported");

        private final String displayName;
        private final boolean hasNMSAccess;
        private final boolean isModded;
        private final String minimumVersion;
        private final String preferredVersion;

        ServerType(String displayName, boolean hasNMSAccess, boolean isModded, String minimumVersion, String preferredVersion) {
            this.displayName = displayName;
            this.hasNMSAccess = hasNMSAccess;
            this.isModded = isModded;
            this.minimumVersion = minimumVersion;
            this.preferredVersion = preferredVersion;
        }

        /**
         Gets the display name of the platform in a human readable format

         @return the display name
         */
        public String getDisplayName() {
            return this.displayName;
        }

        /**
         Returns whether or not the platform provides access to NMS.

         @return the boolean
         */
        public boolean hasNMSAccess() {
            return this.hasNMSAccess;
        }

        /**
         Gets minimum version.

         @return the minimum version
         */
        public String getMinimumVersion() {
            return this.minimumVersion;
        }

        /**
         Gets preferred version.

         @return the preferred version
         */
        public String getPreferredVersion() {
            return this.preferredVersion;
        }

        /**
         Returns whether or not the platform provides access to a mod loader.
         This can be especially useful when using {@link ConfigurateManager#getModdedPlatformModsConfigDir()} as it
         may return {@link Optional#empty()} depending on the availability mods on the platform.

         @return the boolean
         */
        public boolean isModded() {
            return this.isModded;
        }
    }

    private static final Logger log = LoggerFactory.getLogger(Selene.class);
    private String version;
    private LocalDateTime lastUpdate;
    /**
     Constant value holding the GitHub username(s) of the author(s) of {@link Selene}. This does not include names of
     extension developers.
     */
    protected static final String[] authors = {"GuusLieben"};

    private static Selene instance;

    private final transient List<Injector> injectors = new CopyOnWriteArrayList<>();
    private Injector mainInjector;

    /**
     Instantiates {@link Selene}, creating a local injector based on the provided {@link SeleneInjectModule}.
     Also verifies dependency artifacts and injector bindings. Proceeds to {@link Selene#construct()} once verified.

     @param injector
     the injector provided by the Selene implementation
     */
    protected Selene(SeleneInjectModule injector) {
        this.verifyArtifacts();
        this.mainInjector = Guice.createInjector(injector);
        this.construct();
    }

    /**
     Instantiates {@link Selene}, creating a local injector based on the provided {@link AbstractModule}s.
     Also verifies dependency artifacts and injector bindings. Proceeds to {@link Selene#construct()} once verified.

     @param moduleInjector
     the module injector
     @param exceptionInjector
     the exception injector
     @param utilInjector
     the util injector
     */
    protected Selene(
            AbstractModuleInjector moduleInjector,
            AbstractExceptionInjector exceptionInjector,
            AbstractUtilInjector utilInjector
    ) {
        this.verifyArtifacts();
        this.mainInjector = Guice.createInjector();
        if (null != moduleInjector) this.mainInjector = this.mainInjector.createChildInjector(moduleInjector);
        if (null != exceptionInjector) this.mainInjector = this.mainInjector.createChildInjector(exceptionInjector);
        if (null != utilInjector) this.mainInjector = this.mainInjector.createChildInjector(utilInjector);
        this.construct();
    }

    private void verifyArtifacts() {
        // TODO: Maven repository verification
//        for (LibraryArtifact artifact : this.getAllArtifacts()) { }
    }

    private void verifyInjectorBindings() {
        for (Class<?> bindingType : SeleneInjectModule.Companion.getRequiredBindings()) {
            try {
                this.mainInjector.getBinding(bindingType);
            } catch (ConfigurationException e) {
                log().error("Missing binding for " + bindingType.getCanonicalName() + "! While it is possible to inject it later, it is recommended to do so through the default platform injector!");
            }
        }
    }

    /**
     Loads various properties from selene.properties, including the latest update and version.
     Once done sets the static instance equal to this instance.
     */
    protected void construct() {

        this.verifyInjectorBindings();
        this.upgradeInjectors(this.mainInjector);

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

     @return The instance, if present. Otherwise returns null
     */
    public static <T> T getInstance(Class<T> type, Class<?> extension, InjectorProperty<?>... additionalProperties) {
        T typeInstance = null;
        // Prepare modules
        ExtensionModule extensionModule = null;
        AbstractModule propertyModule = new AbstractModule() {
            @Override
            protected void configure() {
                this.bind(InjectorProperty[].class).toInstance(additionalProperties);
            }
        };

        // Attempt to get extension instance and get the extension module
        if (type.isAnnotationPresent(Extension.class)) {
            typeInstance = getInstance(ExtensionManager.class).getInstance(type).orElse(null);
        }

        if (extension.isAnnotationPresent(Extension.class)) {
            extensionModule = getServer().getExtensionModule(extension, extension.getAnnotation(Extension.class), null);
        }

        // If the instance didn't exist, or wasn't a extension, attempt to use any known injector to create one
        if (null == typeInstance && null != getServer()) {
            for (Injector injector : getServer().getInjectors()) {
                try {
                    typeInstance = injector.getInstance(type);
                    if (null != typeInstance)
                        break;
                } catch (ConfigurationException | ProvisionException ignored) {
                }
            }
        }

        // Prepare the local injector, depending on whether or not the extension was present
        Injector localInjector;
        if (null == extensionModule) {
            localInjector = Guice.createInjector(propertyModule);
        } else {
            localInjector = Guice.createInjector(propertyModule, extensionModule);
        }

        // If the current instance was not created using a existing injector, try it again with our local injector
        if (null == typeInstance) {
            try {
                typeInstance = localInjector.getInstance(type);
            } catch (ConfigurationException | ProvisionException ignored) {
            }
        }

        // Always inject members afterwards, even if the instance was created using another injector
        localInjector.injectMembers(typeInstance);

        // Inject all members using all known injectors
        if (null != typeInstance && null != getServer()) {
            typeInstance = getServer().injectMembers(typeInstance);
        }

        // Inject properties if applicable
        if (typeInstance instanceof InjectableType) {
            ((InjectableType) typeInstance).injectProperties(additionalProperties);
        }

        // May be null, but we have used all possible injectors, it's up to the developer now
        return typeInstance;
    }

    public Injector createExtensionInjector(Object instance) {
        if (null != instance && instance.getClass().isAnnotationPresent(Extension.class)) {
            Optional<ExtensionContext> context = getInstance(ExtensionManager.class).getContext(instance.getClass());
            Extension extension;
            extension = context
                    .map(ExtensionContext::getExtension)
                    .orElseGet(() -> instance.getClass().getAnnotation(Extension.class));
            return this.createExtensionInjector(instance, extension, context.orElse(null));
        }
        return this.mainInjector;
    }

    private <T> ExtensionModule getExtensionModule(T instance, Extension header, ExtensionContext context) {
        ExtensionModule module = new ExtensionModule();
        if (null != instance) {
            if (instance instanceof Class<?>) {
                module.bind(Logger.class).toInstance(LoggerFactory.getLogger((Class<?>) instance));
            } else {
                module.bind(Logger.class).toInstance(LoggerFactory.getLogger(instance.getClass()));
                //noinspection unchecked
                module.bind((Class<T>) instance.getClass()).toInstance(instance);
            }
        }
        if (null != header)
            module.bind(Extension.class).toInstance(header);
        if (null != context)
            module.bind(ExtensionContext.class).toInstance(context);

        return module;
    }

    public Injector createExtensionInjector(Object instance, Extension header, ExtensionContext context) {
        return this.mainInjector.createChildInjector(this.getExtensionModule(instance, header, context));
    }

    /**
     Creates a custom binding for a given contract and implementation using a custom {@link AbstractModule}. Requires
     the implementation to extend the contract type.

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
        getServer().mainInjector = getServer().mainInjector.createChildInjector(localModule);
    }

    /**
     Gets the injector used for instance mapping. Holds both implementation provided mappings and manually created
     mappings.

     @return The injector
     */
    private List<Injector> getInjectors() {
        return this.injectors;
    }

    public <T> T injectMembers(T type) {
        if (null != type) {
            for (Injector injector : this.getInjectors()) {
                injector.injectMembers(type);
            }
        }

        return type;
    }

    public <T> T injectMembers(T type, Object extensionInstance) {
        if (null != type) {
            for (Injector injector : this.getInjectors()) {
                injector.injectMembers(type);
            }

            if (null != extensionInstance && extensionInstance.getClass().isAnnotationPresent(Extension.class)) {
                this.createExtensionInjector(extensionInstance).injectMembers(type);
            }
        }

        return type;
    }

    protected void upgradeInjectors(Injector injector) {
        if (null != injector)
            this.getInjectors().add(injector);
    }

    private Map<Key<?>, Binding<?>> getAllBindings() {
        Map<Key<?>, Binding<?>> bindingsMap = new ConcurrentHashMap<>();
        for (Injector injector : this.getInjectors()) {
            bindingsMap.putAll(injector.getAllBindings());
        }
        return bindingsMap;
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

        this.initIntegratedExtensions(this.getConsumer("integrated", cb, eb, cm, du));

        getInstance(EventBus.class).post(new ServerEvent.Init());
    }

    /**
     Prints information about registered instances. This includes injection bindings, extensions, and event handlers.
     This method is typically only used when starting the server.
     */
    protected void debugRegisteredInstances() {
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
            Optional<Extension> header = em.getHeader(ext);
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
        getInstance(EventBus.class).getListenerToInvokers().forEach((listener, invokers) -> {
            Class<?> type;
            if (listener instanceof Class) type = (Class<?>) listener;
            else type = listener.getClass();

            log().info("  - \u00A77" + type.getCanonicalName());
            invokers.forEach(invoker -> log().info("  | - \u00A77" + invoker.getEventType().getSimpleName() + ": \u00A78" + invoker.getMethod().getName()));
        });
    }

    private Consumer<ExtensionContext> getConsumer(String contextType, CommandBus cb, EventBus eb, ExtensionManager em, DiscordUtils du) {
        return (ExtensionContext ctx) -> {
            Class<?> type = ctx.getExtensionClass();
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

    private LibraryArtifact[] getAllArtifacts() {
        List<LibraryArtifact> artifacts = new ArrayList<>(Arrays.asList(this.getPlatformArtifacts()));
        artifacts.add(new LibraryArtifact("org.reflections", "reflections", "0.9.11"));
        artifacts.add(new LibraryArtifact("com.fasterxml.jackson.core", "jackson-databind", "2.9.8"));
        artifacts.add(new LibraryArtifact("com.fasterxml.jackson.dataformat", "jackson-dataformat-yaml", "2.9.8"));
        artifacts.add(new LibraryArtifact("com.fasterxml.jackson.datatype", "jackson-datatype-jsr310", "2.9.8"));
        artifacts.add(new LibraryArtifact("org.apache.commons", "commons-collections4", "4.1"));
        return artifacts.toArray(new LibraryArtifact[0]);
    }

    /**
     Get the library artifacts of platform specific dependencies. These are usually only used to check whether or not
     the dependencies are present during construction stages.

     @return The array of libary artifacts
     */
    protected abstract LibraryArtifact[] getPlatformArtifacts();

    /**
     Gets the used version of the implementation platform.

     @return The platform version
     */
    public abstract String getPlatformVersion();

    /**
     Gets the used Minecraft version.

     @return The Minecraft version
     */
    public abstract String getMinecraftVersion();

    private static final class ExtensionModule extends AbstractModule {

        private ExtensionModule() {
        }

        @Override
        protected void configure() {
            // To be done externally
        }

        @Override
        public <T> AnnotatedBindingBuilder<T> bind(Class<T> clazz) {
            return super.bind(clazz);
        }
    }
}
