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

package org.dockbox.hartshorn.core;

import java.lang.annotation.Annotation;

/**
 * An interface which can be used to indicate an activator is required for the implementing
 * class.
 *
 * @param <A> The annotation which is used to indicate the activator.
 *
 * @author Guus Lieben
 * @since 22.1
 * @see org.dockbox.hartshorn.core.services.ComponentPreProcessor
 * @see org.dockbox.hartshorn.core.services.ComponentPostProcessor
 */
@FunctionalInterface
public interface ActivatorFiltered<A extends Annotation> {
    Class<A> activator();
}
