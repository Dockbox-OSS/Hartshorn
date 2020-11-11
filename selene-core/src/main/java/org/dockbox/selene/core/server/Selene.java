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
import com.google.inject.Module;
import com.google.inject.ProvisionException;
import com.google.inject.util.Modules;

import org.dockbox.selene.core.command.CommandBus;
import org.dockbox.selene.core.events.server.ServerEvent;
import org.dockbox.selene.core.server.config.ExceptionLevels;
import org.dockbox.selene.core.server.config.GlobalConfig;
import org.dockbox.selene.core.server.properties.InjectableType;
import org.dockbox.selene.core.server.properties.InjectorProperty;
import org.dockbox.selene.core.util.SeleneUtils;
import org.dockbox.selene.core.util.discord.DiscordUtils;
import org.dockbox.selene.core.util.environment.MinecraftVersion;
import org.dockbox.selene.core.util.events.EventBus;
import org.dockbox.selene.core.util.exceptions.ExceptionHelper;
import org.dockbox.selene.core.util.extension.Extension;
import org.dockbox.selene.core.util.extension.ExtensionContext;
import org.dockbox.selene.core.util.extension.ExtensionManager;
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
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 The global {@link Selene} instance used to grant access to various components.
 */
@SuppressWarnings("ClassWithTooManyMethods")
public abstract class Selene {

    private static final Logger log = LoggerFactory.getLogger(Selene.class);
    private String version;
    private LocalDateTime lastUpdate;
    /**
     Constant value holding the GitHub username(s) of the author(s) of {@link Selene}. This does not include names of
     extension developers.
     */
    protected static final String[] authors = {"GuusLieben"};

    private static Selene instance;

    private final transient List<AbstractModule> injectorModules = new CopyOnWriteArrayList<>();

    /**
     Instantiates {@link Selene}, creating a local injector based on the provided {@link SeleneInjectModule}.
     Also verifies dependency artifacts and injector bindings. Proceeds to {@link Selene#construct()} once verified.

     @param injector
     the injector provided by the Selene implementation
     */
    protected Selene(SeleneInjectModule injector) {
        this.verifyArtifacts();
        this.injectorModules.add(injector);
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
        if (null != moduleInjector) this.injectorModules.add(moduleInjector);
        if (null != exceptionInjector) this.injectorModules.add(exceptionInjector);
        if (null != utilInjector) this.injectorModules.add(utilInjector);
        this.construct();
    }

    private void verifyArtifacts() {
        // TODO: Maven repository verification
//        for (LibraryArtifact artifact : this.getAllArtifacts()) { }
    }

    private void verifyInjectorBindings() {
        for (Class<?> bindingType : SeleneInjectModule.Companion.getRequiredBindings()) {
            try {
                this.createInjector().getBinding(bindingType);
            } catch (ConfigurationException e) {
                log().error("Missing binding for " + bindingType.getCanonicalName() + "! While it is possible to inject it later, it is recommended to do so through the default platform injector!");
            }
        }
    }

    private Injector createInjector(AbstractModule... additionalModules) {
        Module collectedModule = Modules
                .override(this.injectorModules)
                .with(Arrays.stream(additionalModules)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()));

        return Guice.createInjector(collectedModule);
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
     @param extension
     The type of the extension to lookup, this is only used for extension-specific bindings if the type is annotated
     with {@link Extension}
     @param type
     The type of the instance
     @param additionalProperties
     The additional properties which can optionally be injected in the type, if the type implements
     {@link InjectableType}

     @return The instance, if present. Otherwise returns null
     */
    public static <T> T getInstance(Class<T> type, Class<?> extension, InjectorProperty<?>... additionalProperties) {
        T typeInstance = null;

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

        // Attempt to get extension instance and get the extension module
        if (type.isAnnotationPresent(Extension.class)) {
            typeInstance = getInstance(ExtensionManager.class).getInstance(type).orElse(null);
        }

        Injector injector = getServer().createInjector(extensionModule, propertyModule);
        try {
            typeInstance = injector.getInstance(type);
        } catch (ProvisionException e) {
            log().error("Could not create instance using registered injector " + injector + " for [" + type + "]", e);
        } catch (ConfigurationException ignored) {
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
            Optional<ExtensionContext> context = getInstance(ExtensionManager.class).getContext(instance.getClass());
            Extension extension;
            extension = context
                    .map(ExtensionContext::getExtension)
                    .orElseGet(() -> instance.getClass().getAnnotation(Extension.class));
            return this.createExtensionInjector(instance, extension, context.orElse(null));
        }
        return this.createInjector();
    }

    @SuppressWarnings("unchecked")
    private <T> ExtensionModule getExtensionModule(T instance, Extension header, ExtensionContext context) {
        ExtensionModule module = new ExtensionModule();

        if (null != instance) {
            if (instance instanceof Class<?>) {
                module.acceptBinding(Logger.class, LoggerFactory.getLogger((Class<?>) instance));
            } else {
                module.acceptBinding(Logger.class, LoggerFactory.getLogger(instance.getClass()));
                module.acceptBinding((Class<T>) instance.getClass(), instance);
            }
        }
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
            this.createInjector().injectMembers(type);
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
        Map<Key<?>, Binding<?>> bindings = new ConcurrentHashMap<>();
        this.createInjector().getAllBindings().forEach((Key<?> key, Binding<?> binding) -> {
            try {
                Class<?> keyType = binding.getKey().getTypeLiteral().getRawType();
                Class<?> providerType = binding.getProvider().get().getClass();

                if (!keyType.equals(providerType) && null != providerType)
                    bindings.put(key, binding);
            } catch (ProvisionException | AssertionError ignored) {
            }
        });

        // TODO, SeleneUtils unmodifiableMap
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
        log().info("\u00A77Loading \u00A7bSelene " + this.getVersion());

        EventBus eb = getInstance(EventBus.class);
        CommandBus cb = getInstance(CommandBus.class);
        ExtensionManager cm = getInstance(ExtensionManager.class);
        DiscordUtils du = getInstance(DiscordUtils.class);

        this.initIntegratedExtensions(this.getExtensionContextConsumer(cb, eb, cm, du));

        getInstance(EventBus.class).post(new ServerEvent.Init());
    }

    /**
     Prints information about registered instances. This includes injection bindings, extensions, and event handlers.
     This method is typically only used when starting the server.
     */
    protected void debugRegisteredInstances() {
        /* String constants */
        final List<String> astronaut = Arrays.asList(
                "                                    ",
                "                                    ",
                "             _..._                  ",
                "           .'     '.      _         ",
                "          /    .-\"\"-\\   _/ \\        ",
                "        .-|   /:.   |  |   |        ",
                "        |  \\  |:.   /.-'-./         ",
                "        | .-'-;:__.'    =/          ",
                "       .'=   *=|  GL _.='           ",
                "      /  _ .   |    ;               ",
                "     ;-.-'|     \\   |               ",
                "    /   | \\     _\\  _\\              ",
                "    \\__/'._;.   ==' ==\\             ",
                "             \\     \\   |            ",
                "             /     /   /            ",
                "             /-._/ -._/             ",
                "             \\   '\\   \\             ",
                "              '-._/.__/             "
        );

        final String separator = "\u00A73===============================================";
        final String lateIndent = "                                    ";

        /* Selene information */
        final List<String> lines = new ArrayList<>();
        lines.add(separator);
        lines.add(this.formatDottedLine("Selene Version") + this.getVersion());
        lines.add(this.formatDottedLine("Minecraft Version") + this.getMinecraftVersion().getReadableVersionString());
        lines.add(this.formatDottedLine("Platform Version") + this.getPlatformVersion());
        lines.add(separator);

        /* Extension information */
        ExtensionManager em = getInstance(ExtensionManager.class);
        int extensionCount = em.getRegisteredExtensionIds().size();
        lines.add(this.formatDottedLine("Extensions") + "\u00A77(" + extensionCount + ')');
        em.getRegisteredExtensionIds().forEach(ext -> {
            Optional<Extension> header = em.getHeader(ext);
            if (header.isPresent()) {
                Extension ex = header.get();
                lines.add("  " + this.formatDottedLine(ex.name()) + ex.description());
            }
        });

        lines.add(separator);

        /* Event listener information */
        EventBus eb = getInstance(EventBus.class);
        int listenerCount = eb.getListenerToInvokers().size();
        lines.add(this.formatDottedLine("Event handlers") + "\u00A77(" + listenerCount + ')');
        eb.getListenerToInvokers().forEach((listener, invokers) -> {
            Class<?> type;
            if (listener instanceof Class) type = (Class<?>) listener;
            else type = listener.getClass();

            /* Listener type */
            lines.add("  " + this.formatDottedLine(type.getSimpleName()) + "\u00A77(" + invokers.size() + ')');

            /* Listener method(s) */
            invokers.forEach(invoker -> lines.add("    " + this.formatDottedLine(
                    invoker.getEventType().getSimpleName(), '7') + invoker.getMethod().getName()));
        });

        lines.add(separator);

        /* Binding information */
        int bindingCount = this.getAllBindings().size();
        lines.add(this.formatDottedLine("Bindings") + "\u00A77(" + bindingCount + ')');
        this.getAllBindings().forEach((Key<?> key, Binding<?> binding) -> {
            Class<?> keyType = binding.getKey().getTypeLiteral().getRawType();
            Class<?> providerType = binding.getProvider().get().getClass();
            lines.add("  " + this.formatDottedLine(keyType.getSimpleName()) + providerType.getSimpleName());
        });

        lines.add(separator);

        /* Merging lines with the logo */
        StringBuilder builder = new StringBuilder("\n");
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (i < astronaut.size()) {
                builder.append("\u00A7e").append(astronaut.get(i));
            } else {
                builder.append(lateIndent);
            }
            builder.append(line).append("\n");
        }
        log.info(builder.toString());
    }

    private String formatDottedLine(String title, char overrideColorCode) {
        final int extensionLineDotCount = 30;
        int lineDotCount = extensionLineDotCount < title.length() ? 0 : extensionLineDotCount - title.length();
        String dots = SeleneUtils.repeat("\u00A77.", lineDotCount);
        return " \u00A77- \u00A7" + overrideColorCode + title + dots + "\u00A7f: \u00A78";
    }

    private String formatDottedLine(String title) {
        return formatDottedLine(title, 'f');
    }

    private Consumer<ExtensionContext> getExtensionContextConsumer(CommandBus cb, EventBus eb, ExtensionManager
            em, DiscordUtils du) {
        return (ExtensionContext ctx) -> {
            Class<?> type = ctx.getExtensionClass();
            log().info("Found type [" + type.getCanonicalName() + "] in integrated context");
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
    public abstract MinecraftVersion getMinecraftVersion();

}
