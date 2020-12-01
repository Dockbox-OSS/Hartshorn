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
package org.dockbox.selene.core.impl.files.process;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

/**
 * Setting Processors, when used in conjunction on a {@link ninja.leaping.configurate.objectmapping.Setting} annotated
 * field within a {@link ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable} class, allow for some
 * lightweight processing of getting and setting the node value.
 *
 * <p>
 *     Implementors must ensure that there is a parameterless constructor.
 * </p>
 *
 * <p>
 *     While this is a {@link FunctionalInterface} for simpler matters, the two default methods can be overridden if
 *     required.
 * </p>
 */
@FunctionalInterface
public interface SettingProcessor {

    /**
     * Transforms the node before it's set on the {@link ninja.leaping.configurate.objectmapping.Setting}.
     *
     * @param input The input {@link ConfigurationNode}
     * @throws ObjectMappingException thrown if the node cannot be loaded.
     */
    default void onGet(ConfigurationNode input) throws ObjectMappingException {
        this.process(input);
    }

    /**
     * Transforms the node before it's set in the configuration file.
     *
     * @param output The output {@link ConfigurationNode}
     * @throws ObjectMappingException thrown if the node cannot be loaded.
     */
    default void onSet(ConfigurationNode output) throws ObjectMappingException {
        this.process(output);
    }

    /**
     * By default, processes the configuration node and transforms it into the requested form.
     *
     * @param cn The {@link ConfigurationNode}
     * @throws ObjectMappingException thrown if the node cannot be transformed.
     */
    void process(ConfigurationNode cn) throws ObjectMappingException;
}
