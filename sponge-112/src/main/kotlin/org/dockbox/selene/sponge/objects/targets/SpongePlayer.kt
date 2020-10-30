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

import com.boydti.fawe.FaweAPI
import com.boydti.fawe.`object`.FawePlayer
import java.util.*
import java.util.function.Function
import org.dockbox.selene.core.i18n.common.Language
import org.dockbox.selene.core.i18n.common.ResourceEntry
import org.dockbox.selene.core.i18n.entry.IntegratedResource
import org.dockbox.selene.core.objects.FieldReferenceHolder
import org.dockbox.selene.core.objects.item.Item
import org.dockbox.selene.core.objects.location.Location
import org.dockbox.selene.core.objects.location.Location.Companion.EMPTY
import org.dockbox.selene.core.objects.location.World
import org.dockbox.selene.core.objects.optional.Exceptional
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
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.property.SlotPos
import org.spongepowered.api.item.inventory.query.QueryOperationTypes
import org.spongepowered.api.service.permission.SubjectData
import org.spongepowered.api.service.user.UserStorageService
import org.spongepowered.api.util.Tristate

class SpongePlayer(uniqueId: UUID, name: String) : Player(uniqueId, name) {
    
    private val spongePlayer = FieldReferenceHolder(Sponge.getServer().getPlayer(uniqueId), Function() {
        return@Function if (it == null) Sponge.getServer().getPlayer(uniqueId)
        else Optional.empty()
    }, org.spongepowered.api.entity.living.player.Player::class.java)

    override fun isOnline(): Boolean {
        return spongePlayer.referenceExists() && spongePlayer.reference.get().isOnline
    }

    override fun getFawePlayer(): Exceptional<FawePlayer<*>> {
        return if (spongePlayer.referenceExists()) Exceptional.of(FaweAPI.wrapPlayer(spongePlayer.reference.get()))
        else Exceptional.empty()
    }

    override fun kick(message: Text) {
        if (spongePlayer.referenceExists()) spongePlayer.reference.get().kick()
    }

    override fun getGamemode(): Gamemode {
        return if (spongePlayer.referenceExists()) {
            val mode = spongePlayer.reference.get().get(Keys.GAME_MODE).orElse(GameModes.NOT_SET)
            SpongeConversionUtil.fromSponge(mode)
        } else Gamemode.OTHER
    }

    override fun setGamemode(gamemode: Gamemode) {
        if (spongePlayer.referenceExists()) spongePlayer.reference.get().offer(Keys.GAME_MODE, SpongeConversionUtil.toSponge(gamemode))
    }

    override fun getLanguage(): Language {
        return Selene.getInstance(PlayerStorageService::class.java).getLanguagePreference(this.uniqueId)
    }

    override fun setLanguage(lang: Language) {
        Selene.getInstance(PlayerStorageService::class.java).setLanguagePreference(this.uniqueId, lang)
    }

    override fun execute(command: String) {
        if (spongePlayer.referenceExists()) Sponge.getCommandManager().process(spongePlayer.reference.get(), command)
    }

    override fun send(text: Text) {
        if (spongePlayer.referenceExists()) spongePlayer.reference.get().sendMessage(SpongeConversionUtil.toSponge(text))
    }

    override fun send(text: CharSequence) {
        if (spongePlayer.referenceExists()) spongePlayer.reference.get().sendMessage(org.spongepowered.api.text.Text.of(
                IntegratedResource.parseColors(text)))
    }

    override fun sendWithPrefix(text: ResourceEntry) {
        val formattedValue = IntegratedResource.parseColors(text.getValue(getLanguage()))
        sendWithPrefix(of(formattedValue))
    }

    override fun sendWithPrefix(text: Text) {
        if (spongePlayer.referenceExists()) spongePlayer.reference.get().sendMessage(org.spongepowered.api.text.Text.of(
                SpongeConversionUtil.toSponge(IntegratedResource.PREFIX.asText()),
                SpongeConversionUtil.toSponge(text)
        )
        )
    }

    override fun sendWithPrefix(text: CharSequence) {
        if (spongePlayer.referenceExists()) spongePlayer.reference.get().sendMessage(org.spongepowered.api.text.Text.of(
                SpongeConversionUtil.toSponge(IntegratedResource.PREFIX.asText()),
                org.spongepowered.api.text.Text.of(text)
        )
        )
    }

    override fun sendPagination(pagination: Pagination) {
        if (spongePlayer.referenceExists()) {
            SpongeConversionUtil.toSponge(pagination).sendTo(spongePlayer.reference.get())
        }
    }

    override fun hasPermission(permission: String): Boolean {
        return if (spongePlayer.referenceExists()) spongePlayer.reference.get().hasPermission(permission) else Sponge.getServiceManager().provide(UserStorageService::class.java)
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
        if (spongePlayer.referenceExists()) spongePlayer.reference.get().subjectData.setPermission(SubjectData.GLOBAL_CONTEXT, permission, Tristate.fromBoolean(value)) else Sponge.getServiceManager().provide(UserStorageService::class.java)
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
        return if (spongePlayer.referenceExists()) SpongeConversionUtil.fromSponge(spongePlayer.reference.get().location) else EMPTY
    }

    override fun setLocation(location: Location) {
        if (spongePlayer.referenceExists()) {
            SpongeConversionUtil.toSponge(location).ifPresent { spongePlayer.reference.get().location = it }
        }
    }

    override fun getWorld(): World {
        // No reference refresh required as this is done by getLocation. Should never throw NPE as Location is either
        // valid or EMPTY (World instance follows this same guideline).
        return getLocation().world
    }

    @Suppress("UNCHECKED_CAST")
    override fun giveItem(item: Item<*>) {
        if (item.referenceType != ItemStack::class.java) {
            return
        }
        if (spongePlayer.referenceExists()) {
            spongePlayer.reference.ifPresent {
                it.inventory.offer(SpongeConversionUtil.toSponge(item as Item<ItemStack>))
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun giveItem(item: Item<*>, row: Int, column: Int) {
        if (item.referenceType != ItemStack::class.java) {
            return
        }
        if (spongePlayer.referenceExists()) {
            spongePlayer.reference.ifPresent {
                val inventory = it.inventory.query<Inventory>(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(column, row)))
                val spItem = SpongeConversionUtil.toSponge(item as Item<ItemStack>)
                Selene.log().info("Type: " + spItem.type)
                inventory.set(spItem)
            }
        }
    }

    override fun getItemAt(row: Int, column: Int): Exceptional<Item<*>> {
        if (spongePlayer.referenceExists()) {
            return spongePlayer.reference.map {
                val slot = it.inventory.query<Inventory>(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(column, row)))
                return@map slot.peek().map { item -> SpongeConversionUtil.fromSponge(item) }
                        .map { item -> item as Item<*> }
                        .orElse(Item.of("0"))
            }
        }
        return Exceptional.of(IllegalStateException("Player reference lost"))
    }

    override fun getInventory(): Array<Array<Item<*>>> {
        TODO("Not yet implemented")
    }
}
