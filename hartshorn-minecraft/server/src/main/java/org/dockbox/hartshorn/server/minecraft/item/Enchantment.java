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

package org.dockbox.hartshorn.server.minecraft.item;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.i18n.common.ResourceEntry;
import org.dockbox.hartshorn.server.minecraft.MinecraftVersion;

import java.util.function.Function;

public enum Enchantment {

    /**
     * Increases regular underwater mining speed.
     *
     * <p>In vanilla the maximum level is 1.
     */
    AQUA_AFFINITY(1, EnchantmentResources::aquaAffinity, MinecraftVersion.MC1_16),

    /**
     * Increases damages and causes slowness for a variable amount of time depending on the level to
     * "arthropod" mobs. In vanilla this includes spiders, cave spiders, silverfish, and endermites.
     *
     * <p>In vanilla the maximum level is 5.
     */
    BANE_OF_ARTHROPODS(5, EnchantmentResources::baneOfArthropods, MinecraftVersion.MC1_16),

    /**
     * Prevents removal of the cursed items that reside in the armor slots.
     *
     * <p>In vanilla the maximum level is 1.
     */
    BINDING_CURSE(1, EnchantmentResources::bindingCurse, MinecraftVersion.MC1_16),

    /**
     * Reduces explosion damage.
     *
     * <p>In vanilla the maximum level is 4.
     */
    BLAST_PROTECTION(4, EnchantmentResources::blastProtection, MinecraftVersion.MC1_16),

    /**
     * Increases underwater movement speed.
     *
     * <p>In vanilla the maximum level is 3.
     */
    DEPTH_STRIDER(3, EnchantmentResources::depthStrider, MinecraftVersion.MC1_16),

    /**
     * Increases mining speed.
     *
     * <p>In vanilla the maximum level is 5.
     */
    EFFICIENCY(5, EnchantmentResources::efficiency, MinecraftVersion.MC1_16),

    /**
     * Reduces fall damage.
     *
     * <p>In vanilla the maximum level is 4.
     */
    FEATHER_FALLING(4, EnchantmentResources::featherFalling, MinecraftVersion.MC1_16),

    /**
     * Sets the target on fire.
     *
     * <p>In vanilla the maximum level is 2.
     */
    FIRE_ASPECT(2, EnchantmentResources::fireAspect, MinecraftVersion.MC1_16),

    /**
     * Reduces fire damage.
     *
     * <p>In vanilla the maximum level is 4.
     */
    FIRE_PROTECTION(4, EnchantmentResources::fireProtection, MinecraftVersion.MC1_16),

    /**
     * Sets your shot arrows on fire.
     *
     * <p>In vanilla the maximum level is 1.
     */
    FLAME(1, EnchantmentResources::flame, MinecraftVersion.MC1_16),

    /**
     * Increases block drops.
     *
     * <p>In vanilla the maximum level is 3.
     */
    FORTUNE(3, EnchantmentResources::fortune, MinecraftVersion.MC1_16),

    /**
     * Creates frosted ice blocks when walking over water.
     *
     * <p>In vanilla the maximum level is 2.
     */
    FROST_WALKER(2, EnchantmentResources::frostWalker, MinecraftVersion.MC1_16),

    /**
     * Causing shooting arrows to not consume regular arrows.
     *
     * <p>In vanilla the maximum level is 1.
     */
    INFINITY(1, EnchantmentResources::infinity, MinecraftVersion.MC1_16),

    /**
     * Increases attack knockback.
     *
     * <p>In vanilla the maximum level is 2.
     */
    KNOCKBACK(2, EnchantmentResources::knockback, MinecraftVersion.MC1_16),

    /**
     * Causes mobs drop more loot.
     *
     * <p>In vanilla the maximum level is 3.
     */
    LOOTING(3, EnchantmentResources::looting, MinecraftVersion.MC1_16),

    /**
     * Increases luck while fishing.
     *
     * <p>In vanilla the maximum level is 3.
     */
    LUCK_OF_THE_SEA(3, EnchantmentResources::luckOfTheSea, MinecraftVersion.MC1_16),

    /**
     * Increases rate of fish biting your hook while fishing.
     *
     * <p>In vanilla the maximum level is 3.
     */
    LURE(3, EnchantmentResources::lure, MinecraftVersion.MC1_16),

    /**
     * Repair item durability with experience.
     *
     * <p>In vanilla the maximum level is 1.
     */
    MENDING(1, EnchantmentResources::mending, MinecraftVersion.MC1_16),

    /**
     * Increases shot arrow damage.
     *
     * <p>In vanilla the maximum level is 5.
     */
    POWER(5, EnchantmentResources::power, MinecraftVersion.MC1_16),

    /**
     * Reduces projectile damage you take, for example from arrows, ghasts, blaze fire charges, and
     * similar in vanilla.
     *
     * <p>In vanilla the maximum level is 4.
     */
    PROJECTILE_PROTECTION(4, EnchantmentResources::projectileProtection, MinecraftVersion.MC1_16),

    /**
     * Reduces all damage, outside of a few sources that bypass armor, such as the void, the kill
     * command, and hunger damage in vanilla.
     *
     * <p>In vanilla the maximum level is 4.
     */
    PROTECTION(4, EnchantmentResources::protection, MinecraftVersion.MC1_16),

    /**
     * Increases knockback by shot arrows.
     *
     * <p>In vanilla the maximum level is 2.
     */
    PUNCH(2, EnchantmentResources::punch, MinecraftVersion.MC1_16),

    /**
     * Extends underwater breathing time.
     *
     * <p>In vanilla the maximum level is 3.
     */
    RESPIRATION(3, EnchantmentResources::respiration, MinecraftVersion.MC1_16),

    /**
     * Increases melee damage.
     *
     * <p>In vanilla the maximum level is 5.
     */
    SHARPNESS(5, EnchantmentResources::sharpness, MinecraftVersion.MC1_16),

    /**
     * Allows collection of blocks that are normally unobtainable, such as diamond ore, cocoa,
     * mycelium, and similar in vanilla.
     *
     * <p>In vanilla the maximum level is 1.
     */
    SILK_TOUCH(1, EnchantmentResources::silkTouch, MinecraftVersion.MC1_16),

    /**
     * Increases damage to "undead" mobs. In vanilla this includes skeletons, skeletons, zombies,
     * withers, wither skeletons, zombie pigmen, skeleton horses and zombie horses.
     *
     * <p>In vanilla the maximum level is 5.
     */
    SMITE(5, EnchantmentResources::smite, MinecraftVersion.MC1_16),

    /**
     * Increases the damage of the sweeping attack.
     *
     * <p>In vanilla the maximum level is 3.
     */
    SWEEPING(3, EnchantmentResources::sweeping, MinecraftVersion.MC1_16),

    /**
     * Attackers are damaged when they deal damage to the wearer.
     *
     * <p>In vanilla the maximum level is 3.
     */
    THORNS(3, EnchantmentResources::thorns, MinecraftVersion.MC1_16),

    /**
     * Increases effective durability.
     *
     * <p>In vanilla the maximum level is 3.
     */
    UNBREAKING(3, EnchantmentResources::unbreaking, MinecraftVersion.MC1_16),

    /**
     * Causes the item to disappear on death.
     *
     * <p>In vanilla the maximum level is 1.
     */
    VANISHING_CURSE(1, EnchantmentResources::vanishingCurse, MinecraftVersion.MC1_16);

    private final int maximumLevel;
    private final ResourceEntry nameResource;
    private final MinecraftVersion minimumMinecraftVersion;

    Enchantment(int maximumLevel, Function<EnchantmentResources, ResourceEntry> resource, MinecraftVersion minimumMinecraftVersion) {
        this.maximumLevel = maximumLevel;
        this.nameResource = resource.apply(Hartshorn.context().get(EnchantmentResources.class));
        this.minimumMinecraftVersion = minimumMinecraftVersion;
    }

    public int getMaximumLevel() {
        return this.maximumLevel;
    }

    public ResourceEntry getNameResource() {
        return this.nameResource;
    }

    public MinecraftVersion getMinimumMinecraftVersion() {
        return this.minimumMinecraftVersion;
    }
}
