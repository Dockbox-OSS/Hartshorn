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
package org.dockbox.selene.core.impl.util.files.process;

import com.google.common.collect.Maps;

import org.dockbox.selene.core.impl.util.files.util.ClassConstructor;

import java.util.Map;

public final class SettingProcessorCache {

    private SettingProcessorCache() {
    }

    private static final Map<Class<? extends SettingProcessor>, SettingProcessor> processorMap = Maps.newHashMap();

    @SuppressWarnings("unchecked")
    public static <T extends SettingProcessor> T getOrAdd(Class<T> processor, ClassConstructor<SettingProcessor> constructor)
            throws Throwable {
        if (!processorMap.containsKey(processor)) {
            processorMap.put(processor, constructor.construct(processor));
        }

        return (T) processorMap.get(processor);
    }
}
