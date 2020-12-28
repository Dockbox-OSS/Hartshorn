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

/*
 * This file is part of Neutrino, licensed under the MIT License (MIT).
 */
package org.dockbox.selene.core.impl.files.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

/**
 * If a config entry is null or empty and is decorated with this, Neutrino will attempt to parse this value instead.
 *
 * <p>
 *     This annotation has another use - it's lazy loading for a field if the key is null. Rather than create a full blown object when we're likely
 *     to just blow it away anyway, we use this to only construct the default object if the config node is {@code null}.
 * </p>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Default {

    /**
     * The default value of the node. This {@link String} must be parsable by the {@link TypeSerializer} of the field.
     *
     * @return The default value.
     */
    String value();

    /**
     * If true, this value is also used to save the default if the node doesn't exist.
     *
     * @return <code>true</code> if so.
     */
    boolean saveDefaultIfNull() default false;

    /**
     * If true, the default is used if the node is an empty string.
     *
     * @return <code>true</code> to use the default if the node is empty.
     */
    boolean useDefaultIfEmpty() default false;
}
