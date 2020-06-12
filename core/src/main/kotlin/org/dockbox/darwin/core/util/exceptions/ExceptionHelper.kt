package org.dockbox.darwin.core.util.exceptions

interface ExceptionHelper {

    fun printFriendly(message: String?="No message provided", exception: Throwable?, stacktrace: Boolean?=true)

    fun printMinimal(message: String?="No message provided", exception: Throwable?, stacktrace: Boolean?=true)

}
