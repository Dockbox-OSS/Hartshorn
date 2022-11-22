package org.dockbox.hartshorn.jpa.entitymanager;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.jpa.JpaRepository;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.Collection;
import java.util.List;

import jakarta.inject.Singleton;
import jakarta.persistence.Entity;

@Singleton
public class JpaEntityTypeLookup implements EntityTypeLookup {


    @Override
    public TypeView<?> entityType(final ApplicationContext applicationContext, final MethodView<?, ?> context,
                                  final Class<?> guessedType) {

        final TypeView<?> queryEntityType = applicationContext.environment().introspect(guessedType);
        if (!queryEntityType.isVoid()) return queryEntityType;

        final TypeView<?> returnType = context.genericReturnType();
        if (returnType.isVoid() && context.declaredBy().isChildOf(JpaRepository.class)) {
            final List<TypeView<?>> parameters = context.declaredBy().typeParameters().from(JpaRepository.class);
            return parameters.get(0);
        }

        if (returnType.annotations().has(Entity.class)) {
            return returnType;
        }

        if (returnType.isChildOf(Collection.class)) {
            final List<TypeView<?>> typeParameters = returnType.typeParameters().all();
            if (typeParameters.isEmpty()) {
                return null;
            }
            return typeParameters.get(0);
        }

        return null;
    }
}
