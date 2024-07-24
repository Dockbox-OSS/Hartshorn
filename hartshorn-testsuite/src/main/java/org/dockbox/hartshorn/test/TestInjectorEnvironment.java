package org.dockbox.hartshorn.test;

import org.dockbox.hartshorn.inject.ComponentKeyResolver;
import org.dockbox.hartshorn.inject.ExceptionHandler;
import org.dockbox.hartshorn.inject.InjectorConfiguration;
import org.dockbox.hartshorn.inject.InjectorEnvironment;
import org.dockbox.hartshorn.inject.LoggingExceptionHandler;
import org.dockbox.hartshorn.inject.StandardAnnotationComponentKeyResolver;
import org.dockbox.hartshorn.inject.targets.ComponentInjectionPointsResolver;
import org.dockbox.hartshorn.inject.targets.MethodsAndFieldsInjectionPointResolver;
import org.dockbox.hartshorn.proxy.ProxyOrchestrator;
import org.dockbox.hartshorn.proxy.javassist.JavassistProxyOrchestrator;
import org.dockbox.hartshorn.util.SimpleSingleElementContext;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.SupplierAdapterIntrospector;
import org.dockbox.hartshorn.util.introspect.SupplierAdapterProxyLookup;
import org.dockbox.hartshorn.util.introspect.annotations.VirtualHierarchyAnnotationLookup;
import org.dockbox.hartshorn.util.introspect.reflect.ReflectionIntrospector;

public class TestInjectorEnvironment implements InjectorEnvironment {

    private final ExceptionHandler exceptionHandler;
    private final Introspector introspector;
    private final ProxyOrchestrator proxyOrchestrator;
    private final ComponentInjectionPointsResolver injectionPointsResolver;
    private final ComponentKeyResolver componentKeyResolver;

    public TestInjectorEnvironment() {
        this.exceptionHandler = new LoggingExceptionHandler();
        this.introspector = new ReflectionIntrospector(
                new SupplierAdapterProxyLookup(this::proxyOrchestrator),
                new VirtualHierarchyAnnotationLookup()
        );
        this.proxyOrchestrator = new JavassistProxyOrchestrator(
                new SupplierAdapterIntrospector(this::introspector)
        );
        this.injectionPointsResolver = MethodsAndFieldsInjectionPointResolver.create(configuration -> {
            configuration.withJakartaAnnotations();
            configuration.withJavaxAnnotations();
        }).initialize(SimpleSingleElementContext.create(this));
        this.componentKeyResolver = new StandardAnnotationComponentKeyResolver();
    }

    @Override
    public ComponentKeyResolver componentKeyResolver() {
        return this.componentKeyResolver;
    }

    @Override
    public ComponentInjectionPointsResolver injectionPointsResolver() {
        return this.injectionPointsResolver;
    }

    @Override
    public Introspector introspector() {
        return this.introspector;
    }

    @Override
    public ProxyOrchestrator proxyOrchestrator() {
        return this.proxyOrchestrator;
    }

    @Override
    public InjectorConfiguration configuration() {
        return () -> true;
    }

    @Override
    public ExceptionHandler exceptionHandler() {
        return this.exceptionHandler;
    }
}
