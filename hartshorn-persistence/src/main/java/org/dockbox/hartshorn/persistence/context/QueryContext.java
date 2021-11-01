package org.dockbox.hartshorn.persistence.context;

import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.ParameterContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.persistence.JpaRepository;
import org.dockbox.hartshorn.persistence.annotations.Query;

import java.util.LinkedList;

import javax.persistence.EntityManager;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class QueryContext {

    private final Query annotation;
    private final Object[] args;
    private final MethodContext<?, ?> method;
    private final TypeContext<?> entityType;

    @Getter private final JpaRepository<?, ?> repository;
    @Getter private final boolean transactional;
    @Getter private final boolean modifiesEntity;

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
            case JPQL -> {
                if (this.modifiesEntity || this.entityType.isVoid()) yield entityManager.createQuery(query.value());
                else yield entityManager.createQuery(query.value(), this.entityType.type());
            }
            case NATIVE -> {
                if (this.modifiesEntity || this.entityType.isVoid()) yield entityManager.createNativeQuery(query.value());
                else yield entityManager.createNativeQuery(query.value(), this.entityType.type());
            }
            default -> throw new IllegalStateException("Unexpected value: " + query.type());
        };
    }
}
