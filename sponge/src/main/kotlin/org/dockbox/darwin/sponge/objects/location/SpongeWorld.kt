package org.dockbox.darwin.sponge.objects.location

import org.dockbox.darwin.core.objects.location.World
import org.spongepowered.api.Sponge
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class SpongeWorld(worldUniqueId: UUID, name: String) : World(worldUniqueId, name) {
    private val reference = ThreadLocal<Optional<org.spongepowered.api.world.World?>>()
    private fun refreshReference() {
        if (reference.get().isPresent) reference.set(Sponge.getServer().getWorld(worldUniqueId))
    }

    fun getReference(): org.spongepowered.api.world.World? {
        refreshReference()
        return reference.get().orElse(null)
    }

    private fun referenceExists(): Boolean {
        refreshReference()
        return reference.get().isPresent
    }

    override fun getPlayerCount(): Int {
        return if (referenceExists()) getReference()!!.players.size else 0
    }

    override fun unload(): Boolean {
        return if (referenceExists()) {
            val didUnload = AtomicBoolean(false)
            Sponge.getServer().getWorld(worldUniqueId).ifPresent {
                Sponge.getServer().unloadWorld(it)
                didUnload.set(true)
            }
            didUnload.get()
        } else false
    }
}
