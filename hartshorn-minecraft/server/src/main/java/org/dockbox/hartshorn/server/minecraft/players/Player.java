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

package org.dockbox.hartshorn.server.minecraft.players;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.domain.AbstractIdentifiable;
import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.domain.tuple.Tristate;
import org.dockbox.hartshorn.api.i18n.PermissionHolder;
import org.dockbox.hartshorn.api.i18n.common.Language;
import org.dockbox.hartshorn.api.i18n.permissions.Permission;
import org.dockbox.hartshorn.api.i18n.permissions.PermissionContext;
import org.dockbox.hartshorn.api.i18n.text.Text;
import org.dockbox.hartshorn.api.keys.Keys;
import org.dockbox.hartshorn.api.keys.PersistentDataHolder;
import org.dockbox.hartshorn.api.keys.PersistentDataKey;
import org.dockbox.hartshorn.commands.source.CommandSource;
import org.dockbox.hartshorn.server.minecraft.dimension.Locatable;
import org.dockbox.hartshorn.server.minecraft.dimension.position.Location;
import org.dockbox.hartshorn.server.minecraft.dimension.world.World;
import org.dockbox.hartshorn.server.minecraft.entities.Entity;
import org.dockbox.hartshorn.server.minecraft.inventory.InventoryHolder;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.packets.PacketReceiver;
import org.dockbox.hartshorn.server.minecraft.players.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class Player extends AbstractIdentifiable implements CommandSource, PermissionHolder, Locatable, InventoryHolder, PacketReceiver, PersistentDataHolder, Entity {

    // An empty context targets only global permissions
    private static final PermissionContext GLOBAL = PermissionContext.builder().build();
    public static final PersistentDataKey<Integer> LANGUAGE = Keys.persistent(Integer.class, "HartshornPlayerLanguage", Hartshorn.class);

    protected Player(@NotNull UUID uniqueId, @NotNull String name) {
        super(uniqueId, name);
    }

    public abstract boolean isOnline();

    public abstract void kick(Text reason);

    public abstract Gamemode getGamemode();

    public abstract void setGamemode(Gamemode gamemode);

    @Override
    public boolean hasAnyPermission(@NotNull String @NotNull ... permissions) {
        for (String permission : permissions) {
            if (this.hasPermission(permission)) return true;
        }
        return false;
    }

    @Override
    public boolean hasAllPermissions(@NotNull String @NotNull ... permissions) {
        for (String permission : permissions) {
            if (!this.hasPermission(permission)) return false;
        }
        return true;
    }

    @Override
    public boolean hasAnyPermission(@NotNull Permission @NotNull ... permissions) {
        for (Permission permission : permissions) {
            if (this.hasPermission(permission)) return true;
        }
        return false;
    }

    @Override
    public boolean hasAllPermissions(@NotNull Permission @NotNull ... permissions) {
        for (Permission permission : permissions) {
            if (!this.hasPermission(permission)) return false;
        }
        return true;
    }

    @Override
    public void setPermissions(Tristate state, @NotNull String @NotNull ... permissions) {
        for (String permission : permissions) {
            this.setPermission(permission, state);
        }
    }

    @Override
    public void setPermissions(Tristate state, @NotNull Permission @NotNull ... permissions) {
        for (Permission permission : permissions) {
            this.setPermission(permission, state);
        }
    }

    @Override
    public boolean isAlive() {
        return this.getHealth() > 0;
    }

    @Override
    public boolean summon(Location location) {
        throw new UnsupportedOperationException("Cannot re-summon players");
    }

    @Override
    public boolean destroy() {
        throw new UnsupportedOperationException("Cannot destroy players");
    }

    @Override
    public World getWorld() {
        // No reference refresh required as this is done by getLocation. Should never throw NPE as
        // Location is either
        // valid or EMPTY (World instance follows this same guideline).
        return this.getLocation().getWorld();
    }

    @Override
    public PermissionContext activeContext() {
        if (!this.isOnline()) {
            return GLOBAL;
        } else {
            return PermissionContext.builder()
                    .world(this.getWorld().getName())
                    .build();
        }
    }

    @Override
    public Language getLanguage() {
        return this.get(LANGUAGE).map(ordinal -> Language.values()[ordinal]).or(Language.EN_US);
    }

    @Override
    public void setLanguage(Language language) {
        this.set(LANGUAGE, language.ordinal());
    }

    public abstract Item getItemInHand(Hand hand);

    public abstract void setItemInHand(Hand hand, Item item);

    public abstract void play(Sounds sound);

    @Override
    public int hashCode() {
        return this.getUniqueId().hashCode();
    }

    @SuppressWarnings("OverlyStrongTypeCast")
    @Override
    public boolean equals(Object obj) {
        if (null == obj) return false;
        if (obj instanceof Player) return this.getUniqueId().equals(((Player) obj).getUniqueId());
        return false;
    }

    public abstract boolean isSneaking();

    public abstract Profile getProfile();

    public abstract Exceptional<Location> getLookingAtBlockPos();

    @Override
    public abstract PlayerInventory getInventory();

    public abstract GameSettings getGameSettings();
}
