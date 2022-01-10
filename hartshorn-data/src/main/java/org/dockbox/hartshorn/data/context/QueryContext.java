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

package org.dockbox.hartshorn.data.context;

import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.services.parameter.ParameterLoader;
import org.dockbox.hartshorn.data.annotations.Query;
import org.dockbox.hartshorn.data.jpa.JpaRepository;

import javax.persistence.EntityManager;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class QueryContext {

    private ParameterLoader<JpaParameterLoaderContext> parameterLoader;

    private final Query annotation;
    private final Object[] args;
    private final MethodContext<?, ?> method;
    private final TypeContext<?> entityType;

    @Getter private final ApplicationContext applicationContext;
    @Getter private final JpaRepository<?, ?> repository;
    @Getter private final boolean modifiesEntity;

    public boolean automaticClear() {
        return this.annotation.automaticClear();
    }

    public boolean automaticFlush() {
        return this.annotation.automaticFlush();
    }

    public javax.persistence.Query query(final EntityManager entityManager) {
        final javax.persistence.Query persistenceQuery = this.persistenceQuery(entityManager, this.annotation);
        final JpaParameterLoaderContext loaderContext = new JpaParameterLoaderContext(this.method, this.entityType, null, this.applicationContext, persistenceQuery);
        this.parameterLoader().loadArguments(loaderContext, this.args);
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

    protected ParameterLoader<JpaParameterLoaderContext> parameterLoader() {
        if (this.parameterLoader == null) {
            this.parameterLoader = this.applicationContext().get(Key.of(ParameterLoader.class, "jpa_query"));
        }
        return this.parameterLoader;
    }
}
