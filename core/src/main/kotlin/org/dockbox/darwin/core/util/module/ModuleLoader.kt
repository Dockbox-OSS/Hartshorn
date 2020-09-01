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

package org.dockbox.darwin.core.util.module

import org.dockbox.darwin.core.annotations.Module
import org.dockbox.darwin.core.objects.module.ModuleCandidate
import org.dockbox.darwin.core.objects.module.ModuleInformation
import org.dockbox.darwin.core.objects.module.ModuleRegistration
import java.util.*

interface ModuleLoader {

    fun getModuleInformation(module: Class<*>): ModuleInformation
    fun getModuleInformation(module: String): ModuleInformation

    fun getModuleSource(module: Class<*>): String
    fun getModuleSource(module: String): String

    fun getModule(module: Class<*>): Module
    fun getModule(module: String): Module

    fun <I> getModuleInstance(module: Class<I>): Optional<I>

    fun getAllRegistrations(): Iterable<ModuleRegistration>
    fun getRegistration(module: Class<*>): ModuleRegistration
    fun getRegistration(module: String): ModuleRegistration

    fun loadCandidate(candidate: ModuleCandidate)
    fun loadCandidate(clazz: Class<*>)

}
