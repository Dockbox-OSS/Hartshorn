/*
 * Copyright 2019-2025 the original author or authors.
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

package org.dockbox.hartshorn.launchpad.activation;

import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.inject.processing.ComponentPreProcessor;
import org.dockbox.hartshorn.inject.processing.HierarchicalBinderPostProcessor;
import org.dockbox.hartshorn.launchpad.condition.RequiresActivator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Meta-annotation used to mark annotations as module activators. Module activators indicate whether
 * specific components processors become active. Additionally, they can be used to filter the
 * activation of components using the {@link RequiresActivator} annotation.
 *
 * <p>Module activators always need to be annotated with {@link ModuleActivator}. If an annotation
 * is used as activator, but is not annotated with {@link ModuleActivator}, it will be rejected by
 * the active {@link InjectionCapableApplication}.
 *
 * <p>Module activators offer a way to specify {@link #scanPackages() base packages} which become
 * active when the activator is present on the application activator. These packages will be scanned
 * when the application is initializing.
 *
 * @since 0.4.1
 *
 * @author Guus Lieben
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface ModuleActivator {

    /**
     * Additional packages to scan for components if this module activator is present. If a package
     * has already been processed before, it is up to the active {@link InjectionCapableApplication}
     * to decide whether to process it again.
     *
     * @return The additional packages to scan for components.
     */
    String[] scanPackages() default {};

    /**
     * Component pre-processors to register if this module activator is present.
     *
     * @return The component pre-processors to register.
     */
    Class<? extends ComponentPreProcessor>[] componentPreProcessors() default {};

    /**
     * Component post-processors to register if this module activator is present.
     *
     * @return The component post-processors to register.
     */
    Class<? extends ComponentPostProcessor>[] componentPostProcessors() default {};

    /**
     * Hierarchical binder post-processors to register if this module activator is present.
     *
     * @return The hierarchical binder post-processors to register.
     */
    Class<? extends HierarchicalBinderPostProcessor>[] binderPostProcessors() default {};
}
