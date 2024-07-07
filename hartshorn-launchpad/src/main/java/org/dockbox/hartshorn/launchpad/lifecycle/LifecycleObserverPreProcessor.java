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

package org.dockbox.hartshorn.launchpad.lifecycle;

import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.inject.processing.ComponentPreProcessor;
import org.dockbox.hartshorn.inject.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.inject.processing.ProcessingPriority;

/**
 * A pre-processor that registers lifecycle observers if the application environment is itself an
 * observable environment. Will register any observer that is a child of the {@link Observer} interface.
 *
 * @see LifecycleObservable
 * @see Observer
 *
 * @since 0.4.8
 *
 * @author Guus Lieben
 */
public class LifecycleObserverPreProcessor extends ComponentPreProcessor {

    @Override
    public <T> void process(ApplicationContext context, ComponentProcessingContext<T> processingContext) {
        if (context.environment() instanceof ObservableApplicationEnvironment observableEnvironment) {
            if (processingContext.type().isChildOf(Observer.class)) {
                observableEnvironment.register((Class<? extends Observer>) processingContext.type().type());
            }
        }
    }

    @Override
    public int priority() {
        return ProcessingPriority.HIGH_PRECEDENCE - 1024;
    }
}
