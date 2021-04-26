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

import org.dockbox.selene.api.exceptions.Except;
import org.dockbox.selene.di.InjectConfiguration;
import org.dockbox.selene.di.InjectableBootstrap;
import org.dockbox.selene.di.Preloadable;
import org.dockbox.selene.di.Provider;
import org.dockbox.selene.di.annotations.RequiresBinding;
import org.dockbox.selene.util.Reflect;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Properties;

/**
 * The global bootstrapping component which instantiates all configured modules and provides access
 * to server information.
 */
public abstract class SeleneBootstrap extends InjectableBootstrap {

    private static SeleneBootstrap instance;
    private String version;
    private LocalDateTime lastUpdate;

    /**
     * Instantiates {@link Selene}, creating a local injector based on the provided {@link
     * InjectConfiguration}. Also verifies dependency artifacts and injector bindings. Proceeds
     * to {@link SeleneBootstrap#construct()} once verified.
     *
     * @param moduleConfiguration
     *         the injector provided by the Selene implementation
     */
    protected SeleneBootstrap(InjectConfiguration moduleConfiguration) {
        super.bind(SeleneInformation.PACKAGE_PREFIX, moduleConfiguration);
        this.construct();
    }

    /**
     * Loads various properties from selene.properties, including the latest update and version. Once
     * done sets the static instance equal to this instance.
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
                    properties.getOrDefault("last_update", formatter.format(Instant.now())).toString(),
                    formatter
            );
            tVer = properties.getOrDefault("version", "dev").toString();
        }
        catch (IOException e) {
            Except.handle("Failed to convert resource file", e);
        }

        this.version = tVer;
        this.lastUpdate = tLU;

        InjectableBootstrap.setInstance(this);
    }

    public static boolean isConstructed() {
        return getInstance() != null;
    }

    public static SeleneBootstrap getInstance() {
        return (SeleneBootstrap) InjectableBootstrap.getInstance();
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
        Selene.log().info("\u00A7e ,-,");
        Selene.log().info("\u00A7e/.(");
        Selene.log().info("\u00A7e\\ {");
        Selene.log().info("\u00A7e `-`");
        Selene.log().info("     \u00A77Initiating \u00A7bSelene " + this.getVersion());

        // Register pre-loadable types early on, these typically modify initialisation logic
        Reflect.subTypes(SeleneInformation.PACKAGE_PREFIX, Preloadable.class)
                .forEach(t -> Provider.provide(t).preload());
        // Ensure all services requiring a platform implementation have one present
        Reflect.annotatedTypes(SeleneInformation.PACKAGE_PREFIX, RequiresBinding.class).forEach(type -> {
            if (Reflect.subTypes(SeleneInformation.PACKAGE_PREFIX, type).isEmpty()) {
                Selene.log().error("No implementation exists for [" + type.getCanonicalName() + "], this will cause functionality to misbehave or not function!");
            }
        });
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
     * Gets the last update of {@link Selene}, based on the injected value in {@link #construct()}
     *
     * @return The last update
     */
    @NotNull
    public LocalDateTime getLastUpdate() {
        return this.lastUpdate;
    }

    /**
     * Gets the {@link GlobalConfig} instance as injected by the {@link Selene} implementation.
     *
     * @return The global config
     */
    @SuppressWarnings("MethodMayBeStatic")
    @NotNull
    public GlobalConfig getGlobalConfig() {
        return Provider.provide(GlobalConfig.class);
    }

    /**
     * Gets the used version of the implementation platform.
     *
     * @return The platform version
     */
    public abstract String getPlatformVersion();

}
