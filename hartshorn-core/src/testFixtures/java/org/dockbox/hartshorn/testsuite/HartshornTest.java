/*
 * Copyright 2019-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.testsuite;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.dockbox.hartshorn.application.StandardApplicationContextFactory;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.processing.ComponentProcessor;
import org.dockbox.hartshorn.component.processing.ServiceActivator;
import org.dockbox.hartshorn.inject.Populate;
import org.dockbox.hartshorn.inject.Populate.Type;
import org.dockbox.hartshorn.util.Customizer;
import org.dockbox.hartshorn.util.introspect.annotations.Extends;
import org.junit.jupiter.api.extension.ExtendWith;

import org.dockbox.hartshorn.inject.Inject;

/**
 * Annotation for test classes that should be run with the Hartshorn test suite. This will automatically
 * provide the test class with a {@link HartshornLifecycleExtension} that will provide an active
 * {@link ApplicationContext} for the test class. The provided {@link ApplicationContext} is refreshed
 * according to the lifecycle of the test class.
 *
 * <p>Additionally, the {@link HartshornLifecycleExtension} will inject fields annotated with {@link Inject}
 * within the test class. Like the active {@link ApplicationContext}, this will be refreshed according to
 * the lifecycle of the test class.
 *
 * @see HartshornLifecycleExtension
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(HartshornLifecycleExtension.class)
@Extends(Populate.class)
@Populate(Type.FIELDS)
public @interface HartshornTest {

    /**
     * Additional {@link ComponentProcessor}s to use when creating the {@link ApplicationContext} for
     * the test class or method. These will be added to the default set of {@link ComponentProcessor}s
     * used by the test suite, even if they are not found through prefix scanning.
     *
     * @see StandardApplicationContextFactory.Configurer#componentPreProcessors(Customizer)
     * @see StandardApplicationContextFactory.Configurer#componentPostProcessors(Customizer)
     */
    Class<? extends ComponentProcessor>[] processors() default  {};

    /**
     * Additional packages to scan for {@link ComponentProcessor}s when creating the {@link ApplicationContext}
     * for the test class or method. These will be added to the default set of packages scanned by the
     * test suite, even if they are not known to the chosen {@link #mainClass()}.
     *
     * @see StandardApplicationContextFactory.Configurer#scanPackages(Customizer)
     */
    String[] scanPackages() default {};

    /**
     * Whether to include the base package of the main class explicitly, or to only use the prefixes provided to
     * {@link #scanPackages()}. Defaults to {@code true}.
     *
     * @see StandardApplicationContextFactory.Configurer#includeBasePackages(boolean)
     */
    boolean includeBasePackages() default true;

    /**
     * Sets the main class to use. Depending on the type of application environment, this may require the
     * class to have relevant {@link ServiceActivator service activators}. Alternative metadata sources may be
     * used, depending on the application environment.
     *
     * @see org.dockbox.hartshorn.application.StandardApplicationBuilder.Configurer#mainClass(Class)
     */
    Class<?> mainClass() default Void.class;
}
