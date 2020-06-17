package org.dockbox.darwin.core.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Disabled(
        val reason: String
)
