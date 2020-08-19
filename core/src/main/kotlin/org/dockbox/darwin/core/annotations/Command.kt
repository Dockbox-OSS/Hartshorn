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
        val permissions: Array<Permission> = [Permission.GLOBAL_BYPASS]
)
