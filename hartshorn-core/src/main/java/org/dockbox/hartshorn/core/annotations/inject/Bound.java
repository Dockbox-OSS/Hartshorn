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

package org.dockbox.hartshorn.core.annotations.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a constructor as bound. This means that the constructor arguments will
 * not be injected through regular injection, and instead requires manual provision of
 * arguments. This is implemented through {@link org.dockbox.hartshorn.core.annotations.Factory}
 * services.
 *
 * @see org.dockbox.hartshorn.core.annotations.Factory
 * @see org.dockbox.hartshorn.core.services.FactoryServicePreProcessor
 * @see org.dockbox.hartshorn.core.services.FactoryServicePostProcessor
 *
 * @author Guus Lieben
 * @since 21.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.CONSTRUCTOR)
public @interface Bound {
}
