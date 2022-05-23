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

package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.component.Component;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.util.reflect.AnnotatedElementContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.component.ComponentContainer;
import org.dockbox.hartshorn.component.ComponentUtilities;

import javax.inject.Singleton;
import javax.persistence.Entity;

public class InjectorMetaProvider implements MetaProvider {

    private final ApplicationContext context;

    public InjectorMetaProvider(final ApplicationContext context) {
        this.context = context;
    }

    @Override
    public TypedOwner lookup(final TypeContext<?> type) {
        final Result<Entity> annotated = type.annotation(Entity.class);
        if (annotated.present()) {
            return TypedOwnerImpl.of(annotated.get().name());
        }
        else {
            final Result<ComponentContainer> container = this.context.locator().container(type);
            if (container.present()) {
                final ComponentContainer service = container.get();
                if (!service.owner().isVoid()) return this.lookup(service.owner());
                else {
                    if (!"".equals(service.id())) return TypedOwnerImpl.of(service.id());
                }
            }
        }
        return TypedOwnerImpl.of(ComponentUtilities.id(this.context, type));
    }

    @Override
    public boolean singleton(final TypeContext<?> type) {
        if (type.annotation(Singleton.class).present()) return true;

        return this.context.locator()
                .container(type)
                .map(ComponentContainer::singleton)
                .or(false);
    }

    @Override
    public boolean singleton(final AnnotatedElementContext<?> element) {
        return element.annotation(Singleton.class).present();
    }

    @Override
    public boolean isComponent(final TypeContext<?> type) {
        return type.annotation(Component.class).present();
    }
}
