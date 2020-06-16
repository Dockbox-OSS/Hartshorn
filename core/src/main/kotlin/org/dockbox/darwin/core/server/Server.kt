package org.dockbox.darwin.core.server

import org.slf4j.Logger
import java.util.*

interface Server {

    fun getLog(): Logger
    fun getVersion(): String
    fun getLastUpdate(): Date
    fun getAuthors(): Array<String>
    fun except(msg: String?, vararg e: Throwable?)
    fun getServerType(): ServerType

    enum class ServerType {
        SPONGE, MAGMA, SPIGOT, PAPER, OTHER
    }

}
