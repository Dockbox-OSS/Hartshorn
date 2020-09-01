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

package org.dockbox.darwin.core.annotations

import org.dockbox.darwin.core.i18n.Permission

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class Command (
        val aliases: Array<String>,
        val usage: String,
        val minArguments: Int = 0,
        val maxArguments: Int = -1,
        val shortFlags: String = "",
        val valueFlags: String = "",
        val anyFlags: Boolean = false,
        val context: String,
        val permission: Permission = Permission.GLOBAL_BYPASS,
        val permissionKey: String = ""
)
