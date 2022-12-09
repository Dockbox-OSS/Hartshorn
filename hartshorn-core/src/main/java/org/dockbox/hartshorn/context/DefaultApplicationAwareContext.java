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

package org.dockbox.hartshorn.context;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public abstract class DefaultApplicationAwareContext extends DefaultContext implements ApplicationAwareContext {

    private final ApplicationContext applicationContext;

    public DefaultApplicationAwareContext(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }

    @Override
    public <C extends Context> Option<C> first(final Class<C> context) {
        return this.first(context, applicationContext -> {
            final TypeView<C> typeView = this.applicationContext().environment().introspect(context);
            if (typeView.annotations().has(InstallIfAbsent.class)) {
                this.applicationContext().log().debug("Context with type " + context.getSimpleName() + " does not exist in current context (" + this.getClass().getSimpleName() + "), but is marked to be automatically created");
                return this.applicationContext().get(context);
            }
            else return null;
        });
    }

    @Override
    public <C extends Context> Option<C> first(final Class<C> context, final String name) {
        return this.first(context, name, applicationContext -> {
            final TypeView<C> typeView = this.applicationContext().environment().introspect(context);
            if (typeView.annotations().has(InstallIfAbsent.class)) {
                this.applicationContext().log().debug("Context with type " + context.getSimpleName() + " and name '" + name + "' does not exist in current context (" + this.getClass().getSimpleName() + "), but is marked to be automatically created");
                return this.applicationContext().get(context);
            }
            else return null;
        });
    }

    @Override
    public <C extends Context> Option<C> first(final Class<C> context, final Function<ApplicationContext, C> fallback) {
        return super.first(context).orCompute(() -> this.applyFallback(fallback));
    }

    @NotNull
    private <C extends Context> C applyFallback(final Function<ApplicationContext, C> fallback) {
        final C created = fallback.apply(this.applicationContext());
        if (created != null) this.add(created);
        return created;
    }

    @Override
    public <C extends Context> Option<C> first(final Class<C> context, final String name, final Function<ApplicationContext, C> fallback) {
        return super.first(context, name).orCompute(() -> this.applyFallback(fallback));
    }

    @Override
    public <C extends Context> Option<C> first(final ComponentKey<C> context, final Function<ApplicationContext, C> fallback) {
        return super.first(context).orCompute(() -> this.applyFallback(fallback));
    }
}
