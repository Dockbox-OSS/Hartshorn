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

package org.dockbox.selene.minecraft.item;

import org.dockbox.selene.api.i18n.entry.DefaultResource;
import org.dockbox.selene.server.minecraft.MinecraftVersion;

public enum Enchantment {

    /**
     * Increases regular underwater mining speed.
     *
     * <p>In vanilla the maximum level is 1.
     */
    AQUA_AFFINITY(1, DefaultResource.AQUA_AFFINITY, MinecraftVersion.MC1_12),

    /**
     * Increases damages and causes slowness for a variable amount of time depending on the level to
     * "arthropod" mobs. In vanilla this includes spiders, cave spiders, silverfish, and endermites.
     *
     * <p>In vanilla the maximum level is 5.
     */
    BANE_OF_ARTHROPODS(5, DefaultResource.BANE_OF_ARTHROPODS, MinecraftVersion.MC1_12),

    /**
     * Prevents removal of the cursed items that reside in the armor slots.
     *
     * <p>In vanilla the maximum level is 1.
     */
    BINDING_CURSE(1, DefaultResource.BINDING_CURSE, MinecraftVersion.MC1_12),

    /**
     * Reduces explosion damage.
     *
     * <p>In vanilla the maximum level is 4.
     */
    BLAST_PROTECTION(4, DefaultResource.BLAST_PROTECTION, MinecraftVersion.MC1_12),

    /**
     * Increases underwater movement speed.
     *
     * <p>In vanilla the maximum level is 3.
     */
    DEPTH_STRIDER(3, DefaultResource.DEPTH_STRIDER, MinecraftVersion.MC1_12),

    /**
     * Increases mining speed.
     *
     * <p>In vanilla the maximum level is 5.
     */
    EFFICIENCY(5, DefaultResource.EFFICIENCY, MinecraftVersion.MC1_12),

    /**
     * Reduces fall damage.
     *
     * <p>In vanilla the maximum level is 4.
     */
    FEATHER_FALLING(4, DefaultResource.FEATHER_FALLING, MinecraftVersion.MC1_12),

    /**
     * Sets the target on fire.
     *
     * <p>In vanilla the maximum level is 2.
     */
    FIRE_ASPECT(2, DefaultResource.FIRE_ASPECT, MinecraftVersion.MC1_12),

    /**
     * Reduces fire damage.
     *
     * <p>In vanilla the maximum level is 4.
     */
    FIRE_PROTECTION(4, DefaultResource.FIRE_PROTECTION, MinecraftVersion.MC1_12),

    /**
     * Sets your shot arrows on fire.
     *
     * <p>In vanilla the maximum level is 1.
     */
    FLAME(1, DefaultResource.FLAME, MinecraftVersion.MC1_12),

    /**
     * Increases block drops.
     *
     * <p>In vanilla the maximum level is 3.
     */
    FORTUNE(3, DefaultResource.FORTUNE, MinecraftVersion.MC1_12),

    /**
     * Creates frosted ice blocks when walking over water.
     *
     * <p>In vanilla the maximum level is 2.
     */
    FROST_WALKER(2, DefaultResource.FROST_WALKER, MinecraftVersion.MC1_12),

    /**
     * Causing shooting arrows to not consume regular arrows.
     *
     * <p>In vanilla the maximum level is 1.
     */
    INFINITY(1, DefaultResource.INFINITY, MinecraftVersion.MC1_12),

    /**
     * Increases attack knockback.
     *
     * <p>In vanilla the maximum level is 2.
     */
    KNOCKBACK(2, DefaultResource.KNOCKBACK, MinecraftVersion.MC1_12),

    /**
     * Causes mobs drop more loot.
     *
     * <p>In vanilla the maximum level is 3.
     */
    LOOTING(3, DefaultResource.LOOTING, MinecraftVersion.MC1_12),

    /**
     * Increases luck while fishing.
     *
     * <p>In vanilla the maximum level is 3.
     */
    LUCK_OF_THE_SEA(3, DefaultResource.LUCK_OF_THE_SEA, MinecraftVersion.MC1_12),

    /**
     * Increases rate of fish biting your hook while fishing.
     *
     * <p>In vanilla the maximum level is 3.
     */
    LURE(3, DefaultResource.LURE, MinecraftVersion.MC1_12),

    /**
     * Repair item durability with experience.
     *
     * <p>In vanilla the maximum level is 1.
     */
    MENDING(1, DefaultResource.MENDING, MinecraftVersion.MC1_12),

    /**
     * Increases shot arrow damage.
     *
     * <p>In vanilla the maximum level is 5.
     */
    POWER(5, DefaultResource.POWER, MinecraftVersion.MC1_12),

    /**
     * Reduces projectile damage you take, for example from arrows, ghasts, blaze fire charges, and
     * similar in vanilla.
     *
     * <p>In vanilla the maximum level is 4.
     */
    PROJECTILE_PROTECTION(4, DefaultResource.PROJECTILE_PROTECTION, MinecraftVersion.MC1_12),

    /**
     * Reduces all damage, outside of a few sources that bypass armor, such as the void, the kill
     * command, and hunger damage in vanilla.
     *
     * <p>In vanilla the maximum level is 4.
     */
    PROTECTION(4, DefaultResource.PROTECTION, MinecraftVersion.MC1_12),

    /**
     * Increases knockback by shot arrows.
     *
     * <p>In vanilla the maximum level is 2.
     */
    PUNCH(2, DefaultResource.PUNCH, MinecraftVersion.MC1_12),

    /**
     * Extends underwater breathing time.
     *
     * <p>In vanilla the maximum level is 3.
     */
    RESPIRATION(3, DefaultResource.RESPIRATION, MinecraftVersion.MC1_12),

    /**
     * Increases melee damage.
     *
     * <p>In vanilla the maximum level is 5.
     */
    SHARPNESS(5, DefaultResource.SHARPNESS, MinecraftVersion.MC1_12),

    /**
     * Allows collection of blocks that are normally unobtainable, such as diamond ore, cocoa,
     * mycelium, and similar in vanilla.
     *
     * <p>In vanilla the maximum level is 1.
     */
    SILK_TOUCH(1, DefaultResource.SILK_TOUCH, MinecraftVersion.MC1_12),

    /**
     * Increases damage to "undead" mobs. In vanilla this includes skeletons, skeletons, zombies,
     * withers, wither skeletons, zombie pigmen, skeleton horses and zombie horses.
     *
     * <p>In vanilla the maximum level is 5.
     */
    SMITE(5, DefaultResource.SMITE, MinecraftVersion.MC1_12),

    /**
     * Increases the damage of the sweeping attack.
     *
     * <p>In vanilla the maximum level is 3.
     */
    SWEEPING(3, DefaultResource.SWEEPING, MinecraftVersion.MC1_12),

    /**
     * Attackers are damaged when they deal damage to the wearer.
     *
     * <p>In vanilla the maximum level is 3.
     */
    THORNS(3, DefaultResource.THORNS, MinecraftVersion.MC1_12),

    /**
     * Increases effective durability.
     *
     * <p>In vanilla the maximum level is 3.
     */
    UNBREAKING(3, DefaultResource.UNBREAKING, MinecraftVersion.MC1_12),

    /**
     * Causes the item to disappear on death.
     *
     * <p>In vanilla the maximum level is 1.
     */
    VANISHING_CURSE(1, DefaultResource.VANISHING_CURSE, MinecraftVersion.MC1_12);

    private final int maximumLevel;
    private final DefaultResource nameResource;
    private final MinecraftVersion minimumMinecraftVersion;

    Enchantment(
            int maximumLevel, DefaultResource nameResource, MinecraftVersion minimumMinecraftVersion) {
        this.maximumLevel = maximumLevel;
        this.nameResource = nameResource;
        this.minimumMinecraftVersion = minimumMinecraftVersion;
    }

    public int getMaximumLevel() {
        return this.maximumLevel;
    }

    public DefaultResource getNameResource() {
        return this.nameResource;
    }

    public MinecraftVersion getMinimumMinecraftVersion() {
        return this.minimumMinecraftVersion;
    }
}
