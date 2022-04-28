/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.application.lifecycle;

import org.dockbox.hartshorn.application.UseBootstrap;
import org.dockbox.hartshorn.component.processing.ServicePreProcessor;
import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.component.processing.AutomaticActivation;
import org.dockbox.hartshorn.application.context.ApplicationContext;

@AutomaticActivation
public class LifecycleObserverPreProcessor implements ServicePreProcessor<UseBootstrap> {

    @Override
    public Class<UseBootstrap> activator() {
        return UseBootstrap.class;
    }

    @Override
    public boolean preconditions(final ApplicationContext context, final Key<?> key) {
        return key.type().childOf(LifecycleObserver.class);
    }

    @Override
    public <T> void process(final ApplicationContext context, final Key<T> key) {
        context.environment().manager().register((LifecycleObserver) context.get(key));
    }
}