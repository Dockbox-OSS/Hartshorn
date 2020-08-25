package org.dockbox.darwin.sponge.objects.targets

import com.boydti.fawe.`object`.FawePlayer
import org.dockbox.darwin.core.i18n.I18N
import org.dockbox.darwin.core.i18n.Languages
import org.dockbox.darwin.core.objects.location.Location
import org.dockbox.darwin.core.objects.location.Location.Companion.EMPTY
import org.dockbox.darwin.core.objects.location.World
import org.dockbox.darwin.core.objects.user.Gamemode
import org.dockbox.darwin.core.objects.user.Player
import org.dockbox.darwin.core.text.Text
import org.dockbox.darwin.core.text.Text.Companion.of
import org.dockbox.darwin.sponge.util.SpongeConversionUtil
import org.spongepowered.api.Sponge
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.gamemode.GameModes
import org.spongepowered.api.service.permission.SubjectData
import org.spongepowered.api.service.user.UserStorageService
import org.spongepowered.api.util.Tristate
import java.util.*

class SpongePlayer(uniqueId: UUID, name: String) : Player(uniqueId, name) {

    private val referencePlayer = ThreadLocal<Optional<org.spongepowered.api.entity.living.player.Player?>>()

    private fun refreshReference() {
        if (!referencePlayer.get().isPresent) referencePlayer.set(Sponge.getServer().getPlayer(uniqueId))
    }

    private val reference: org.spongepowered.api.entity.living.player.Player?
        get() {
            refreshReference()
            return referencePlayer.get().orElse(null)
        }

    private fun referenceExists(): Boolean {
        refreshReference()
        return referencePlayer.get().isPresent
    }

    override fun isOnline(): Boolean {
        return referenceExists() && reference!!.isOnline
    }

    override fun getFawePlayer(): Optional<FawePlayer<*>> {
        return Optional.empty()
    }

    override fun kick(message: Text) {
        if (referenceExists()) reference!!.kick()
    }

    override fun getGamemode(): Gamemode {
        return if (referenceExists()) {
            val mode = reference!!.get(Keys.GAME_MODE).orElse(GameModes.NOT_SET)
            SpongeConversionUtil.fromSponge(mode)
        } else Gamemode.OTHER
    }

    override fun setGamemode(gamemode: Gamemode) {
        if (referenceExists()) reference!!.offer(Keys.GAME_MODE, SpongeConversionUtil.toSponge(gamemode))
    }

    override fun getLanguage(): Languages {
        return Languages.EN_US
    }

    override fun setLanguage(lang: Languages) {
        TODO("Not yet implemented")
    }

    override fun execute(command: String) {
        refreshReference()
        if (referenceExists()) Sponge.getCommandManager().process(reference!!, command)
    }

    override fun send(text: Text) {
        refreshReference()
        if (referenceExists()) reference!!.sendMessage(SpongeConversionUtil.toSponge(text))
    }

    override fun send(text: CharSequence) {
        if (referenceExists()) reference!!.sendMessage(org.spongepowered.api.text.Text.of(text))
    }

    override fun sendWithPrefix(text: I18N) {
        sendWithPrefix(of(text.getValue(getLanguage())))
    }

    override fun sendWithPrefix(text: Text) {
        if (referenceExists()) reference!!.sendMessage(org.spongepowered.api.text.Text.of(
                SpongeConversionUtil.toSponge(I18N.PREFIX.asText()),
                SpongeConversionUtil.toSponge(text)
        )
        )
    }

    override fun sendWithPrefix(text: CharSequence) {
        if (referenceExists()) reference!!.sendMessage(org.spongepowered.api.text.Text.of(
                SpongeConversionUtil.toSponge(I18N.PREFIX.asText()),
                org.spongepowered.api.text.Text.of(text)
        )
        )
    }

    override fun hasPermission(permission: String): Boolean {
        return if (referenceExists()) reference!!.hasPermission(permission) else Sponge.getServiceManager().provide(UserStorageService::class.java)
                .map {
                    return@map it[uniqueId]
                            .map { user -> user.hasPermission(permission) }
                            .orElse(false)
                }
                .orElse(false)
    }

    override fun hasAnyPermission(vararg permissions: String): Boolean {
        return permissions.any { hasPermission(it) }
    }

    override fun hasAllPermissions(vararg permissions: String): Boolean {
        return permissions.all { hasPermission(it) }
    }

    override fun setPermission(permission: String, value: Boolean) {
        if (referenceExists()) reference!!.subjectData.setPermission(SubjectData.GLOBAL_CONTEXT, permission, Tristate.fromBoolean(value)) else Sponge.getServiceManager().provide(UserStorageService::class.java)
                .flatMap { it[uniqueId] }
                .ifPresent {
                    it.subjectData
                            .setPermission(SubjectData.GLOBAL_CONTEXT, permission, Tristate.fromBoolean(value))
                }
    }

    override fun setPermissions(value: Boolean, vararg permissions: String) {
        for (permission in permissions) setPermission(permission, value)
    }

    override fun send(text: I18N) {
        send(of(text.getValue(getLanguage())))
    }

    override fun getLocation(): Location {
        return if (referenceExists()) SpongeConversionUtil.fromSponge(reference!!.location) else EMPTY
    }

    override fun setLocation(location: Location) {
        if (referenceExists()) reference!!.location = SpongeConversionUtil.toSponge(location)
    }

    override fun getWorld(): World {
        // No reference refresh required as this is done by getLocation. Should never throw NPE as Location is either
        // valid or EMPTY (World instance follows this same guideline).
        return getLocation().world
    }

    init {
        referencePlayer.set(Sponge.getServer().getPlayer(uniqueId))
    }
}
