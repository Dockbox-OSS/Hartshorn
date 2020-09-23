/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.sponge.objects.targets

import com.boydti.fawe.`object`.FawePlayer
import java.util.*
import org.dockbox.selene.core.i18n.common.Language
import org.dockbox.selene.core.i18n.common.ResourceEntry
import org.dockbox.selene.core.i18n.entry.IntegratedResource
import org.dockbox.selene.core.objects.location.Location
import org.dockbox.selene.core.objects.location.Location.Companion.EMPTY
import org.dockbox.selene.core.objects.location.World
import org.dockbox.selene.core.objects.user.Gamemode
import org.dockbox.selene.core.objects.user.Player
import org.dockbox.selene.core.server.Selene
import org.dockbox.selene.core.text.Text
import org.dockbox.selene.core.text.Text.of
import org.dockbox.selene.core.text.navigation.Pagination
import org.dockbox.selene.core.util.player.PlayerStorageService
import org.dockbox.selene.sponge.util.SpongeConversionUtil
import org.spongepowered.api.Sponge
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.gamemode.GameModes
import org.spongepowered.api.service.permission.SubjectData
import org.spongepowered.api.service.user.UserStorageService
import org.spongepowered.api.util.Tristate

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

    override fun getLanguage(): Language {
        return Selene.getInstance(PlayerStorageService::class.java).getLanguagePreference(this.uniqueId)
    }

    override fun setLanguage(lang: Language) {
        Selene.getInstance(PlayerStorageService::class.java).setLanguagePreference(this.uniqueId, lang)
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
        if (referenceExists()) reference!!.sendMessage(org.spongepowered.api.text.Text.of(
                IntegratedResource.parseColors(text)))
    }

    override fun sendWithPrefix(text: ResourceEntry) {
        val formattedValue = IntegratedResource.parseColors(text.getValue(getLanguage()))
        sendWithPrefix(of(formattedValue))
    }

    override fun sendWithPrefix(text: Text) {
        if (referenceExists()) reference!!.sendMessage(org.spongepowered.api.text.Text.of(
                SpongeConversionUtil.toSponge(IntegratedResource.PREFIX.asText()),
                SpongeConversionUtil.toSponge(text)
        )
        )
    }

    override fun sendWithPrefix(text: CharSequence) {
        if (referenceExists()) reference!!.sendMessage(org.spongepowered.api.text.Text.of(
                SpongeConversionUtil.toSponge(IntegratedResource.PREFIX.asText()),
                org.spongepowered.api.text.Text.of(text)
        )
        )
    }

    override fun sendPagination(pagination: Pagination) {
        if (referenceExists()) {
            SpongeConversionUtil.toSponge(pagination).sendTo(reference!!)
        }
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

    override fun send(text: ResourceEntry) {
        val formattedValue = IntegratedResource.parseColors(text.getValue(getLanguage()))
        send(of(formattedValue))
    }

    override fun getLocation(): Location {
        return if (referenceExists()) SpongeConversionUtil.fromSponge(reference!!.location) else EMPTY
    }

    override fun setLocation(location: Location) {
        if (referenceExists()) {
            SpongeConversionUtil.toSponge(location).ifPresent { reference!!.location = it }
        }
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
