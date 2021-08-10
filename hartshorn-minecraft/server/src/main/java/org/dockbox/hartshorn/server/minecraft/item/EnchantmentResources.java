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

import org.dockbox.hartshorn.di.annotations.service.Service;
import org.dockbox.hartshorn.i18n.annotations.Resource;
import org.dockbox.hartshorn.i18n.common.ResourceEntry;

@Service
public interface EnchantmentResources {

    @Resource(value = "Aqua Affinity", key = "minecraft.enchant.aquaaffinity")
    ResourceEntry aquaAffinity();

    @Resource(value = "Bane Of Arthropods", key = "minecraft.enchant.baneofarthopods")
    ResourceEntry baneOfArthropods();

    @Resource(value = "Binding Curse", key = "minecraft.enchant.binding")
    ResourceEntry bindingCurse();

    @Resource(value = "Blast Protection", key = "minecraft.enchant.protection.blast")
    ResourceEntry blastProtection();

    @Resource(value = "Depth Strider", key = "minecraft.enchant.depthstrider")
    ResourceEntry depthStrider();

    @Resource(value = "Efficiency", key = "minecraft.enchant.efficiency")
    ResourceEntry efficiency();

    @Resource(value = "Feather Falling", key = "minecraft.enchant.featherfalling")
    ResourceEntry featherFalling();

    @Resource(value = "Fire Aspect", key = "minecraft.enchant.fireaspect")
    ResourceEntry fireAspect();

    @Resource(value = "Fire Protection", key = "minecraft.enchant.protection.fire")
    ResourceEntry fireProtection();

    @Resource(value = "Flame", key = "minecraft.enchant.flame")
    ResourceEntry flame();

    @Resource(value = "Fortune", key = "minecraft.enchant.fortune")
    ResourceEntry fortune();

    @Resource(value = "Frost Walker", key = "minecraft.enchant.frostwalker")
    ResourceEntry frostWalker();

    @Resource(value = "Infinity", key = "minecraft.enchant.infinity")
    ResourceEntry infinity();

    @Resource(value = "Knockback", key = "minecraft.enchant.knockback")
    ResourceEntry knockback();

    @Resource(value = "Looting", key = "minecraft.enchant.looting")
    ResourceEntry looting();

    @Resource(value = "Luck Of The Sea", key = "minecraft.enchant.sealuck")
    ResourceEntry luckOfTheSea();

    @Resource(value = "Lure", key = "minecraft.enchant.lure")
    ResourceEntry lure();

    @Resource(value = "Mending", key = "minecraft.enchant.mending")
    ResourceEntry mending();

    @Resource(value = "Power", key = "minecraft.enchant.power")
    ResourceEntry power();

    @Resource(value = "Projectile Protection", key = "minecraft.enchant.protection.projectile")
    ResourceEntry projectileProtection();

    @Resource(value = "Protection", key = "minecraft.enchant.protection")
    ResourceEntry protection();

    @Resource(value = "Punch", key = "minecraft.enchant.punch")
    ResourceEntry punch();

    @Resource(value = "Respiration", key = "minecraft.enchant.respiration")
    ResourceEntry respiration();

    @Resource(value = "Sharpness", key = "minecraft.enchant.sharpness")
    ResourceEntry sharpness();

    @Resource(value = "Silk Touch", key = "minecraft.enchant.silktouch")
    ResourceEntry silkTouch();

    @Resource(value = "Smite", key = "minecraft.enchant.smite")
    ResourceEntry smite();

    @Resource(value = "Sweeping", key = "minecraft.enchant.sweeping")
    ResourceEntry sweeping();

    @Resource(value = "Thorns", key = "minecraft.enchant.thorns")
    ResourceEntry thorns();

    @Resource(value = "Unbreaking", key = "minecraft.enchant.unbreaking")
    ResourceEntry unbreaking();

    @Resource(value = "Vanishing Curse", key = "minecraft.enchant.vanishing")
    ResourceEntry vanishingCurse();

}
