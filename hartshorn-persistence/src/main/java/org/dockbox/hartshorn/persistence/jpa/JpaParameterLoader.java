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

import org.dockbox.hartshorn.core.annotations.inject.Binds;
import org.dockbox.hartshorn.core.context.element.ParameterContext;
import org.dockbox.hartshorn.core.services.parameter.ParameterLoader;
import org.dockbox.hartshorn.core.services.parameter.RuleBasedParameterLoader;
import org.dockbox.hartshorn.persistence.context.JpaParameterLoaderContext;

import javax.inject.Named;

@Binds(value = ParameterLoader.class, named = @Named("jpa_query"))
public class JpaParameterLoader extends RuleBasedParameterLoader<JpaParameterLoaderContext> {

    public JpaParameterLoader() {
        add(new JpaPaginationParameterRule());
    }

    @Override
    protected <T> T loadDefault(ParameterContext<T> parameter, int index, JpaParameterLoaderContext context, Object... args) {
        Object value = args[index];
        context.query().setParameter(parameter.name(), value);
        return super.loadDefault(parameter, index, context, args);
    }
}
