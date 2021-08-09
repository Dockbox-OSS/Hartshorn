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

package org.dockbox.hartshorn.di.context;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.exceptions.ApplicationException;
import org.dockbox.hartshorn.di.ComponentType;
import org.dockbox.hartshorn.di.InjectionPoint;
import org.dockbox.hartshorn.di.ProvisionFailure;
import org.dockbox.hartshorn.di.annotations.activate.Activator;
import org.dockbox.hartshorn.di.annotations.inject.Wired;
import org.dockbox.hartshorn.di.annotations.service.ServiceActivator;
import org.dockbox.hartshorn.di.binding.Bindings;
import org.dockbox.hartshorn.di.inject.InjectionModifier;
import org.dockbox.hartshorn.di.properties.AttributeHolder;
import org.dockbox.hartshorn.di.properties.Attribute;
import org.dockbox.hartshorn.di.services.ComponentContainer;
import org.dockbox.hartshorn.di.services.ServiceProcessor;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.dockbox.hartshorn.util.Reflect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import lombok.AccessLevel;
import lombok.Getter;

public abstract class ManagedHartshornContext extends DefaultContext implements ApplicationContext {

    protected static final Logger log = LoggerFactory.getLogger(ManagedHartshornContext.class);
    protected final transient Set<InjectionPoint<?>> injectionPoints = HartshornUtils.emptyConcurrentSet();

    protected final transient Set<InjectionModifier<?>> injectionModifiers = HartshornUtils.emptyConcurrentSet();
    protected final transient Set<ServiceProcessor<?>> serviceProcessors = HartshornUtils.emptyConcurrentSet();
    private final Set<Class<?>> services = HartshornUtils.emptyConcurrentSet();

    @Getter(AccessLevel.PROTECTED) private final Activator activator;
    private final List<Annotation> activators;

    public ManagedHartshornContext(final Class<?> activationSource) {
        final Exceptional<Activator> activator = Reflect.annotation(activationSource, Activator.class);
        if (activator.absent()) {
            throw new IllegalStateException("Activation source is not marked with @Activator");
        }
        this.activator = activator.get();
        this.activators = Reflect.annotationsWith(activationSource, ServiceActivator.class);
    }

    /**
     * Non-exposed method which can be used by bootstrapping services to register default activators.
     */
    public void addActivator(final Annotation annotation) {
        if (Reflect.annotation(annotation.annotationType(), ServiceActivator.class).present())
            this.activators.add(annotation);
    }

    @Override
    public void add(final InjectionPoint<?> property) {
        if (null != property) this.injectionPoints.add(property);
    }

    public abstract <T> T create(Class<T> type, T typeInstance, Attribute<?>... properties);

    public <T> T inject(final Class<T> type, T typeInstance, final Attribute<?>... properties) {
        for (final InjectionPoint<?> injectionPoint : this.injectionPoints) {
            if (injectionPoint.accepts(type)) {
                try {
                    //noinspection unchecked
                    typeInstance = ((InjectionPoint<T>) injectionPoint).apply(typeInstance, type, properties);
                }
                catch (final ClassCastException e) {
                    log.warn("Attempted to apply injection point to incompatible type [" + type.getCanonicalName() + "]");
                }
            }
        }
        return typeInstance;
    }

    public <T> void enable(final T typeInstance) {
        if (typeInstance == null) return;
        HartshornUtils.merge(Reflect.fields(typeInstance.getClass(), Wired.class)).stream()
                .filter(field -> Reflect.annotation(field, Wired.class).get().enable())
                .filter(field -> Reflect.assigns(AttributeHolder.class, field.getType()))
                .map(field -> {
                    try {
                        // As we're enabling fields they may be accessed even if their
                        // modifier indicates otherwise.
                        field.setAccessible(true);
                        return field.get(typeInstance);
                    }
                    catch (final IllegalAccessException e) {
                        log.warn("Could not access field " + field.getName() + " in " + field.getDeclaringClass().getSimpleName());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .forEach(injectableType -> {
                    try {
                        Bindings.enable(injectableType);
                    } catch (final ApplicationException e) {
                        throw e.runtime();
                    }
                });
    }

    public <T> T raw(final Class<T> type) throws ProvisionFailure {
        return this.raw(type, true);
    }

    @Override
    public <T> T raw(final Class<T> type, final boolean populate) throws ProvisionFailure {
        try {
            final Constructor<T> ctor = type.getDeclaredConstructor();
            ctor.setAccessible(true);
            final T t = ctor.newInstance();
            if (populate) this.populate(t);
            return t;
        }
        catch (final Exception e) {
            throw new ProvisionFailure("Could not provide raw instance of " + type.getSimpleName(), e);
        }
    }

    protected void process(final String prefix) {
        this.locator().register(prefix);
        final Collection<ComponentContainer> containers = this.locator().containers(ComponentType.FUNCTIONAL);
        for (final ServiceProcessor<?> serviceProcessor : this.serviceProcessors) {
            for (final ComponentContainer container : containers) {
                if (container.activators().stream().allMatch(this::hasActivator)) {
                    final Class<?> service = container.type();
                    if (serviceProcessor.preconditions(service)) serviceProcessor.process(this, service);
                    this.services.add(service);
                }
            }
        }
    }

    @Override
    public void add(final ServiceProcessor<?> processor) {
        this.serviceProcessors.add(processor);
    }

    @Override
    public void add(final InjectionModifier<?> modifier) {
        this.injectionModifiers.add(modifier);
    }

    @Override
    public List<Annotation> activators() {
        return HartshornUtils.asUnmodifiableList(this.activators);
    }

    @Override
    public boolean hasActivator(final Class<? extends Annotation> activator) {
        if (Reflect.annotation(activator, ServiceActivator.class).absent())
            throw new IllegalArgumentException("Requested activator " + activator.getSimpleName() + " is not decorated with @ServiceActivator");

        return this.activators.stream()
                .map(Annotation::annotationType)
                .toList()
                .contains(activator);
    }

    @Override
    public <A> A activator(final Class<A> activator) {
        //noinspection unchecked
        return (A) this.activators.stream().filter(a -> a.annotationType().equals(activator)).findFirst().orElse(null);
    }

}
