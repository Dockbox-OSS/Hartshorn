/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

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
