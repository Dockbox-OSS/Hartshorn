package org.dockbox.darwin.core.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Listener(val value: Priority = Priority.NORMAL) {
    enum class Priority(val priority: Int) {
        LAST(0x14), LATE(0xf), NORMAL(0xa), EARLY(0x5), FIRST(0x0);
    }
}
