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
package org.dockbox.selene.core.impl.util.files.annotations;

import org.dockbox.selene.core.impl.util.files.process.SettingProcessor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that, when this annotation is present alongside a {@link ninja.leaping.configurate.objectmapping.Setting}
 * annotation, some transformation of the node should occur at the following moments:
 *
 * <ul>
 *     <li>before the value of the field is set from the {@link ninja.leaping.configurate.ConfigurationNode}</li>
 *     <li>after the value of the field is read from the {@link ninja.leaping.configurate.ConfigurationNode}, but just
 *     before it is written to the file</li>
 * </ul>
 *
 * <p>
 *     {@link SettingProcessor}s are used to take the {@link ninja.leaping.configurate.ConfigurationNode}s and alter
 *     them directly before the final value for the task is read.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface ProcessSetting {

    /**
     * The {@link Class}es that represent the {@link SettingProcessor}s to run on the
     * {@link ninja.leaping.configurate.ConfigurationNode} that populates this field. The processors are executed in
     * the order provided here. They must all have parameterless constructors.
     *
     * @return The {@link Class}es of the {@link SettingProcessor}s to use.
     */
    Class<? extends SettingProcessor>[] value();
}
