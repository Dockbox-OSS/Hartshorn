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

package org.dockbox.selene.api;

import org.dockbox.selene.api.config.GlobalConfig;
import org.dockbox.selene.api.exceptions.Except;
import org.dockbox.selene.di.InjectConfiguration;
import org.dockbox.selene.di.InjectableBootstrap;
import org.dockbox.selene.di.annotations.Required;
import org.dockbox.selene.di.preload.Preloadable;
import org.dockbox.selene.util.Reflect;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Properties;

import lombok.Getter;

/**
 * The global bootstrapping component which instantiates all configured modules and provides access
 * to server information.
 */
public abstract class SeleneBootstrap extends InjectableBootstrap {

    @Getter
    private String version;
    @Getter
    private final GlobalConfig config;

    /**
     * Instantiates {@link Selene}, creating a local injector based on the provided {@link
     * InjectConfiguration}. Also verifies dependency artifacts and injector bindings. Proceeds
     * to {@link SeleneBootstrap#construct()} once verified.
     *
     * @param early
     *         the injector provided by the Selene implementation
     */
    protected SeleneBootstrap(InjectConfiguration early, InjectConfiguration late, Class<?> activationSource) {
        super(SeleneInformation.PACKAGE_PREFIX, activationSource);
        Reflections.log = null; // Don't output Reflections
        this.enter(BootstrapPhase.PRE_CONSTRUCT);
        super.getContext().bind(SeleneInformation.PACKAGE_PREFIX);
        super.getContext().bind(early);
        this.config = this.getContext().get(GlobalConfig.class);
        Except.useStackTraces(this.config.getStacktracesAllowed());
        Except.with(this.config.getExceptionLevel());

        this.enter(BootstrapPhase.CONSTRUCT);
        super.getContext().bind(late);
        this.construct();
    }

    /**
     * Loads various properties from selene.properties, including the latest update and version. Once
     * done sets the static instance equal to this instance.
     */
    protected void construct() {
        String version = "dev";

        try {
            Properties properties = new Properties();
            properties.load(this.getClass().getResourceAsStream("/selene.properties"));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm")
                    .withLocale(Locale.getDefault())
                    .withZone(ZoneId.systemDefault());

            version = properties.getOrDefault("version", "dev").toString();
        }
        catch (IOException e) {
            Except.handle("Failed to convert resource file", e);
        }

        this.version = version;
    }

    public static boolean isConstructed() {
        return instance() != null;
    }

    public static SeleneBootstrap instance() {
        return (SeleneBootstrap) InjectableBootstrap.instance();
    }

    /**
     * Gets the array of authors as defined by {@link SeleneInformation#AUTHORS}.
     *
     * @return A non-null array of authors
     */
    @NotNull
    public static String @NotNull [] getAuthors() {
        return SeleneInformation.AUTHORS;
    }

    /**
     * Initiates a {@link Selene} instance. Collecting integrated modules and registering them to the
     * appropriate instances where required.
     */
    protected void init() {
        Selene.log().info("Initiating Selene " + this.getVersion());

        // Ensure all services requiring a platform implementation have one present
        Reflect.annotatedTypes(SeleneInformation.PACKAGE_PREFIX, Required.class).forEach(type -> {
            if (Reflect.subTypes(SeleneInformation.PACKAGE_PREFIX, type).isEmpty()) {
                this.handleMissingBinding(type);
            }
        });
    }

    protected void handleMissingBinding(Class<?> type) {
        throw new IllegalStateException("No implementation exists for [" + type.getCanonicalName() + "], this will cause functionality to misbehave or not function!");
    }

    /**
     * Gets the {@link GlobalConfig} instance as injected by the {@link Selene} implementation.
     *
     * @return The global config
     */
    @SuppressWarnings("MethodMayBeStatic")
    @NotNull
    public GlobalConfig getGlobalConfig() {
        return this.getContext().get(GlobalConfig.class);
    }

    /**
     * Gets the used version of the implementation platform.
     *
     * @return The platform version
     */
    public abstract String getPlatformVersion();

    protected void enter(BootstrapPhase phase) {
        Selene.log().info("Selene changed phase to " + phase);
        // Register pre-loadable types early on, these typically modify initialisation logic
        Reflect.subTypes(SeleneInformation.PACKAGE_PREFIX, Preloadable.class)
                .stream()
                .filter(t -> t.isAnnotationPresent(Phase.class) && t.getAnnotation(Phase.class).value().equals(phase))
                .forEach(t -> {
                    Selene.log().info("Preloading " + t.getSimpleName());
                    this.getContext().get(t).preload();
                });
    }

}
