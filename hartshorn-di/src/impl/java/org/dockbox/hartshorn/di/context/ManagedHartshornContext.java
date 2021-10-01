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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.exceptions.ApplicationException;
import org.dockbox.hartshorn.di.ApplicationContextAware;
import org.dockbox.hartshorn.di.ComponentType;
import org.dockbox.hartshorn.di.InjectionPoint;
import org.dockbox.hartshorn.di.Key;
import org.dockbox.hartshorn.di.ProvisionFailure;
import org.dockbox.hartshorn.di.annotations.activate.Activator;
import org.dockbox.hartshorn.di.annotations.inject.Enable;
import org.dockbox.hartshorn.di.annotations.service.ServiceActivator;
import org.dockbox.hartshorn.di.binding.Bindings;
import org.dockbox.hartshorn.di.binding.Providers;
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.di.inject.InjectionModifier;
import org.dockbox.hartshorn.di.properties.Attribute;
import org.dockbox.hartshorn.di.properties.AttributeHolder;
import org.dockbox.hartshorn.di.services.ComponentContainer;
import org.dockbox.hartshorn.di.services.ServiceOrder;
import org.dockbox.hartshorn.di.services.ServiceProcessor;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.Getter;

public abstract class ManagedHartshornContext extends DefaultContext implements ApplicationContext {

    protected static final Logger log = LoggerFactory.getLogger(ManagedHartshornContext.class);
    protected final transient Set<InjectionPoint<?>> injectionPoints = HartshornUtils.emptyConcurrentSet();

    protected final transient Multimap<ServiceOrder, InjectionModifier<?>> injectionModifiers = ArrayListMultimap.create();
    protected final transient Multimap<ServiceOrder, ServiceProcessor<?>> serviceProcessors = ArrayListMultimap.create();

    protected final transient Map<String, Object> environmentValues = HartshornUtils.emptyConcurrentMap();

    @Getter(AccessLevel.PROTECTED) private final Activator activator;
    @Getter private final ApplicationEnvironment environment;
    private final List<Annotation> activators;

    public ManagedHartshornContext(final ApplicationContextAware application, final Class<?> activationSource, final Collection<String> prefixes) {
        this.environment = new ApplicationEnvironment(prefixes, application);
        final TypeContext<?> typeContext = TypeContext.of(activationSource);
        final Exceptional<Activator> activator = typeContext.annotation(Activator.class);
        if (activator.absent()) {
            throw new IllegalStateException("Activation source is not marked with @Activator");
        }
        this.activator = activator.get();
        this.activators = this.environment().annotationsWith(activationSource, ServiceActivator.class);
    }

    /**
     * Non-exposed method which can be used by bootstrapping services to register default activators.
     */
    public void addActivator(final Annotation annotation) {
        if (TypeContext.of(annotation.annotationType()).annotation(ServiceActivator.class).present()) {
            this.activators.add(annotation);
        }
    }

    @Override
    public void add(final InjectionPoint<?> property) {
        if (null != property) this.injectionPoints.add(property);
    }

    public abstract <T> T create(Key<T> type, T typeInstance, Attribute<?>... properties);

    public <T> T inject(final Key<T> key, T typeInstance, final Attribute<?>... properties) {
        for (final InjectionPoint<?> injectionPoint : this.injectionPoints) {
            if (injectionPoint.accepts(key.contract())) {
                try {
                    //noinspection unchecked
                    typeInstance = ((InjectionPoint<T>) injectionPoint).apply(typeInstance, key.contract(), properties);
                }
                catch (final ClassCastException e) {
                    log.warn("Attempted to apply injection point to incompatible type [" + key.contract().qualifiedName() + "]");
                }
            }
        }
        return typeInstance;
    }

    public <T> void enable(final T typeInstance) {
        if (typeInstance == null) return;
        TypeContext.unproxy(this, typeInstance).fields(Inject.class).stream()
                .filter(field -> field.type().childOf(AttributeHolder.class))
                .filter(field -> {
                    final Exceptional<Enable> enable = field.annotation(Enable.class);
                    return (enable.absent() || enable.get().value());
                })
                .map(field -> field.get(typeInstance))
                .filter(Objects::nonNull)
                .forEach(injectableType -> {
                    try {
                        Bindings.enable(injectableType);
                    }
                    catch (final ApplicationException e) {
                        throw e.runtime();
                    }
                });
    }

    public <T> T raw(final TypeContext<T> type) throws ProvisionFailure {
        return this.raw(type, true);
    }

    @Override
    public <T> T raw(final TypeContext<T> type, final boolean populate) throws ProvisionFailure {
        try {
            final Exceptional<T> instance = Providers.of(type).provide(this);
            if (instance.present()) {
                final T t = instance.get();
                if (populate) this.populate(t);
                return t;
            }
        }
        catch (final Exception e) {
            throw new ProvisionFailure("Could not provide raw instance of " + type.name(), e);
        }
        return null;
    }

    @Override
    public void add(final ServiceProcessor<?> processor) {
        this.serviceProcessors.put(processor.order(), processor);
    }

    @Override
    public void add(final InjectionModifier<?> modifier) {
        this.injectionModifiers.put(modifier.order(), modifier);
    }

    @Override
    public List<Annotation> activators() {
        return HartshornUtils.asUnmodifiableList(this.activators);
    }

    @Override
    public boolean hasActivator(final Class<? extends Annotation> activator) {
        if (TypeContext.of(activator).annotation(ServiceActivator.class).absent())
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

    @Override
    public void reset() {
        this.environment.context().reset();
    }

    protected void process(final String prefix) {
        this.locator().register(prefix);
        final Collection<ComponentContainer> containers = this.locator().containers(ComponentType.FUNCTIONAL);
        for (ServiceOrder order : ServiceOrder.values()) this.process(order, containers);
    }

    protected void process(ServiceOrder order, Collection<ComponentContainer> containers) {
        for (final ServiceProcessor<?> serviceProcessor : this.serviceProcessors.get(order)) {
            for (final ComponentContainer container : containers) {
                if (container.activators().stream().allMatch(this::hasActivator)) {
                    final TypeContext<?> service = container.type();
                    if (serviceProcessor.preconditions(this, service)) serviceProcessor.process(this, service);
                }
            }
        }
    }

    @Override
    public <T> Exceptional<T> property(String key) {
        //noinspection unchecked
        return Exceptional.of(() -> (T) this.environmentValues.getOrDefault(key, System.getenv(key)));
    }

    @Override
    public boolean hasProperty(String key) {
        return this.property(key).present();
    }

    @Override
    public <T> void property(String key, T value) {
        this.environmentValues.put(key, value);
    }

    @Override
    public void properties(Map<String, Object> tree) {
        for (Entry<String, Object> entry : tree.entrySet())
            this.property(entry.getKey(), entry.getValue());
    }
}
