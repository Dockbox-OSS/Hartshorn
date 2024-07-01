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

package org.dockbox.hartshorn.component;

import org.dockbox.hartshorn.inject.provider.LifecycleType;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Objects;

/**
 * TODO: #1060 Add documentation
 *
 * @param <T> ...
 *
 * @since 0.4.1
 *
 * @author Guus Lieben
 */
public class ComponentContainerImpl<T> implements ComponentContainer<T> {

    private final Component annotation;
    private final TypeView<T> component;

    public ComponentContainerImpl(TypeView<T> component) {
        Option<Component> annotated = component.annotations().get(Component.class);
        if (annotated.absent()) {
            throw new InvalidComponentException("Provided component candidate (" + component.qualifiedName() + ") is not annotated with @" + Component.class.getSimpleName());
        }
        if (component.isAnnotation()) {
            throw new InvalidComponentException("Provided component candidate (" + component.qualifiedName() + ") is an annotation and cannot be used as a component, is it a component stereotype?");
        }

        this.component = component;
        this.annotation = annotated.get();
    }

    public Component annotation() {
        return this.annotation;
    }

    public Class<?> component() {
        return this.component.type();
    }

    @Override
    public String id() {
        String id = this.annotation().id();
        if (id != null && id.isEmpty()) {
            return ComponentUtilities.id(this, true);
        }
        return id;
    }

    @Override
    public String name() {
        String name = this.annotation().name();
        if (name != null && name.isEmpty()) {
            return ComponentUtilities.name(this, true);
        }
        return name;
    }

    @Override
    public TypeView<T> type() {
        return this.component;
    }

    @Override
    public LifecycleType lifecycle() {
        return this.annotation().lifecycle();
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
