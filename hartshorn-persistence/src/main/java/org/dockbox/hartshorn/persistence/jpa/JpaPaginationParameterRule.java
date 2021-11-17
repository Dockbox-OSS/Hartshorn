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

package org.dockbox.hartshorn.persistence.jpa;

import org.dockbox.hartshorn.core.context.element.ParameterContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.services.parameter.ParameterLoaderRule;
import org.dockbox.hartshorn.persistence.context.JpaParameterLoaderContext;

import javax.persistence.Query;

public class JpaPaginationParameterRule implements ParameterLoaderRule<JpaParameterLoaderContext> {

    @Override
    public boolean accepts(ParameterContext<?> parameter, int index, JpaParameterLoaderContext context, Object... args) {
        return parameter.type().childOf(Pagination.class);
    }

    @Override
    public <T> Exceptional<T> load(ParameterContext<T> parameter, int index, JpaParameterLoaderContext context, Object... args) {
        Query query = context.query();
        Pagination pagination = (Pagination) args[index];
        if (pagination.max() != null) query.setMaxResults(pagination.max());
        if (pagination.start() != null) query.setFirstResult(pagination.start());
        return Exceptional.of((T) args[index]);
    }
}
