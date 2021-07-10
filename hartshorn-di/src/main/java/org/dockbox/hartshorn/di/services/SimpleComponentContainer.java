/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.di.services;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.di.ApplicationContextAware;
import org.dockbox.hartshorn.di.annotations.service.Service;
import org.dockbox.hartshorn.di.annotations.service.ServiceActivator;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.lang.annotation.Annotation;
import java.util.List;

import lombok.Getter;

@Getter
public class SimpleComponentContainer<A extends Annotation> implements ComponentContainer {

    private final ComponentMetaProcessor<A> processor;
    private final A annotation;
    private final Class<?> component;
    private final List<Class<? extends Annotation>> activators = HartshornUtils.emptyList();

    public SimpleComponentContainer(Class<?> component) {
        final Exceptional<Class<? extends Annotation>> decorator = ApplicationContextAware.instance().getContext().meta().decorator(component);
        if (decorator.absent()) throw new IllegalArgumentException("Provided component candidate has no assigned decorator");

        this.component = component;
        //noinspection unchecked
        final Class<A> decoratorType = (Class<A>) decorator.get();
        this.annotation = component.getAnnotation(decoratorType);
        this.processor = new ComponentMetaProcessor<>(decoratorType);

        if (this.annotation instanceof Service service) {
            this.activators.addAll(HartshornUtils.asList(service.activators()));
        }
    }

    @Override
    public String getId() {
        return this.get(ComponentAspect.ID);
    }

    @Override
    public String getName() {
        return this.get(ComponentAspect.NAME);
    }

    @Override
    public boolean enabled() {
        return this.get(ComponentAspect.ENABLED);
    }

    @Override
    public Class<?> getType() {
        return this.component;
    }

    @Override
    public Class<?> owner() {
        return this.get(ComponentAspect.OWNER);
    }

    @Override
    public List<Class<? extends Annotation>> activators() {
        return HartshornUtils.asUnmodifiableList(this.activators);
    }

    @Override
    public boolean hasActivator() {
        return !(this.activators.isEmpty() || (this.activators.size() == 1 && Service.class.equals(this.activators.get(0))));
    }

    @Override
    public boolean hasActivator(Class<? extends Annotation> activator) {
        if (!activator.isAnnotationPresent(ServiceActivator.class))
            throw new IllegalArgumentException("Requested activator " + activator.getSimpleName() + " is not decorated with @ServiceActivator");

        return this.activators().contains(activator);
    }

    @Override
    public boolean singleton() {
        return this.get(ComponentAspect.SINGLETON);
    }

    private <T> T get(ComponentAspect aspect) {
        return this.processor.get(aspect, this.component, this.annotation);
    }
}
