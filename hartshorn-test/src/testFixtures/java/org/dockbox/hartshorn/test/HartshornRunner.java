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

package org.dockbox.hartshorn.test;

import com.google.common.collect.Multimap;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.exceptions.ApplicationException;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.context.HartshornApplicationContext;
import org.dockbox.hartshorn.di.context.ManagedHartshornContext;
import org.dockbox.hartshorn.di.guice.GuiceInjector;
import org.dockbox.hartshorn.di.guice.HartshornModule;
import org.dockbox.hartshorn.di.inject.Injector;
import org.dockbox.hartshorn.di.services.ProviderServiceProcessor;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.dockbox.hartshorn.util.PrefixContext;
import org.dockbox.hartshorn.util.Reflect;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.Mockito;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HartshornRunner implements BeforeAllCallback, AfterAllCallback, AfterEachCallback {

    private boolean reset = false;

    public static final class HartshornRunnerBuilder {
        private boolean reset = false;

        private HartshornRunnerBuilder() {}

        /**
         * Whether the build should reset after each test.
         * @param reset <code>true</code> to reset after each test, or <code>false</code>
         * @return The current builder
         */
        public HartshornRunnerBuilder resetEach(boolean reset) {
            this.reset = reset;
            return this;
        }

        public HartshornRunner build() {
            HartshornRunner hartshornRunner = new HartshornRunner();
            hartshornRunner.reset = this.reset;
            return hartshornRunner;
        }
    }

    public static HartshornRunnerBuilder builder() {
        return new HartshornRunnerBuilder();
    }

    private static final Field module;
    private static final Field bindings;
    private static final Field injectionPoints;
    private static final Field serviceModifiers;
    private static final Field serviceProcessors;
    private static final Field context;
    private static final Field annotationHierarchy;
    private static final Method internalInjector;

    static {
        try {
            module = GuiceInjector.class.getDeclaredField("module");
            module.setAccessible(true);

            bindings = GuiceInjector.class.getDeclaredField("bindings");
            bindings.setAccessible(true);

            injectionPoints = ManagedHartshornContext.class.getDeclaredField("injectionPoints");
            injectionPoints.setAccessible(true);

            serviceModifiers = ManagedHartshornContext.class.getDeclaredField("injectionModifiers");
            serviceModifiers.setAccessible(true);

            serviceProcessors = ManagedHartshornContext.class.getDeclaredField("serviceProcessors");
            serviceProcessors.setAccessible(true);

            internalInjector = HartshornApplicationContext.class.getDeclaredMethod("internalInjector");
            internalInjector.setAccessible(true);

            annotationHierarchy = PrefixContext.class.getDeclaredField("annotationHierarchy");
            annotationHierarchy.setAccessible(true);

            context = Reflect.class.getDeclaredField("context");
            context.setAccessible(true);

        } catch (NoSuchFieldException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        JUnit5Application.prepareBootstrap();
    }


    @Override
    public void afterAll(ExtensionContext context) {
        // To ensure static mocking does not affect other tests
        Mockito.clearAllCaches();
    }

    @Override
    public void afterEach(ExtensionContext ctx) throws Exception {
        if (this.reset) {
            try {
                final ApplicationContext context = Hartshorn.context();

                Injector injector = (Injector) internalInjector.invoke(context);

                module.set(injector, new HartshornModule());
                bindings.set(injector, HartshornUtils.emptyConcurrentSet());
                injectionPoints.set(context, HartshornUtils.emptyConcurrentSet());
                serviceModifiers.set(context, HartshornUtils.emptyConcurrentSet());
                serviceProcessors.set(context, HartshornUtils.emptyConcurrentSet());

                final PrefixContext prefixContext = (PrefixContext) HartshornRunner.context.get(null);
                //noinspection unchecked
                final var oldHierarchy = (Multimap<Class<? extends Annotation>, Class<? extends Annotation>>) annotationHierarchy.get(prefixContext);

                // Non existing package to ensure no keys are cached early on
                final PrefixContext newContext = new PrefixContext(HartshornUtils.asList("a.b"));
                annotationHierarchy.set(newContext, oldHierarchy);
                HartshornRunner.context.set(null, newContext);

                injector.reset();

                context.add(new ProviderServiceProcessor());
            }
            catch (IllegalAccessException | InvocationTargetException e) {
                throw new ApplicationException(e);
            }
        }
    }
}
