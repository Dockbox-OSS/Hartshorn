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

package org.dockbox.darwin.core.server

import org.dockbox.darwin.core.server.config.GlobalConfig
import org.slf4j.Logger
import java.util.*

interface KServer {

    fun getLog(): Logger
    fun getVersion(): String
    fun getLastUpdate(): Date
    fun getAuthors(): Array<String>
    fun except(msg: String?, vararg e: Throwable?)
    fun getServerType(): ServerType
    fun getGlobalConfig(): GlobalConfig

    enum class ServerType(val hasNMSAccess: Boolean, val minimumVersion: String, val preferredVersion: String) {
        SPONGE(true, "1.12.2-2555-7.1.0-BETA-2815", "1.12.2-2838-7.2.2-RC0"),
        MAGMA(true, "Not (yet) supported", "Not (yet) supported"),
        SPIGOT(true, "Not (yet) supported", "Not (yet) supported"),
        PAPER(true, "Not (yet) supported", "Not (yet) supported"),
        OTHER(true, "Not (yet) supported", "Not (yet) supported")
    }

    companion object {
        private lateinit var instance: KServer

        fun getInstance(): KServer {
            return instance
        }
    }

}
