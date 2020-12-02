/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.core.i18n.entry;

import org.dockbox.selene.core.SeleneUtils;
import org.dockbox.selene.core.i18n.common.Language;
import org.dockbox.selene.core.i18n.common.ResourceEntry;
import org.dockbox.selene.core.objects.user.Player;
import org.dockbox.selene.core.server.Selene;

import java.util.Map;

public enum IntegratedResource implements ResourceEntry {
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
    CONFIRM_COMMAND_MESSAGE("$1This command requires confirmation, click $2[here] $1to confirm"),
    CONFIRM_COMMAND_MESSAGE_HOVER("$1Confirm running command"),

    // Default characters
    DEFAULT_SEPARATOR(" - "),
    DEFAULT_PAGINATION_PADDING("&m$2="),

    // Sources
    UNKNOWN("Unknown"),
    NONE("None"),
    CONSOLE("Console"),

    // Warnings
    IN_ACTIVE_COOLDOWN("$4You are in cooldown! Please wait before performing this action again."),

    // Enchantments
    AQUA_AFFINITY("Aqua Affinity"),
    BANE_OF_ARTHROPODS("Bane Of Arthropods"),
    BINDING_CURSE("Binding Curse"),
    BLAST_PROTECTION("Blast Protection"),
    DEPTH_STRIDER("Depth Strider"),
    EFFICIENCY("Efficiency"),
    FEATHER_FALLING("Feather Falling"),
    FIRE_ASPECT("Fire Aspect"),
    FIRE_PROTECTION("Fire Protection"),
    FLAME("Flame"),
    FORTUNE("Fortune"),
    FROST_WALKER("Frost Walker"),
    INFINITY("Infinity"),
    KNOCKBACK("Knockback"),
    LOOTING("Looting"),
    LUCK_OF_THE_SEA("Luck Of The Sea"),
    LURE("Lure"),
    MENDING("Mending"),
    POWER("Power"),
    PROJECTILE_PROTECTION("Projectile Protection"),
    PROTECTION("Protection"),
    PUNCH("Punch"),
    RESPIRATION("Respiration"),
    SHARPNESS("Sharpness"),
    SILK_TOUCH("Silk Touch"),
    SMITE("Smite"),
    SWEEPING("Sweeping"),
    THORNS("Thorns"),
    UNBREAKING("Unbreaking"),
    VANISHING_CURSE("Vanishing Curse"),
    ;

    private String value;
    private final Map<Language, String> translations = SeleneUtils.emptyConcurrentMap();

    IntegratedResource(String value) {
        this.value = value;
    }

    public String getValue(Player player) {
        return this.getValue(player.getLanguage());
    }

    @Override
    public String getValue() {
        return this.getValue(Selene.getServer().getGlobalConfig().getDefaultLanguage());
    }

    @Override
    public String getValue(Language lang) {
        if (this.translations.containsKey(lang)) return this.translations.get(lang);
        return this.value;
    }

    @Override
    public void setValue(String value) {
        this.translations.put(Selene.getServer().getGlobalConfig().getDefaultLanguage(), value);
        this.value = value;
    }

    public void setLanguageValue(Language lang, String value) {
        this.translations.put(lang, value);
        if (lang == Selene.getServer().getGlobalConfig().getDefaultLanguage()) this.value = value;
    }

    public static String parse(CharSequence input) {
        return NONE.parseColors(input.toString());
    }

}
