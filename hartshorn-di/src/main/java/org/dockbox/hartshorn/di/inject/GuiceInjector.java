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

package org.dockbox.hartshorn.di.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Binding;
import com.google.inject.Guice;
import com.google.inject.Key;
import com.google.inject.ProvisionException;
import com.google.inject.internal.LinkedBindingImpl;
import com.google.inject.spi.InstanceBinding;
import com.google.inject.spi.LinkedKeyBinding;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.domain.tuple.Tuple;
import org.dockbox.hartshorn.di.ApplicationContextAware;
import org.dockbox.hartshorn.di.InjectConfiguration;
import org.dockbox.hartshorn.di.annotations.Binds;
import org.dockbox.hartshorn.di.annotations.Combines;
import org.dockbox.hartshorn.di.annotations.Named;
import org.dockbox.hartshorn.di.annotations.Wired;
import org.dockbox.hartshorn.di.binding.BindingData;
import org.dockbox.hartshorn.di.binding.Bindings;
import org.dockbox.hartshorn.di.inject.modules.GuicePrefixScannerModule;
import org.dockbox.hartshorn.di.inject.modules.InjectConfigurationModule;
import org.dockbox.hartshorn.di.inject.modules.InstanceMetaModule;
import org.dockbox.hartshorn.di.inject.modules.InstanceModule;
import org.dockbox.hartshorn.di.inject.modules.ProvisionMetaModule;
import org.dockbox.hartshorn.di.inject.modules.ProvisionModule;
import org.dockbox.hartshorn.di.inject.modules.StaticMetaModule;
import org.dockbox.hartshorn.di.inject.modules.StaticModule;
import org.dockbox.hartshorn.di.inject.wired.ConstructorWireContext;
import org.dockbox.hartshorn.di.inject.wired.WireContext;
import org.dockbox.hartshorn.di.properties.AnnotationProperty;
import org.dockbox.hartshorn.di.properties.BindingMetaProperty;
import org.dockbox.hartshorn.di.properties.InjectorProperty;
import org.dockbox.hartshorn.util.Reflect;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Supplier;

public class GuiceInjector implements Injector {

    private final transient Set<WireContext<?, ?>> bindings = HartshornUtils.emptyConcurrentSet();
    private final transient Set<AbstractModule> modules = HartshornUtils.emptyConcurrentSet();

    private com.google.inject.Injector internalInjector;

    @Override
    public <C, T extends C, A extends Annotation> void provide(Class<C> contract, Supplier<? extends T> supplier) {
        this.modules.add(new ProvisionModule<>(contract, supplier));
        this.reset();
    }

    @Override
    public <C, T extends C, A extends Annotation> void provide(Class<C> contract, Supplier<? extends T> supplier, Named meta) {
        this.modules.add(new ProvisionMetaModule<>(contract, supplier, meta));
        this.reset();
    }

    /**
     * Creates a custom binding for a given contract and implementation using a custom {@link
     * AbstractModule}. Requires the implementation to extend the contract type.
     *
     * <p>The binding is created by Guice, and can be annotated using Guice supported annotations
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
        AbstractModule localModule = new StaticModule<>(contract, implementation);
        this.modules.add(localModule);
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
            this.modules.add(new InjectConfigurationModule(configuration, this));
            this.reset();
        }
    }

    @Override
    public void bind(String prefix) {
        Map<Key<?>, Class<?>> scannedBinders = this.scan(prefix);
        this.modules.add(new GuicePrefixScannerModule(scannedBinders));
        this.reset();
    }

    @Override
    public <T, I extends T> Exceptional<WireContext<T, I>> firstWire(Class<T> contract, InjectorProperty<Named> property) {
        for (WireContext<?, ?> binding : this.bindings) {
            if (binding.getContract().equals(contract)) {
                if (!"".equals(binding.getName())) {
                    if (property == null || !binding.getName().equals(property.getObject().value())) continue;
                }
                //noinspection unchecked
                return Exceptional.of((WireContext<T, I>) binding);
            }
        }
        return Exceptional.empty();
    }

    @Override
    public List<BindingData> getBindingData() {
        List<BindingData> data = HartshornUtils.emptyList();
        for (Entry<Key<?>, Binding<?>> entry : this.getAllBindings().entrySet()) {
            Key<?> key = entry.getKey();
            Binding<?> binding = entry.getValue();
            Key<?> bindingKey = binding.getKey();
            if (binding instanceof LinkedBindingImpl) {
                bindingKey = ((LinkedBindingImpl<?>) binding).getLinkedKey();
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
        data.sort(Comparator.comparing(d -> d.getSource().getSimpleName()));
        return data;
    }

    private Map<Key<?>, Binding<?>> getAllBindings() {
        Map<Key<?>, Binding<?>> bindings = HartshornUtils.emptyConcurrentMap();
        for (Entry<Key<?>, Binding<?>> entry : this.rebuild().getAllBindings().entrySet()) {
            Key<?> key = entry.getKey();
            Binding<?> binding = entry.getValue();
            try {
                if (binding.getProvider() instanceof ProvisionModule
                        || binding.getProvider() instanceof ProvisionMetaModule
                        || null == binding.getProvider().get()
                ) continue;

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
            for (Field field : Reflect.annotatedFields(instance.getClass(), Wired.class)) {
                Object fieldInstance = ApplicationContextAware.instance().getContext().get(field.getType());
                Reflect.set(field, instance, fieldInstance);
            }
        }
        return instance;
    }

    @Override
    public void add(WireContext<?, ?> context) {
        this.bindings.add(context);
    }

    @Override
    public void add(BeanContext<?, ?> context) {
        if (context.isSingleton()) {
            this.modules.add(new InstanceModule<>(context.getKey(), context.getProvider().get()));
        } else {
            this.modules.add(new ProvisionModule<>(context.getKey(), () -> context.getProvider().get()));
        }
        this.reset();
    }

    @Override
    public <T> T invoke(Method method) {
        return this.invoke(method, ApplicationContextAware.instance().getContext().get(method.getDeclaringClass()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T invoke(Method method, Object instance) {
        Parameter[] parameters = method.getParameters();
        Object[] invokingParameters = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            if (parameter.isAnnotationPresent(Named.class)) {
                invokingParameters[i] = ApplicationContextAware.instance().getContext().get(parameter.getType(), BindingMetaProperty.of(parameter.getAnnotation(Named.class)));
            } else {
                invokingParameters[i] = ApplicationContextAware.instance().getContext().get(parameter.getType());
            }
        }
        try {
            return (T) method.invoke(instance, invokingParameters);
        }
        catch (Throwable e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T, I extends T> Exceptional<Class<I>> type(Class<T> type) {
        for (Entry<Key<?>, Binding<?>> binding : this.getAllBindings().entrySet()) {
            if (binding.getKey().getTypeLiteral().getRawType().equals(type)) {
                if (binding.getValue() instanceof LinkedKeyBinding) {
                    return Exceptional.of(() -> (Class<I>) ((LinkedKeyBinding<?>) binding.getValue()).getLinkedKey().getTypeLiteral().getRawType());
                }
                else if (binding.getValue() instanceof InstanceBinding) {
                    return Exceptional.of(() -> (Class<I>) ((InstanceBinding<?>) binding.getValue()).getInstance().getClass());
                }
            }
        }
        return Exceptional.empty();
    }

    private Map<Key<?>, Class<?>> scan(String prefix) {
        Map<Key<?>, Class<?>> bindings = HartshornUtils.emptyMap();

        Collection<Class<?>> binders = Reflect.annotatedTypes(prefix, Binds.class);
        for (Class<?> binder : binders) {
            Binds bindAnnotation = binder.getAnnotation(Binds.class);
            this.handleBinder(bindings, binder, bindAnnotation);
        }

        Collection<Class<?>> multiBinders = Reflect.annotatedTypes(prefix, Combines.class);
        for (Class<?> binder : multiBinders) {
            Combines bindAnnotation = binder.getAnnotation(Combines.class);
            for (Binds annotation : bindAnnotation.value()) {
                this.handleBinder(bindings, binder, annotation);
            }
        }
        return bindings;
    }

    private void handleBinder(Map<Key<?>, Class<?>> bindings, Class<?> binder, Binds annotation) {
        Class<?> binds = annotation.value();

        if (Reflect.annotatedConstructors(binder, Wired.class).isEmpty()) {
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
            Collection<AbstractModule> modules = new ArrayList<>(this.modules);
            modules.addAll(this.modules);
            this.internalInjector = Guice.createInjector(modules);
        }
        return this.internalInjector;
    }

    @Override
    public <C, T extends C> void bind(Class<C> contract, Class<? extends T> implementation, Named meta) {
        AbstractModule localModule = new StaticMetaModule<>(contract, implementation, meta);
        this.modules.add(localModule);
        this.reset();
    }

    @Override
    public <C, T extends C> void bind(Class<C> contract, T instance) {
        AbstractModule localModule = new InstanceModule<>(contract, instance);
        this.modules.add(localModule);
        this.reset();
    }

    @Override
    public <C, T extends C> void bind(Class<C> contract, T instance, Named meta) {
        AbstractModule localModule = new InstanceMetaModule<>(contract, instance, meta);
        this.modules.add(localModule);
        this.reset();
    }

    @Override
    public <T, I extends T> void wire(Class<T> contract, Class<? extends I> implementation) {
        this.wire(contract, implementation, Bindings.named(""));
    }

    @Override
    public <C, T extends C> void wire(Class<C> contract, Class<? extends T> implementation, Named meta) {
        if (Reflect.annotatedConstructors(implementation, Wired.class).isEmpty())
            throw new IllegalArgumentException("Implementation should contain at least one constructor decorated with @AutoWired");

        this.bindings.add(new ConstructorWireContext<>(contract, implementation, meta.value()));
    }
}
