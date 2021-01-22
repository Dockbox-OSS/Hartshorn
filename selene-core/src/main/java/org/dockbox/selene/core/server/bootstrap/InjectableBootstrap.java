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

package org.dockbox.selene.core.server.bootstrap;

import com.google.inject.AbstractModule;
import com.google.inject.Binding;
import com.google.inject.ConfigurationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.ProvisionException;

import org.dockbox.selene.core.annotations.proxy.Proxy;
import org.dockbox.selene.core.annotations.extension.Extension;
import org.dockbox.selene.core.extension.ExtensionContext;
import org.dockbox.selene.core.extension.ExtensionManager;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.objects.keys.Keys;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.server.SeleneInjectConfiguration;
import org.dockbox.selene.core.proxy.ProxyHandler;
import org.dockbox.selene.core.server.properties.AnnotationProperty;
import org.dockbox.selene.core.server.properties.ProxyProperty;
import org.dockbox.selene.core.server.properties.InjectableType;
import org.dockbox.selene.core.server.properties.InjectorProperty;
import org.dockbox.selene.core.util.Reflect;
import org.dockbox.selene.core.util.SeleneUtils;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import sun.misc.Unsafe;

@SuppressWarnings("AbstractClassWithoutAbstractMethods")
public abstract class InjectableBootstrap extends ProxyableBootstrap {

    private Unsafe unsafe;
    private Injector injector;
    private final transient List<AbstractModule> injectorModules = SeleneUtils.emptyConcurrentList();
    private final transient List<ProxyProperty<?, ?>> proxies = SeleneUtils.emptyConcurrentList();

    public <T> Exceptional<T> getInstanceSafe(Class<T> type, InjectorProperty<?>... additionalProperties) {
        return Exceptional.ofNullable(this.getInstance(type, additionalProperties));
    }

    public <T> Exceptional<T> getInstanceSafe(Class<T> type, Object extension, InjectorProperty<?>... additionalProperties) {
        return Exceptional.ofNullable(this.getInstance(type, extension, additionalProperties));
    }

    public <T> Exceptional<T> getInstanceSafe(Class<T> type, Class<?> extension, InjectorProperty<?>... additionalProperties) {
        return Exceptional.ofNullable(this.getInstance(type, extension, additionalProperties));
    }

    public <T> T getInstance(Class<T> type, InjectorProperty<?>... additionalProperties) {
        return this.getInstance(type, type, additionalProperties);
    }

    public <T> T getInstance(Class<T> type, Object extension, InjectorProperty<?>... additionalProperties) {
        if (null != extension) {
            return this.getInstance(type, extension.getClass(), additionalProperties);
        } else {
            return this.getInstance(type, additionalProperties);
        }
    }

    /**
     * Gets an instance of a provided {@link Class} type. If the type is annotated with {@link Extension} it is ran
     * through the {@link ExtensionManager} instance to obtain the instance. If it is not annotated as such, it is ran
     * through the instance {@link Injector} to obtain the instance based on implementation, or manually, provided
     * mappings.
     *
     * @param <T>
     *         The type parameter for the instance to return
     * @param type
     *         The type of the instance
     * @param extension
     *         The type of the extension if extension specific bindings are to be used
     * @param additionalProperties
     *         The properties to be passed into the type either during or after construction
     *
     * @return The instance, if present. Otherwise returns null
     */
    public <T> T getInstance(Class<T> type, Class<?> extension, InjectorProperty<?>... additionalProperties) {
        T typeInstance = null;

        // Extensions are initially created using #getInstance here (assuming SimpleExtensionManager or a derivative is
        // bound to ExtensionManager). Therefore it may not yet be present in the ExtensionManager's registry. In which
        // case `null` is (re-)assigned to typeInstance so that it can be created through the generated
        // extension-specific injector.
        if (type.isAnnotationPresent(Extension.class)) {
            typeInstance = this.getInstanceSafe(ExtensionManager.class)
                    .map(extensionManager -> extensionManager.getInstance(type).orNull())
                    .orNull();

        }

        // Prepare modules
        ExtensionModule extensionModule = null;
        if (extension.isAnnotationPresent(Extension.class)) {
            extensionModule = this.getExtensionModule(extension, extension.getAnnotation(Extension.class), null);
        }

        AbstractModule propertyModule = new AbstractModule() {
            @Override
            protected void configure() {
                this.bind(InjectorProperty[].class).toInstance(additionalProperties);
            }
        };

        Injector injector = this.createInjector(extensionModule, propertyModule);
        // Type instance can be present if it is a extension. These instances are also created using Guice injectors
        // and therefore do not need late member injection here.
        if (null == typeInstance) {
            try {
                typeInstance = this.getInjectedInstance(injector, type, additionalProperties);
            } catch (ProvisionException e) {
                Selene.log().error("Could not create instance using registered injector " + injector + " for [" + type + "]", e);
            } catch (ConfigurationException ce) {
                typeInstance = this
                        .getRawInstance(type, injector)
                        .orElseSupply(() -> this.getUnsafeInstance(type, injector))
                        .orNull();
            }
        }

        // Don't attempt to delegate proxy types
        if (null != typeInstance && !type.isAnnotationPresent(Proxy.class)) {
            typeInstance = this.delegate(typeInstance, additionalProperties);
        }

        // Inject properties if applicable
        if (typeInstance instanceof InjectableType && ((InjectableType) typeInstance).canEnable()) {
            ((InjectableType) typeInstance).stateEnabling(additionalProperties);
        }

        // May be null, but we have used all possible injectors, it's up to the developer now
        return typeInstance;
    }

    private <T> @Nullable T delegate(T instance, InjectorProperty<?>... additionalProperties) {
        try {
            //noinspection rawtypes
            List<ProxyProperty> proxies = Keys.getAllPropertiesOf(
                    ProxyProperty.class,
                    additionalProperties
            );
            this.proxies.stream()
                    .filter(proxy -> Reflect.isAssignableFrom(proxy.getTargetClass(), instance.getClass()))
                    .forEach(proxies::add);

            if (!proxies.isEmpty()) {
                ProxyHandler<T> handler = new ProxyHandler<>(instance);
                proxies.forEach(handler::delegate);
                return handler.proxy();
            }
        } catch (Throwable t) {
            Selene.handle(t);
        }
        return instance;
    }

    private <T> @Nullable T getUnsafeInstance(Class<T> type, Injector injector) {
        Selene.log().warn("Attempting to get instance of [" + type.getCanonicalName() + "] through Unsafe");
        try {
            @SuppressWarnings("unchecked") T t = (T) this.getUnsafe().allocateInstance(type);
            injector.injectMembers(t);
            return t;
        } catch (Exception e) {
            Selene.handle("Could not create instance of [" + type.getCanonicalName() + "] through injected, raw or unsafe construction");
        }
        return null;
    }

    private <T> Exceptional<T> getRawInstance(Class<T> type, Injector injector) {
        try {
            Constructor<T> ctor = type.getDeclaredConstructor();
            ctor.setAccessible(true);
            T t = ctor.newInstance();
            injector.injectMembers(t);
            return Exceptional.of(t);
        } catch (Exception e) {
            return Exceptional.of(e);
        }
    }

    private <T> T getInjectedInstance(Injector injector, Class<T> type, InjectorProperty<?>... additionalProperties) {
        @SuppressWarnings("rawtypes") Exceptional<Class> annotation =
                Keys.getPropertyValue(AnnotationProperty.KEY, Class.class, additionalProperties);
        if (annotation.isPresent() && annotation.get().isAnnotation()) {
            //noinspection unchecked
            return (T) injector.getInstance(Key.get(type, annotation.get()));
        } else {
            return injector.getInstance(type);
        }
    }

    /**
     * Creates a custom binding for a given contract and implementation using a custom {@link AbstractModule}. Requires
     * the implementation to extend the contract type.
     * <p>
     * The binding is created by Guice, and can be annotated using Guice supported annotations (e.g.
     * {@link com.google.inject.Singleton})
     *
     * @param <T>
     *         The type parameter of the contract
     * @param contract
     *         The class type of the contract
     * @param implementation
     *         The class type of the implementation
     */
    public <T> void bindUtility(Class<T> contract, Class<? extends T> implementation) {
        AbstractModule localModule = new AbstractModule() {
            @Override
            protected void configure() {
                super.configure();
                this.bind(contract).to(implementation);
            }
        };
        this.injectorModules.add(localModule);
        this.injector = null; // Reset injector so it regenerates later
    }


    public Injector createExtensionInjector(Object instance) {
        if (null != instance && instance.getClass().isAnnotationPresent(Extension.class)) {
            Exceptional<ExtensionContext> context = this.getInstance(ExtensionManager.class).getContext(instance.getClass());
            Extension extension;
            extension = context
                    .map(ExtensionContext::getExtension)
                    .orElseGet(() -> instance.getClass().getAnnotation(Extension.class));
            return this.createExtensionInjector(instance, extension, context.orNull());
        }
        return this.createInjector();
    }

    @SuppressWarnings("unchecked")
    private <T> ExtensionModule getExtensionModule(T instance, Extension header, ExtensionContext context) {
        ExtensionModule module = new ExtensionModule();

        if (!(null == instance || instance instanceof Class<?>)) {
            module.acceptBinding((Class<T>) instance.getClass(), instance);
            module.acceptInstance(instance);
        }
        if (null != header)
            module.acceptBinding(Extension.class, header);
        if (null != context)
            module.acceptBinding(ExtensionContext.class, context);

        return module;
    }

    public Injector createExtensionInjector(Object instance, Extension header, ExtensionContext context) {
        return this.createInjector(this.getExtensionModule(instance, header, context));
    }

    public <T> T injectMembers(T type) {
        if (null != type) {
            this.createInjector().injectMembers(type);
        }

        return type;
    }

    public Injector createInjector(AbstractModule... additionalModules) {
        if (null == this.injector) {
            Collection<AbstractModule> modules = new ArrayList<>(this.injectorModules);
            modules.addAll(Arrays.stream(additionalModules)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));
            this.injector = Guice.createInjector(modules);
        }
        return this.injector;
    }

    public <T> T injectMembers(T type, Object extensionInstance) {
        if (null != type) {
            if (null != extensionInstance && extensionInstance.getClass().isAnnotationPresent(Extension.class)) {
                this.createExtensionInjector(extensionInstance).injectMembers(type);
            } else {
                this.createInjector().injectMembers(type);
            }
        }
        return type;
    }

    public Map<Key<?>, Binding<?>> getAllBindings() {
        Map<Key<?>, Binding<?>> bindings = SeleneUtils.emptyConcurrentMap();
        this.createInjector().getAllBindings().forEach((Key<?> key, Binding<?> binding) -> {
            try {
                Class<?> keyType = binding.getKey().getTypeLiteral().getRawType();
                Class<?> providerType = binding.getProvider().get().getClass();

                if (!keyType.equals(providerType) && null != providerType)
                    bindings.put(key, binding);
            } catch (ProvisionException | AssertionError ignored) {
            }
        });
        return bindings;
    }

    public void registerGlobal(SeleneInjectConfiguration moduleConfiguration) {
        this.injectorModules.add(moduleConfiguration);
    }

    public void delegate(ProxyProperty<?, ?> property) {
        if (null != property) this.proxies.add(property);
    }

    private Unsafe getUnsafe() {
        if (null == this.unsafe) {
            try {
                Field f = Unsafe.class.getDeclaredField("theUnsafe");
                f.setAccessible(true);
                this.unsafe = (Unsafe) f.get(null);
            } catch (ReflectiveOperationException e) {
                Selene.handle(e);
            }
        }
        return this.unsafe;
    }

}
