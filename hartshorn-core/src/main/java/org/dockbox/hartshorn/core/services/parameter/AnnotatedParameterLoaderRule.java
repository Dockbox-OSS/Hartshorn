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

package org.dockbox.hartshorn.core.services.parameter;

import org.dockbox.hartshorn.core.context.ParameterLoaderContext;
import org.dockbox.hartshorn.core.context.element.ParameterContext;

import java.lang.annotation.Annotation;

public abstract class AnnotatedParameterLoaderRule<A extends Annotation, C extends ParameterLoaderContext> implements ParameterLoaderRule<C>{

    protected abstract Class<A> annotation();

    @Override
    public boolean accepts(final ParameterContext<?> parameter, int index, final C context, final Object... args) {
        return parameter.annotation(this.annotation()).present();
    }
}
