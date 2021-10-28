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

package org.dockbox.hartshorn.core.boot;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.dockbox.hartshorn.core.ApplicationBootstrap;
import org.dockbox.hartshorn.core.ArrayListMultiMap;
import org.dockbox.hartshorn.core.HartshornUtils;
import org.dockbox.hartshorn.core.InjectConfiguration;
import org.dockbox.hartshorn.core.Modifier;
import org.dockbox.hartshorn.core.MultiMap;
import org.dockbox.hartshorn.core.annotations.activate.Activator;
import org.dockbox.hartshorn.core.annotations.inject.InjectConfig;
import org.dockbox.hartshorn.core.annotations.inject.InjectPhase;
import org.dockbox.hartshorn.core.boot.LogLevelModifier.LogLevel;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.exceptions.Except;
import org.dockbox.hartshorn.core.function.CheckedConsumer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * Application starter for Hartshorn applications. This takes a single type annotated with {@link Activator}
 * which provides application metadata, and a set of {@link Modifier modifiers}.
 * <p>The starter uses the provided {@link ApplicationBootstrap} reference to use for bootstrapping the
 * application.
 */
public class HartshornApplication {

    private static final String BANNER = """
                 _   _            _       _                     \s
                | | | | __ _ _ __| |_ ___| |__   ___  _ __ _ __ \s
                | |_| |/ _` | '__| __/ __| '_ \\ / _ \\| '__| '_ \\\s
                |  _  | (_| | |  | |_\\__ \\ | | | (_) | |  | | | |
            ====|_| |_|\\__,_|_|===\\__|___/_|=|_|\\___/|_|==|_|=|_|====
                                             -- Hartshorn v%s --
            """.formatted(Hartshorn.VERSION);

    private static final Map<String, CheckedConsumer<ApplicationContext>> POST_CREATE_CALLBACKS = HartshornUtils.emptyConcurrentMap();

    public static void addCallback(final String identifier, final CheckedConsumer<ApplicationContext> callback) {
        POST_CREATE_CALLBACKS.put(identifier, callback);
    }

    /**
     * Creates the bootstrapped server instance using the provided {@link Activator} metadata. If no valid
     * {@link ApplicationBootstrap} is provided the application will not be started. This directly initializes
     * the application.
     *
     * @param activator
     *         The activator type, providing application metadata
     * @param modifiers
     *         The modifiers to use when bootstrapping
     */
    public static ApplicationContext create(final Class<?> activator, final String[] args, final Modifier... modifiers) {
        return lazy(activator, args, modifiers).load();
    }

    /**
     * Creates the bootstrapped server instance using the provided {@link Activator} metadata. If no valid
     * {@link ApplicationBootstrap} is provided the application will not be started. This does not initialize
     * the application. The returned {@link Runnable} can be used to initialize the server at the desired
     * time.
     *
     * @param activator
     *         The activator type, providing application metadata
     * @param modifiers
     *         The modifiers to use when bootstrapping
     *
     * @return A {@link Runnable} to initialize the application
     */
    public static HartshornLoader lazy(final Class<?> activator, final String[] args, final Modifier... modifiers) {
        for (final Modifier modifier : modifiers) {
            if (modifier instanceof LogLevelModifier levelModifier) setLogLevel(levelModifier.level());
        }
        final Activator annotation = verifyActivator(activator);
        final Class<? extends ApplicationBootstrap> bootstrap = annotation.value();

        Hartshorn.log().info("Requested bootstrap is " + bootstrap.getSimpleName());
        try {
            final ApplicationBootstrap applicationBootstrap = instance(bootstrap);
            return load(applicationBootstrap, annotation, TypeContext.of(activator), args, modifiers);
        }
        catch (final InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            Except.handle("Could not bootstrap application " + activator.getSimpleName(), e);
            return () -> {
                throw new RuntimeException("Hartshorn could not be bootstrapped, see cause for details", e);
            };
        }
    }

    public static HartshornLoader load(
            final ApplicationBootstrap injectableBootstrap,
            final Activator annotation,
            final TypeContext<?> activator,
            final String[] args,
            final Modifier... modifiers
    ) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        Hartshorn.log().info("Starting " + Hartshorn.PROJECT_NAME + " with activator " + activator.name());
        final long start = System.currentTimeMillis();

        if (!injectableBootstrap.isCI()) {
            for (final String line : BANNER.split("\n")) {
                Hartshorn.log().info(line);
            }
            Hartshorn.log().info("");
        }

        final String prefix = "".equals(annotation.prefix()) ? activator.type().getPackage().getName() : annotation.prefix();

        final MultiMap<InjectPhase, InjectConfiguration> configurations = new ArrayListMultiMap<>();
        for (final InjectConfig config : annotation.configs()) {
            Hartshorn.log().debug("Adding configuration " + config.value().getSimpleName() + " for phase " + config.phase());
            configurations.put(config.phase(), instance(config.value()));
        }

        final List<String> prefixes = HartshornUtils.asList(Hartshorn.PACKAGE_PREFIX);
        if (!prefix.startsWith(Hartshorn.PACKAGE_PREFIX)) {
            prefixes.add(prefix);
        }

        injectableBootstrap.create(
                prefixes,
                activator,
                HartshornUtils.emptyList(),
                configurations,
                args,
                modifiers);
        final long creationTime = System.currentTimeMillis() - start;

        return () -> {
            final long initStart = System.currentTimeMillis();
            injectableBootstrap.init();
            final long initTime = System.currentTimeMillis() - initStart;
            Hartshorn.log().info("Started " + Hartshorn.PROJECT_NAME + " in " + (creationTime + initTime) + "ms (" + creationTime + "ms creation, " + initTime + "ms init)");
            final ApplicationContext context = injectableBootstrap.context();

            POST_CREATE_CALLBACKS.forEach(($, callback) -> Except.handle(() -> callback.accept(context)));
            return context;
        };
    }

    public static void setLogLevel(final LogLevel level) {
        final LoggerContext loggerContext = LoggerContext.getContext(false);
        final Configuration configuration = loggerContext.getConfiguration();
        final LoggerConfig loggerConfig = configuration.getLoggerConfig("org.dockbox.hartshorn");
        loggerConfig.setLevel(Level.getLevel(level.name()));
    }

    private static Activator verifyActivator(final Class<?> activator) {
        final Exceptional<Activator> annotation = TypeContext.of(activator).annotation(Activator.class);
        if (annotation.absent())
            throw new IllegalArgumentException("Application type should be decorated with @Activator");

        if (TypeContext.of(activator).isAbstract())
            throw new IllegalArgumentException("Bootstrap type cannot be abstract, got " + activator.getSimpleName());

        return annotation.get();
    }

    private static <T> T instance(final Class<T> type) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        final Constructor<T> constructor = type.getConstructor();
        return constructor.newInstance();
    }

}