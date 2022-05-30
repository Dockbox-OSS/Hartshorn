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
import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.reflect.TypeContext;

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
    public <C extends Context> Result<C> first(final Class<C> context) {
        return Result.of(this.contexts.stream()
                        .filter(c -> TypeContext.unproxy(this.applicationContext(), c).childOf(context))
                        .findFirst())
                .orElse(() -> {
                    final TypeContext<C> typeContext = TypeContext.of(context);
                    if (typeContext.annotation(AutoCreating.class).present()) {
                        this.applicationContext().log().debug("Context with key " + Key.of(context) + " does not exist in current context (" + TypeContext.of(this).name() + "), but is marked to be automatically created");
                        final C created = this.applicationContext().get(context);
                        this.add(created);
                        return created;
                    }
                    else return null;
                })
                .map(c -> (C) c);
    }

    @Override
    public <C extends Context> Result<C> first(final Class<C> context, final String name) {
        return Result.of(this.namedContexts.get(name).stream()
                        .filter(c -> TypeContext.of(c).childOf(context))
                        .findFirst())
                .orElse(() -> {
                    final TypeContext<C> typeContext = TypeContext.of(context);
                    if (typeContext.annotation(AutoCreating.class).present()) {
                        this.applicationContext().log().debug("Context with key " + Key.of(context, name) + " does not exist in current context (" + TypeContext.of(this).name() + "), but is marked to be automatically created");
                        final C created = this.applicationContext().get(context);
                        this.add(name, created);
                        return created;
                    }
                    else return null;
                })
                .map(c -> (C) c);
    }

    @Override
    public <C extends Context> Result<C> first(final Key<C> key) {
        if (key.name() == null) return this.first(key.type().type());
        else return this.first(key.type().type(), key.name().value());
    }
}
