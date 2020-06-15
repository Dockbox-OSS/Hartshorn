package org.dockbox.darwin.core.annotations

import org.dockbox.darwin.core.i18n.I18N

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class Documented(
        val value: I18N
)
