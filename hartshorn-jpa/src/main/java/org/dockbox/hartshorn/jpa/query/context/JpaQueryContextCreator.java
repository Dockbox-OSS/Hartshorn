package org.dockbox.hartshorn.jpa.query.context;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.proxy.MethodInterceptorContext;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

public interface JpaQueryContextCreator {

    // TODO:
    // - ImplicitNamedQueryContextConfigurator

    <T> Option<JpaQueryContext> create(
            final ApplicationContext applicationContext,
            final MethodInterceptorContext<T, ?> interceptorContext,
            final TypeView<?> entityType,
            final Object persistenceCapable);

    <T> boolean supports(final ComponentProcessingContext<T> processingContext, final MethodView<T, ?> method);

}
