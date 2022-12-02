package org.dockbox.hartshorn.jpa.query.context;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.jpa.query.QueryExecuteType;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

public interface JpaQueryContext {


    ApplicationContext applicationContext();

    Object persistenceCapable();

    MethodView<?, ?> method();

    TypeView<?> entityType();

    EntityManager entityManager();

    boolean automaticClear();

    boolean automaticFlush();

    Query query();

    QueryExecuteType queryType();
}
