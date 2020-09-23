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

package org.dockbox.darwin.core.i18n.entry

import java.util.concurrent.ConcurrentHashMap
import org.dockbox.darwin.core.i18n.common.Language
import org.dockbox.darwin.core.i18n.common.ResourceEntry
import org.dockbox.darwin.core.objects.user.Player
import org.dockbox.darwin.core.server.Server


enum class IntegratedResource(private var value: String): ResourceEntry {

    // Color formats
    COLOR_PRIMARY("b"),
    COLOR_SECONDARY("3"),
    COLOR_MINOR("7"),
    COLOR_ERROR("c"),

    // Message formats
    PREFIX("$3[] $1"),
    DEFAULT_SINGLE_MESSAGE("$3[] $1{0}"),

    // Errors
    // - Confirm command errors
    CONFIRM_INVALID_ID("$4Could not confirm command: Invalid runner ID"),
    CONFIRM_INVALID_ENTRY("$4Could not confirm command: Invalid runner entry"),
    CONFIRM_EXPIRED("$4You have no commands waiting for confirmation"),
    CONFIRM_WRONG_SOURCE("$4This command can only be used by identifiable sources (players, console)"),

    // - Generic common error message
    UNKNOWN_ERROR("$1An error occurred. {0}"),

    // Discord
    DISCORD_COMMAND_UNKNOWN("Sorry, I don't know what to do with that command!"),
    DISCORD_COMMAND_NOT_PERMITTED("You are not permitted to use that command!"),
    DISCORD_COMMAND_ERRORED("Sorry, I could not start that command. Please report this in our support channel."),

    // CommandBus Confirmation
    CONFIRM_COMMAND_MESSAGE("$1This command requires confirmation, click here to confirm running $2{0}"),
    CONFIRM_COMMAND_MESSAGE_HOVER("$1Confirm running $2{0}"),

    // Default characters
    DEFAULT_SEPARATOR(" - "),
    DEFAULT_PAGINATION_PADDING("&m$2="),

    // Sources
    UNKNOWN("Unknown"),
    NONE("None"),
    CONSOLE("Console"),

    // Warnings
    IN_ACTIVE_COOLDOWN("$4You are in cooldown! Please wait before performing this action again."),
    ;

    private var translations: MutableMap<Language, String> = ConcurrentHashMap()

    fun getValue(player: Player): String {
        return getValue(player.getLanguage())
    }

    override fun getValue(): String {
        return getValue(Server.getServer().getGlobalConfig().getDefaultLanguage())
    }

    override fun getValue(lang: Language): String {
        return if (translations.containsKey(lang)) translations[lang]!!
        else this.value
    }

    override fun setValue(value: String) {
        this.translations[Server.getServer().getGlobalConfig().getDefaultLanguage()] = value
        this.value = value
    }

    fun setLanguageValue(lang: Language, value: String) {
        this.translations[lang] = value
        if (lang == Server.getServer().getGlobalConfig().getDefaultLanguage()) this.value = value
    }

    companion object {
        fun parseColors(input: CharSequence): String {
            return IntegratedResource.NONE.parseColors(input.toString())
        }
    }

}
