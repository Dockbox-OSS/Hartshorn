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

package org.dockbox.hartshorn.core.context;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.core.ActivatorFiltered;
import org.dockbox.hartshorn.core.ArrayListMultiMap;
import org.dockbox.hartshorn.core.ComponentType;
import org.dockbox.hartshorn.core.DefaultModifiers;
import org.dockbox.hartshorn.core.Enableable;
import org.dockbox.hartshorn.core.HartshornUtils;
import org.dockbox.hartshorn.core.InjectConfiguration;
import org.dockbox.hartshorn.core.InjectionPoint;
import org.dockbox.hartshorn.core.InjectorMetaProvider;
import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.MetaProvider;
import org.dockbox.hartshorn.core.MetaProviderModifier;
import org.dockbox.hartshorn.core.Modifier;
import org.dockbox.hartshorn.core.MultiMap;
import org.dockbox.hartshorn.core.annotations.activate.Activator;
import org.dockbox.hartshorn.core.annotations.inject.Binds;
import org.dockbox.hartshorn.core.annotations.inject.Combines;
import org.dockbox.hartshorn.core.annotations.inject.Context;
import org.dockbox.hartshorn.core.annotations.inject.Enable;
import org.dockbox.hartshorn.core.annotations.service.AutomaticActivation;
import org.dockbox.hartshorn.core.annotations.service.ServiceActivator;
import org.dockbox.hartshorn.core.binding.BindingHierarchy;
import org.dockbox.hartshorn.core.binding.ContextWrappedHierarchy;
import org.dockbox.hartshorn.core.binding.NativeBindingHierarchy;
import org.dockbox.hartshorn.core.binding.Provider;
import org.dockbox.hartshorn.core.binding.Providers;
import org.dockbox.hartshorn.core.boot.ApplicationLogger;
import org.dockbox.hartshorn.core.boot.ApplicationManager;
import org.dockbox.hartshorn.core.boot.ApplicationProxier;
import org.dockbox.hartshorn.core.boot.ExceptionHandler;
import org.dockbox.hartshorn.core.boot.LifecycleObservable;
import org.dockbox.hartshorn.core.context.element.FieldContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.core.exceptions.BeanProvisionException;
import org.dockbox.hartshorn.core.inject.InjectionModifier;
import org.dockbox.hartshorn.core.inject.ProviderContext;
import org.dockbox.hartshorn.core.proxy.ProxyLookup;
import org.dockbox.hartshorn.core.services.ComponentContainer;
import org.dockbox.hartshorn.core.services.ComponentLocator;
import org.dockbox.hartshorn.core.services.ComponentProcessor;
import org.dockbox.hartshorn.core.services.ServiceImpl;
import org.dockbox.hartshorn.core.services.ServiceOrder;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.AccessLevel;
import lombok.Getter;

public class HartshornApplicationContext extends DefaultContext implements ApplicationContext {

    private static final Pattern ARGUMENTS = Pattern.compile("-H([a-zA-Z0-9\\.]+)=(.+)");

    protected final transient Set<InjectionPoint<?>> injectionPoints = HartshornUtils.emptyConcurrentSet();
    protected final transient MultiMap<ServiceOrder, InjectionModifier<?>> injectionModifiers = new ArrayListMultiMap<>();
    protected final transient MultiMap<ServiceOrder, ComponentProcessor<?>> processors = new ArrayListMultiMap<>();
    protected final transient Properties environmentValues = new Properties();

    @Getter(AccessLevel.PROTECTED) private final Activator activator;
    @Getter private final ApplicationEnvironment environment;

    private final ComponentLocator locator;
    private final Set<Modifier> modifiers;
    private final Set<Annotation> activators = HartshornUtils.emptyConcurrentSet();
    private final Map<Key<?>, Object> singletons = HartshornUtils.emptyConcurrentMap();
    private final Map<Key<?>, BindingHierarchy<?>> hierarchies = HartshornUtils.emptyConcurrentMap();
    private MetaProvider metaProvider;

    public HartshornApplicationContext(final ApplicationEnvironment environment, final Function<ApplicationContext, ComponentLocator> componentLocator,
                                       final TypeContext<?> activationSource, final Set<String> prefixes, final Set<String> args, final Set<Modifier> modifiers) {
        this.singletons.put(Key.of(ApplicationContext.class), this);
        this.environment = environment;
        final Exceptional<Activator> activator = activationSource.annotation(Activator.class);
        if (activator.absent()) {
            throw new IllegalStateException("Activation source is not marked with @Activator");
        }
        this.activator = activator.get();
        this.environment().annotationsWith(activationSource, ServiceActivator.class).forEach(this::addActivator);
        this.addActivator(new ServiceImpl());

        this.log().debug("Located %d service activators".formatted(this.activators().size()));

        this.populateArguments(args);

        this.locator = componentLocator.apply(this);
        this.modifiers = modifiers;
        this.modify(this.modifiers);

        this.bind(Key.of(ApplicationContext.class), this);
        this.bind(Key.of(MetaProvider.class), this.metaProvider);
        this.bind(Key.of(ComponentLocator.class), this.locator());
        this.bind(Key.of(ApplicationManager.class), this.environment().manager());
        this.bind(Key.of(ApplicationLogger.class), this.environment().manager());
        this.bind(Key.of(ApplicationProxier.class), this.environment().manager());
        this.bind(Key.of(ProxyLookup.class), this.environment().manager());
        this.bind(Key.of(LifecycleObservable.class), this.environment().manager());
    }

    public void addActivator(final Annotation annotation) {
        if (this.activators.contains(annotation)) return;
        final TypeContext<? extends Annotation> annotationType = TypeContext.of(annotation.annotationType());
        final Exceptional<ServiceActivator> activator = annotationType.annotation(ServiceActivator.class);
        if (activator.present()) {
            this.activators.add(annotation);
            for (final String scanPackage : activator.get().scanPackages()) {
                this.environment().prefix(scanPackage);
            }
            this.environment().annotationsWith(annotationType, ServiceActivator.class).forEach(this::addActivator);
        }
    }

    @Override
    public void add(final InjectionPoint<?> property) {
        if (null != property) this.injectionPoints.add(property);
    }

    public <T> T inject(final Key<T> key, T typeInstance) {
        for (final InjectionPoint<?> injectionPoint : this.injectionPoints) {
            if (injectionPoint.accepts(key.contract())) {
                try {
                    typeInstance = ((InjectionPoint<T>) injectionPoint).apply(typeInstance, key.contract());
                }
                catch (final ClassCastException e) {
                    this.log().warn("Attempted to apply injection point to incompatible type [" + key.contract().qualifiedName() + "]");
                }
            }
        }
        return typeInstance;
    }

    public <T> T raw(final TypeContext<T> type) throws BeanProvisionException {
        return this.raw(type, true);
    }

    @Override
    public <T> T raw(final TypeContext<T> type, final boolean populate) throws BeanProvisionException {
        try {
            final Exceptional<T> instance = Providers.of(type).provide(this);
            if (instance.present()) {
                final T t = instance.get();
                if (populate) this.populate(t);
                return t;
            }
        }
        catch (final Exception e) {
            throw new BeanProvisionException("Could not provide instance of " + type.name() + " through constructor injection", e);
        }
        return null;
    }

    @Override
    public void add(final ComponentProcessor<?> processor) {
        final ServiceOrder order = processor.order();
        this.processors.put(order, processor);
        this.log().debug("Added " + TypeContext.of(processor).name() + " for component processing at phase " + order);
    }

    @Override
    public void add(final InjectionModifier<?> modifier) {
        final ServiceOrder order = modifier.order();
        this.injectionModifiers.put(order, modifier);
        this.log().debug("Added " + TypeContext.of(modifier).name() + " for component modification at phase " + order);
    }

    @Override
    public List<Annotation> activators() {
        return HartshornUtils.asUnmodifiableList(this.activators);
    }

    @Override
    public <A> A activator(final Class<A> activator) {
        return (A) this.activators.stream().filter(a -> a.annotationType().equals(activator)).findFirst().orElse(null);
    }

    protected void process(final String prefix) {
        this.locator().register(prefix);
        final Collection<ComponentContainer> containers = this.locator().containers(ComponentType.FUNCTIONAL);
        this.log().debug("Located %d functional components in prefix %s".formatted(containers.size(), prefix));
        for (final ServiceOrder order : ServiceOrder.VALUES) this.process(order, containers);
    }

    protected void process(final ServiceOrder order, final Collection<ComponentContainer> containers) {
        for (final ComponentProcessor<?> serviceProcessor : this.processors.get(order)) {
            for (final ComponentContainer container : containers) {
                final TypeContext<?> service = container.type();
                if (serviceProcessor.processable(this, service)) {
                    this.log().debug("Processing component %s with registered processor %s in phase %s".formatted(container.id(), TypeContext.of(serviceProcessor).name(), order));
                    serviceProcessor.process(this, service);
                }
            }
        }
    }

    @Override
    public <T> Exceptional<T> property(final String key) {
        return Exceptional.of(() -> (T) this.environmentValues.getOrDefault(key, System.getenv(key)));
    }

    @Override
    public boolean hasProperty(final String key) {
        return this.property(key).present();
    }

    @Override
    public <T> void property(final String key, final T value) {
        this.environmentValues.put(key, value);
    }

    @Override
    public void properties(final Map<String, Object> tree) {
        for (final Entry<String, Object> entry : tree.entrySet())
            this.property(entry.getKey(), entry.getValue());
    }

    @Override
    public Properties properties() {
        return this.environmentValues;
    }

    private void populateArguments(final Set<String> args) {
        for (final String arg : args) {
            final Matcher matcher = ARGUMENTS.matcher(arg);
            if (matcher.find()) this.property(matcher.group(1), matcher.group(2));
        }
    }

    protected void modify(final Set<Modifier> modifiers) {
        for (final Modifier modifier : modifiers) {
            if (modifier instanceof MetaProviderModifier metaProviderModifier) {
                this.metaProvider = metaProviderModifier.provider(this);
            }
        }
        if (this.metaProvider == null) this.metaProvider = new InjectorMetaProvider(this);
    }

    @Override
    public ComponentLocator locator() {
        return this.locator;
    }

    @Override
    public MetaProvider meta() {
        return this.metaProvider;
    }

    private <C> void inHierarchy(final Key<C> key, final Consumer<BindingHierarchy<C>> consumer) {
        final BindingHierarchy<C> hierarchy = (BindingHierarchy<C>) this.hierarchies.getOrDefault(key, new NativeBindingHierarchy<>(key, this));
        consumer.accept(hierarchy);
        this.hierarchies.put(key, hierarchy);
    }

    @Override
    public <T> T get(final Key<T> key) {
        return this.get(key, true);
    }

    private <T> T get(final Key<T> key, final boolean enable) {
        if (this.singletons.containsKey(key)) return (T) this.singletons.get(key);

        T instance = this.create(key);

        // Recreating field instances ensures all fields are created through bootstrapping, allowing injection
        // points to apply correctly
        this.populate(instance);

        instance = this.inject(key, instance);

        for (final ServiceOrder order : ServiceOrder.VALUES) instance = this.modify(order, key, instance);

        // Inject properties if applicable
        if (enable) {
            try {
                this.enable(instance);
            }
            catch (final ApplicationException e) {
                ExceptionHandler.unchecked(e);
            }
        }

        final MetaProvider meta = this.meta();
        // Ensure the order of resolution is to first resolve the instance singleton state, and only after check the type state.
        // Typically, the implementation decided whether it should be a singleton, so this cuts time complexity in half.
        if (instance != null && (meta.singleton(key.contract()) || meta.singleton(TypeContext.unproxy(this, instance))))
            this.singletons.put(key, instance);

        // May be null, but we have used all possible injectors, it's up to the developer now
        return instance;
    }

    protected <T> T modify(final ServiceOrder order, final Key<T> key, T instance) {
        for (final InjectionModifier<?> serviceModifier : this.injectionModifiers.get(order)) {
            if (serviceModifier.preconditions(this, key.contract(), instance))
                instance = serviceModifier.process(this, key.contract(), instance);
        }
        return instance;
    }

    @Nullable
    public <T> T create(final Key<T> key) {
        final Exceptional<T> provision = this.provide(key).rethrowUnchecked();
        if (provision.present())
            return provision.get();

        final TypeContext<T> type = key.contract();

        final Exceptional<T> raw = Exceptional.of(() -> this.raw(type)).rethrowUnchecked();
        if (raw.present())
            return raw.get();

        if (type.isAbstract() && this.meta().isComponent(type))
            return this.environment().manager().proxy(type).rethrowUnchecked().orNull();

        return null;
    }

    @Override
    public boolean hasActivator(final Class<? extends Annotation> activator) {
        final Exceptional<ServiceActivator> annotation = TypeContext.of(activator).annotation(ServiceActivator.class);
        if (annotation.absent())
            throw new IllegalArgumentException("Requested activator " + activator.getSimpleName() + " is not decorated with @ServiceActivator");

        if (this.modifiers.contains(DefaultModifiers.ACTIVATE_ALL)) return true;
        else {
            return this.activators.stream()
                    .map(Annotation::annotationType)
                    .toList()
                    .contains(activator);
        }
    }

    public <T> Exceptional<T> provide(final Key<T> type) {
        return Exceptional.of(type)
                .map(this::hierarchy)
                .flatMap(hierarchy -> {
                    // Will continue going through each provider until a provider was successful or no other providers remain
                    for (final Provider<T> provider : hierarchy.providers()) {
                        final Exceptional<T> provided = provider.provide(this);
                        if (provided.present()) return provided;
                    }
                    return Exceptional.empty();
                });
    }

    @Override
    public void bind(final InjectConfiguration configuration) {
        this.log().debug("Activating configuration binder " + TypeContext.of(configuration).name());
        configuration.binder(this).collect(this);
    }

    @Override
    public void bind(final String prefix) {
        this.environment().prefix(prefix);

        final Collection<TypeContext<?>> binders = this.environment().types(prefix, Binds.class, false);

        for (final TypeContext<?> binder : binders) {
            final Binds bindAnnotation = binder.annotation(Binds.class).get();
            this.handleBinder(binder, bindAnnotation);
        }

        final Collection<TypeContext<?>> multiBinders = this.environment().types(prefix, Combines.class, false);
        for (final TypeContext<?> binder : multiBinders) {
            final Combines bindAnnotation = binder.annotation(Combines.class).get();
            for (final Binds annotation : bindAnnotation.value()) {
                this.handleBinder(binder, annotation);
            }
        }

        this.process(prefix);
    }

    @Override
    public <T> T populate(final T instance) {
        if (null != instance) {
            final TypeContext<T> unproxied = TypeContext.unproxy(this, instance);
            for (final FieldContext<?> field : unproxied.fields(Inject.class)) {
                Key<?> fieldKey = Key.of(field.type());
                if (field.annotation(Named.class).present()) fieldKey = Key.of(field.type(), field.annotation(Named.class).get());

                final Exceptional<Enable> enableAnnotation = field.annotation(Enable.class);
                final boolean enable = !enableAnnotation.present() || enableAnnotation.get().value();

                final Object fieldInstance = this.get(fieldKey, enable);
                field.set(instance, fieldInstance);
            }
            for (final FieldContext<?> field : unproxied.fields(Context.class)) {
                this.populateContextField(field, instance);
            }
        }
        return instance;
    }

    protected void populateContextField(final FieldContext<?> field, final Object instance) {
        final TypeContext<?> type = field.type();
        final Context annotation = field.annotation(Context.class).get();

        final Exceptional<org.dockbox.hartshorn.core.context.Context> context;
        if ("".equals(annotation.value())) {
            context = this.first((Class<org.dockbox.hartshorn.core.context.Context>) type.type());
        }
        else {
            context = this.first(annotation.value(), (Class<org.dockbox.hartshorn.core.context.Context>) type.type());
        }
        field.set(instance, context.orNull());
    }

    @Override
    public <T> void add(final ProviderContext<T> context) {
        final Key<T> key = context.key();
        this.inHierarchy(key, hierarchy -> {
            if (context.singleton()) {
                hierarchy.add(context.priority(), Providers.of(context.provider().get()));
            }
            else {
                hierarchy.add(context.priority(), Providers.of(context.provider()));
            }
        });
    }

    @Override
    public <T> T invoke(final MethodContext<T, ?> method) {
        return this.invoke((MethodContext<? extends T, Object>) method, this.get(method.parent()));
    }

    @Override
    public <T, P> T invoke(final MethodContext<T, P> method, final P instance) {
        final List<TypeContext<?>> parameters = method.parameterTypes();

        final Object[] invokingParameters = new Object[parameters.size()];
        for (int i = 0; i < parameters.size(); i++) {
            final TypeContext<?> parameter = parameters.get(i);
            final Exceptional<Named> annotation = parameter.annotation(Named.class);
            if (annotation.present()) {
                invokingParameters[i] = this.get(parameter, annotation.get());
            }
            else {
                invokingParameters[i] = this.get(parameter);
            }
        }
        try {
            return (T) method.invoke(instance, invokingParameters);
        }
        catch (final Throwable e) {
            return null;
        }
    }

    @Override
    public void enable(Object instance) throws ApplicationException {
        if (instance instanceof Enableable enableable && enableable.canEnable()) {
            enableable.enable();
        }
    }

    @Override
    public <T> BindingHierarchy<T> hierarchy(final Key<T> key) {
        final BindingHierarchy<T> hierarchy = (BindingHierarchy<T>) this.hierarchies.getOrDefault(key, new NativeBindingHierarchy<>(key, this));
        // onUpdate callback is purely so updates will still be saved even if the reference is lost
        if (hierarchy instanceof ContextWrappedHierarchy) return hierarchy;
        else return new ContextWrappedHierarchy<>(hierarchy, this, updated -> this.hierarchies.put(key, updated));
    }

    private <C, T extends C> void handleBinder(final TypeContext<T> binder, final Binds annotation) {
        final TypeContext<C> binds = TypeContext.of((Class<C>) annotation.value());

        if (binder.boundConstructors().isEmpty()) {
            this.handleScanned(binder, binds, annotation);
        }
        else {
            this.bind(Key.of(binds), binder.type());
        }
    }

    private <C> void handleScanned(final TypeContext<? extends C> binder, final TypeContext<C> binds, final Binds bindAnnotation) {
        final Named meta = bindAnnotation.named();
        final Key<C> key;
        if (!"".equals(meta.value())) {
            key = Key.of(binds, meta);
        }
        else {
            key = Key.of(binds);
        }
        this.inHierarchy(key, hierarchy -> hierarchy.add(bindAnnotation.priority(), Providers.of(binder)));
    }

    @Override
    public <C> void bind(final Key<C> contract, final Supplier<C> supplier) {
        this.inHierarchy(contract, hierarchy -> hierarchy.add(Providers.of(supplier)));
    }

    @Override
    public <C, T extends C> void bind(final Key<C> contract, final Class<? extends T> implementation) {
        final TypeContext<? extends T> context = TypeContext.of(implementation);
        if (context.defaultConstructor().present() || !context.injectConstructors().isEmpty()) {
            this.inHierarchy(contract, hierarchy -> hierarchy.add(Providers.of(context)));
        }
        if (!context.boundConstructors().isEmpty()) {
            this.inHierarchy(contract, hierarchy -> hierarchy.addNext(Providers.of(implementation)));
        }
    }

    @Override
    public <C, T extends C> void bind(final Key<C> contract, final T instance) {
        this.inHierarchy(contract, hierarchy -> hierarchy.add(Providers.of(instance)));
    }

    public void lookupActivatables() {
        for (final String prefix : this.environment().prefixContext().prefixes()) {
            this.lookup(prefix, ComponentProcessor.class, ApplicationContext::add);
            this.lookup(prefix, InjectionModifier.class, ApplicationContext::add);
        }
    }

    private <T extends ActivatorFiltered<?>> void lookup(final String prefix, final Class<T> type, final BiConsumer<ApplicationContext, T> consumer) {
        final Collection<TypeContext<? extends T>> children = this.environment().children(type);
        for (final TypeContext<? extends T> child : children) {
            if (child.isAbstract()) continue;

            if (child.annotation(AutomaticActivation.class).present()) {
                final T raw = this.raw(child, false);
                if (this.hasActivator(raw.activator()))
                    consumer.accept(this, raw);
            }
        }
    }

    @Override
    public void handle(Throwable throwable) {
        this.environment().manager().handle(throwable);
    }

    @Override
    public void handle(String message, Throwable throwable) {
        this.environment().manager().handle(message, throwable);
    }

    @Override
    public ExceptionHandler stacktraces(boolean stacktraces) {
        return this.environment().manager().stacktraces(stacktraces);
    }
}
