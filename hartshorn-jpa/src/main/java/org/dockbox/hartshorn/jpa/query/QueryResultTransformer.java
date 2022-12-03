package org.dockbox.hartshorn.jpa.query;

import org.dockbox.hartshorn.util.introspect.TypeParametersIntrospector;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.Collection;

import jakarta.persistence.Query;

public interface QueryResultTransformer {

    Query transform(Query query, TypeView<?> resultType);

    static TypeView<?> createSafeTargetType(final TypeView<?> resultType) {
        if (resultType.isChildOf(Collection.class)) {
            final TypeParametersIntrospector typeParameters = resultType.typeParameters();
            if (typeParameters.count() == 1) {
                return typeParameters.at(0).get();
            }
        }
        return resultType;
    }
}
