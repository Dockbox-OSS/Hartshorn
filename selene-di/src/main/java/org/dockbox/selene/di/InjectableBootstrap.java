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

package org.dockbox.selene.di;

import com.google.inject.AbstractModule;
import com.google.inject.Binding;
import com.google.inject.ConfigurationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.ProvisionException;

import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.api.entity.annotations.DoNotEnable;
import org.dockbox.selene.di.annotations.BindingMeta;
import org.dockbox.selene.di.modules.SingleAnnotatedImplementationModule;
import org.dockbox.selene.di.modules.SingleImplementationModule;
import org.dockbox.selene.di.modules.SingleInstanceModule;
import org.dockbox.selene.di.properties.AnnotationProperty;
import org.dockbox.selene.di.properties.BindingMetaProperty;
import org.dockbox.selene.di.properties.InjectableType;
import org.dockbox.selene.di.properties.InjectorProperty;
import org.dockbox.selene.util.Reflect;
import org.dockbox.selene.util.SeleneUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import sun.misc.Unsafe;

// Reject
// Permitted
// Replace?
// Reject
// Okay

@SuppressWarnings("AbstractClassWithoutAbstractMethods")
public abstract class InjectableBootstrap {

    private static final Logger log = LoggerFactory.getLogger("selene-di");
    private static InjectableBootstrap instance;
    private final transient Set<AbstractModule> injectorModules = SeleneUtils.emptyConcurrentSet();
    private final transient Set<InjectionPoint<?>> injectionPoints = SeleneUtils.emptyConcurrentSet();
    private final transient Set<ProvisionSupplier> suppliers = SeleneUtils.emptyConcurrentSet();
    private Unsafe unsafe;
    private Injector injector;

    public static InjectableBootstrap getInstance() {
        return instance;
    }

    protected static void setInstance(InjectableBootstrap bootstrap) {
        InjectableBootstrap.instance = bootstrap;
    }

    public void registerSupplier(ProvisionSupplier supplier) {
        this.suppliers.add(supplier);
    }

    private static <T> T getRawInstance(Class<T> type, Injector injector) throws ProvisionFailure {
        try {
            Constructor<T> ctor = type.getDeclaredConstructor();
            ctor.setAccessible(true);
            T t = ctor.newInstance();
            injector.injectMembers(t);
            return t;
        }
        catch (Exception e) {
            throw new ProvisionFailure("Could not provide raw instance", e);
        }
    }

    private static <T> T getInjectedInstance(Injector injector, Class<T> type, InjectorProperty<?>... additionalProperties) {
        @SuppressWarnings("rawtypes") @Nullable
        Exceptional<Class> annotation = Bindings.value(AnnotationProperty.KEY, Class.class, additionalProperties);
        if (annotation.present() && annotation.get().isAnnotation()) {
            //noinspection unchecked
            return (T) injector.getInstance(Key.get(type, annotation.get()));
        }
        else {
            @Nullable Exceptional<BindingMeta> meta = Bindings.value(BindingMetaProperty.KEY, BindingMeta.class, additionalProperties);
            if (meta.present()) {
                return injector.getInstance(Key.get(type, meta.get()));
            }
            else {
                return injector.getInstance(type);
            }
        }
    }

    /**
     * Gets an instance of a provided {@link Class} type.
     *
     * @param <T>
     *         The type parameter for the instance to return
     * @param type
     *         The type of the instance
     * @param additionalProperties
     *         The properties to be passed into the type either during or after
     *         construction
     *
     * @return The instance, if present. Otherwise returns null
     */
    public <T> T getInstance(Class<T> type, InjectorProperty<?>... additionalProperties) {
        T typeInstance = null;

        // Modules are initially created using #getInstance here (assuming SimpleModuleManager or a
        // derivative is
        // bound to ModuleManager). Therefore it may not yet be present in the ModuleManager's registry.
        // In which
        // case `null` is (re-)assigned to typeInstance so that it can be created through the generated
        // module-specific injector.
        for (ProvisionSupplier supplier : this.suppliers) {
            if (supplier.validate(type, additionalProperties)) {
                //noinspection unchecked
                typeInstance = (T) supplier.provide(type, additionalProperties);
            }
        }

        Injector injector = this.rebuildInjector();

        // Type instance can be present if it is a module. These instances are also created using Guice
        // injectors
        // and therefore do not need late member injection here.
        typeInstance = this.createInstanceOf(type, typeInstance, injector, additionalProperties);

        if (null != typeInstance) typeInstance = this.applyInjectionPoints(type, typeInstance);

        // Enables all fields which are not annotated with @Module or @DoNotEnable
        this.enableInjectionPoints(typeInstance);

        // Inject properties if applicable
        if (typeInstance instanceof InjectableType && ((InjectableType) typeInstance).canEnable())
            ((InjectableType) typeInstance).stateEnabling(additionalProperties);

        // May be null, but we have used all possible injectors, it's up to the developer now
        return typeInstance;
    }

    private <T> void enableInjectionPoints(T typeInstance) {
        if (typeInstance == null) return;
        SeleneUtils.merge(Reflect.annotatedFields(Inject.class, typeInstance.getClass())).stream()
                .filter(field -> field.isAnnotationPresent(DoNotEnable.class))
                .filter(field -> Reflect.assignableFrom(InjectableType.class, field.getType()))
                .map(field -> {
                    try {
                        return field.get(typeInstance);
                    }
                    catch (IllegalAccessException e) {
                        log.warn("Could not access field " + field.getName() + " in " + field.getDeclaringClass().getSimpleName());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .map(fieldInstance -> (InjectableType) fieldInstance)
                .filter(InjectableType::canEnable)
                .forEach(InjectableType::stateEnabling);
    }

    @Nullable
    private <T> T createInstanceOf(Class<T> type, T typeInstance, Injector injector, InjectorProperty<?>[] additionalProperties) {
        if (null == typeInstance) {
            try {
                typeInstance = InjectableBootstrap.getInjectedInstance(injector, type, additionalProperties);
            }
            catch (ProvisionException e) {
                log.error("Could not create instance using registered injector " + injector + " for [" + type.getCanonicalName() + "]", e);
            }
            catch (ConfigurationException ce) {
                try {
                    typeInstance = InjectableBootstrap.getRawInstance(type, injector);
                }
                catch (ProvisionFailure e) {
                    typeInstance = this.getUnsafeInstance(type, injector);
                }
            }
        }
        return typeInstance;
    }

    private <T> T applyInjectionPoints(Class<T> type, T typeInstance) {
        for (InjectionPoint<?> injectionPoint : this.injectionPoints) {
            if (injectionPoint.accepts(type)) {
                try {
                    //noinspection unchecked
                    typeInstance = ((InjectionPoint<T>) injectionPoint).apply(typeInstance);
                }
                catch (ClassCastException e) {
                    log.warn("Attempted to apply injection point to incompatible type [" + type.getCanonicalName() + "]");
                }
            }
        }
        return typeInstance;
    }

    private <T> @Nullable T getUnsafeInstance(Class<T> type, Injector injector) {
        log.warn("Attempting to get instance of [" + type.getCanonicalName() + "] through Unsafe");
        try {
            @SuppressWarnings("unchecked")
            T t = (T) this.getUnsafe().allocateInstance(type);
            injector.injectMembers(t);
            return t;
        }
        catch (Exception e) {
            throw new ProvisionFailure("Could not create instance of [" + type.getCanonicalName() + "] through injected, raw or unsafe construction", e);
        }
    }

    public <T> T injectMembers(T type) {
        if (null != type) {
            this.rebuildInjector().injectMembers(type);
        }

        return type;
    }

    private Injector rebuildInjector(AbstractModule... additionalModules) {
        if (null == this.injector) {
            Collection<AbstractModule> modules = new ArrayList<>(this.injectorModules);
            modules.addAll(Arrays.stream(additionalModules).filter(Objects::nonNull).collect(Collectors.toList()));
            this.injector = Guice.createInjector(modules);
        }
        return this.injector;
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
    public <T> void bind(Class<T> contract, Class<? extends T> implementation) {
        AbstractModule localModule = new SingleImplementationModule<>(contract, implementation);
        this.injectorModules.add(localModule);
        this.injector = null; // Reset injector so it regenerates later
    }

    public <C, T extends C> void bind(Class<C> contract, T instance) {
        AbstractModule localModule = new SingleInstanceModule<>(contract, instance);
        this.injectorModules.add(localModule);
        this.injector = null; // Reset injector so it regenerates later
    }

    public <C, T extends C, A extends Annotation> void bind(Class<C> contract, Class<? extends T> implementation, Class<A> annotation) {
        AbstractModule localModule = new SingleAnnotatedImplementationModule<>(contract, implementation, annotation);
        this.injectorModules.add(localModule);
        this.injector = null; // Reset injector so it regenerates later
    }

    public void bind(InjectConfiguration moduleConfiguration) {
        this.injectorModules.add(moduleConfiguration);
    }

    public void injectAt(InjectionPoint<?> property) {
        if (null != property) this.injectionPoints.add(property);
    }

    private Unsafe getUnsafe() {
        if (null == this.unsafe) {
            try {
                Field f = Unsafe.class.getDeclaredField("theUnsafe");
                f.setAccessible(true);
                this.unsafe = (Unsafe) f.get(null);
            }
            catch (ReflectiveOperationException e) {
                throw new ProvisionFailure("Could not access 'theUnsafe' field", e);
            }
        }
        return this.unsafe;
    }

    @SuppressWarnings("unchecked")
    public <T, I extends T> Class<I> getBinding(Class<T> type) {
        for (Entry<Key<?>, Binding<?>> binding : this.getAllBindings().entrySet()) {
            if (binding.getKey().getTypeLiteral().getRawType().equals(type)) {
                return (Class<I>) binding.getValue().getKey().getTypeLiteral().getRawType();
            }
        }
        return null;
    }

    public Map<Key<?>, Binding<?>> getAllBindings() {
        Map<Key<?>, Binding<?>> bindings = SeleneUtils.emptyConcurrentMap();
        this.rebuildInjector().getAllBindings().forEach((Key<?> key, Binding<?> binding) -> {
            try {
                Class<?> keyType = binding.getKey().getTypeLiteral().getRawType();
                Class<?> providerType = binding.getProvider().get().getClass();

                if (!keyType.equals(providerType) && null != providerType)
                    bindings.put(key, binding);
            }
            catch (ProvisionException | AssertionError ignored) {
            }
        });
        return bindings;
    }
}
