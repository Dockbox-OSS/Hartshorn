package org.dockbox.darwin.core.command

@Suppress("LeakingThis")
abstract class AbstractArgumentValue<T>(argument: SimpleCommandBus.Arguments?, protected var permission: String, key: String?) {
    private var element: T
    protected abstract fun parseArgument(argument: SimpleCommandBus.Arguments?, key: String?): T
    abstract val argument: T

    init {
        element = parseArgument(argument, key)
    }
}
