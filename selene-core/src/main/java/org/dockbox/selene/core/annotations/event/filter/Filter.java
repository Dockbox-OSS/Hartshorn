/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.core.annotations.event.filter;

import org.dockbox.selene.core.objects.events.Filterable;
import org.dockbox.selene.core.util.events.FilterTypes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 Indicates whether or not to call an event based on a given filter. A filter includes the parameter to perform a filter
 on, the {@link FilterTypes} to use, and the value it should match. Only applies if a event is an instance of
 {@link Filterable}. Any event implementing {@link Filterable} can provide a list of supported params and
 {@link FilterTypes}. It is up to the event to indicate the exact filter implementation based on the information provided.
 */
@Repeatable(Filters.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Filter {
    String param();
    String value() default "";
    Class<?> target() default Void.class;
    FilterTypes type() default FilterTypes.EQUALS;
}
