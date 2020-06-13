package org.dockbox.darwin.core.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Module (
        val id: String,
        val name: String,
        val version: String = "unknown",
        val description: String,
        val url: String = "none",
        val authors: Array<String>
        )
