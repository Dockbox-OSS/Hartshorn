package org.dockbox.hartshorn.persistence.context;

import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.ParameterContext;
import org.dockbox.hartshorn.persistence.JpaRepository;
import org.dockbox.hartshorn.persistence.annotations.Query;

import java.util.LinkedList;

import javax.persistence.EntityManager;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class QueryContext {

    private Query annotation;
    private Object[] args;
    private MethodContext<?, ?> method;

    @Getter private JpaRepository<?, ?> repository;
    @Getter private boolean transactional;
    @Getter private boolean modifiesEntity;

    public boolean automaticClear() {
        return this.annotation.automaticClear();
    }

    public boolean automaticFlush() {
        return this.annotation.automaticFlush();
    }

    public javax.persistence.Query query(final EntityManager entityManager) {
        final javax.persistence.Query persistenceQuery = this.persistenceQuery(entityManager, this.annotation);

        final LinkedList<ParameterContext<?>> parameters = this.method.parameters();
        for (int i = 0; i < parameters.size(); i++) {
            final ParameterContext<?> parameter = parameters.get(i);
            final Object value = this.args[i];
            persistenceQuery.setParameter(parameter.name(), value);
        }

        return persistenceQuery;
    }

    protected javax.persistence.Query persistenceQuery(final EntityManager entityManager, final Query query) throws IllegalArgumentException {
        return switch (query.type()) {
            case JPQL -> entityManager.createQuery(query.value());
            case NATIVE -> entityManager.createNativeQuery(query.value());
            default -> throw new IllegalStateException("Unexpected value: " + query.type());
        };
    }
}
