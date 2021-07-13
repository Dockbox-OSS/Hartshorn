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

package org.dockbox.hartshorn.di.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Binding;
import com.google.inject.Guice;
import com.google.inject.Key;
import com.google.inject.ProvisionException;
import com.google.inject.spi.LinkedKeyBinding;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.domain.tuple.Tuple;
import org.dockbox.hartshorn.di.ApplicationContextAware;
import org.dockbox.hartshorn.di.InjectConfiguration;
import org.dockbox.hartshorn.di.annotations.inject.Binds;
import org.dockbox.hartshorn.di.annotations.inject.Combines;
import org.dockbox.hartshorn.di.annotations.inject.Named;
import org.dockbox.hartshorn.di.annotations.inject.Wired;
import org.dockbox.hartshorn.di.binding.BindingData;
import org.dockbox.hartshorn.di.binding.Bindings;
import org.dockbox.hartshorn.di.inject.BeanContext;
import org.dockbox.hartshorn.di.inject.Injector;
import org.dockbox.hartshorn.di.inject.KeyBinding;
import org.dockbox.hartshorn.di.inject.wired.ConstructorWireContext;
import org.dockbox.hartshorn.di.inject.wired.WireContext;
import org.dockbox.hartshorn.di.properties.AnnotationProperty;
import org.dockbox.hartshorn.di.properties.BindingMetaProperty;
import org.dockbox.hartshorn.di.properties.InjectorProperty;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.dockbox.hartshorn.util.Reflect;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Supplier;

public class GuiceInjector implements Injector {

    private final transient Set<WireContext<?, ?>> bindings = HartshornUtils.emptyConcurrentSet();
    private final transient HartshornModule module = new HartshornModule();
    private com.google.inject.Injector internalInjector;

    @Override
    public <C, T extends C, A extends Annotation> void provide(Class<C> contract, Supplier<? extends T> supplier) {
        this.module.add(contract, supplier);
        this.reset();
    }

    @Override
    public <C, T extends C, A extends Annotation> void provide(Class<C> contract, Supplier<? extends T> supplier, Named meta) {
        this.module.add(contract, meta, supplier);
        this.reset();
    }

    /**
     * Creates a custom binding for a given contract and implementation using a custom {@link
     * AbstractModule}. Requires the implementation to extend the contract type.
     *
     * <p>The binding is created by Guice, and can be annotated using Guice supported annotationsWith
     * (e.g. {@link com.google.inject.Singleton})
     *
     * @param <T>
     *         The type parameter of the contract
     * @param contract
     *         The class type of the contract
     * @param implementation
     *         The class type of the implementation
     */
    @Override
    public <C, T extends C> void bind(Class<C> contract, Class<? extends T> implementation) {
        this.module.add(contract, implementation);
        this.reset();
    }

    @Override
    public void reset() {
        this.internalInjector = null;
    }

    @Override
    public <T> Exceptional<T> get(Class<T> type, InjectorProperty<?>... additionalProperties) {
        return Exceptional.of(() -> {
            @SuppressWarnings("rawtypes") @Nullable
            Exceptional<Class> annotation = Bindings.value(AnnotationProperty.KEY, Class.class, additionalProperties);
            if (annotation.present() && annotation.get().isAnnotation()) {
                //noinspection unchecked
                return (T) this.rebuild().getInstance(Key.get(type, annotation.get()));
            }
            else {
                @Nullable Exceptional<Named> meta = Bindings.value(BindingMetaProperty.KEY, Named.class, additionalProperties);
                if (meta.present()) {
                    return this.rebuild().getInstance(Key.get(type, meta.get()));
                }
                else {
                    return this.rebuild().getInstance(type);
                }
            }
        });
    }

    @Override
    public void bind(InjectConfiguration configuration) {
        if (configuration != null) {
            this.module.add(new InjectConfigurationModule(configuration, this));
            this.reset();
        }
    }

    @Override
    public void bind(String prefix) {
        Map<Key<?>, Class<?>> scannedBinders = this.scan(prefix);
        this.module.add(scannedBinders);
        this.reset();
    }

    @Override
    public <T, I extends T> Exceptional<WireContext<T, I>> firstWire(Class<T> contract, InjectorProperty<Named> property) {
        for (WireContext<?, ?> binding : this.bindings) {
            if (binding.contract().equals(contract)) {
                if (!"".equals(binding.name())) {
                    if (property == null || !binding.name().equals(property.value().value())) continue;
                }
                //noinspection unchecked
                return Exceptional.of((WireContext<T, I>) binding);
            }
        }
        return Exceptional.empty();
    }

    @Override
    public List<BindingData> bindingData() {
        List<BindingData> data = HartshornUtils.emptyList();
        for (Entry<Key<?>, Binding<?>> entry : this.bindings().entrySet()) {
            Key<?> key = entry.getKey();
            Binding<?> binding = entry.getValue();
            Key<?> bindingKey = binding.getKey();
            if (binding instanceof LinkedKeyBinding) {
                bindingKey = ((LinkedKeyBinding<?>) binding).getLinkedKey();
            }

            Class<?> rawKey = key.getTypeLiteral().getRawType();
            Annotation annotation = key.getAnnotation();
            Class<?> rawBinding = bindingKey.getTypeLiteral().getRawType();

            if (annotation instanceof Named) {
                data.add(new BindingData(rawKey, rawBinding, (Named) annotation));
            }
            else if (annotation instanceof javax.inject.Named) {
                data.add(new BindingData(rawKey, rawBinding, Bindings.named(((javax.inject.Named) annotation).value())));
            }
            else if (annotation instanceof com.google.inject.name.Named) {
                data.add(new BindingData(rawKey, rawBinding, Bindings.named(((com.google.inject.name.Named) annotation).value())));
            }
            else {
                data.add(new BindingData(rawKey, rawBinding));
            }
        }
        data.sort(Comparator.comparing(d -> d.source().getSimpleName()));
        return data;
    }

    private Map<Key<?>, Binding<?>> bindings() {
        Map<Key<?>, Binding<?>> bindings = HartshornUtils.emptyConcurrentMap();

        for (Entry<Key<?>, Binding<?>> entry : this.rebuild().getAllBindings().entrySet()) {
            Key<?> key = entry.getKey();
            Binding<?> binding = entry.getValue();
            try {
                Class<?> keyType = binding.getKey().getTypeLiteral().getRawType();
                Class<?> providerType = binding.getProvider().get().getClass();

                if (!keyType.equals(providerType) && null != providerType)
                    bindings.put(key, binding);
            }
            catch (ProvisionException | AssertionError ignored) {
            }
        }
        return bindings;
    }

    @Override
    public <T> T populate(T instance) {
        if (null != instance) {
            this.rebuild().injectMembers(instance);
            for (Field field : Reflect.fields(instance.getClass(), Wired.class)) {
                Object fieldInstance = ApplicationContextAware.instance().context().get(field.getType());
                Reflect.set(field, instance, fieldInstance);
            }
        }
        return instance;
    }

    @Override
    public void add(WireContext<?, ?> context) {
        this.bindings.add(context);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void add(BeanContext<?, ?> context) {
        final KeyBinding<?> binding = context.key();
        Key<?> key = Key.get(binding.type());
        if (binding.annotation() != null) {
            key = key.withAnnotation(binding.annotation());
        }
        if (context.singleton()) {
            final Object instance = context.provider().get();
            this.module.add((KeyBinding<Object>) binding, () ->instance);
        } else {
            this.module.add((KeyBinding<Object>) binding, () -> context.provider().get());
        }
        this.reset();
    }

    @Override
    public <T> T invoke(Method method) {
        return this.invoke(method, ApplicationContextAware.instance().context().get(method.getDeclaringClass()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T invoke(Method method, Object instance) {
        Parameter[] parameters = method.getParameters();
        Object[] invokingParameters = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            final Exceptional<Named> annotation = Reflect.annotation(parameter, Named.class);
            if (annotation.present()) {
                invokingParameters[i] = ApplicationContextAware.instance().context().get(parameter.getType(), BindingMetaProperty.of(annotation.get()));
            } else {
                invokingParameters[i] = ApplicationContextAware.instance().context().get(parameter.getType());
            }
        }
        try {
            return (T) method.invoke(instance, invokingParameters);
        }
        catch (Throwable e) {
            return null;
        }
    }

    private Map<Key<?>, Class<?>> scan(String prefix) {
        Reflect.prefix(prefix);
        Map<Key<?>, Class<?>> bindings = HartshornUtils.emptyMap();

        Collection<Class<?>> binders = Reflect.types(Binds.class);
        for (Class<?> binder : binders) {
            Binds bindAnnotation = Reflect.annotation(binder, Binds.class).get();
            this.handleBinder(bindings, binder, bindAnnotation);
        }

        Collection<Class<?>> multiBinders = Reflect.types(Combines.class);
        for (Class<?> binder : multiBinders) {
            Combines bindAnnotation = Reflect.annotation(binder, Combines.class).get();
            for (Binds annotation : bindAnnotation.value()) {
                this.handleBinder(bindings, binder, annotation);
            }
        }
        return bindings;
    }

    private void handleBinder(Map<Key<?>, Class<?>> bindings, Class<?> binder, Binds annotation) {
        Class<?> binds = annotation.value();

        if (Reflect.constructors(binder, Wired.class).isEmpty()) {
            Entry<Key<?>, Class<?>> entry = this.handleScanned(binder, binds, annotation);
            bindings.put(entry.getKey(), entry.getValue());
        }
        else {
            //noinspection unchecked
            this.wire((Class<Object>) binds, binder);
        }
    }

    private Map.Entry<Key<?>, Class<?>> handleScanned(Class<?> binder, Class<?> binds, Binds bindAnnotation) {
        Named meta = bindAnnotation.named();
        Key<?> key;
        if (!"".equals(meta.value())) {
            key = Key.get(binds, meta);
        }
        else {
            key = Key.get(binds);
        }
        return Tuple.of(key, binder);
    }

    private com.google.inject.Injector rebuild() {
        if (null == this.internalInjector) {
            this.internalInjector = Guice.createInjector(this.module);
        }
        return this.internalInjector;
    }

    @Override
    public <C, T extends C> void bind(Class<C> contract, Class<? extends T> implementation, Named meta) {
        this.module.add(contract, meta, implementation);
        this.reset();
    }

    @Override
    public <C, T extends C> void bind(Class<C> contract, T instance) {
        this.module.add(contract, () -> instance);
        this.reset();
    }

    @Override
    public <C, T extends C> void bind(Class<C> contract, T instance, Named meta) {
        this.module.add(contract, meta, () -> instance);
        this.reset();
    }

    @Override
    public <T, I extends T> void wire(Class<T> contract, Class<? extends I> implementation) {
        this.wire(contract, implementation, Bindings.named(""));
    }

    @Override
    public <C, T extends C> void wire(Class<C> contract, Class<? extends T> implementation, Named meta) {
        if (Reflect.constructors(implementation, Wired.class).isEmpty())
            throw new IllegalArgumentException("Implementation should contain at least one constructor decorated with @AutoWired");

        this.bindings.add(new ConstructorWireContext<>(contract, implementation, meta.value()));
    }
}
