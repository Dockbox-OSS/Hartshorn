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

    enum class ServerType(val hasNMSAccess: Boolean, val minimumVersion: String, val preferredVersion: String) {
        SPONGE(true, "1.12.2-2555-7.1.0-BETA-2815", "1.12.2-2838-7.2.2-RC0"),
        MAGMA(true, "Not (yet) supported", "Not (yet) supported"),
        SPIGOT(true, "Not (yet) supported", "Not (yet) supported"),
        PAPER(true, "Not (yet) supported", "Not (yet) supported"),
        OTHER(true, "Not (yet) supported", "Not (yet) supported")
    }

}
