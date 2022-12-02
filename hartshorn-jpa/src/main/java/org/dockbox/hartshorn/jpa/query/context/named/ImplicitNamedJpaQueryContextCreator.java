package org.dockbox.hartshorn.jpa.query.context.named;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.jpa.JpaRepository;
import org.dockbox.hartshorn.jpa.entitymanager.EntityTypeLookup;
import org.dockbox.hartshorn.jpa.query.context.JpaQueryContext;
import org.dockbox.hartshorn.jpa.query.context.JpaQueryContextCreator;
import org.dockbox.hartshorn.jpa.query.context.application.ApplicationNamedQueriesContext;
import org.dockbox.hartshorn.jpa.query.context.application.ComponentNamedQueryContext;
import org.dockbox.hartshorn.proxy.MethodInterceptorContext;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import jakarta.persistence.Entity;

public class ImplicitNamedJpaQueryContextCreator implements JpaQueryContextCreator {

    private final EntityTypeLookup entityTypeLookup;

    public ImplicitNamedJpaQueryContextCreator(final EntityTypeLookup entityTypeLookup) {
        this.entityTypeLookup = entityTypeLookup;
    }

    @Override
    public <T> Option<JpaQueryContext> create(final ApplicationContext applicationContext,
                                              final MethodInterceptorContext<T, ?> interceptorContext,
                                              final TypeView<?> entityType, final Object persistenceCapable) {

        if (!interceptorContext.method().declaredBy().isChildOf(JpaRepository.class)) {
            applicationContext.log()
                    .warn("Implicitly creating named query context for method " + interceptorContext.method().qualifiedName() + " which is not declared by a JpaRepository. " +
                            "This is not recommended, as this may cause unexpected behaviour.");
        }
        return this.queryContext(applicationContext, interceptorContext.method())
                .map(queryContext -> new ComponentNamedJpaQueryContext(
                        queryContext,
                        interceptorContext.args(),
                        interceptorContext.method(),
                        entityType,
                        applicationContext,
                        persistenceCapable
                ));
    }

    @Override
    public <T> boolean supports(final ComponentProcessingContext<T> processingContext, final MethodView<T, ?> method) {
        return this.queryContext(processingContext.applicationContext(), method).present();
    }

    protected Option<ComponentNamedQueryContext> queryContext(final ApplicationContext applicationContext,
                                                              final MethodView<?, ?> method) {
        final TypeView<?> entityType = this.entityTypeLookup.entityType(applicationContext, method, null);
        if (entityType == null) return Option.empty();

        final String methodName = method.name();
        final ApplicationNamedQueriesContext queriesContext = applicationContext.first(ApplicationNamedQueriesContext.class)
                .get();

        return entityType.annotations().get(Entity.class)
                .map(Entity::name)
                .flatMap(entityName -> queriesContext.get(entityName + "." + methodName))
                .orComputeFlat(() -> queriesContext.get(entityType.name() + "." + method.name()))
                .orComputeFlat(() -> queriesContext.get(method.declaredBy().name() + "." + method.name()));
    }
}
