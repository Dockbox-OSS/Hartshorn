package org.dockbox.hartshorn.inject.processing;

import jakarta.inject.Singleton;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentContainer;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.ComponentLocator;
import org.dockbox.hartshorn.component.InstallTo;
import org.dockbox.hartshorn.component.condition.ConditionMatcher;
import org.dockbox.hartshorn.component.processing.Binds;
import org.dockbox.hartshorn.inject.ComponentInitializationException;
import org.dockbox.hartshorn.inject.binding.BindingFunction;
import org.dockbox.hartshorn.introspect.IntrospectionViewContextAdapter;
import org.dockbox.hartshorn.introspect.ViewContextAdapter;
import org.dockbox.hartshorn.proxy.ModifiableProxyManager;
import org.dockbox.hartshorn.proxy.Proxy;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.function.CheckedSupplier;
import org.dockbox.hartshorn.util.graph.BreadthFirstGraphVisitor;
import org.dockbox.hartshorn.util.graph.GraphException;
import org.dockbox.hartshorn.util.graph.GraphNode;
import org.dockbox.hartshorn.util.introspect.view.*;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DependencyContextGraphVisitor extends BreadthFirstGraphVisitor<ProviderContext> {

    private final Set<LateSingletonContext<?>> proxiesToInitialize = ConcurrentHashMap.newKeySet();
    private final ApplicationContext applicationContext;
    private final int phase;

    public DependencyContextGraphVisitor(final int phase, final ApplicationContext applicationContext) {
        this.phase = phase;
        this.applicationContext = applicationContext;
    }

    @Override
    protected boolean visit(final GraphNode<ProviderContext> node) throws GraphException {
        final ProviderContext provider = node.value();

        final ComponentKey<?> key = provider.key();
        final AnnotatedElementView element = provider.element();

        applicationContext.log().debug("Processing provider context of " + element.qualifiedName() + " for " + key + " in phase " + this.phase);

        try {
            if (element instanceof MethodView<?, ?> methodView) {
                this.process(key, methodView, applicationContext);
            } else if (element instanceof FieldView<?, ?> fieldView) {
                this.process(key, fieldView, applicationContext);
            }
        }
        catch (final ApplicationException e) {
            throw new GraphException(e);
        }
        return true;
    }

    private <R, E extends AnnotatedElementView & GenericTypeView<?>> void process(final ComponentKey<R> key, final E element,
                                                                                  final ApplicationContext applicationContext) throws ApplicationException {
        final ConditionMatcher conditionMatcher = applicationContext.get(ConditionMatcher.class);
        final Binds annotation = element.annotations().get(Binds.class).get();

        final boolean singleton = applicationContext.environment().singleton(key.type()) || element.annotations().has(Singleton.class);

        if (conditionMatcher.match(element)) {
            if (element.type().is(Class.class)) {
                this.processClassBinding(applicationContext, TypeUtils.adjustWildcards(element, View.class), key, singleton, annotation);
            }
            else {
                applicationContext.log().debug("Processing instance binding for " + element.type().name());
                this.processInstanceBinding(applicationContext, TypeUtils.adjustWildcards(element, View.class), key, singleton, annotation);
            }
        }
    }

    private <R, E extends AnnotatedElementView & GenericTypeView<R>> void processInstanceBinding(final ApplicationContext context,
                                                                                                 final E element, final ComponentKey<R> key,
                                                                                                 final boolean singleton, final Binds annotation) throws ApplicationException {
        final BindingFunction<R> function = context.bind(key).priority(annotation.priority());
        element.annotations().get(InstallTo.class).peek(installTo -> function.installTo(installTo.value()));

        final ViewContextAdapter contextAdapter = new IntrospectionViewContextAdapter(context);
        final CheckedSupplier<R> supplier = () -> contextAdapter.load(element)
                .mapError(error -> new ComponentInitializationException("Failed to obtain instance for " + element.qualifiedName(), error))
                .orNull();

        if (singleton) {
            if (annotation.lazy()) function.lazySingleton(supplier);
            else function.singleton(supplier.get());
        }
        else function.to(supplier);
    }

    private <R, E extends AnnotatedElementView & GenericTypeView<Class<R>>> void processClassBinding(final ApplicationContext context,
                                                                                                     final E element, final ComponentKey<R> key, boolean singleton,
                                                                                                     final Binds annotation) throws ApplicationException {
        final ViewContextAdapter contextAdapter = new IntrospectionViewContextAdapter(context);
        final Class<R> targetType = contextAdapter.load(element)
                .mapError(error -> new ComponentInitializationException("Failed to obtain class type for " + element.qualifiedName(), error))
                .rethrow()
                .orElseThrow(() -> new ComponentInitializationException("Failed to obtain class type for " + element.qualifiedName()));

        context.log().debug("Processing class binding for %s -> %s".formatted(key.type().getSimpleName(), targetType.getSimpleName()));

        singleton = singleton || context.environment().singleton(targetType);
        final BindingFunction<R> function = context.bind(key).priority(annotation.priority());
        element.annotations().get(InstallTo.class).peek(installTo -> function.installTo(installTo.value()));

        if (singleton) {
            final boolean lazy = annotation.lazy() || Boolean.TRUE.equals(context.get(ComponentLocator.class)
                    .container(targetType)
                    .map(ComponentContainer::lazy)
                    .orElse(false)
            );
            if (lazy) {
                function.lazySingleton(() -> context.get(targetType));
            }
            else {
                final Proxy<R> proxy = TypeUtils.adjustWildcards(context.environment().factory(targetType)
                        .proxy()
                        .mapError(ApplicationException::new)
                        .rethrow()
                        .orElseThrow(() -> new ComponentInitializationException("Could create temporary empty proxy for " + targetType.getSimpleName() + ", any errors may be displayed above.")), Proxy.class);
                this.proxiesToInitialize.add(new LateSingletonContext<>(targetType, proxy));
            }
        }
        else function.to(targetType);
    }

    public void finalizeProxies(final ApplicationContext applicationContext) throws ComponentInitializationException {
        if (this.proxiesToInitialize.isEmpty()) return;

        for (final LateSingletonContext<?> proxyContext : new ArrayList<>(this.proxiesToInitialize)) {
            this.proxiesToInitialize.remove(proxyContext);

            final Object instance = applicationContext.get(proxyContext.targetType);
            if (proxyContext.proxy.manager() instanceof ModifiableProxyManager) {
                final ModifiableProxyManager<Object> proxyManager = TypeUtils.adjustWildcards(proxyContext.proxy.manager(), ModifiableProxyManager.class);
                proxyManager.delegate(instance);
            }
            else {
                throw new ComponentInitializationException("Cannot lazily initialize singletons for non-modifiable proxy " + proxyContext.proxy);
            }
        }
    }

    private record LateSingletonContext<T>(Class<T> targetType, Proxy<T> proxy) {
    }
}
