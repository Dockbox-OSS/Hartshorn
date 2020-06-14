package org.dockbox.darwin.core.command

abstract class CommandValue<T>(val value: T,
                               val key: String) {

    class Argument<T>(value: T, key: String) : CommandValue<T>(value, key)
    class Flag<T>(value: T, key: String) : CommandValue<T>(value, key)

}
