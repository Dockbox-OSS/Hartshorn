package org.dockbox.hartshorn.test.junit;

import java.lang.reflect.Method;
import java.util.Optional;

import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.introspect.ApplicationBoundParameterLoaderContext;
import org.dockbox.hartshorn.inject.introspect.ExecutableElementContextParameterLoader;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.util.ParameterLoader;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class HartshornInjectParameterResolver implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return extensionContext.getTestMethod()
                .filter(method -> method.getParameterCount() > 0)
                .isPresent();
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        Optional<Method> testMethod = extensionContext.getTestMethod();
        if (testMethod.isEmpty()) {
            throw new ParameterResolutionException("Test method was not provided to runner");
        }

        InjectionCapableApplication application = HartshornJUnitNamespace.application(extensionContext);
        Introspector introspector = application.environment().introspector();
        MethodView<?, ?> executable = introspector.introspect(testMethod.get());

        ParameterLoader parameterLoader = new ExecutableElementContextParameterLoader(application);
        ApplicationBoundParameterLoaderContext parameterLoaderContext = new ApplicationBoundParameterLoaderContext(
                executable,
                extensionContext.getTestInstance().orElse(null),
                application
        );

        return parameterLoader.loadArgument(parameterLoaderContext, parameterContext.getIndex());
    }
}
