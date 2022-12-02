package org.dockbox.hartshorn.jpa.query;

import org.dockbox.hartshorn.jpa.query.context.AbstractJpaQueryContext;

import jakarta.persistence.Query;

public interface QueryConstructor {

    Query createJpqlQuery(String jpql, AbstractJpaQueryContext context);

    Query createNativeQuery(String nativeQuery, AbstractJpaQueryContext context);

    Query createNamedQuery(String name, AbstractJpaQueryContext context);

}
