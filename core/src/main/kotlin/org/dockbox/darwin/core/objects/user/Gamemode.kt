package org.dockbox.darwin.core.objects.user

enum class Gamemode(private val value: Int) {
    SURVIVAL(0), CREATIVE(1), ADVENTURE(2), SPECTATOR(3), OTHER(-1);

    fun intValue(): Int {
        return value
    }
}
