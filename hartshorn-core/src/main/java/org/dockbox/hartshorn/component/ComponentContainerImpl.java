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

package org.dockbox.hartshorn.component;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Objects;

public class ComponentContainerImpl<T> implements ComponentContainer<T> {

    private final Component annotation;
    private final Class<?> component;
    private final TypeView<T> introspectedComponent;
    private final ApplicationContext context;

    public ComponentContainerImpl(ApplicationContext context, Class<T> component) {
        this.introspectedComponent = context.environment().introspector().introspect(component);
        Option<Component> annotated = this.introspectedComponent.annotations().get(Component.class);
        if (annotated.absent()) {
            throw new InvalidComponentException("Provided component candidate (" + component.getCanonicalName() + ") is not annotated with @" + Component.class.getSimpleName());
        }

        this.component = component;
        this.annotation = annotated.get();
        this.context = context;
    }

    public Component annotation() {
        return this.annotation;
    }

    public Class<?> component() {
        return this.component;
    }

    public ApplicationContext context() {
        return this.context;
    }

    @Override
    public String id() {
        String id = this.annotation().id();
        if (id != null && id.isEmpty()) {
            return ComponentUtilities.id(this.context, this.component, true);
        }
        return id;
    }

    @Override
    public String name() {
        String name = this.annotation().name();
        if (name != null && name.isEmpty()) {
            return ComponentUtilities.name(this.context, this.component, true);
        }
        return name;
    }

    @Override
    public TypeView<T> type() {
        return this.introspectedComponent;
    }

    @Override
    public boolean singleton() {
        return this.annotation().singleton();
    }

    @Override
    public boolean lazy() {
        return this.annotation().lazy();
    }

    @Override
    public ComponentType componentType() {
        return this.annotation().type();
    }

    @Override
    public boolean permitsProxying() {
        return this.permitsProcessing() && this.annotation().permitProxying();
    }

    @Override
    public boolean permitsProcessing() {
        return this.annotation().permitProcessing();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        ComponentContainerImpl container = (ComponentContainerImpl) other;
        return this.component.equals(container.component);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.component);
    }
}
