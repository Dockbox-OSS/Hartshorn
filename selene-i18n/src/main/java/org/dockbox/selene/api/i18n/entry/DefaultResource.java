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

package org.dockbox.selene.api.i18n.entry;

import org.dockbox.selene.api.Selene;
import org.dockbox.selene.api.i18n.MessageReceiver;
import org.dockbox.selene.api.i18n.annotations.Resources;
import org.dockbox.selene.api.i18n.common.Language;
import org.dockbox.selene.api.i18n.common.ResourceEntry;
import org.dockbox.selene.util.SeleneUtils;

import java.util.Map;

@Resources(Selene.class)
public enum DefaultResource implements ResourceEntry {
    // Color formats
    COLOR_PRIMARY("b", "color.primary"),
    COLOR_SECONDARY("3", "color.secondary"),
    COLOR_MINOR("7", "color.minor"),
    COLOR_ERROR("c", "color.caught"),

    // Message formats
    PREFIX("$3[] $1", "prefix"),
    DEFAULT_SINGLE_MESSAGE("$3[] $1{0}", "message"),

    // Errors
    // - Confirm command errors
    CONFIRM_INVALID_ID("$4Could not confirm command: Invalid runner ID", "confirm.invalid.id"),
    CONFIRM_INVALID_ENTRY("$4Could not confirm command: Invalid runner entry", "confirm.invalid.entry"),
    CONFIRM_EXPIRED("$4You have no commands waiting for confirmation", "confirm.expired"),
    CONFIRM_WRONG_SOURCE("$4This command can only be used by players", "confirm.invalid.source"),

    // - Generic common caught message
    UNKNOWN_ERROR("$4An caught occurred. $3{0}", "caught"),
    KEY_BINDING_FAILED("$4Key cannot be applied to this type", "caught.keys.failedbinding"),
    LOST_REFERENCE("$4Reference to object lost", "caught.reference.lost"),

    // CommandBus
    CONFIRM_COMMAND_MESSAGE("$1This command requires confirmation, click $2[here] $1to confirm", "confirm.message"),
    CONFIRM_COMMAND_MESSAGE_HOVER("$1Confirm running command", "confirm.message.hover"),
    MISSING_ARGUMENTS("$4The command requires arguments", "caught.command.missingargs"),

    // Default characters
    DEFAULT_SEPARATOR(" - ", "separator"),
    DEFAULT_PAGINATION_PADDING("&m$2=", "pagination.padding"),

    // Sources
    UNKNOWN("Unknown", "source.unknown"),
    NONE("None", "source.none"),
    CONSOLE("Console", "source.console"),

    // Warnings
    IN_ACTIVE_COOLDOWN("$4You are in cooldown! Please wait before performing this action again.", "cooldown.warning"),

    // Enchantments
    AQUA_AFFINITY("Aqua Affinity", "minecraft.enchant.aquaaffinity"),
    BANE_OF_ARTHROPODS("Bane Of Arthropods", "minecraft.enchant.baneofarthopods"),
    BINDING_CURSE("Binding Curse", "minecraft.enchant.binding"),
    BLAST_PROTECTION("Blast Protection", "minecraft.enchant.protection.blast"),
    DEPTH_STRIDER("Depth Strider", "minecraft.enchant.depthstrider"),
    EFFICIENCY("Efficiency", "minecraft.enchant.efficiency"),
    FEATHER_FALLING("Feather Falling", "minecraft.enchant.featherfalling"),
    FIRE_ASPECT("Fire Aspect", "minecraft.enchant.fireaspect"),
    FIRE_PROTECTION("Fire Protection", "minecraft.enchant.protection.fire"),
    FLAME("Flame", "minecraft.enchant.flame"),
    FORTUNE("Fortune", "minecraft.enchant.fortune"),
    FROST_WALKER("Frost Walker", "minecraft.enchant.frostwalker"),
    INFINITY("Infinity", "minecraft.enchant.infinity"),
    KNOCKBACK("Knockback", "minecraft.enchant.knockback"),
    LOOTING("Looting", "minecraft.enchant.looting"),
    LUCK_OF_THE_SEA("Luck Of The Sea", "minecraft.enchant.sealuck"),
    LURE("Lure", "minecraft.enchant.lure"),
    MENDING("Mending", "minecraft.enchant.mending"),
    POWER("Power", "minecraft.enchant.power"),
    PROJECTILE_PROTECTION("Projectile Protection", "minecraft.enchant.protection.projectile"),
    PROTECTION("Protection", "minecraft.enchant.protection"),
    PUNCH("Punch", "minecraft.enchant.punch"),
    RESPIRATION("Respiration", "minecraft.enchant.respiration"),
    SHARPNESS("Sharpness", "minecraft.enchant.sharpness"),
    SILK_TOUCH("Silk Touch", "minecraft.enchant.silktouch"),
    SMITE("Smite", "minecraft.enchant.smite"),
    SWEEPING("Sweeping", "minecraft.enchant.sweeping"),
    THORNS("Thorns", "minecraft.enchant.thorns"),
    UNBREAKING("Unbreaking", "minecraft.enchant.unbreaking"),
    VANISHING_CURSE("Vanishing Curse", "minecraft.enchant.vanishing"),
    EXCEPTION("$4{0}", "selene.exception");

    private final String key;
    private final Map<Language, String> translations = SeleneUtils.emptyConcurrentMap();
    private String value;

    DefaultResource(String value, String key) {
        this.value = value;
        this.key = key;
    }

    public static String parse(CharSequence input) {
        return NONE.parseColors(input.toString());
    }

    public String getValue(MessageReceiver receiver) {
        return this.translate(receiver.getLanguage()).asString();
    }

    @Override
    public String getKey() {
        return "selene." + this.key;
    }

    @Override
    public String asString() {
        return this.parseColors(this.value);
    }

    @Override
    public String plain() {
        return ResourceEntry.plain(this.value);
    }

    @Override
    public ResourceEntry translate(Language lang) {
        if (this.translations.containsKey(lang))
            return new Resource(this.translations.get(lang), this.getKey());
        return this;
    }

    public void setLanguageValue(Language lang, String value) {
        this.translations.put(lang, value);
        if (lang == Language.EN_US) this.value = value;
    }
}
