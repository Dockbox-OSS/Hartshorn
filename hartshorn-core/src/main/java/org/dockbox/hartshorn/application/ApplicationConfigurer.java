/*
 * Copyright 2019-2023 the original author or authors.
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

import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.context.ContextKey;
import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.util.Configurer;

import java.util.function.Consumer;

public abstract class ApplicationConfigurer extends DefaultContext implements Configurer {

    // TODO: Determine how we want to pass this context for the application setup
    public static final ContextKey<ApplicationSetupContext> CONTEXT = ContextKey.builder(ApplicationSetupContext.class)
            .name("application-setup-context")
            .fallback(ApplicationSetupContext::new)
            .build();

    protected ApplicationSetupContext context() {
        return this.first(CONTEXT).orElseThrow(() -> new IllegalStateException("Application setup context not available"));
    }

    protected <T, R extends T> Consumer<R> bind(Class<T> type) {
        return instance -> this.context().cache().put(ComponentKey.of(type), instance);
    }
}
