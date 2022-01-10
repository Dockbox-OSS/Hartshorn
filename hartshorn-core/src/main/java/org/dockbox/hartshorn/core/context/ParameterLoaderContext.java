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

package org.dockbox.hartshorn.core.context;

import org.dockbox.hartshorn.core.context.element.ExecutableElementContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ParameterLoaderContext extends DefaultContext implements ContextCarrier {
    private final ExecutableElementContext<?, ?> executable;
    private final TypeContext<?> type;
    private final Object instance;
    private final ApplicationContext applicationContext;
}
