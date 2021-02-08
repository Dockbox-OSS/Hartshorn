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

package org.dockbox.selene.api.objects.item;

import org.dockbox.selene.api.MinecraftVersion;
import org.dockbox.selene.api.i18n.entry.IntegratedResource;

public enum Enchantment
{

    /**
     * Increases regular underwater mining speed.
     *
     * <p>In vanilla the maximum level is 1.</p>
     */
    AQUA_AFFINITY(1, IntegratedResource.AQUA_AFFINITY, MinecraftVersion.MC1_12),

    /**
     * Increases damages and causes slowness for a variable amount of time
     * depending on the level to "arthropod" mobs. In vanilla this includes
     * spiders, cave spiders, silverfish, and endermites.
     *
     * <p>In vanilla the maximum level is 5.</p>
     */
    BANE_OF_ARTHROPODS(5, IntegratedResource.BANE_OF_ARTHROPODS, MinecraftVersion.MC1_12),

    /**
     * Prevents removal of the cursed items that reside in the armor slots.
     *
     * <p>In vanilla the maximum level is 1.</p>
     */
    BINDING_CURSE(1, IntegratedResource.BINDING_CURSE, MinecraftVersion.MC1_12),

    /**
     * Reduces explosion damage.
     *
     * <p>In vanilla the maximum level is 4.</p>
     */
    BLAST_PROTECTION(4, IntegratedResource.BLAST_PROTECTION, MinecraftVersion.MC1_12),

    /**
     * Increases underwater movement speed.
     *
     * <p>In vanilla the maximum level is 3.</p>
     */
    DEPTH_STRIDER(3, IntegratedResource.DEPTH_STRIDER, MinecraftVersion.MC1_12),

    /**
     * Increases mining speed.
     *
     * <p>In vanilla the maximum level is 5.</p>
     */
    EFFICIENCY(5, IntegratedResource.EFFICIENCY, MinecraftVersion.MC1_12),

    /**
     * Reduces fall damage.
     *
     * <p>In vanilla the maximum level is 4.</p>
     */
    FEATHER_FALLING(4, IntegratedResource.FEATHER_FALLING, MinecraftVersion.MC1_12),

    /**
     * Sets the target on fire.
     *
     * <p>In vanilla the maximum level is 2.</p>
     */
    FIRE_ASPECT(2, IntegratedResource.FIRE_ASPECT, MinecraftVersion.MC1_12),

    /**
     * Reduces fire damage.
     *
     * <p>In vanilla the maximum level is 4.</p>
     */
    FIRE_PROTECTION(4, IntegratedResource.FIRE_PROTECTION, MinecraftVersion.MC1_12),

    /**
     * Sets your shot arrows on fire.
     *
     * <p>In vanilla the maximum level is 1.</p>
     */
    FLAME(1, IntegratedResource.FLAME, MinecraftVersion.MC1_12),

    /**
     * Increases block drops.
     *
     * <p>In vanilla the maximum level is 3.</p>
     */
    FORTUNE(3, IntegratedResource.FORTUNE, MinecraftVersion.MC1_12),

    /**
     * Creates frosted ice blocks when walking over water.
     *
     * <p>In vanilla the maximum level is 2.</p>
     */
    FROST_WALKER(2, IntegratedResource.FROST_WALKER, MinecraftVersion.MC1_12),

    /**
     * Causing shooting arrows to not consume regular arrows.
     *
     * <p>In vanilla the maximum level is 1.</p>
     */
    INFINITY(1, IntegratedResource.INFINITY, MinecraftVersion.MC1_12),

    /**
     * Increases attack knockback.
     *
     * <p>In vanilla the maximum level is 2.</p>
     */
    KNOCKBACK(2, IntegratedResource.KNOCKBACK, MinecraftVersion.MC1_12),

    /**
     * Causes mobs drop more loot.
     *
     * <p>In vanilla the maximum level is 3.</p>
     */
    LOOTING(3, IntegratedResource.LOOTING, MinecraftVersion.MC1_12),

    /**
     * Increases luck while fishing.
     *
     * <p>In vanilla the maximum level is 3.</p>
     */
    LUCK_OF_THE_SEA(3, IntegratedResource.LUCK_OF_THE_SEA, MinecraftVersion.MC1_12),

    /**
     * Increases rate of fish biting your hook while fishing.
     *
     * <p>In vanilla the maximum level is 3.</p>
     */
    LURE(3, IntegratedResource.LURE, MinecraftVersion.MC1_12),

    /**
     * Repair item durability with experience.
     *
     * <p>In vanilla the maximum level is 1.</p>
     */
    MENDING(1, IntegratedResource.MENDING, MinecraftVersion.MC1_12),

    /**
     * Increases shot arrow damage.
     *
     * <p>In vanilla the maximum level is 5.</p>
     */
    POWER(5, IntegratedResource.POWER, MinecraftVersion.MC1_12),

    /**
     * Reduces projectile damage you take, for example from arrows, ghasts,
     * blaze fire charges, and similar in vanilla.
     *
     * <p>In vanilla the maximum level is 4.</p>
     */
    PROJECTILE_PROTECTION(4, IntegratedResource.PROJECTILE_PROTECTION, MinecraftVersion.MC1_12),

    /**
     * Reduces all damage, outside of a few sources that bypass armor, such as
     * the void, the kill command, and hunger damage in vanilla.
     *
     * <p>In vanilla the maximum level is 4.</p>
     */
    PROTECTION(4, IntegratedResource.PROTECTION, MinecraftVersion.MC1_12),

    /**
     * Increases knockback by shot arrows.
     *
     * <p>In vanilla the maximum level is 2.</p>
     */
    PUNCH(2, IntegratedResource.PUNCH, MinecraftVersion.MC1_12),

    /**
     * Extends underwater breathing time.
     *
     * <p>In vanilla the maximum level is 3.</p>
     */
    RESPIRATION(3, IntegratedResource.RESPIRATION, MinecraftVersion.MC1_12),

    /**
     * Increases melee damage.
     *
     * <p>In vanilla the maximum level is 5.</p>
     */
    SHARPNESS(5, IntegratedResource.SHARPNESS, MinecraftVersion.MC1_12),

    /**
     * Allows collection of blocks that are normally unobtainable, such as
     * diamond ore, cocoa, mycelium, and similar in vanilla.
     *
     * <p>In vanilla the maximum level is 1.</p>
     */
    SILK_TOUCH(1, IntegratedResource.SILK_TOUCH, MinecraftVersion.MC1_12),

    /**
     * Increases damage to "undead" mobs. In vanilla this includes skeletons,
     * skeletons, zombies, withers, wither skeletons, zombie pigmen,
     * skeleton horses and zombie horses.
     *
     * <p>In vanilla the maximum level is 5.</p>
     */
    SMITE(5, IntegratedResource.SMITE, MinecraftVersion.MC1_12),

    /**
     * Increases the damage of the sweeping attack.
     *
     * <p>In vanilla the maximum level is 3.</p>
     */
    SWEEPING(3, IntegratedResource.SWEEPING, MinecraftVersion.MC1_12),

    /**
     * Attackers are damaged when they deal damage to the wearer.
     *
     * <p>In vanilla the maximum level is 3.</p>
     */
    THORNS(3, IntegratedResource.THORNS, MinecraftVersion.MC1_12),

    /**
     * Increases effective durability.
     *
     * <p>In vanilla the maximum level is 3.</p>
     */
    UNBREAKING(3, IntegratedResource.UNBREAKING, MinecraftVersion.MC1_12),

    /**
     * Causes the item to disappear on death.
     *
     * <p>In vanilla the maximum level is 1.</p>
     */
    VANISHING_CURSE(1, IntegratedResource.VANISHING_CURSE, MinecraftVersion.MC1_12);

    private final int maximumLevel;
    private final IntegratedResource nameResource;
    private final MinecraftVersion minimumMinecraftVersion;

    Enchantment(int maximumLevel, IntegratedResource nameResource, MinecraftVersion minimumMinecraftVersion)
    {
        this.maximumLevel = maximumLevel;
        this.nameResource = nameResource;
        this.minimumMinecraftVersion = minimumMinecraftVersion;
    }

    public int getMaximumLevel()
    {
        return this.maximumLevel;
    }

    public IntegratedResource getNameResource()
    {
        return this.nameResource;
    }

    public MinecraftVersion getMinimumMinecraftVersion()
    {
        return this.minimumMinecraftVersion;
    }
}
