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

package org.dockbox.hartshorn.application;

import java.util.function.Predicate;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.launchpad.lifecycle.LifecycleObserver;
import org.dockbox.hartshorn.inject.component.ComponentContainer;
import org.dockbox.hartshorn.inject.component.ComponentRegistry;
import org.dockbox.hartshorn.inject.provider.LifecycleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Activates all non-lazy singleton components in the application context when the application starts. This is done to
 * ensure that all components are instantiated and ready for use when the application starts.
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public class ComponentActivatorObserver implements LifecycleObserver {

    private static final Logger LOG = LoggerFactory.getLogger(ComponentActivatorObserver.class);

    @Override
    public void onStarted(ApplicationContext applicationContext) {
        applicationContext.get(ComponentRegistry.class).containers().stream()
            .filter(container -> container.lifecycle() == LifecycleType.SINGLETON)
            .filter(Predicate.not(ComponentContainer::lazy))
            .forEach(container -> {
                LOG.debug("Activating non-lazy singleton {} in application context", container.id());
                // No need to store the instance manually, as the container will do this for us
                applicationContext.get(container.type().type());
            });
    }
}
